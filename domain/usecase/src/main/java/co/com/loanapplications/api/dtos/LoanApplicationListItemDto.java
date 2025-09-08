package co.com.loanapplications.api.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanApplicationListItemDto {
    private Long id;
    private Double amount;
    private Integer termMonths;
    private String email;
    private String fullName;
    private String loanType;
    private BigDecimal interestRate;
    private String status;
    private Double baseSalary;
    private Double monthlyAmount;
}
