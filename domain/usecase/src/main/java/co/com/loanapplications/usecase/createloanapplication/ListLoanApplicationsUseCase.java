package co.com.loanapplications.usecase.createloanapplication;

import co.com.loanapplications.api.dtos.ListApplicationResponseDto;
import co.com.loanapplications.api.dtos.LoanApplicationListItemDto;
import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.LoanType;
import co.com.loanapplications.model.loanapplication.identity.UserDto;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.IdentityRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ListLoanApplicationsUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ApplicationStatusRepository statusRepository;
    private final IdentityRepository identityRepository;
    private static final List<String> DEFAULT_STATUSES = List.of("PENDING_REVIEW", "REJECTED", "MANUAL_REVIEW");

    public Mono<ListApplicationResponseDto<LoanApplicationListItemDto>> list(int page, int size, String statuses) {
        int offset = page * size;

        List<String> statusFilter;
        if (statuses != null && !statuses.isBlank()) {
            statusFilter = Arrays.stream(statuses.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        } else {
            statusFilter = DEFAULT_STATUSES;
        }

        Mono<Long> totalMono = loanApplicationRepository.countByStatusNames(statusFilter);
        Flux<LoanApplication> itemsFlux = loanApplicationRepository.findByStatusNames(statusFilter, offset, size);

        return itemsFlux
                .collectList()
                .flatMapMany(list -> {
                    Set<String> emails = list.stream().map(LoanApplication::getEmail).collect(Collectors.toSet());
                    Map<String, Mono<UserDto>> userMonoMap = new ConcurrentHashMap<>();
                    emails.forEach(email -> userMonoMap.put(
                            email, identityRepository.findByEmail(email).cache()));

                    return Flux.fromIterable(list)
                            .flatMap(la -> {
                                Mono<UserDto> userMono = userMonoMap.getOrDefault(la.getEmail(), Mono.empty());
                                Mono<LoanType> loanTypeMono = loanTypeRepository.findById(la.getLoanTypeId());
                                Mono<ApplicationStatus> statusMono = statusRepository.findById(la.getStatusId());


                                return Mono.zip(
                                                userMono.defaultIfEmpty(new UserDto()),
                                                loanTypeMono.defaultIfEmpty(new LoanType()),
                                                statusMono.defaultIfEmpty(new ApplicationStatus()))
                                        .map(tuple -> {
                                            UserDto userDto = tuple.getT1();
                                            LoanType lt = tuple.getT2();
                                            ApplicationStatus st = tuple.getT3();

                                            BigDecimal interest = (lt.getInterestRate() != null) ? lt.getInterestRate() : BigDecimal.ZERO;
                                            double monthlyAmount = la.getAmount() * (interest.doubleValue()) / Math.max(1, la.getTermMonths());

                                            return LoanApplicationListItemDto.builder()
                                                    .id(la.getId())
                                                    .amount(la.getAmount())
                                                    .termMonths(la.getTermMonths())
                                                    .email(la.getEmail())
                                                    .fullName(userDto.getFirstName() + " " + userDto.getLastName())
                                                    .loanType(lt.getName())
                                                    .interestRate(lt.getInterestRate())
                                                    .status(st.getName())
                                                    .baseSalary(userDto.getBaseSalary())
                                                    .monthlyAmount(monthlyAmount)
                                                    .build();
                                        });
                            });
                })
                .collectList()
                .zipWith(totalMono)
                .map(tuple -> ListApplicationResponseDto.<LoanApplicationListItemDto>builder()
                        .items(tuple.getT1())
                        .total(tuple.getT2())
                        .page(page)
                        .size(size)
                        .build());
    }
}
