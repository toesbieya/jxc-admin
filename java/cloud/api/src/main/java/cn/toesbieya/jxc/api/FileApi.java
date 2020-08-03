package cn.toesbieya.jxc.api;

import java.util.List;

public interface FileApi {
    String getToken();

    void delete(String key);

    void deleteBatch(String... key);

    default void deleteBatch(List<String> key) {
        deleteBatch(key.toArray(new String[0]));
    }
}
