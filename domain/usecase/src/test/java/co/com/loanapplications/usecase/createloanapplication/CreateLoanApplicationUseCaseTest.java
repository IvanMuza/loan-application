package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.LoanType;
import co.com.loanapplications.model.loanapplication.exceptions.NotFoundException;
import co.com.loanapplications.model.loanapplication.exceptions.ValidationException;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.IdentityRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CreateLoanApplicationUseCaseTest {
    private LoanTypeRepository loanTypeRepository;
    private IdentityRepository identityRepository;
    private ApplicationStatusRepository statusRepository;
    private LoanApplicationRepository loanApplicationRepository;
    private CreateLoanApplicationUseCase createLoanApplicationUseCase;

    @BeforeEach
    void setUp() {
        loanTypeRepository = Mockito.mock(LoanTypeRepository.class);
        identityRepository = Mockito.mock(IdentityRepository.class);
        statusRepository = Mockito.mock(ApplicationStatusRepository.class);
        loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);
        createLoanApplicationUseCase = new CreateLoanApplicationUseCase(
                loanApplicationRepository,
                loanTypeRepository,
                statusRepository,
                identityRepository
        );
    }

    private LoanApplication buildValidLoanApplication() {
        return LoanApplication.builder()
                .amount(5_000_000D)
                .termMonths(12)
                .email("ivanmuza@test.com")
                .build();
    }

    private LoanType buildValidLoanType() {
        return LoanType.builder()
                .id(1L)
                .name("PERSONAL")
                .minAmount(1_000_000D)
                .maxAmount(10_000_000D)
                .build();
    }

    @Test
    void shouldCreateLoanApplicationWhenValid() {
        LoanApplication loanApp = buildValidLoanApplication();
        LoanType loanType = buildValidLoanType();

        when(loanTypeRepository.findByName("PERSONAL")).thenReturn(Mono.just(loanType));
        when(identityRepository.emailExists(loanApp.getEmail())).thenReturn(Mono.just(true));
        when(statusRepository.findById(anyLong())).thenReturn(Mono.just(
                ApplicationStatus.builder().id(100L).name("PENDING_REVIEW").build()
        ));
        when(loanApplicationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "PERSONAL"))
                .expectNextMatches(saved -> saved.getLoanTypeId().equals(loanType.getId()))
                .verifyComplete();

        verify(loanApplicationRepository).save(any());
    }

    @Test
    void shouldFailWhenLoanApplicationIsNull() {
        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(null, "PERSONAL"))
                .expectError(ValidationException.class)
                .verify();

        verifyNoInteractions(loanTypeRepository, identityRepository, loanApplicationRepository);
    }

    @Test
    void shouldFailWhenAmountIsMissing() {
        LoanApplication loanApp = buildValidLoanApplication();
        loanApp.setAmount(null);

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "PERSONAL"))
                .expectError(ValidationException.class)
                .verify();

        verifyNoInteractions(loanTypeRepository);
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        LoanApplication loanApp = buildValidLoanApplication();
        loanApp.setEmail("invalid-email");

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "PERSONAL"))
                .expectError(ValidationException.class)
                .verify();

        verifyNoInteractions(loanTypeRepository);
    }

    @Test
    void shouldFailWhenLoanTypeNotFound() {
        LoanApplication loanApp = buildValidLoanApplication();

        when(loanTypeRepository.findByName("UNKNOWN")).thenReturn(Mono.empty());

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "UNKNOWN"))
                .expectError(NotFoundException.class)
                .verify();

        verify(loanTypeRepository).findByName("UNKNOWN");
    }

    @Test
    void shouldFailWhenUserEmailDoesNotExist() {
        LoanApplication loanApp = buildValidLoanApplication();
        LoanType loanType = buildValidLoanType();

        when(loanTypeRepository.findByName("PERSONAL")).thenReturn(Mono.just(loanType));
        when(identityRepository.emailExists(loanApp.getEmail())).thenReturn(Mono.just(false));

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "PERSONAL"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenAmountOutOfRange() {
        LoanApplication loanApp = buildValidLoanApplication();
        loanApp.setAmount(50_000_000D);
        LoanType loanType = buildValidLoanType();

        when(loanTypeRepository.findByName("PERSONAL")).thenReturn(Mono.just(loanType));
        when(identityRepository.emailExists(loanApp.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "PERSONAL"))
                .expectError(ValidationException.class)
                .verify();
    }
}
