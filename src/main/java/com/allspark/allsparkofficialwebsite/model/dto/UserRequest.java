package com.allspark.allsparkofficialwebsite.model.dto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * UserRequest
 * Description:
 *
 * @author lotus
 * @version 1.0
 * @since 2025/1/15 下午8:57
 */
@Data
public class UserRequest {
    /**
     * 名字
     */
    @NotBlank(message = "名字不能为空")
    private String userName;

    /**
     * QQ号
     */
    @NotBlank(message = "QQ号不能为空")
    @Pattern(regexp = "^\\d+$", message = "QQ号码只能包含数字")
    @Size(max = 11, message = "QQ号码长度不能超过11")
    private String qqNumber;

    /**
     * 年级
     */
    @NotNull(message = "年级不能为空")
    @Min(value = 0, message = "年级必须在0到3之间")
    @Max(value = 3, message = "年级必须在0到3之间")
    private Integer grade;

    /*
      方向选择 前端 / 后端 / 产品
     */
    @NotBlank(message = "方向不能为空")
    @Pattern(regexp = "^(前端|后端|产品)$", message = "方向只能是前端、后端或产品")
    private String direction;

    /**
     * 学习进展
     */
    @NotBlank(message = "进度不能为空")
    @Size(max = 200, message = "长度不能超过200")
    private String progress;
}
