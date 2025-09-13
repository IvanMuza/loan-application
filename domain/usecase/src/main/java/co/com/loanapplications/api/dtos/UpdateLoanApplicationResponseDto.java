package co.com.loanapplications.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLoanApplicationResponseDto {
    private Long applicationId;
    private String userEmail;
    private Double loanAmount;
    private String termMonths;
    private String loanTypeName;
    private String newStatus;
}
