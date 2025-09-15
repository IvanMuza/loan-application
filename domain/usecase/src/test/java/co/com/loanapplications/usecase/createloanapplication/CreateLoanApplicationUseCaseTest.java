package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.LoanType;
import co.com.loanapplications.model.loanapplication.enums.PredefinedStatusesEnum;
import co.com.loanapplications.model.loanapplication.events.CapacityRequestEvent;
import co.com.loanapplications.model.loanapplication.exceptions.*;
import co.com.loanapplications.model.loanapplication.gateways.*;
import co.com.loanapplications.model.loanapplication.identity.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateLoanApplicationUseCaseTest {
    private LoanTypeRepository loanTypeRepository;
    private IdentityRepository identityRepository;
    private ApplicationStatusRepository statusRepository;
    private LoanApplicationRepository loanApplicationRepository;
    private CreateLoanApplicationUseCase createLoanApplicationUseCase;
    private LoanApplicationStatusEventRepository loanApplicationStatusEventRepository;

    @BeforeEach
    void setUp() {
        loanTypeRepository = Mockito.mock(LoanTypeRepository.class);
        identityRepository = Mockito.mock(IdentityRepository.class);
        statusRepository = Mockito.mock(ApplicationStatusRepository.class);
        loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);
        loanApplicationStatusEventRepository = Mockito.mock(LoanApplicationStatusEventRepository.class);
        createLoanApplicationUseCase = new CreateLoanApplicationUseCase(
                loanApplicationRepository,
                loanTypeRepository,
                statusRepository,
                identityRepository,
                loanApplicationStatusEventRepository
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
                .name("Basic")
                .minAmount(1_000_000D)
                .maxAmount(10_000_000D)
                .automaticValidation(false)
                .build();
    }

    private UserDto buildValidUser() {
        return UserDto.builder()
                .email("ivanmuza@test.com")
                .baseSalary(2_000_000D)
                .build();
    }

    @Test
    void shouldCreateLoanApplicationWhenValid() {
        LoanApplication loanApp = buildValidLoanApplication();
        LoanType loanType = buildValidLoanType();

        when(loanTypeRepository.findByName("Basic")).thenReturn(Mono.just(loanType));
        when(identityRepository.findByEmail(loanApp.getEmail())).thenReturn(Mono.just(buildValidUser()));
        when(statusRepository.findByName("PENDING_REVIEW")).thenReturn(Mono.just(
                ApplicationStatus.builder().id(2L).name("PENDING_REVIEW").build()
        ));
        when(loanApplicationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectNextMatches(saved -> saved.getLoanTypeId().equals(loanType.getId()))
                .verifyComplete();

        verify(loanApplicationRepository).save(any());
    }

    @Test
    void shouldFailWhenLoanApplicationIsNull() {
        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(null, "PERSONAL"))
                .expectError(LoanApplicationNotValidException.class)
                .verify();

        verifyNoInteractions(loanTypeRepository, identityRepository, loanApplicationRepository);
    }

    @Test
    void shouldFailWhenAmountIsMissing() {
        LoanApplication loanApp = buildValidLoanApplication();
        loanApp.setAmount(null);

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectError(AmountNotValidException.class)
                .verify();

        verifyNoInteractions(loanTypeRepository);
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        LoanApplication loanApp = buildValidLoanApplication();
        loanApp.setEmail("invalid-email");

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectError(EmailNotValidException.class)
                .verify();

        verifyNoInteractions(loanTypeRepository);
    }

    @Test
    void shouldFailWhenLoanTypeNotFound() {
        LoanApplication loanApp = buildValidLoanApplication();

        when(loanTypeRepository.findByName("UNKNOWN")).thenReturn(Mono.empty());

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "UNKNOWN"))
                .expectError(LoanTypeNotFoundException.class)
                .verify();

        verify(loanTypeRepository).findByName("UNKNOWN");
    }

    @Test
    void shouldFailWhenUserEmailDoesNotExist() {
        LoanApplication loanApp = buildValidLoanApplication();
        LoanType loanType = buildValidLoanType();

        when(loanTypeRepository.findByName("Basic")).thenReturn(Mono.just(loanType));
        when(identityRepository.findByEmail(loanApp.getEmail())).thenReturn(Mono.empty());

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectError(UserEmailNotFoundException.class)
                .verify();
    }

    @Test
    void shouldFailWhenAmountOutOfRange() {
        LoanApplication loanApp = buildValidLoanApplication();
        loanApp.setAmount(50_000_000D);
        LoanType loanType = buildValidLoanType();

        when(loanTypeRepository.findByName("Basic")).thenReturn(Mono.just(loanType));
        when(identityRepository.findByEmail(loanApp.getEmail())).thenReturn(Mono.just(buildValidUser()));

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectError(AmountOutOfRangeException.class)
                .verify();
    }

    @Test
    void shouldFailWhenTermMonthsIsMissing() {
        LoanApplication loanApp = buildValidLoanApplication();
        loanApp.setTermMonths(null);

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectError(TermMonthsNotValidException.class)
                .verify();

        verifyNoInteractions(loanTypeRepository, identityRepository, loanApplicationRepository);
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        LoanApplication loanApp = buildValidLoanApplication();
        loanApp.setEmail(null);

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectError(EmailNotValidException.class)
                .verify();

        verifyNoInteractions(loanTypeRepository, identityRepository, loanApplicationRepository);
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        LoanApplication loanApp = buildValidLoanApplication();
        loanApp.setEmail(" ");

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectError(EmailNotValidException.class)
                .verify();

        verifyNoInteractions(loanTypeRepository, identityRepository, loanApplicationRepository);
    }

    @Test
    void shouldUseDefaultStatusWhenStatusNotFound() {
        LoanType loanType = buildValidLoanType();
        LoanApplication loanApp = buildValidLoanApplication();

        when(loanTypeRepository.findByName("Basic"))
                .thenReturn(Mono.just(loanType));
        when(identityRepository.findByEmail(loanApp.getEmail()))
                .thenReturn(Mono.just(buildValidUser()));
        when(statusRepository.findByName(PredefinedStatusesEnum.PENDING_REVIEW.getName()))
                .thenReturn(Mono.empty());
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectNextMatches(saved -> saved.getLoanTypeId().equals(loanType.getId())
                )
                .verifyComplete();

        verify(statusRepository).findByName(PredefinedStatusesEnum.PENDING_REVIEW.getName());
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void shouldFailWhenApprovedStatusNotFoundInCalculateDebtAndPublish() {
        LoanApplication loanApp = buildValidLoanApplication().toBuilder()
                .id(100L)
                .build();

        LoanType loanType = buildValidLoanType().toBuilder()
                .automaticValidation(true)
                .interestRate(BigDecimal.valueOf(12))
                .build();

        UserDto user = UserDto.builder()
                .email(loanApp.getEmail())
                .baseSalary(5_000_000D)
                .build();

        when(loanTypeRepository.findByName("Basic")).thenReturn(Mono.just(loanType));
        when(identityRepository.findByEmail(loanApp.getEmail())).thenReturn(Mono.just(user));
        when(statusRepository.findByName(PredefinedStatusesEnum.PENDING_REVIEW.getName()))
                .thenReturn(Mono.just(ApplicationStatus.builder().id(2L).name("PENDING_REVIEW").build()));
        when(loanApplicationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Para calculateDebtAndPublish: devolvemos vacío en el status APPROVED
        when(statusRepository.findByName(PredefinedStatusesEnum.APPROVED.getName())).thenReturn(Mono.empty());

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectError(ApplicationStatusNotFoundException.class)
                .verify();
    }

    @Test
    void shouldPublishCapacityRequestEventWhenAutomaticValidationEnabled() {
        LoanApplication loanApp = buildValidLoanApplication().toBuilder()
                .id(101L)
                .amount(2_000_000D)
                .termMonths(24)
                .build();

        LoanType loanType = buildValidLoanType().toBuilder()
                .automaticValidation(true)
                .interestRate(BigDecimal.valueOf(12))
                .build();

        UserDto user = UserDto.builder()
                .email(loanApp.getEmail())
                .baseSalary(4_000_000D)
                .build();

        ApplicationStatus pendingStatus = ApplicationStatus.builder().id(2L).name("PENDING_REVIEW").build();
        ApplicationStatus approvedStatus = ApplicationStatus.builder().id(3L).name("APPROVED").build();

        when(loanTypeRepository.findByName("Basic")).thenReturn(Mono.just(loanType));
        when(identityRepository.findByEmail(loanApp.getEmail())).thenReturn(Mono.just(user));
        when(statusRepository.findByName(PredefinedStatusesEnum.PENDING_REVIEW.getName())).thenReturn(Mono.just(pendingStatus));
        when(loanApplicationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(statusRepository.findByName(PredefinedStatusesEnum.APPROVED.getName())).thenReturn(Mono.just(approvedStatus));
        when(loanApplicationRepository.findByEmailAndStatusId(user.getEmail(), approvedStatus.getId()))
                .thenReturn(Flux.just(LoanApplication.builder()
                        .amount(1_000_000D)
                        .termMonths(12)
                        .loanTypeId(loanType.getId())
                        .build()));
        when(loanApplicationStatusEventRepository.validate(any(CapacityRequestEvent.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(createLoanApplicationUseCase.createLoanApplication(loanApp, "Basic"))
                .expectNextMatches(saved -> saved.getId().equals(101L))
                .verifyComplete();

        verify(loanApplicationStatusEventRepository).validate(any(CapacityRequestEvent.class));
    }


}
