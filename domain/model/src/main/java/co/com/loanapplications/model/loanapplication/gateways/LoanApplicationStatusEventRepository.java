package co.com.loanapplications.model.loanapplication.gateways;

import co.com.loanapplications.model.loanapplication.events.CapacityRequestEvent;
import co.com.loanapplications.model.loanapplication.events.LoanApplicationStatusEvent;
import reactor.core.publisher.Mono;

public interface LoanApplicationStatusEventRepository {
    Mono<Void> publish(LoanApplicationStatusEvent event);
    Mono<Void> validate(CapacityRequestEvent event);
}
