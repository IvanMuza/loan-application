package co.com.loanapplications.r2dbc;

import co.com.loanapplications.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<LoanTypeEntity, Long>,
        ReactiveQueryByExampleExecutor<LoanTypeEntity> {
    Mono<Boolean> existsById(Long id);
}
