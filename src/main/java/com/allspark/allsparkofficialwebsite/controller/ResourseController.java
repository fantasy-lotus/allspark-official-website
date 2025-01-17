package com.allspark.allsparkofficialwebsite.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
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

import javax.annotation.PostConstruct;
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
 *
 * @author lotus
 * @version 1.0
 * @since 2025/1/16 下午12:10
 */
@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class ResourseController {

    @Value("${storage.image.path}")
    private String imageStoragePath;

    @Value("${storage.json.path}")
    private String jsonStoragePath;

    private Path storagePath;

    private Path jsonPath;

//    @PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 获取项目根目录
        String projectDir = System.getProperty("user.dir");
        // 解析存储路径
        storagePath = Paths.get(projectDir, imageStoragePath).toAbsolutePath().normalize();
        jsonPath = Paths.get(projectDir, jsonStoragePath).toAbsolutePath().normalize();
        // 创建存储目录
        if (!Files.exists(storagePath)) {
            try {
                Files.createDirectories(storagePath);
                log.info("图片存储目录已初始化:{}", storagePath);
            } catch (IOException e) {
                log.error("无法创建图片存储目录:{}", storagePath, e);
                throw new RuntimeException("无法初始化存储目录", e);
            }
        } else {
            log.info("图片存储目录:{}", storagePath);
        }
        if (!Files.exists(jsonPath)) {
            try {
                Files.createDirectories(jsonPath);
                log.info("json存储目录已初始化:{}", jsonPath);
            } catch (IOException e) {
                log.error("无法创建json存储目录:{}", jsonPath, e);
                throw new RuntimeException("无法初始化存储目录", e);
            }
        } else {
            log.info("json存储目录:{}", jsonPath);
        }
    }

    // 获取图片URL接口(已弃用)
    @Deprecated
    @GetMapping("/img/{imageName}")
    public BaseResponse getImageUrl(@PathVariable String imageName, HttpServletRequest request) {
        if (RandomUtils.nextInt(0, 100) < 114514) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "滚");
        }
        if (!StringUtils.hasText(imageName)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 防止路径遍历
        if (StrUtil.contains(imageName, "..")) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "非法请求");
        }

        File imageFile = FileUtil.file(imageStoragePath, imageName);
        if (!FileUtil.exist(imageFile)) {
            log.warn("未找到图片文件:" + imageFile.getAbsolutePath());
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "未找到文件");
        }

        // 构建图片的URL
        String imageUrl = request.getHeader("Host") + "/api/images/" + imageName;
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
        if (request.getHeader("Authorization") == null || !request.getHeader("Authorization").equals("allspark520")) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "Unauthorized");
        }        // 1. 验证文件是否为空
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

        if (CharSequenceUtil.contains(imageName, "..")) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "非法请求");
        }

        try {
            Path targetPath = resolveImagePath(imageName);
            log.info("保存图片到: {}", targetPath);

            Path parentDir = targetPath.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                log.info("创建父目录: {}", parentDir);
            }

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("图片保存成功: {}", targetPath);

            String imageUrl = "/images/" + imageName;

            return ResultUtils.success(imageUrl);

        } catch (IOException e) {
            log.error("保存图片失败: {}", imageName, e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "更新图片失败");
        }
    }

    // 获取json
    @GetMapping("/json/{fileName}")
    public BaseResponse<String> getJsonData(@PathVariable("fileName") String fileName) {
        File jsonFile = jsonPath.resolve(fileName).normalize().toFile();

        // 判断文件是否存在
        if (!jsonFile.exists()) {
            ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "未找到文件");
        }
        return ResultUtils.success(FileUtil.readUtf8String(jsonFile));
    }

    // 更新 JSON 数据
    @PostMapping("/json/{fileName}")
    public BaseResponse updateJsonData(@PathVariable("fileName") String fileName, @RequestBody String jsonData,HttpServletRequest request) {
        try {
            if (request.getHeader("Authorization") == null || !request.getHeader("Authorization").equals("allspark520")) {
                ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "Unauthorized");
            }
            JSONUtil.parseObj(jsonData);
            File jsonFile = jsonPath.resolve(fileName).normalize().toFile();
            if (!jsonFile.exists()) {
                FileUtil.touch(jsonFile);
            }
            FileUtil.writeUtf8String(jsonData, jsonFile);
            return ResultUtils.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
    }
    /**
     * 解析图片路径，确保路径的规范性
     *
     * @param imageName 图片名称
     * @return 解析后的路径
     */
    private Path resolveImagePath(String imageName) {
        return storagePath.resolve(imageName).normalize();
    }

}
