package co.com.loanapplications.model.loanapplication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanApplicationStatusEvent {
    private Long applicationId;
    private String userEmail;
    private Double loanAmount;
    private Integer termMonths;
    private String loanTypeName;
    private String newStatus;
}
