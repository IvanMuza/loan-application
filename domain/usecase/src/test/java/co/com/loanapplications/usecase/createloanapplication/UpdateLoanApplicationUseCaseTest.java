package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.LoanType;
import co.com.loanapplications.model.loanapplication.exceptions.ApplicationStatusNotAcceptedException;
import co.com.loanapplications.model.loanapplication.exceptions.LoanApplicationNotFoundException;
import co.com.loanapplications.model.loanapplication.exceptions.LoanTypeNotFoundException;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationStatusEventRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateLoanApplicationUseCaseTest {
    private LoanApplicationRepository loanApplicationRepository;
    private ApplicationStatusRepository statusRepository;
    private LoanTypeRepository loanTypeRepository;
    private LoanApplicationStatusEventRepository loanApplicationStatusEventRepository;
    private UpdateLoanApplicationUseCase updateLoanApplicationUseCase;
    private PublishLoanApprovedEventUseCase publishLoanApprovedEventUseCase;

    @BeforeEach
    void setUp() {
        loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);
        statusRepository = Mockito.mock(ApplicationStatusRepository.class);
        loanTypeRepository = Mockito.mock(LoanTypeRepository.class);
        loanApplicationStatusEventRepository = Mockito.mock(LoanApplicationStatusEventRepository.class);
        publishLoanApprovedEventUseCase = Mockito.mock(PublishLoanApprovedEventUseCase.class);

        updateLoanApplicationUseCase = new UpdateLoanApplicationUseCase(
                loanApplicationRepository,
                statusRepository,
                loanTypeRepository,
                loanApplicationStatusEventRepository,
                publishLoanApprovedEventUseCase
        );
    }

    private LoanApplication buildLoanApplication() {
        return LoanApplication.builder()
                .id(1L)
                .amount(2000D)
                .termMonths(12)
                .email("ivantest@gmail.com")
                .statusId(99L)
                .loanTypeId(10L)
                .build();
    }

    private LoanType buildLoanType() {
        return LoanType.builder()
                .id(10L)
                .name("Basic")
                .minAmount(1000D)
                .maxAmount(5000D)
                .build();
    }

    private ApplicationStatus buildApprovedStatus() {
        return ApplicationStatus.builder()
                .id(5L)
                .name("APPROVED")
                .description("Status APPROVED")
                .build();
    }

    @Test
    void shouldUpdateStatusAndPublishEventWhenValidApproved() {
        LoanApplication loanApp = buildLoanApplication();
        LoanType loanType = buildLoanType();
        ApplicationStatus status = buildApprovedStatus();

        when(loanApplicationRepository.findById(1L)).thenReturn(Mono.just(loanApp));
        when(statusRepository.findByName("APPROVED")).thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(loanTypeRepository.findById(10L)).thenReturn(Mono.just(loanType));
        when(loanApplicationStatusEventRepository.publish(any())).thenReturn(Mono.empty());
        when(publishLoanApprovedEventUseCase.execute(any())).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanApplicationUseCase.updateStatus(1L, "APPROVED"))
                .expectNextMatches(saved ->
                        saved.getNewStatus().equals(status.getName()))
                .verifyComplete();

        verify(loanApplicationRepository).findById(1L);
        verify(statusRepository).findByName("APPROVED");
        verify(loanApplicationRepository).save(any());
        verify(loanTypeRepository).findById(10L);
        verify(loanApplicationStatusEventRepository).publish(any());
    }

    @Test
    void shouldFailWhenApplicationIdIsNull() {
        StepVerifier.create(updateLoanApplicationUseCase.updateStatus(null, "APPROVED"))
                .expectError(LoanApplicationNotFoundException.class)
                .verify();

        verifyNoInteractions(loanApplicationRepository, statusRepository, loanTypeRepository, loanApplicationStatusEventRepository);
    }

    @Test
    void shouldFailWhenStatusNotAccepted() {
        StepVerifier.create(updateLoanApplicationUseCase.updateStatus(1L, "PENDING_REVIEW"))
                .expectError(ApplicationStatusNotAcceptedException.class)
                .verify();

        verifyNoInteractions(loanApplicationRepository, statusRepository, loanTypeRepository, loanApplicationStatusEventRepository);
    }

    @Test
    void shouldFailWhenLoanApplicationNotFound() {
        when(loanApplicationRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanApplicationUseCase.updateStatus(99L, "APPROVED"))
                .expectError(LoanApplicationNotFoundException.class)
                .verify();

        verify(loanApplicationRepository).findById(99L);
        verifyNoInteractions(statusRepository, loanTypeRepository, loanApplicationStatusEventRepository);
    }

    @Test
    void shouldFailWhenLoanTypeNotFound() {
        LoanApplication loanApp = buildLoanApplication();
        ApplicationStatus status = buildApprovedStatus();

        when(loanApplicationRepository.findById(1L)).thenReturn(Mono.just(loanApp));
        when(statusRepository.findByName("APPROVED")).thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(loanTypeRepository.findById(10L)).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanApplicationUseCase.updateStatus(1L, "APPROVED"))
                .expectError(LoanTypeNotFoundException.class)
                .verify();

        verify(loanApplicationRepository).findById(1L);
        verify(statusRepository).findByName("APPROVED");
        verify(loanApplicationRepository).save(any());
        verify(loanTypeRepository).findById(10L);
        verifyNoInteractions(loanApplicationStatusEventRepository);
    }

    @Test
    void shouldUpdateStatusWhenStatusNotFoundInRepository() {
        LoanApplication loanApp = buildLoanApplication();
        LoanType loanType = buildLoanType();

        when(loanApplicationRepository.findById(1L)).thenReturn(Mono.just(loanApp));
        when(statusRepository.findByName("APPROVED")).thenReturn(Mono.empty());
        when(loanApplicationRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(loanTypeRepository.findById(10L)).thenReturn(Mono.just(loanType));
        when(loanApplicationStatusEventRepository.publish(any())).thenReturn(Mono.empty());
        when(publishLoanApprovedEventUseCase.execute(any())).thenReturn(Mono.empty());

        StepVerifier.create(updateLoanApplicationUseCase.updateStatus(1L, "APPROVED"))
                .expectNextMatches(response ->
                        response.getApplicationId().equals(1L)
                                && response.getNewStatus().equals("APPROVED")
                                && response.getLoanTypeName().equals("Basic"))
                .verifyComplete();

        verify(loanApplicationRepository).findById(1L);
        verify(statusRepository).findByName("APPROVED");
        verify(loanApplicationRepository).save(any());
        verify(loanTypeRepository).findById(10L);
        verify(loanApplicationStatusEventRepository).publish(any());
    }
}
