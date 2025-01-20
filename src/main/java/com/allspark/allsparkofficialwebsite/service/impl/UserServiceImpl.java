package com.allspark.allsparkofficialwebsite.service.impl;

import com.allspark.allsparkofficialwebsite.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allspark.allsparkofficialwebsite.model.User;
import com.allspark.allsparkofficialwebsite.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 31964
* @description 针对表【user(储存普通用户信息的表)】的数据库操作Service实现
* @createDate 2025-01-15 20:26:35
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




