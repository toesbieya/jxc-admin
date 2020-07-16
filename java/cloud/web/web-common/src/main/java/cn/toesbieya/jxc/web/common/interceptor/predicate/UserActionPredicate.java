package cn.toesbieya.jxc.web.common.interceptor.predicate;

import javax.servlet.http.HttpServletRequest;

public interface UserActionPredicate {
    boolean allowed(HttpServletRequest request);
}
