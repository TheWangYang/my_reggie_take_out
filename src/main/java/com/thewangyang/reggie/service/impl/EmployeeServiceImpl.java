package com.thewangyang.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thewangyang.reggie.entity.Employee;
import com.thewangyang.reggie.mapper.EmployeeMapper;
import com.thewangyang.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

// 雇员的接口实现类
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
