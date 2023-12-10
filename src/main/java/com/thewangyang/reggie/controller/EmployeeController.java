package com.thewangyang.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thewangyang.reggie.common.R;
import com.thewangyang.reggie.entity.Employee;
import com.thewangyang.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


// 设置的雇员controller类
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    // 创建service对象
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录操作
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest httpServletRequest,
                             @RequestBody Employee employee){

        // 得到密码
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 查询数据库
        // 封装查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());

        // 得到数据库查询的结果对象
        Employee emp = employeeService.getOne(queryWrapper);

        // 判断是否为空
        if(emp == null){
            return R.error("登录失败，查询用户为空");
        }

        // 密码比对
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }

        if(emp.getStatus() == 0){
            return R.error("登录失败，账号禁用");
        }

        httpServletRequest.getSession().setAttribute("employee", emp.getId());


        return R.success(emp);
    }


    // 退出系统方法
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest){

        // 清理session中的用户id
        httpServletRequest.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

}
