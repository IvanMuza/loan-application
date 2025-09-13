package co.com.loanapplications.model.loanapplication.gateways;

import co.com.loanapplications.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);

    Flux<LoanApplication> findByStatusNames(List<String> statusNames, int offset, int limit);

    Mono<Long> countByStatusNames(List<String> statusNames);

    Mono<LoanApplication> findById(Long id);
}
