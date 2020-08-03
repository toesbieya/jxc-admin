package cn.toesbieya.jxc.file.service;

import cn.toesbieya.jxc.api.FileApi;
import cn.toesbieya.jxc.file.config.QiniuTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@org.apache.dubbo.config.annotation.Service
public class FileService implements FileApi {
    @Resource
    private QiniuTemplate template;

    @Override
    public String getToken() {
        return template.getToken();
    }

    @Override
    public void delete(String key) {
        template.delete(key);
    }

    @Override
    public void deleteBatch(String... key) {
        template.deleteBatch(key);
    }

    /*@Override
    public void deleteBatch(List<String> key) {
        deleteBatch(key.toArray(new String[0]));
    }*/
}
