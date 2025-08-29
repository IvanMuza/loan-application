package co.com.loanapplications.model.loanapplication;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private Long id;
    private Double amount;
    private Integer term;
    private String email;
    private String documentId;

    private ApplicationStatus status;
    private LoanType loanType;

    private LocalDateTime createdAt;
}
