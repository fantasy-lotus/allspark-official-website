package com.allspark.allsparkofficialwebsite.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.allspark.allsparkofficialwebsite.common.BaseResponse;
import com.allspark.allsparkofficialwebsite.common.ErrorCode;
import com.allspark.allsparkofficialwebsite.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

    // 初始化存储路径
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 创建存储目录
        createDirectoryIfNotExist(Paths.get(imageStoragePath).toAbsolutePath().normalize(), "图片");
        createDirectoryIfNotExist(Paths.get(jsonStoragePath).toAbsolutePath().normalize(), "文本");
    }

    private void createDirectoryIfNotExist(Path path, String resourceName) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                log.info("{}存储目录已初始化:{}", resourceName, path);
            } catch (IOException e) {
                log.error("无法创建{}存储目录:{}", resourceName, path, e);
                throw new RuntimeException("无法初始化存储目录", e);
            }
        } else {
            log.info("{}存储目录:{}", resourceName, path);
        }
    }

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
        String imagePath = imageStoragePath + imageName;
        Path targetPath = Paths.get(imagePath).toAbsolutePath().normalize();
        File file = targetPath.toFile();
        if (!file.exists()) {
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
            @RequestParam("auth") String auth) {

        log.info("收到更新图片的请求: {}", imageName);
        if (!"allspark520".equals(auth)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "Unauthorized");
        }

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
        String imagePath = imageStoragePath + imageName;
        Path targetPath = Paths.get(imagePath).toAbsolutePath().normalize();
        File targetFile = targetPath.toFile();
        if (!targetFile.exists()) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "未找到文件");
        }
        log.info("保存图片到: {}", imagePath);
        // 保存文件
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("图片保存成功: {}", targetPath);
            String imageUrl = "/images/" + imageName;
            return ResultUtils.success(imageUrl);
        } catch (IOException e) {
            log.error("保存图片失败: {}", targetPath, e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "保存图片失败");
        }
    }

    // 获取json数据
    @GetMapping("/json/{fileName}")
    public BaseResponse getJsonData(@PathVariable("fileName") String fileName) {
        log.info("获取JSON数据: {}", fileName);
        String jsonPath = jsonStoragePath + fileName;
        File jsonFile = Paths.get(jsonPath).toAbsolutePath().normalize().toFile();
        // 判断文件是否存在
        if (!jsonFile.exists()) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "未找到文件");
        }
        return ResultUtils.success(FileUtil.readUtf8String(jsonFile));
    }

    // 更新 JSON 数据
    @PostMapping("/json/{fileName}")
    public BaseResponse updateJsonData(@PathVariable("fileName") String fileName, @RequestBody String jsonData, @RequestParam("auth") String auth) {
        try {
            log.info("更新JSON数据: {}", fileName);
            if (!"allspark520".equals(auth)) {
                return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "Unauthorized");
            }
            JSONUtil.parseObj(jsonData); // 验证JSON格式
            String jsonPath = jsonStoragePath + fileName;
            File jsonFile = Paths.get(jsonPath).toAbsolutePath().normalize().toFile();
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
