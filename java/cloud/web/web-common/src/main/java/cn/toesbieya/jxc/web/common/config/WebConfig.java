package cn.toesbieya.jxc.web.common.config;

import cn.toesbieya.jxc.common.config.FastJsonConfigFactory;
import cn.toesbieya.jxc.web.common.interceptor.SecurityInterceptor;
import cn.toesbieya.jxc.web.common.interceptor.UserActionInterceptor;
import cn.toesbieya.jxc.web.common.interceptor.predicate.UserActionPredicate;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private UserActionPredicate userActionPredicate;

    @Autowired
    public void setUserActionInterceptor(UserActionPredicate userActionPredicate) {
        this.userActionPredicate = userActionPredicate;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (int i = converters.size() - 1; i >= 0; i--) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                converters.remove(i);
            }
        }

        converters.add(getFastJsonConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new UserActionInterceptor(userActionPredicate)).addPathPatterns("/**");
    }

    private FastJsonHttpMessageConverter getFastJsonConverter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFastJsonConfig(FastJsonConfigFactory.defaultConfig());

        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(fastMediaTypes);

        return converter;
    }
}
