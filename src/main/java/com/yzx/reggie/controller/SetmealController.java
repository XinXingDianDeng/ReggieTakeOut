package com.yzx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yzx.reggie.common.R;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.dto.SetmealDto;
import com.yzx.reggie.entity.Category;
import com.yzx.reggie.entity.Dish;
import com.yzx.reggie.entity.Setmeal;
import com.yzx.reggie.entity.SetmealDish;
import com.yzx.reggie.service.ICategoryService;
import com.yzx.reggie.service.IDishService;
import com.yzx.reggie.service.ISetmealDishService;
import com.yzx.reggie.service.ISetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    private ISetmealService setmealService;

    private ISetmealDishService setmealDishService;

    private ICategoryService categoryService;

    @Autowired
    private IDishService dishService;

    @Autowired
    public void setCategoryService(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    public void setSetmealService(ISetmealService setmealService) {
        this.setmealService = setmealService;
    }

    @Autowired
    public void setSetmealDishService(ISetmealDishService setmealDishService) {
        this.setmealDishService = setmealDishService;
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        System.out.println(setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> getByPage(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lwq = new LambdaQueryWrapper<>();
        lwq.like(StringUtils.isNotBlank(name), Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, lwq);

        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> dtos = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(dtos);

        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status, @RequestParam List<Long> ids) {
        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", ids)
                .set("status", status);
        setmealService.update(new Setmeal(), updateWrapper);
        return R.success(status == 0 ? "停售成功" : "起售成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getDishes(@PathVariable Long id) {
        return R.success(setmealService.getWithDish(id));
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("更新套餐成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lwq = new LambdaQueryWrapper<>();
        lwq.eq(Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(lwq);
        return R.success(setmeals);
    }

    /*
    @GetMapping("/list")
    public R<List<SetmealDto>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lwq = new LambdaQueryWrapper<>();
        lwq.eq(Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(lwq);
        List<SetmealDto> setmealDtoList = setmeals.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SetmealDish::getSetmealId, item.getId()).orderByDesc(SetmealDish::getSort);
            List<SetmealDish> setmealDishes = setmealDishService.list(lambdaQueryWrapper);
            setmealDto.setSetmealDishes(setmealDishes);
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(setmealDtoList);
    }
    */

    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dishList(@PathVariable Long id) {
        List<DishDto> dishDtoList = setmealService.getSetmealDish(id);
        return R.success(dishDtoList);
    }
}
