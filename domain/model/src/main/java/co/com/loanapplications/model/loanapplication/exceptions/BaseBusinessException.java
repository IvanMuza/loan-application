package co.com.loanapplications.model.loanapplication.exceptions;

import lombok.Getter;

@Getter
public class BaseBusinessException extends RuntimeException {
    private final String code;
    public BaseBusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}
