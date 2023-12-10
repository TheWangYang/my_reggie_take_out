package com.thewangyang.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.thewangyang.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 创建用户登录过滤器类
@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    // 定义路径匹配器对象，支持通配符匹配
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 实现doFilter方法
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 得到httpServletRequest对象
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        log.info("拦截到请求： {}", httpServletRequest.getRequestURI());

        // 获得本次请求的uri
        String requestURI = httpServletRequest.getRequestURI();

        // 定于不需要拦截的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        // 检查请求是否匹配上了
        boolean isCheckPath = checkPath(urls, requestURI);

        if(isCheckPath){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }


        // 从当前登录状态获得信息，如果可以获得用户登录信息，那么直接放行
        if(httpServletRequest.getSession().getAttribute("employee") != null){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }


        // 如果都没有登录，那么向客户端发送未登录消息
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }


    /**
     * 设置检查路径是否一致的函数
     * @param requestURI
     * @return
     */
    public boolean checkPath(String[] urls, String requestURI){
        for (String url: urls){
            boolean match = PATH_MATCHER.match(url, requestURI);

            if(match){
                return true;
            }
        }

        return false;
    }
}
