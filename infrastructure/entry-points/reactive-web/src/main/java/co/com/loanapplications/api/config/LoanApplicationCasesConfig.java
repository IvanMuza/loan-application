package co.com.loanapplications.api.config;

import co.com.loanapplications.model.loanapplication.gateways.*;
import co.com.loanapplications.usecase.createloanapplication.CreateLoanApplicationUseCase;
import co.com.loanapplications.usecase.createloanapplication.ListLoanApplicationsUseCase;
import co.com.loanapplications.usecase.createloanapplication.UpdateLoanApplicationUseCase;
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
                loanApplicationRepository,
                loanTypeRepository,
                applicationStatusRepository,
                identityRepository);
    }

    @Bean
    public ListLoanApplicationsUseCase listLoanApplicationsUseCase(
            LoanApplicationRepository loanApplicationRepository,
            LoanTypeRepository loanTypeRepository,
            ApplicationStatusRepository applicationStatusRepository,
            IdentityRepository identityRepository) {
        return new ListLoanApplicationsUseCase(
                loanApplicationRepository,
                loanTypeRepository,
                applicationStatusRepository,
                identityRepository);
    }

    @Bean
    public UpdateLoanApplicationUseCase updateLoanApplicationUseCase(
            LoanApplicationRepository loanApplicationRepository,
            ApplicationStatusRepository statusRepository,
            LoanTypeRepository loanTypeRepository,
            LoanApplicationStatusEventRepository loanApplicationStatusEventRepository) {
        return new UpdateLoanApplicationUseCase(
                loanApplicationRepository,
                statusRepository,
                loanTypeRepository,
                loanApplicationStatusEventRepository
                );
    }
}
