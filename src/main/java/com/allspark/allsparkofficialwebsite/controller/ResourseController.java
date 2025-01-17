package com.allspark.allsparkofficialwebsite.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.allspark.allsparkofficialwebsite.common.BaseResponse;
import com.allspark.allsparkofficialwebsite.common.ErrorCode;
import com.allspark.allsparkofficialwebsite.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * ResourseController
 * Description:
 * 返回静态资源接口
 */
@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class ResourseController {

    // 静态资源路径, Spring Boot会自动处理static文件夹下的资源
    @Value("${storage.image.path}")
    private String imageStoragePath;

    @Value("${storage.json.path}")
    private String jsonStoragePath;

//    // 初始化存储路径
//    @EventListener(ApplicationReadyEvent.class)
//    public void init() {
//        // 获取项目根目录
//        String projectDir = System.getProperty("user.dir");
//        // 解析静态资源路径
//        storagePath = Paths.get(projectDir, "src/main/resources/static/images").toAbsolutePath().normalize();
//        jsonPath = Paths.get(projectDir, "src/main/resources/static/json").toAbsolutePath().normalize();
//
//        // 创建存储目录
//        createDirectoryIfNotExist(storagePath, "图片");
//        createDirectoryIfNotExist(jsonPath, "JSON");
//    }
//
//    private void createDirectoryIfNotExist(Path path, String resourceName) {
//        if (!Files.exists(path)) {
//            try {
//                Files.createDirectories(path);
//                log.info("{}存储目录已初始化:{}", resourceName, path);
//            } catch (IOException e) {
//                log.error("无法创建{}存储目录:{}", resourceName, path, e);
//                throw new RuntimeException("无法初始化存储目录", e);
//            }
//        } else {
//            log.info("{}存储目录:{}", resourceName, path);
//        }
//    }

    // 获取图片URL接口
    @GetMapping("/img/{imageName}")
    public BaseResponse getImageUrl(@PathVariable String imageName, HttpServletRequest request) {
        log.info("获取图片URL: {}", imageName);
        if (!StringUtils.hasText(imageName)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 防止路径遍历
        if (imageName.contains("..")) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "非法请求");
        }

        Path imageFile = Paths.get(imageStoragePath, imageName).toAbsolutePath();
        if (!Files.exists(imageFile)) {
            log.warn("未找到图片文件: {}", imageFile);
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "未找到文件");
        }

        String imageUrl = request.getScheme() + "://" + request.getHeader("Host") + "/api/images/" + imageName;
        return ResultUtils.success(imageUrl);
    }

    // 修改图片内容
    @PostMapping("/img/{imageName}")
    public BaseResponse updateImage(
            @PathVariable @NotBlank(message = "图片名称不能为空")
            @Size(max = 255, message = "图片名称不能超过255个字符")
            String imageName,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        log.info("收到更新图片的请求: {}", imageName);
//        if (request.getHeader("Authorization") == null || !request.getHeader("Authorization").equals("allspark520")) {
//            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "Unauthorized");
//        }

        // 1. 验证文件是否为空
        if (file.isEmpty()) {
            log.warn("上传的文件为空");
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        // 2. 验证文件类型（仅允许图片）
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("不支持的文件类型: {}", contentType);
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "仅允许上传图片文件");
        }

        if (imageName.contains("..")) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "非法请求");
        }

        try {
            // 更新图片存储路径到 static/images
            Path targetPath = Paths.get(imageStoragePath, imageName).toAbsolutePath();
            log.info("保存图片到: {}", targetPath);

            // 创建父目录（如果不存在）
            Path parentDir = targetPath.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                log.info("创建父目录: {}", parentDir);
            }

            // 保存文件
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("图片保存成功: {}", targetPath);

            String imageUrl = "/images/" + imageName;
            return ResultUtils.success(imageUrl);

        } catch (IOException e) {
            log.error("保存图片失败: {}", imageName, e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "更新图片失败");
        }
    }

    // 获取json数据
    @GetMapping("/json/{fileName}")
    public BaseResponse<String> getJsonData(@PathVariable("fileName") String fileName) {
        log.info("获取JSON数据: {}", fileName);
        File jsonFile = Paths.get(jsonStoragePath, fileName).toAbsolutePath().normalize().toFile();

        // 判断文件是否存在
        if (!jsonFile.exists()) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "未找到文件");
        }
        return ResultUtils.success(FileUtil.readUtf8String(jsonFile));
    }

    // 更新 JSON 数据
    @PostMapping("/json/{fileName}")
    public BaseResponse updateJsonData(@PathVariable("fileName") String fileName, @RequestBody String jsonData, HttpServletRequest request) {
        try {
            log.info("更新JSON数据: {}", fileName);
            if (request.getHeader("Authorization") == null || !request.getHeader("Authorization").equals("allspark520")) {
                return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "Unauthorized");
            }
            JSONUtil.parseObj(jsonData); // 验证JSON格式

            File jsonFile = Paths.get(jsonStoragePath, fileName).toAbsolutePath().normalize().toFile();
            if (!jsonFile.exists()) {
                FileUtil.touch(jsonFile);
            }

            FileUtil.writeUtf8String(jsonData, jsonFile);
            log.info("更新JSON成功: {}", fileName);
            return ResultUtils.success("更新成功");
        } catch (Exception e) {
            log.error("更新JSON失败: {}", fileName, e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
    }

}
