package co.com.loanapplications.model.loanapplication.gateways;

import co.com.loanapplications.model.loanapplication.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<LoanType> findByName(String name);
}
