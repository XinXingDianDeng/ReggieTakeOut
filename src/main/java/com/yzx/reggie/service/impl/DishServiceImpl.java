package com.yzx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzx.reggie.common.CustomException;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.entity.Dish;
import com.yzx.reggie.entity.DishFlavor;
import com.yzx.reggie.mapper.DishMapper;
import com.yzx.reggie.service.IDishFlavorService;
import com.yzx.reggie.service.IDishService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {

    private IDishFlavorService dishFlavorService;

    private DishMapper dishMapper;

    @Autowired
    public DishServiceImpl(IDishFlavorService dishFlavorService, DishMapper dishMapper) {
        this.dishFlavorService = dishFlavorService;
        this.dishMapper = dishMapper;
    }

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();

        if (flavors.size() > 0) {
            flavors = flavors.stream().map((item) -> {
                item.setDishId(dishId);
                return item;
            }).collect(Collectors.toList());
            dishFlavorService.saveBatch(flavors);
        }
    }

    @Override
    public HashMap getByPage(int page, int pageSize, String name) {
        List<DishDto> dishDtos = dishMapper.selectDishDtoByPage((page - 1) * pageSize, pageSize, name);
        LambdaQueryWrapper<Dish> lwq = new LambdaQueryWrapper<>();
        lwq.eq(Dish::getIsDeleted, 0)
                .like(StringUtils.isNotBlank(name), Dish::getName, name);
        Integer total = dishMapper.selectCount(lwq);
        HashMap map = new HashMap();
        map.put("records", dishDtos);
        map.put("total", total);
        return map;
    }

    @Override
    public DishDto getByIdWithFlavors(Long id) {
        return dishMapper.selectByIdWithFlavor(id);
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品
        this.updateById(dishDto);
        //删除当前菜品口味
        LambdaQueryWrapper<DishFlavor> lwq = new LambdaQueryWrapper<>();
        lwq.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lwq);
        //重新添加口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors.size() > 0) {
            flavors = flavors.stream().map((item) -> {
                item.setDishId(dishDto.getId());
                return item;
            }).collect(Collectors.toList());
            dishFlavorService.saveBatch(flavors);
        }
    }

    @Override
    public void removeWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.in(Dish::getId, ids)
                .eq(Dish::getStatus, 0);
        int count = this.count(dishQueryWrapper);
        if(count>0){
            throw new CustomException("有菜品正在售卖，删除失败");
        }
        this.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishFlavorQueryWrapper);
        //TODO: 删除菜品图片
    }

}
