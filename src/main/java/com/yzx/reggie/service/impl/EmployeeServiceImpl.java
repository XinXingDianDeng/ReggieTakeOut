package com.yzx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzx.reggie.common.R;
import com.yzx.reggie.entity.Employee;
import com.yzx.reggie.mapper.EmployeeMapper;
import com.yzx.reggie.service.IEmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
//@Transactional
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    public R<Employee> login(Employee employee) {
        //将密码用MD5编码
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //从数据库获取员工数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee employee1 = employeeMapper.selectOne(queryWrapper);
        if(employee1==null || !employee1.getPassword().equals(password)){
            return R.error("登录失败，账号或密码错误");
        }
        if (employee1.getStatus()==0){
            return R.error("账号已禁用");
        }
        return R.success(employee1);
    }

    @Override
    public R<String> save(HttpServletRequest request, Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        Long empid = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empid);
//        employee.setUpdateUser(empid);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        employeeMapper.insert(employee);
        return R.success("新增员工成功");
    }

    @Override
    public IPage<Employee> selectByPage(Long currentPage, Long size, String name) {
        IPage<Employee> page = new Page<>(currentPage, size);
        LambdaQueryWrapper<Employee> lwq = new LambdaQueryWrapper<>();
        lwq.like(StringUtils.isNoneBlank(name), Employee::getName, name)
                .orderByDesc(Employee::getUpdateTime);
        employeeMapper.selectPage(page, lwq);
        return page;
    }


}
