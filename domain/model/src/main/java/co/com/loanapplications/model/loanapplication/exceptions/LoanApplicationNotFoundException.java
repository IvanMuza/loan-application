package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class LoanApplicationNotFoundException extends BaseBusinessException {
    public LoanApplicationNotFoundException() {
        super(ErrorCodesEnum.LOAN_APPLICATION_NOT_FOUND_404);
    }
}
