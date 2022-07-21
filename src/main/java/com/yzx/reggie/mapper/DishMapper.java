package com.yzx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    List<DishDto> selectDishDtoByPage(int offset, int pageSize, String name);

    DishDto selectByIdWithFlavor(Long id);
}
