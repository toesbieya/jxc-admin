package cn.toesbieya.jxc.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateConfig {
    @Bean
    public KeyResolver urlKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
}
