package co.com.loanapplications.api.config;

import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.IdentityRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanTypeRepository;
import co.com.loanapplications.usecase.createloanapplication.CreateLoanApplicationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanApplicationCasesConfig {
    @Bean
    public CreateLoanApplicationUseCase createLoanApplicationUseCase(
            LoanApplicationRepository loanApplicationRepository,
            LoanTypeRepository loanTypeRepository,
            ApplicationStatusRepository applicationStatusRepository,
            IdentityRepository identityRepository) {
        return new CreateLoanApplicationUseCase(
                loanApplicationRepository, loanTypeRepository, applicationStatusRepository, identityRepository);
    }
}
