package cn.toesbieya.jxc.constant;

public interface DocumentConstant {
    String UPDATE_DOCUMENTS_LOCK_KEY = "UPDATE_DOCUMENTS";
    String[] DOCUMENT_TYPE = {"CGDD", "CGRK", "CGTH", "XSDD", "XSCK", "XSTH"};
    String DOCUMENT_TYPE_REDIS_KEY = "documentsID";
}
