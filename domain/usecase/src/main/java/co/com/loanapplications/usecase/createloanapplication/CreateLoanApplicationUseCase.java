package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.LoanType;
import co.com.loanapplications.model.loanapplication.enums.PredefinedStatusesEnum;
import co.com.loanapplications.model.loanapplication.events.CapacityRequestEvent;
import co.com.loanapplications.model.loanapplication.events.LoanSummary;
import co.com.loanapplications.model.loanapplication.events.NewLoan;
import co.com.loanapplications.model.loanapplication.exceptions.*;
import co.com.loanapplications.model.loanapplication.gateways.*;
import co.com.loanapplications.model.loanapplication.identity.UserDto;
import co.com.loanapplications.usecase.createloanapplication.helpers.EmailValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ApplicationStatusRepository statusRepository;
    private final IdentityRepository identityRepository;
    private final LoanApplicationStatusEventRepository loanApplicationStatusEventRepository;

    public Mono<LoanApplication> createLoanApplication(LoanApplication loanApplication, String loanTypeName) {

        if (loanApplication == null) {
            return Mono.error(new LoanApplicationNotValidException());
        }
        if (loanApplication.getAmount() == null) {
            return Mono.error(new AmountNotValidException());
        }
        if (loanApplication.getTermMonths() == null) {
            return Mono.error(new TermMonthsNotValidException());
        }
        if (loanApplication.getEmail() == null || loanApplication.getEmail().isBlank()) {
            return Mono.error(new EmailNotValidException());
        }
        if (!EmailValidator.isValid(loanApplication.getEmail())) {
            return Mono.error(new EmailNotValidException());
        }

        return loanTypeRepository.findByName(loanTypeName)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException()))
                .flatMap(loanType -> {
                    LoanApplication updatedLoanApp = loanApplication.toBuilder()
                            .loanTypeId(loanType.getId())
                            .build();

                    return identityRepository.findByEmail(updatedLoanApp.getEmail())
                            .switchIfEmpty(Mono.error(new UserEmailNotFoundException()))
                            .flatMap(userDto -> {
                                Double min = loanType.getMinAmount();
                                Double max = loanType.getMaxAmount();
                                Double amount = updatedLoanApp.getAmount();

                                if ((min != null && amount < min) || (max != null && amount > max)) {
                                    return Mono.error(new AmountOutOfRangeException());
                                }

                                return statusRepository.findByName(PredefinedStatusesEnum.PENDING_REVIEW.getName())
                                        .switchIfEmpty(Mono.fromSupplier(() ->
                                                ApplicationStatus.builder()
                                                        .name(PredefinedStatusesEnum.PENDING_REVIEW.getName())
                                                        .description(PredefinedStatusesEnum.PENDING_REVIEW.getDescription())
                                                        .build()))
                                        .flatMap(initialStatus -> {
                                            LoanApplication toSave = updatedLoanApp.toBuilder()
                                                    .statusId(initialStatus.getId())
                                                    .build();
                                            return loanApplicationRepository.save(toSave)
                                                    .flatMap(saved -> {
                                                        if (loanType.getAutomaticValidation()) {
                                                            return calculateDebtAndPublish(saved, loanType, userDto)
                                                                    .thenReturn(saved);
                                                        }
                                                        return Mono.just(saved);
                                                    });
                                        });
                            });

                });
    }

    private Mono<Void> calculateDebtAndPublish(LoanApplication newApp, LoanType loanType, UserDto user) {
        return statusRepository.findByName(PredefinedStatusesEnum.APPROVED.getName())
                .switchIfEmpty(Mono.error(new ApplicationStatusNotFoundException()))
                .flatMapMany(approvedStatus ->
                        loanApplicationRepository.findByEmailAndStatusId(user.getEmail(), approvedStatus.getId())
                )
                .map(loan -> new LoanSummary(
                        loan.getAmount(),
                        loanType.getInterestRate(),
                        loan.getTermMonths()
                ))
                .collectList()
                .flatMap(activeLoans -> {
                    double totalDebt = activeLoans.stream()
                            .mapToDouble(l -> calculateMonthlyInstallment(
                                    l.getPrincipal(),
                                    l.getAnnualInterestRate(),
                                    l.getTermMonths()))
                            .sum();

                    double newLoanInstallment = calculateMonthlyInstallment(
                            newApp.getAmount(),
                            loanType.getInterestRate(),
                            newApp.getTermMonths()
                    );

                    CapacityRequestEvent event = CapacityRequestEvent.builder()
                            .applicationId(newApp.getId())
                            .applicantEmail(user.getEmail())
                            .applicantMonthlyIncome(user.getBaseSalary())
                            .currentDebt(totalDebt)
                            .activeLoans(activeLoans)
                            .newLoan(new NewLoan(
                                    newApp.getAmount(),
                                    loanType.getInterestRate(),
                                    newApp.getTermMonths(),
                                    loanType.getName()))
                            .newLoanMonthlyInstallment(newLoanInstallment)
                            .build();

                    return loanApplicationStatusEventRepository.validate(event);
                });
    }

    private double calculateMonthlyInstallment(double amount, BigDecimal annualRate, int termMonths) {
        double rateDecimal = annualRate.doubleValue() / 100;
        double monthlyRate = rateDecimal / 12.0;
        return (amount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -termMonths));
    }

}
