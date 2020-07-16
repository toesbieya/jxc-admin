package cn.toesbieya.jxc.web.common.interceptor;

import cn.toesbieya.jxc.web.common.interceptor.predicate.UserActionPredicate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PredicateConfig {
    @Bean
    @ConditionalOnMissingBean(UserActionPredicate.class)
    public UserActionPredicate defaultUserActionPredicate() {
        return request -> true;
    }
}
