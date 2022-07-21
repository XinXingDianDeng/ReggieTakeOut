package com.yzx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzx.reggie.common.CustomException;
import com.yzx.reggie.entity.Category;
import com.yzx.reggie.entity.Dish;
import com.yzx.reggie.entity.Setmeal;
import com.yzx.reggie.mapper.CategoryMapper;
import com.yzx.reggie.service.ICategoryService;
import com.yzx.reggie.service.IDishService;
import com.yzx.reggie.service.ISetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private final CategoryMapper categoryMapper;

    private final IDishService dishService;

    private final ISetmealService setmealService;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper, IDishService dishService, ISetmealService setmealService) {
        this.categoryMapper = categoryMapper;
        this.dishService = dishService;
        this.setmealService = setmealService;
    }

    @Override
    public IPage<Category> selectByPage(Long currentPage, Long size) {
        LambdaQueryWrapper<Category> lwq = new LambdaQueryWrapper<>();
        lwq.orderByAsc(Category::getSort);
        IPage<Category> page = new Page<>(currentPage, size);
        categoryMapper.selectPage(page, lwq);
        return page;
    }

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount > 0) {
            throw new CustomException("当前分类关联了菜品，无法删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setMealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setMealCount > 0) {
            throw new CustomException("当前分类关联了套餐，无法删除");
        }
        super.removeById(id);
    }

}
