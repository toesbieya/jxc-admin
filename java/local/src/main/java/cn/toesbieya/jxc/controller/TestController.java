package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.model.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("test")
@CrossOrigin
@Slf4j
public class TestController {

    @GetMapping("pressure/1")
    @ResponseBody
    public void pressure1() {
        Map<String, String> map = new LinkedHashMap<>();
        map = null;
    }

    @GetMapping("pressure/2")
    @ResponseBody
    public void pressure2() {
        Map<String, String> map = new LinkedHashMap<>();
    }

    @PostMapping("test")
    @ResponseBody
    public R test() {
        return R.success();
    }

    @PostMapping("upload")
    @ResponseBody
    public R upload(MultipartFile file, @RequestParam(required = false) String extraParam1, @RequestParam(required = false) String extraParam2) throws IOException {
        /*System.out.println("extraParam1:" + extraParam1);
        System.out.println("extraParam2:" + extraParam2);
        System.out.println("文件名称：" + file.getOriginalFilename());
        String tempPath = "C:/static/" + file.getOriginalFilename();
        file.transferTo(Paths.get(tempPath));*/
        System.out.println("文件名称：" + file.getOriginalFilename());
        return R.success("ok");
    }
}
