package cn.toesbieya.jxc.web.common.utils;

import cn.toesbieya.jxc.common.utils.RedisUtil;
import cn.toesbieya.jxc.web.common.config.QiniuConfig;
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

@Slf4j
@Component
@DependsOn("redisUtil")
public class QiniuUtil {
    private final QiniuConfig config;
    private final Auth auth;
    private final BucketManager bucketManager;

    public String getToken() {
        String key = config.getRedisCacheKey();

        String token = (String) RedisUtil.get(key);

        if (token == null) {
            token = auth.uploadToken(config.getBucket());
            RedisUtil.set(key, token, config.getTokenExpire());
        }

        return token;
    }

    public void delete(String key) {
        try {
            bucketManager.delete(config.getBucket(), key);
        }
        catch (QiniuException e) {
            log.info("七牛云删除单个文件失败,{}", e.getMessage());
        }
    }

    public void deleteBatch(String... key) {
        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        batchOperations.addDeleteOp(config.getBucket(), key);
        try {
            bucketManager.batch(batchOperations);
        }
        catch (QiniuException e) {
            log.info("七牛云批量删除文件失败,{}", e.getMessage());
        }
    }

    @Autowired
    public QiniuUtil(QiniuConfig config) {
        this.config = config;

        if (StringUtils.isEmpty(config.getAccessKey())) {
            this.auth = null;
            this.bucketManager = null;
            return;
        }

        this.auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        this.bucketManager = new BucketManager(this.auth, new Configuration(Region.huadong()));
    }
}
