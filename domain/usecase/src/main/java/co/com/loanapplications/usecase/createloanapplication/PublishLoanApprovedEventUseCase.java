package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.enums.PredefinedStatusesEnum;
import co.com.loanapplications.model.loanapplication.events.LoanApprovedEvent;
import co.com.loanapplications.model.loanapplication.exceptions.ApplicationStatusNotFoundException;
import co.com.loanapplications.model.loanapplication.exceptions.LoanApplicationNotValidException;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApprovedEventPublisher;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PublishLoanApprovedEventUseCase {
    private final LoanApprovedEventPublisher loanApprovedEventPublisher;
    private final ApplicationStatusRepository applicationStatusRepository;

    public Mono<Void> execute(LoanApplication loanApplication) {
        if (loanApplication == null || loanApplication.getId() == null) {
            return Mono.error(new LoanApplicationNotValidException());
        }

        return applicationStatusRepository.findByName(PredefinedStatusesEnum.APPROVED.getName())
                .switchIfEmpty(Mono.error(new ApplicationStatusNotFoundException()))
                .flatMap(approvedStatus -> {
                    if (!approvedStatus.getId().equals(loanApplication.getStatusId())) {
                        return Mono.empty();
                    }

                    LoanApprovedEvent event = new LoanApprovedEvent();
                    event.setLoanId(loanApplication.getId());
                    event.setEmail(loanApplication.getEmail());
                    event.setAmount(loanApplication.getAmount());

                    return loanApprovedEventPublisher.publish(event);
                });
    }
}
