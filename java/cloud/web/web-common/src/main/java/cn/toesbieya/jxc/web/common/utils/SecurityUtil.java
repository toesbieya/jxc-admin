package cn.toesbieya.jxc.web.common.utils;

import cn.toesbieya.jxc.common.model.vo.UserVo;

import java.util.Set;
import java.util.stream.Collectors;

public class SecurityUtil {
    //获取字符串形式的数据范围，sql中使用 in (${param}) 拼接
    public static String getScopeParam() {
        UserVo user = ThreadUtil.getUser();
        if (user == null) return "''";
        Set<Integer> deptIds = user.getDepartmentIds();
        if (deptIds == null) return null;
        return deptIds.isEmpty() ? "''" : deptIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}
