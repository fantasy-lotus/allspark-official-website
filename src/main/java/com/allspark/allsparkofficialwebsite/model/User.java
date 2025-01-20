package com.allspark.allsparkofficialwebsite.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 储存普通用户信息的表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;

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

    /**
     * 填写申请表的时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}