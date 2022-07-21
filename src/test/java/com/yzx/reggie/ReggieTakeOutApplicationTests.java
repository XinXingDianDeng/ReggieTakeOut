package com.yzx.reggie;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.entity.Employee;
import com.yzx.reggie.mapper.DishMapper;
import com.yzx.reggie.mapper.EmployeeMapper;
import com.yzx.reggie.service.IEmployeeService;
import com.yzx.reggie.service.impl.EmployeeServiceImpl;
import com.yzx.reggie.utils.SMSUtils;
import com.yzx.reggie.utils.ValidateCodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class ReggieTakeOutApplicationTests {

    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DishMapper dishMapper;

//    @Autowired
//    private

    @Test
    void testPage() throws JsonProcessingException {
        Long current = 1L;
        Long size = 5L;
        String name = "李四";
        IPage<Employee> page = new Page<>(current, size);
        LambdaQueryWrapper<Employee> lwq = new LambdaQueryWrapper<>();
        lwq.like(StringUtils.isNotBlank(name), Employee::getName, name);
        IPage<Employee> iPage = employeeMapper.selectPage(page, lwq);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(iPage));

//        List<Employee> records = iPage.getRecords();
//        for (Employee record : records) {
//            System.out.println(record);
//        }
    }

    @Test
    void testGetByPage() {
        int page = 2;
        int pageSize = 10;
        List<DishDto> dishDtos = dishMapper.selectDishDtoByPage((page - 1) * pageSize, pageSize, "鸡");
        System.out.println(dishDtos);
    }

    @Test
    void testGetById(){
        DishDto dishDto = dishMapper.selectByIdWithFlavor(1546068531135709186L);
        System.out.println(dishDto);
    }

    @Test
    void testSMSSender() {
//        String regex = "${code}";
//        String regex = "\\$\\{code\\}";
        String s = "您正在进行登录操作，这是您的验证码: #{code}，五分钟之内有效。（验证码请勿泄露）";
//        String s1 = s.replace(regex, "3425");
//        String s1 = s.replaceAll(regex, "3425");
//        System.out.println(regex);
//        System.out.println(s1);
//        SMSUtils.sendMessage(s,"123456789@qq.com","345564");
//        System.out.println(SMSUtils.s);
        System.out.println(ValidateCodeUtils.generateValidateCode4String(4));
    }
}
