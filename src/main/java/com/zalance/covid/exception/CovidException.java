package com.zalance.covid.exception;

@SuppressWarnings("serial")
public class CovidException extends Exception {

    private final int errorCode;

    public CovidException() {
        super();
        this.errorCode = 0;
    }

    public CovidException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public CovidException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CovidException(String message) {
        super(message);
        this.errorCode = 0;

    }

    public CovidException(int errorCode, String message, Throwable t) {
        super(message, t);
        this.errorCode = errorCode;
    }

    public CovidException(String message, Throwable cause) {
        super(message, cause);
        errorCode = 0;
    }

    public CovidException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;

    }

    public CovidException(int errorCode, Throwable t) {
        super(t.getMessage(), t);

        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
