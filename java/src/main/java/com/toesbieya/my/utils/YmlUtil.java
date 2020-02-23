package com.toesbieya.my.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

public class YmlUtil {
    private static Properties properties;

    public static Object get(String key) {
        if (properties != null) {
            return properties.get(key);
        }
        Resource resource = new ClassPathResource("application.yml");
        try {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(resource);
            properties = yamlFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        assert properties != null;
        return properties.get(key);
    }
}
