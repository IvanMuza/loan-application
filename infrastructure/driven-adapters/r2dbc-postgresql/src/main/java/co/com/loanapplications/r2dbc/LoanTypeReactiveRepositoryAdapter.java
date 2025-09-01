package co.com.loanapplications.r2dbc;

import co.com.loanapplications.model.loanapplication.LoanType;
import co.com.loanapplications.model.loanapplication.gateways.LoanTypeRepository;
import co.com.loanapplications.r2dbc.entity.LoanTypeEntity;
import co.com.loanapplications.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public class LoanTypeReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<LoanType, LoanTypeEntity, Long, LoanTypeReactiveRepository>
        implements LoanTypeRepository {
    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class));
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return this.repository.existsById(id);
    }
}
