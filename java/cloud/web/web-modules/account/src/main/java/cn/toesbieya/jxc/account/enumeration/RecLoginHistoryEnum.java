package cn.toesbieya.jxc.account.enumeration;

public enum RecLoginHistoryEnum {
    LOGOUT(0), LOGIN(1);

    private final int code;

    RecLoginHistoryEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
