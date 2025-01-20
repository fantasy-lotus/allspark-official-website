package com.allspark.allsparkofficialwebsite.controller;

import com.allspark.allsparkofficialwebsite.common.BaseResponse;
import com.allspark.allsparkofficialwebsite.common.ErrorCode;
import com.allspark.allsparkofficialwebsite.common.ResultUtils;
import com.allspark.allsparkofficialwebsite.model.User;
import com.allspark.allsparkofficialwebsite.model.dto.UserRequest;
import com.allspark.allsparkofficialwebsite.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "*")
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    @Autowired
    UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void dbInit() {
        userService.count();
    }

    @PostMapping("/register")
    public BaseResponse register(@Valid @RequestBody UserRequest user) {
        if(user==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        String userName = user.getUserName();
        if(StringUtils.isBlank(userName)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "姓名不能为空");
        }
        if(userName.length() > 15){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "姓名过长");
        }
        String qqNumber = user.getQqNumber();
        if(StringUtils.isBlank(qqNumber)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "QQ号不能为空");
        }
        String pattern = "^\\d{5,15}$";
        if(!qqNumber.matches(pattern)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "QQ号格式错误");
        }
        Integer grade = user.getGrade();
        if(grade == null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "年级不能为空");
        }
        if(grade < 0 || grade > 3){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "年级格式错误");
        }
        String direction = user.getDirection();
        if(StringUtils.isBlank(direction)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "方向不能为空");
        }
        if(!"前端".equals(direction) && !"后端".equals(direction) && !"产品经理".equals(direction)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "方向格式错误");
        }
        String progress = user.getProgress();
        if(StringUtils.isBlank(progress)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "学习进展不能为空");
        }
        if(progress.length() > 200){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "学习进展过长");
        }
        User newUser = new User();
        newUser.setUserName(userName);
        newUser.setQqNumber(qqNumber);
        newUser.setGrade(grade);
        newUser.setDirection(direction);
        newUser.setProgress(progress);
        User one = userService.getOne(new QueryWrapper<User>().eq("qq_number", user.getQqNumber()));
        if(one != null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "该QQ号已报名,请联系管理员");
        }
        boolean save = userService.save(newUser);
        log.info("用户注册:{}", newUser);
        if (save) {
            return ResultUtils.success(newUser.getUserId());
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "报名失败");
        }
    }
}
