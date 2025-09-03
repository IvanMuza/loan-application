package co.com.loanapplications.r2dbc;

import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.r2dbc.entity.LoanApplicationEntity;
import co.com.loanapplications.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class LoanApplicationReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<LoanApplication, LoanApplicationEntity, UUID, LoanApplicationReactiveRepository>
implements LoanApplicationRepository {
    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
    }

}
