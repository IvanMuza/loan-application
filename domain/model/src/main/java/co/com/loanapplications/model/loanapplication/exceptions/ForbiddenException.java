package co.com.loanapplications.model.loanapplication.exceptions;

public class ForbiddenException extends BaseBusinessException {
    public ForbiddenException(String code, String message) {
        super(code, message);
    }
}
