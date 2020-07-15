package com.toesbieya.my.utils;

import com.google.common.io.ByteStreams;
import com.toesbieya.my.constant.DocumentConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
public class DocumentUtil {
    private static String GET_DOCUMENT_ID_SCRIPT;

    static {
        try {
            GET_DOCUMENT_ID_SCRIPT = new String(ByteStreams.toByteArray(new ClassPathResource("/script/get_document_id.lua").getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取单号，形如 {type}0001
    public static String getDocumentID(String type) {
        if (!Arrays.asList(DocumentConstant.DOCUMENT_TYPE).contains(type)) {
            log.error("单据类型有误，【{}】不存在", type);
            return null;
        }

        RedisScript<Long> redisScript = new DefaultRedisScript<>(GET_DOCUMENT_ID_SCRIPT, Long.class);

        Long result = RedisUtil.getStringRedisTemplate().execute(
                redisScript,
                Arrays.asList(DocumentConstant.UPDATE_DOCUMENTS_LOCK_KEY, DocumentConstant.DOCUMENT_TYPE_REDIS_KEY),
                String.valueOf(DateUtil.getTimestampNow()),
                type
        );

        if (result == null || result <= 1) return null;

        return String.format("%s%s%04d", type, DateUtil.dateFormat(DateTimeFormatter.BASIC_ISO_DATE), result - 1);
    }
}
