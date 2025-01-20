package com.allspark.allsparkofficialwebsite.mapper;

import com.allspark.allsparkofficialwebsite.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 31964
* @description 针对表【user(储存普通用户信息的表)】的数据库操作Mapper
* @createDate 2025-01-15 20:26:35
* @Entity com.allspark.allsparkofficialwebsite.model.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




