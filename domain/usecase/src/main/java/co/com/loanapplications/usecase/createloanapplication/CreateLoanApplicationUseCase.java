package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;
import co.com.loanapplications.model.loanapplication.enums.PredefinedStatusesEnum;
import co.com.loanapplications.model.loanapplication.exceptions.NotFoundException;
import co.com.loanapplications.model.loanapplication.exceptions.ValidationException;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanTypeRepository;
import co.com.loanapplications.usecase.createloanapplication.helpers.EmailValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateLoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ApplicationStatusRepository statusRepository;

    public Mono<LoanApplication> createLoanApplication(LoanApplication input, Long loanTypeId) {
        if (input == null) {
            return Mono.error(new ValidationException(ErrorCodesEnum.APPLICATION_REQUIRED.getCode(),
                    ErrorCodesEnum.APPLICATION_REQUIRED.getDefaultMessage()));
        }
        if (input.getAmount() == null) {
            return Mono.error(new ValidationException(ErrorCodesEnum.AMOUNT_REQUIRED.getCode(),
                    ErrorCodesEnum.AMOUNT_REQUIRED.getDefaultMessage()));
        }
        if (input.getTermMonths() == null) {
            return Mono.error(new ValidationException(ErrorCodesEnum.TERM_REQUIRED.getCode(),
                    ErrorCodesEnum.TERM_REQUIRED.getDefaultMessage()));
        }
        if (input.getEmail() == null || input.getEmail().isBlank()) {
            return Mono.error(new ValidationException(ErrorCodesEnum.EMAIL_REQUIRED.getCode(),
                    ErrorCodesEnum.EMAIL_REQUIRED.getDefaultMessage()));
        }
        if (!EmailValidator.isValid(input.getEmail())) {
            return Mono.error(new ValidationException(ErrorCodesEnum.EMAIL_INVALID.getCode(),
                    ErrorCodesEnum.EMAIL_INVALID.getDefaultMessage()));
        }
        if (loanTypeId == null) {
            return Mono.error(new ValidationException(ErrorCodesEnum.LOAN_TYPE_REQUIRED.getCode(),
                    ErrorCodesEnum.LOAN_TYPE_REQUIRED.getDefaultMessage()));
        }

        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        ErrorCodesEnum.LOAN_TYPE_NOT_FOUND.getCode(),
                        ErrorCodesEnum.LOAN_TYPE_NOT_FOUND.getDefaultMessage()
                )))
                .flatMap(loanType -> {
                    Double min = loanType.getMinAmount();
                    Double max = loanType.getMaxAmount();
                    Double amount = input.getAmount();

                    if (min != null && amount < min || max != null && amount > max) {
                        return Mono.error(new ValidationException(
                                ErrorCodesEnum.AMOUNT_OUT_OF_RANGE.getCode(),
                                ErrorCodesEnum.AMOUNT_OUT_OF_RANGE.getDefaultMessage()
                        ));
                    }
                    return statusRepository.findByName(PredefinedStatusesEnum.PENDING_REVIEW.getName())
                            .switchIfEmpty(Mono.fromSupplier(() ->
                                    ApplicationStatus.builder()
                                            .name(PredefinedStatusesEnum.PENDING_REVIEW.getName())
                                            .description(PredefinedStatusesEnum.PENDING_REVIEW.getDescription())
                                            .build()));
                })
                .map(initialStatus -> input.toBuilder()
                        .statusId(initialStatus)
                        .createdAt(LocalDateTime.now())
                        .build()
                )
                .flatMap(loanApplicationRepository::save);

    }

}
