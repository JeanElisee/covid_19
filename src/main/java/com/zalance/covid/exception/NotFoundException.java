package com.zalance.covid.exception;

@SuppressWarnings("serial")
public class NotFoundException extends Exception {

    private final int errorCode;

    public NotFoundException() {
        super();
        this.errorCode = 0;
    }

    public NotFoundException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public NotFoundException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public NotFoundException(String message) {
        super(message);
        this.errorCode = 0;

    }

    public NotFoundException(int errorCode, String message, Throwable t) {
        super(message, t);
        this.errorCode = errorCode;
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        errorCode = 0;
    }

    public NotFoundException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;

    }

    public NotFoundException(int errorCode, Throwable t) {
        super(t.getMessage(), t);

        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
