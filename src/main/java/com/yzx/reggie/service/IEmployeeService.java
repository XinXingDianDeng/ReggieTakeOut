package com.yzx.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yzx.reggie.common.R;
import com.yzx.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface IEmployeeService extends IService<Employee> {
    R<Employee> login(Employee employee);

    R<String> save(HttpServletRequest request, Employee employee);

    IPage<Employee> selectByPage(Long currentPage, Long size, String name);
}
