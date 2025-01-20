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
    private String userName;

    /**
     * QQ号
     */
    private String qqNumber;

    /**
     * 年级
     */
    private Integer grade;

    /*
      方向选择 前端 / 后端 / 产品
     */
    private String direction;

    /**
     * 学习进展
     */
    private String progress;
}
