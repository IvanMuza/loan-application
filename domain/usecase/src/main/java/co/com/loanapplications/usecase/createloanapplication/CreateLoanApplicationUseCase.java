package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.enums.PredefinedStatusesEnum;
import co.com.loanapplications.model.loanapplication.exceptions.*;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.IdentityRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanTypeRepository;
import co.com.loanapplications.usecase.createloanapplication.helpers.EmailValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ApplicationStatusRepository statusRepository;
    private final IdentityRepository identityRepository;

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

                    return identityRepository.emailExists(updatedLoanApp.getEmail())
                            .flatMap(exists -> {
                                if (!exists) {
                                    return Mono.error(new UserEmailNotFoundException());
                                }
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
                                        .map(initialStatus -> updatedLoanApp.toBuilder()
                                                .statusId(initialStatus.getId())
                                                .build());
                            })
                            .flatMap(loanApplicationRepository::save);

                });
    }

}
