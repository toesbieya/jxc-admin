package com.toesbieya.my.enumeration;

public enum BizDocumentStatusEnum {
    DRAFT(0), WAIT_VERIFY(1), VERIFIED(2);

    private final int code;

    BizDocumentStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
