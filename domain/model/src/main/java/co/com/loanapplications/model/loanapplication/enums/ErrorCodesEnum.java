package co.com.loanapplications.model.loanapplication.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCodesEnum {
    APPLICATION_REQUIRED("APPLICATION_REQUIRED", "Loan application must not be null"),
    AMOUNT_REQUIRED("AMOUNT_REQUIRED", "Amount is required"),
    TERM_REQUIRED("TERM_REQUIRED", "Term is required"),
    EMAIL_REQUIRED("EMAIL_REQUIRED", "Email is required"),
    DOCUMENT_ID_REQUIRED("DOCUMENT_ID_REQUIRED", "Document id is required"),
    LOAN_TYPE_REQUIRED("LOAN_TYPE_REQUIRED", "Loan type is required"),
    LOAN_TYPE_NOT_FOUND("LOAN_TYPE_NOT_FOUND", "Selected loan type was not found"),
    AMOUNT_OUT_OF_RANGE("AMOUNT_OUT_OF_RANGE", "Amount is out of allowed range for the selected loan type"),
    EMAIL_INVALID("EMAIL_INVALID", "Email is not valid");

    private final String code;
    private final String defaultMessage;
}
