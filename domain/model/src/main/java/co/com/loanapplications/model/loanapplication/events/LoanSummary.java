package co.com.loanapplications.model.loanapplication.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanSummary {
    private Double principal;
    private BigDecimal annualInterestRate;
    private Integer termMonths;
}
