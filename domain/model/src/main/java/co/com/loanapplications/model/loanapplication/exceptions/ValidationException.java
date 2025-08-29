package co.com.loanapplications.model.loanapplication.exceptions;

public class ValidationException extends BaseBusinessException {
    public ValidationException(String code, String message) {
        super(code, message);
    }
}
