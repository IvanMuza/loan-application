package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.api.dtos.UpdateLoanApplicationResponseDto;
import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.events.LoanApplicationStatusEvent;
import co.com.loanapplications.model.loanapplication.enums.PredefinedStatusesEnum;
import co.com.loanapplications.model.loanapplication.exceptions.ApplicationStatusNotAcceptedException;
import co.com.loanapplications.model.loanapplication.exceptions.LoanApplicationNotFoundException;
import co.com.loanapplications.model.loanapplication.exceptions.LoanTypeNotFoundException;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationStatusEventRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateLoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final ApplicationStatusRepository statusRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanApplicationStatusEventRepository loanApplicationStatusEventRepository;

    public Mono<UpdateLoanApplicationResponseDto> updateStatus(Long applicationId, String newStatus) {
        if (applicationId == null) {
            return Mono.error(new LoanApplicationNotFoundException());
        }

        if (!newStatus.equalsIgnoreCase(PredefinedStatusesEnum.APPROVED.getName()) &&
                !newStatus.equalsIgnoreCase(PredefinedStatusesEnum.REJECTED.getName())) {
            return Mono.error(new ApplicationStatusNotAcceptedException());
        }

        return loanApplicationRepository.findById(applicationId)
                .switchIfEmpty(Mono.error(new LoanApplicationNotFoundException()))
                .flatMap(app -> statusRepository.findByName(newStatus)
                        .switchIfEmpty(Mono.fromSupplier(() ->
                                ApplicationStatus.builder()
                                        .name(newStatus)
                                        .build()
                        ))
                        .flatMap(status -> {
                            LoanApplication updated = app.toBuilder()
                                    .statusId(status.getId())
                                    .build();

                            return loanApplicationRepository.save(updated)
                                    .flatMap(saved ->
                                            loanTypeRepository.findById(saved.getLoanTypeId())
                                                    .switchIfEmpty(Mono.error(new LoanTypeNotFoundException()))
                                                    .flatMap(loanType -> {
                                                        LoanApplicationStatusEvent event = LoanApplicationStatusEvent.builder()
                                                                .applicationId(saved.getId())
                                                                .userEmail(saved.getEmail())
                                                                .loanAmount(saved.getAmount())
                                                                .termMonths(saved.getTermMonths())
                                                                .loanTypeName(loanType.getName())
                                                                .newStatus(newStatus)
                                                                .build();

                                                        return loanApplicationStatusEventRepository.publish(event)
                                                                .thenReturn(new UpdateLoanApplicationResponseDto(
                                                                        saved.getId(),
                                                                        saved.getEmail(),
                                                                        saved.getAmount(),
                                                                        String.valueOf(saved.getTermMonths()),
                                                                        loanType.getName(),
                                                                        newStatus
                                                                ));
                                                    })
                                    );
                        })
                );
    }
}
