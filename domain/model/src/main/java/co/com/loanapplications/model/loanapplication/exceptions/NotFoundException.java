package co.com.loanapplications.model.loanapplication.exceptions;

public class NotFoundException extends BaseBusinessException {
    public NotFoundException(String code, String message) {
        super(code, message);
    }
}
