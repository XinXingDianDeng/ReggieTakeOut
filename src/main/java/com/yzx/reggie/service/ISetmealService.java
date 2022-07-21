package com.yzx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.dto.SetmealDto;
import com.yzx.reggie.entity.Setmeal;

import java.util.List;

public interface ISetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    SetmealDto getWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);

    List<DishDto> getSetmealDish(Long id);
}
