package com.yzx.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yzx.reggie.entity.Category;

public interface ICategoryService extends IService<Category> {
    IPage<Category> selectByPage(Long currentPage, Long size);

    void remove(Long id);
}
