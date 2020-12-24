package cn.toesbieya.jxc.file.controller;

import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.file.service.FileService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("file")
public class FileController {
    @Resource
    private FileService service;

    @GetMapping("delete")
    public R delete(@RequestParam String url) throws UnsupportedEncodingException {
        service.delete(URLDecoder.decode(url, "utf-8"));
        return R.success("删除成功");
    }

    @PostMapping("deleteBatch")
    public R deleteBatch(@RequestBody String[] urls) {
        if (urls.length == 0) {
            return R.fail("参数错误");
        }
        service.deleteBatch(urls);
        return R.success("批量删除成功");
    }

    @GetMapping("getToken")
    public R getToken() {
        return R.success(null, service.getToken());
    }
}
