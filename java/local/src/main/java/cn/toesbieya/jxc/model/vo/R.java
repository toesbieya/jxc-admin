package cn.toesbieya.jxc.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class R {
    /**
     * 返回的代码，200表示成功，其他表示失败
     */
    private int status;
    /**
     * 成功或失败时返回的错误信息
     */
    private String msg;
    /**
     * 成功时返回的数据信息
     */
    private Object data;

    public R(int status) {
        this.status = status;
    }

    public R(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public R(int status, Object data) {
        this.status = status;
        this.data = data;
    }

    public static R success() {
        return new R(200);
    }

    public static R success(String msg) {
        return new R(200, msg);
    }

    public static R success(Object data) {
        return new R(200, data);
    }

    public static R success(String msg, Object data) {
        return new R(200, msg, data);
    }

    public static R fail(String msg) {
        return new R(500, msg);
    }

    public static R notfound() {
        return new R(404, "请求地址不存在");
    }

    public static R requireLogin() {
        return new R(401, "请登录后重试");
    }

    public static R noPermission() {
        return new R(403, "没有权限进行该操作");
    }

    public static R overload() {
        return new R(503, "当前请求的人数过多，请稍后重试");
    }

    public static R tooManyRequest() {
        return new R(429, "操作过于频繁，请稍后重试");
    }

    public boolean isSuccess() {
        return this.status == 200;
    }
}
