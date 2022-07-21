package com.yzx.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.entity.Dish;

import java.util.HashMap;
import java.util.List;

public interface IDishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    HashMap getByPage(int page, int pageSize, String name);

    DishDto getByIdWithFlavors(Long id);

    void updateWithFlavor(DishDto dishDto);

    void removeWithFlavor(List<Long> ids);
}
