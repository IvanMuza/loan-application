package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;
import lombok.Getter;

@Getter
public class BaseBusinessException extends RuntimeException {
    private final String code;

    public BaseBusinessException(ErrorCodesEnum errorCodesEnum) {
        super(errorCodesEnum.getDefaultMessage());
        this.code = errorCodesEnum.getCode();
    }
}
