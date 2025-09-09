package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class AmountNotValidException extends BaseBusinessException {
    public AmountNotValidException() {
        super(ErrorCodesEnum.AMOUNT_REQUIRED_400);
    }
}
