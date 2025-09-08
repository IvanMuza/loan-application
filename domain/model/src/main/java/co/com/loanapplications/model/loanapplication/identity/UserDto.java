package co.com.loanapplications.model.loanapplication.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private Double baseSalary;
}
