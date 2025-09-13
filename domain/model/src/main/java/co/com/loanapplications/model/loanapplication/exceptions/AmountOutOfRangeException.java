package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class AmountOutOfRangeException extends BaseBusinessException {
    public AmountOutOfRangeException() {
        super(ErrorCodesEnum.AMOUNT_OUT_OF_RANGE_400);
    }
}
