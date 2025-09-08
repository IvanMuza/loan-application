package co.com.loanapplications.model.loanapplication.exceptions;

public class UserNotAuthenticatedException extends BaseBusinessException {
    public UserNotAuthenticatedException(String code, String message) {
        super(code, message);
    }
}
