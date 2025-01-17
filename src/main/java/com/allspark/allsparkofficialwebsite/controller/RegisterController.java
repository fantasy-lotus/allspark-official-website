package com.allspark.allsparkofficialwebsite.controller;

import com.allspark.allsparkofficialwebsite.common.BaseResponse;
import com.allspark.allsparkofficialwebsite.common.ErrorCode;
import com.allspark.allsparkofficialwebsite.common.ResultUtils;
import com.allspark.allsparkofficialwebsite.model.User;
import com.allspark.allsparkofficialwebsite.model.dto.UserRequest;
import com.allspark.allsparkofficialwebsite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * RegisterController
 * Description:
 * 报名接口
 * @author lotus
 * @version 1.0
 * @since 2025/1/15 下午8:17
 */
@RestController
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public BaseResponse register(@Valid @RequestBody UserRequest user) {
        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setQqNumber(user.getQqNumber());
        newUser.setGrade(user.getGrade());
        newUser.setDirection(user.getDirection());
        newUser.setProgress(user.getProgress());
        boolean save = userService.save(newUser);
        log.info("用户注册:{}", newUser);
        if (save) {
            return ResultUtils.success(newUser.getUserId());
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "注册失败");
        }
    }
}
