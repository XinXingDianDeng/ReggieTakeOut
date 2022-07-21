package com.yzx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzx.reggie.common.CustomException;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.dto.SetmealDto;
import com.yzx.reggie.entity.Dish;
import com.yzx.reggie.entity.DishFlavor;
import com.yzx.reggie.entity.Setmeal;
import com.yzx.reggie.entity.SetmealDish;
import com.yzx.reggie.mapper.SetmealMapper;
import com.yzx.reggie.service.IDishFlavorService;
import com.yzx.reggie.service.IDishService;
import com.yzx.reggie.service.ISetmealDishService;
import com.yzx.reggie.service.ISetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {

    private ISetmealDishService setmealDishService;

    @Autowired
    private IDishService dishService;

    @Autowired
    private IDishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询是否有正在售卖的套餐
        LambdaQueryWrapper<Setmeal> setmealLWQ = new LambdaQueryWrapper<>();
        setmealLWQ.in(Setmeal::getId, ids)
                .eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLWQ);
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //删除套餐
        this.removeByIds(ids);
        //删除相关的套餐-菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLWQ = new LambdaQueryWrapper<>();
        setmealDishLWQ.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLWQ);
        //TODO:删除对应图片
    }

    @Override
    public SetmealDto getWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐
        this.updateById(setmealDto);
        //删除当前菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //重新添加菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        if (setmealDishes.size() > 0) {
            setmealDishes.stream().map((item) -> {
                item.setSetmealId(setmealDto.getId());
                return item;
            }).collect(Collectors.toList());
            setmealDishService.saveBatch(setmealDishes);
        }
    }

    @Override
    public List<DishDto> getSetmealDish(Long id) {
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //根据SetmealDish中的dishID查询菜品详细信息
        List<DishDto> dishDtoList = setmealDishes.stream().map((item) -> {
            Long dishId = item.getDishId();
            Dish dish = dishService.getById(dishId);
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            dishDto.setCopies(item.getCopies());
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());
        return dishDtoList;
    }

    @Autowired
    public void setSetmealDishService(ISetmealDishService setmealDishService) {
        this.setmealDishService = setmealDishService;
    }
}
