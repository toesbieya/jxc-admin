package com.toesbieya.my.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;

public class FastJsonConfigFactory {
    public static FastJsonConfig defaultConfig() {
        FastJsonConfig config = new FastJsonConfig();

        config.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue,             // 是否输出值为null的字段
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect // 禁用循环引用
        );

        return config;
    }
}
