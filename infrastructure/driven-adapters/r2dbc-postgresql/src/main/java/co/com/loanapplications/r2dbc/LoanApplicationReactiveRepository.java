package co.com.loanapplications.r2dbc;

import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, UUID>,
        ReactiveQueryByExampleExecutor<LoanApplicationEntity> {
    Mono<LoanApplication> findById(Long id);
}
