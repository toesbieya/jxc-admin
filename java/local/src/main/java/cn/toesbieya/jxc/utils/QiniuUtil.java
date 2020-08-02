package cn.toesbieya.jxc.utils;

import cn.toesbieya.jxc.config.QiniuConfig;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Component
@DependsOn("redisUtil")
public class QiniuUtil {
    private static QiniuConfig config;
    private static Auth AUTH ;
    private static BucketManager BUCKET_MANAGER;

    @Autowired
    public QiniuUtil(QiniuConfig config) {
        QiniuUtil.config = config;

        if (StringUtils.isEmpty(config.getAccessKey())) {
            QiniuUtil.AUTH = null;
            QiniuUtil.BUCKET_MANAGER = null;
            return;
        }

        QiniuUtil.AUTH = Auth.create(config.getAccessKey(), config.getSecretKey());
        QiniuUtil.BUCKET_MANAGER = new BucketManager(QiniuUtil.AUTH, new Configuration(Region.huadong()));
    }

    public static String getToken() {
        String token = (String) RedisUtil.get(config.getRedisCacheKey());

        if (token == null) {
            token = AUTH.uploadToken(config.getBucket());
            RedisUtil.set(config.getRedisCacheKey(), token, config.getTokenExpire());
        }

        return token;
    }

    public static void delete(String key) {
        try {
            BUCKET_MANAGER.delete(config.getBucket(), key);
        } catch (QiniuException e) {
            log.info("七牛云删除单个文件失败,{}", e.getMessage());
        }
    }

    public static void deleteBatch(String... key) {
        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        batchOperations.addDeleteOp(config.getBucket(), key);
        try {
            BUCKET_MANAGER.batch(batchOperations);
        } catch (QiniuException e) {
            log.info("七牛云批量删除文件失败,{}", e.getMessage());
        }
    }

    public static void deleteBatch(List<String> key) {
        deleteBatch(key.toArray(new String[0]));
    }
}
