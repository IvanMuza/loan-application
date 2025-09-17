package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.exceptions.ApplicationStatusNotFoundException;
import co.com.loanapplications.model.loanapplication.exceptions.LoanApplicationNotValidException;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApprovedEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PublishLoanApprovedEventUseCaseTest {
    private LoanApprovedEventPublisher loanApprovedEventPublisher;
    private ApplicationStatusRepository applicationStatusRepository;
    private PublishLoanApprovedEventUseCase publishLoanApprovedEventUseCase;

    @BeforeEach
    void setUp() {
        loanApprovedEventPublisher = Mockito.mock(LoanApprovedEventPublisher.class);
        applicationStatusRepository = Mockito.mock(ApplicationStatusRepository.class);

        publishLoanApprovedEventUseCase = new PublishLoanApprovedEventUseCase(
                loanApprovedEventPublisher,
                applicationStatusRepository
        );
    }

    private LoanApplication buildLoanApplication(Long statusId) {
        return LoanApplication.builder()
                .id(1L)
                .email("test@email.com")
                .amount(3000D)
                .statusId(statusId)
                .build();
    }

    private ApplicationStatus buildApprovedStatus() {
        return ApplicationStatus.builder()
                .id(5L)
                .name("APPROVED")
                .description("Approved status")
                .build();
    }

    @Test
    void shouldPublishEventWhenLoanIsApproved() {
        LoanApplication loanApp = buildLoanApplication(5L);
        ApplicationStatus approvedStatus = buildApprovedStatus();

        when(applicationStatusRepository.findByName("APPROVED")).thenReturn(Mono.just(approvedStatus));
        when(loanApprovedEventPublisher.publish(any())).thenReturn(Mono.empty());

        StepVerifier.create(publishLoanApprovedEventUseCase.execute(loanApp))
                .verifyComplete();

        verify(applicationStatusRepository).findByName("APPROVED");
        verify(loanApprovedEventPublisher).publish(any());
    }

    @Test
    void shouldNotPublishEventWhenStatusDoesNotMatch() {
        LoanApplication loanApp = buildLoanApplication(99L);
        ApplicationStatus approvedStatus = buildApprovedStatus();

        when(applicationStatusRepository.findByName("APPROVED")).thenReturn(Mono.just(approvedStatus));

        StepVerifier.create(publishLoanApprovedEventUseCase.execute(loanApp))
                .verifyComplete(); // No error, pero tampoco publica

        verify(applicationStatusRepository).findByName("APPROVED");
        verifyNoInteractions(loanApprovedEventPublisher);
    }

    @Test
    void shouldFailWhenLoanApplicationIsNull() {
        StepVerifier.create(publishLoanApprovedEventUseCase.execute(null))
                .expectError(LoanApplicationNotValidException.class)
                .verify();

        verifyNoInteractions(applicationStatusRepository, loanApprovedEventPublisher);
    }

    @Test
    void shouldFailWhenLoanApplicationIdIsNull() {
        LoanApplication loanApp = LoanApplication.builder()
                .id(null)
                .statusId(5L)
                .build();

        StepVerifier.create(publishLoanApprovedEventUseCase.execute(loanApp))
                .expectError(LoanApplicationNotValidException.class)
                .verify();

        verifyNoInteractions(applicationStatusRepository, loanApprovedEventPublisher);
    }

    @Test
    void shouldFailWhenApprovedStatusNotFound() {
        LoanApplication loanApp = buildLoanApplication(5L);

        when(applicationStatusRepository.findByName("APPROVED")).thenReturn(Mono.empty());

        StepVerifier.create(publishLoanApprovedEventUseCase.execute(loanApp))
                .expectError(ApplicationStatusNotFoundException.class)
                .verify();

        verify(applicationStatusRepository).findByName("APPROVED");
        verifyNoInteractions(loanApprovedEventPublisher);
    }
}
