package co.com.loanapplications.model.loanapplication.exceptions;

public class UserNotAuthorizedException extends BaseBusinessException {
    public UserNotAuthorizedException(String code, String message) {
        super(code, message);
    }
}
