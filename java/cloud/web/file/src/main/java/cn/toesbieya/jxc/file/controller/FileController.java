package cn.toesbieya.jxc.file.controller;

import cn.toesbieya.jxc.common.model.vo.R;
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
    public R delete(@RequestParam String url) throws UnsupportedEncodingException {
        service.delete(URLDecoder.decode(url, "utf-8"));
        return R.success("删除成功");
    }

    @GetMapping("getToken")
    @ResponseBody
    public R getToken() {
        return R.success(null, service.getToken());
    }
}
