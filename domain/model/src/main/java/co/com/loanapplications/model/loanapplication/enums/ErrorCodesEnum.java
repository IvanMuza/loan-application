package co.com.loanapplications.model.loanapplication.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCodesEnum {
    APPLICATION_REQUIRED_400("APPLICATION_REQUIRED_400", "Loan application must not be null"),
    AMOUNT_REQUIRED_400("AMOUNT_REQUIRED_400", "Amount is required"),
    TERM_REQUIRED_400("TERM_REQUIRED_400", "Term is required"),
    EMAIL_REQUIRED_400("EMAIL_REQUIRED_400", "Email is required"),
    DOCUMENT_ID_REQUIRED_400("DOCUMENT_ID_REQUIRED_400", "Document id is required"),
    LOAN_TYPE_REQUIRED_400("LOAN_TYPE_REQUIRED_400", "Loan type is required"),
    LOAN_TYPE_NOT_FOUND_404("LOAN_TYPE_NOT_FOUND_404", "Selected loan type was not found"),
    AMOUNT_OUT_OF_RANGE_400("AMOUNT_OUT_OF_RANGE_400", "Amount is out of allowed range for the selected loan type"),
    EMAIL_INVALID_400("EMAIL_INVALID_400", "Email is not valid"),
    USER_EMAIL_NOT_FOUND_404("USER_EMAIL_NOT_FOUND_404", "User not found with provided email"),
    USER_NOT_AUTHORIZED_403("USER_NOT_AUTHORIZED_403", "User not authorized to perform this operation"),
    USER_APPLICATION_NOT_MATCH_403("USER_APPLICATION_NOT_MATCH_403", "You can only create a loan application for your own account"),
    USER_NOT_AUTHORIZED_TO_CREATE_401("USER_NOT_AUTHORIZED_TO_CREATE_LOAN_APPLICATION_401", "User not authorized to create a loan application"),
    USER_NOT_AUTHENTICATED_401("USER_NOT_AUTHENTICATED_401", "User must be authenticated");

    private final String code;
    private final String defaultMessage;
}
