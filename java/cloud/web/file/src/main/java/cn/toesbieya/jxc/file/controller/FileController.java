package cn.toesbieya.jxc.file.controller;

import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.file.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Controller
@RequestMapping("file")
public class FileController {
    @Resource
    private FileService service;

    @GetMapping("delete")
    @ResponseBody
    public Result delete(@RequestParam String url) throws UnsupportedEncodingException {
        service.delete(URLDecoder.decode(url, "utf-8"));
        return Result.success("删除成功");
    }

    @GetMapping("getToken")
    @ResponseBody
    public Result getToken() {
        return Result.success(null, service.getToken());
    }
}
