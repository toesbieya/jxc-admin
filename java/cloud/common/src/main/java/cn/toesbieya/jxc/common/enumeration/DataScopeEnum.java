package cn.toesbieya.jxc.common.enumeration;

public enum DataScopeEnum {
    ALL(1), SELF(2), SPECIFIC(3);

    private final int code;

    DataScopeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
