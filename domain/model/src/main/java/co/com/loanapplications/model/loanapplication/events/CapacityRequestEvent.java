package co.com.loanapplications.model.loanapplication.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityRequestEvent {
    private Long applicationId;
    private String applicantEmail;
    private Double applicantMonthlyIncome;
    private Double currentDebt;
    private List<LoanSummary> activeLoans;
    private NewLoan newLoan;
}
