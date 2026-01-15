package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@RequiredArgsConstructor //使用 lombok 自动生成构造器对象
public class CommonController {

    private final AliOssUtil aliOssUtil;

//    public CommonController(AliOssUtil aliOssUtil) {
//        this.aliOssUtil = aliOssUtil;
//    }

    @PostMapping("/upload")
    public Result<String> Upload(MultipartFile file){

        try {
            // 获取文件原始名
            String originalFilename = file.getOriginalFilename();
            // 获取文件扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 使用 UUID 生成随机文件名
            String filename = UUID.randomUUID().toString() + extension;

            String filepath = aliOssUtil.upload(file.getBytes(),filename);
            return Result.success(filepath);
        } catch (IOException e) {
            log.error("上传文件失败：" , e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
