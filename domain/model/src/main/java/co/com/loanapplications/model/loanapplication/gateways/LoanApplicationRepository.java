package co.com.loanapplications.model.loanapplication.gateways;

import co.com.loanapplications.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<Boolean> findById(Long id);
    Flux<LoanApplication> findAll();
}
