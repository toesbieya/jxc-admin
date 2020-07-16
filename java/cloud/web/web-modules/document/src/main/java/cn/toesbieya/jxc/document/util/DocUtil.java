package cn.toesbieya.jxc.document.util;

import cn.toesbieya.jxc.common.constant.DocumentConstant;
import cn.toesbieya.jxc.common.model.entity.BizDoc;
import cn.toesbieya.jxc.common.utils.DateUtil;
import cn.toesbieya.jxc.common.utils.RedisUtil;
import cn.toesbieya.jxc.document.model.vo.DocSearch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
public class DocUtil {
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

    public static <T extends BizDoc> LambdaQueryWrapper<T> baseCondition(Class<T> c, DocSearch vo) {
        String id = vo.getId();
        String id_fuzzy = vo.getId_fuzzy();
        Integer cid = vo.getCid();
        String cname = vo.getCname();
        Integer vid = vo.getVid();
        String vname = vo.getVname();
        String status = vo.getStatus();
        Long ctimeStart = vo.getCtimeStart();
        Long ctimeEnd = vo.getCtimeEnd();
        Long vtimeStart = vo.getVtimeStart();
        Long vtimeEnd = vo.getVtimeEnd();

        return Wrappers.lambdaQuery(c)
                .eq(!StringUtils.isEmpty(id), BizDoc::getId, id)
                .like(!StringUtils.isEmpty(id_fuzzy), BizDoc::getId, id_fuzzy)
                .eq(cid != null, BizDoc::getCid, cid)
                .like(!StringUtils.isEmpty(cname), BizDoc::getCname, cname)
                .eq(vid != null, BizDoc::getVid, vid)
                .like(!StringUtils.isEmpty(vname), BizDoc::getVname, vname)
                .inSql(!StringUtils.isEmpty(status), BizDoc::getStatus, status)
                .ge(ctimeStart != null, BizDoc::getCtime, ctimeStart)
                .le(ctimeEnd != null, BizDoc::getCtime, ctimeEnd)
                .ge(vtimeStart != null, BizDoc::getCtime, vtimeStart)
                .le(vtimeEnd != null, BizDoc::getCtime, vtimeEnd)
                .orderByDesc(BizDoc::getCtime);
    }
}
