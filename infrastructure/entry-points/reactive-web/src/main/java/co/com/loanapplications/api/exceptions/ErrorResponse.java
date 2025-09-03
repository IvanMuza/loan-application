package co.com.loanapplications.api.exceptions;

public record ErrorResponse(String code, String message, String path) {
}
