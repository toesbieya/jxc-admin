package com.toesbieya.my.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.toesbieya.my.interceptor.RateControlInterceptor;
import com.toesbieya.my.interceptor.SecurityInterceptor;
import com.toesbieya.my.interceptor.UserActionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (int i = converters.size() - 1; i >= 0; i--) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                converters.remove(i);
            }
        }

        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter.setFastJsonConfig(FastJsonConfigFactory.defaultConfig());

        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);

        converters.add(fastJsonHttpMessageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] exclude = {"/test/**", "/account/login", "/account/logout", "/account/register", "/account/checkName", "/error"};
        addInterceptor(registry, new SecurityInterceptor(), exclude);
        addInterceptor(registry, new RateControlInterceptor(), exclude);
        addInterceptor(registry, new UserActionInterceptor(), exclude);
    }

    private void addInterceptor(InterceptorRegistry registry, HandlerInterceptor interceptor, String[] exclude) {
        InterceptorRegistration registration = registry.addInterceptor(interceptor).addPathPatterns("/**");
        registration.excludePathPatterns(exclude);
    }
}
