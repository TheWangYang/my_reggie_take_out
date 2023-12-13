package com.thewangyang.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thewangyang.reggie.common.R;
import com.thewangyang.reggie.entity.Employee;
import com.thewangyang.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;


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


    /**
     * 退出系统方法
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest){

        // 清理session中的用户id
        httpServletRequest.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }


    /**
     * 新增员工方法
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest httpServletRequest,
                          @RequestBody Employee employee){

        log.info("新增员工操作，员工信息{}", employee.toString());

        // 给新增员工设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("12345".getBytes()));

        // 设置用户创建时间
        employee.setCreateTime(LocalDateTime.now());
        // 设置用户更新时间
        employee.setUpdateTime(LocalDateTime.now());

        // 获得当前用户登录的id
        Long empId = (Long) httpServletRequest.getSession().getAttribute("employee");

        // 给当前新增用户设置用户和更新用户
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        // 保存当前用户
        employeeService.save(employee);

        // 返回更新成功信息
        return R.success("新增员工成功");
    }

    /**
     * 员工信息的分页查询方法
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page: {}, pageSize: {}, name: {}", page, pageSize, name);

        // 创建分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);

        // 添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest httpServletRequest,
                            @RequestBody Employee employee){


        // 得到session中存储的empId
        Long empId = (Long)httpServletRequest.getSession().getAttribute("employee");

        // 更新用户信息修改时间
        employee.setUpdateTime(LocalDateTime.now());

        employee.setUpdateUser(empId);

        // 调用mapper更新数据库
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }


    @GetMapping("/{id}")
    public R<Employee> getById(HttpServletRequest httpServletRequest, @PathVariable Long id){
        log.info("根据id查询用户");

        Employee employee = employeeService.getById(id);

        if(employee != null){
            return R.success(employee);
        }

        return R.error("没有查询到员工信息");
    }
}
