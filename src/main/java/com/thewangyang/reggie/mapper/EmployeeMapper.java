package com.thewangyang.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thewangyang.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

// 创建Employee员工映射类
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {


}
