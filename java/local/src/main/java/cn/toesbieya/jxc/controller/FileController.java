package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.model.vo.R;
import cn.toesbieya.jxc.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("file")
@Slf4j
public class FileController {
    @Resource
    private FileService service;

    @GetMapping("delete")
    public R delete(@RequestParam String url) throws UnsupportedEncodingException {
        service.delete(URLDecoder.decode(url, "utf-8"));
        return R.success("删除成功");
    }

    @GetMapping("getToken")
    public R getToken() {
        return R.success(null, service.getToken());
    }
}
