package com.zalance.covid.constant;

public enum ErrorCode {
    NEED_TO_RETRY(2), ANOTHER_ATTEMPT(3);

    private int fieldValue;

    ErrorCode(int fieldValue) {
        this.fieldValue = fieldValue;
    }

    public int getFieldValue() {
        return fieldValue;
    }
}
