package com.zalance.covid.exception;

@SuppressWarnings("serial")
public class RetryException extends Exception {

    private final int errorCode;

    public RetryException() {
        super();
        this.errorCode = 0;
    }

    public RetryException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public RetryException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public RetryException(String message) {
        super(message);
        this.errorCode = 0;

    }

    public RetryException(int errorCode, String message, Throwable t) {
        super(message, t);
        this.errorCode = errorCode;
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
        errorCode = 0;
    }

    public RetryException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;

    }

    public RetryException(int errorCode, Throwable t) {
        super(t.getMessage(), t);

        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
