package com.yzx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yzx.reggie.common.R;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.entity.Dish;
import com.yzx.reggie.entity.DishFlavor;
import com.yzx.reggie.service.IDishFlavorService;
import com.yzx.reggie.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    private final IDishService dishService;

    private final IDishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    public DishController(IDishService dishService, IDishFlavorService dishFlavorService) {
        this.dishService = dishService;
        this.dishFlavorService = dishFlavorService;
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }

    @GetMapping("/page")
    public R<HashMap> getByPage(int page, int pageSize, String name) {
//        IPage<Dish> dishPage = new Page<>(page,pageSize);
//        IPage<DishDto> dishDtoIPage = new Page<>();
//        LambdaQueryWrapper<Dish> lwq = new LambdaQueryWrapper<>();
//        lwq.like(StringUtils.isNotBlank(name),Dish::getName,name)
//                .orderByDesc(Dish::getUpdateTime);
//        dishService.page(dishPage,lwq);
        HashMap map = dishService.getByPage(page, pageSize, name);
        return R.success(map);
    }

    @GetMapping("/{id}")
    public R<DishDto> getDishDetail(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavors(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    @PostMapping("/status/{status}")
    @Transactional
    public R<String> status(@PathVariable int status, @RequestParam List<Long> ids) {
        UpdateWrapper<Dish> uw = new UpdateWrapper<>();
        uw.in("id", ids)
                .set("status", status);
        dishService.update(new Dish(), uw);
//        LambdaQueryWrapper<>
        return R.success(status == 0 ? "停售成功" : "起售成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.removeWithFlavor(ids);
        return R.success("删除菜品成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //先从缓存中获取数据
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //缓存中存在则直接返回
        if (dishDtos != null) {
            return R.success(dishDtos);
        }
        //不存在则查询并缓存
        LambdaQueryWrapper<Dish> lwq = new LambdaQueryWrapper<>();
        lwq.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, 1)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(lwq);
        dishDtos = dishes.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
            List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key, dishDtos, 60, TimeUnit.MINUTES);
        return R.success(dishDtos);
    }
}
