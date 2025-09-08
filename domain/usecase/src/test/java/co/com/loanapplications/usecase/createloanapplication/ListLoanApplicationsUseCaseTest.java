package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.api.dtos.LoanApplicationListItemDto;
import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.LoanType;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.IdentityRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanTypeRepository;
import co.com.loanapplications.model.loanapplication.identity.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListLoanApplicationsUseCaseTest {
    private LoanApplicationRepository loanApplicationRepository;
    private LoanTypeRepository loanTypeRepository;
    private ApplicationStatusRepository statusRepository;
    private IdentityRepository identityRepository;
    private ListLoanApplicationsUseCase listLoanApplicationsUseCase;

    @BeforeEach
    void setUp() {
        loanApplicationRepository = Mockito.mock(LoanApplicationRepository.class);
        loanTypeRepository = Mockito.mock(LoanTypeRepository.class);
        statusRepository = Mockito.mock(ApplicationStatusRepository.class);
        identityRepository = Mockito.mock(IdentityRepository.class);

        listLoanApplicationsUseCase = new ListLoanApplicationsUseCase(
                loanApplicationRepository,
                loanTypeRepository,
                statusRepository,
                identityRepository
        );
    }

    private LoanApplication buildLoanApplication() {
        return LoanApplication.builder()
                .id(1L)
                .amount(5_000_000D)
                .termMonths(12)
                .email("ivanmuza@test.com")
                .loanTypeId(10L)
                .statusId(20L)
                .build();
    }

    private UserDto buildUserDto() {
        return UserDto.builder()
                .firstName("Ivan")
                .lastName("Muza")
                .baseSalary(6_000_000D)
                .build();
    }

    private LoanType buildLoanType() {
        return LoanType.builder()
                .id(10L)
                .name("Basic")
                .interestRate(BigDecimal.valueOf(0.02)) // 2%
                .build();
    }

    private ApplicationStatus buildStatus() {
        return ApplicationStatus.builder()
                .id(20L)
                .name("PENDING_REVIEW")
                .build();
    }

    @Test
    void shouldListLoanApplicationsWhenValid() {
        LoanApplication loanApp = buildLoanApplication();
        UserDto user = buildUserDto();
        LoanType loanType = buildLoanType();
        ApplicationStatus status = buildStatus();

        when(loanApplicationRepository.countByStatusNames(anyList())).thenReturn(Mono.just(1L));
        when(loanApplicationRepository.findByStatusNames(anyList(), eq(0), eq(10))).thenReturn(Flux.just(loanApp));
        when(identityRepository.findByEmail(loanApp.getEmail())).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(loanApp.getLoanTypeId())).thenReturn(Mono.just(loanType));
        when(statusRepository.findById(loanApp.getStatusId())).thenReturn(Mono.just(status));

        StepVerifier.create(listLoanApplicationsUseCase.list(0, 10, null))
                .assertNext(response -> {
                    assertEquals(1, response.getItems().size());
                    assertEquals(1L, response.getTotal());
                    LoanApplicationListItemDto dto = response.getItems().get(0);

                    assertEquals("Ivan Muza", dto.getFullName());
                    assertEquals("Basic", dto.getLoanType());
                    assertEquals("PENDING_REVIEW", dto.getStatus());
                    assertEquals(6_000_000D, dto.getBaseSalary());
                })
                .verifyComplete();

        verify(loanApplicationRepository).findByStatusNames(anyList(), eq(0), eq(10));
        verify(identityRepository).findByEmail(loanApp.getEmail());
        verify(loanTypeRepository).findById(loanApp.getLoanTypeId());
        verify(statusRepository).findById(loanApp.getStatusId());
    }

    @Test
    void shouldReturnEmptyListWhenNoApplicationsFound() {
        when(loanApplicationRepository.countByStatusNames(anyList())).thenReturn(Mono.just(0L));
        when(loanApplicationRepository.findByStatusNames(anyList(), anyInt(), anyInt())).thenReturn(Flux.empty());

        StepVerifier.create(listLoanApplicationsUseCase.list(0, 10, null))
                .assertNext(response -> {
                    assertTrue(response.getItems().isEmpty());
                    assertEquals(0L, response.getTotal());
                })
                .verifyComplete();
    }

}
