package co.com.loanapplications.model.loanapplication.gateways;

import co.com.loanapplications.model.loanapplication.events.LoanApprovedEvent;
import reactor.core.publisher.Mono;

public interface LoanApprovedEventPublisher {
    Mono<Void> publish(LoanApprovedEvent event);
}
