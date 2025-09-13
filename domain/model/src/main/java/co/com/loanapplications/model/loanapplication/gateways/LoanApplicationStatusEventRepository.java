package co.com.loanapplications.model.loanapplication.gateways;

import co.com.loanapplications.model.loanapplication.LoanApplicationStatusEvent;
import reactor.core.publisher.Mono;

public interface LoanApplicationStatusEventRepository {
    Mono<Void> publish(LoanApplicationStatusEvent event);
}
