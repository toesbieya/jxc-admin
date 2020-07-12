package com.toesbieya.my.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("qiniu")
@Data
public class QiniuConfig {
    private String accessKey;

    private String secretKey;

    private String bucket;

    //临时上传凭证的有效期，单位秒
    private Integer tokenExpire = 3600;

    //临时上传凭证的redis键名
    private String redisCacheKey = "qiniu-token";
}
