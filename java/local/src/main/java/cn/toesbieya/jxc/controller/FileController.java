package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.utils.QiniuUtil;
import cn.toesbieya.jxc.model.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("file")
@Slf4j
public class FileController {
    @GetMapping("delete")
    public Result delete(@RequestParam String url) throws UnsupportedEncodingException {
        QiniuUtil.delete(URLDecoder.decode(url, "utf-8"));
        return Result.success("删除成功");
    }

    @GetMapping("getToken")
    public Result getToken() {
        return Result.success(null, QiniuUtil.getToken());
    }
}
