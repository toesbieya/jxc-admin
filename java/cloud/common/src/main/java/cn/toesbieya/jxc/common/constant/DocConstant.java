package cn.toesbieya.jxc.common.constant;

public interface DocConstant {
    //更新单据ID时的redis锁的键
    String UPDATE_DOC_LOCK_KEY = "UPDATE_DOC";

    //单据ID的redis缓存的键
    String DOC_TYPE_REDIS_KEY = "DOC_ID";

    //单据类型
    String[] DOC_TYPE = {"CGDD", "CGRK", "CGTH", "XSDD", "XSCK", "XSTH"};
}
