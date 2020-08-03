package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.config.QiniuTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FileService {
    @Resource
    private QiniuTemplate template;

    public String getToken() {
        return template.getToken();
    }

    public void delete(String key) {
        template.delete(key);
    }

    public void deleteBatch(String... key) {
        template.deleteBatch(key);
    }

    public void deleteBatch(List<String> key) {
        deleteBatch(key.toArray(new String[0]));
    }
}
