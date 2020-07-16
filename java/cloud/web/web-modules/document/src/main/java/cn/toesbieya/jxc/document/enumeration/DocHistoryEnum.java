package cn.toesbieya.jxc.document.enumeration;

public enum DocHistoryEnum {
    WITHDRAW(0), COMMIT(1), PASS(2), REJECT(3);

    private final int code;

    DocHistoryEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
