package com.yzx.reggie.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yzx.reggie.common.R;
import com.yzx.reggie.entity.Employee;
import com.yzx.reggie.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/employee")
public class EmployeeController {


    private final IEmployeeService employeeService;

    @Autowired
    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * 登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        R<Employee> r = employeeService.login(employee);
        if (r.getCode() == 1) {
            request.getSession().setAttribute("employee", r.getData().getId());
        }
        return r;
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.save(request, employee);
    }

    /**
     * 分页查询员工信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<IPage<Employee>> getByPage(Long page, Long pageSize, String name) {
        IPage<Employee> iPage = employeeService.selectByPage(page, pageSize, name);
        return R.success(iPage);
    }

    /**
     * 更新员工信息
     * @param session
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpSession session, @RequestBody Employee employee) {
//        Long empid = (Long) session.getAttribute("employee");
//        employee.setUpdateUser(empid);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 按id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }
}
