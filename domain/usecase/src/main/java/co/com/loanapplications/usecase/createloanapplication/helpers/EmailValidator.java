package co.com.loanapplications.usecase.createloanapplication.helpers;

public class EmailValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static boolean isValid(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
}
