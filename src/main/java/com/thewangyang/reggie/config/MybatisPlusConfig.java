package com.thewangyang.reggie.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlusConfig配置类
 * 主要用于配置mybatis-plus的分页插件
 */
@Slf4j
@Configuration
public class MybatisPlusConfig {

    // 配置mybatis-plus拦截器
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        log.info("进入MybatisPlusInterceptor拦截器");

        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());

       return mybatisPlusInterceptor;
    }

}
