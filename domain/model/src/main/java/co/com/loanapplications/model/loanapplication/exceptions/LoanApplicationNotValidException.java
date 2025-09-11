package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class LoanApplicationNotValidException extends BaseBusinessException {
    public LoanApplicationNotValidException() {
        super(ErrorCodesEnum.APPLICATION_REQUIRED_400);
    }
}
