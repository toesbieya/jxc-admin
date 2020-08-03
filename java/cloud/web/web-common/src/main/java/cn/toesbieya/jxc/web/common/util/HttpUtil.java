package cn.toesbieya.jxc.web.common.util;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class HttpUtil {
    public static String get(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).method("GET", null).build();
        Call call = okHttpClient.newCall(request);
        try {
            ResponseBody body = call.execute().body();

            return body == null ? null : body.string();
        }
        catch (Exception e) {
            return null;
        }
    }
}
