package co.com.loanapplications.r2dbc.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan_application")
public class LoanApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column("id")
    private Long id;

    @Column("amount")
    private Double amount;

    @Column("term_months")
    private Integer termMonths;

    @Column("email")
    private String email;

    @Column("status_id")
    private Long statusId;

    @Column("loan_type_id")
    private Long loanTypeId;
}
