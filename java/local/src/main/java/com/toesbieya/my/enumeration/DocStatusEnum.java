package com.toesbieya.my.enumeration;

public enum DocStatusEnum {
    DRAFT(0), WAIT_VERIFY(1), VERIFIED(2);

    private final int code;

    DocStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
