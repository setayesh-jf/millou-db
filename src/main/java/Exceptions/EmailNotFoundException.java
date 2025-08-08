package Exceptions;


public class EmailNotFoundException extends RuntimeException {
    private final String code;

    public EmailNotFoundException(String code) {
        super("Email not found with code: " + code);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}