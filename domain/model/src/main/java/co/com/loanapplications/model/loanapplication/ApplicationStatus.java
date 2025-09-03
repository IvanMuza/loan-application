package co.com.loanapplications.model.loanapplication;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApplicationStatus {
    private Long id;
    private String name;
    private String description;
}
