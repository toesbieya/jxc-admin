package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.model.vo.Result;
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
    public Result delete(@RequestParam String url) throws UnsupportedEncodingException {
        service.delete(URLDecoder.decode(url, "utf-8"));
        return Result.success("删除成功");
    }

    @GetMapping("getToken")
    public Result getToken() {
        return Result.success(null, service.getToken());
    }
}
