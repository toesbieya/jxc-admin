package cn.toesbieya.jxc.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@EnableConfigurationProperties(QiniuProperty.class)
@DependsOn("redisUtil")
public class QiniuConfig {
    @Bean
    public QiniuTemplate qiniuTemplate(QiniuProperty property) {
        return new QiniuTemplate(property);
    }
}
