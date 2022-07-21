package com.yzx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yzx.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
