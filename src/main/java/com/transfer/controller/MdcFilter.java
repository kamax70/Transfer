package com.transfer.controller;

import com.transfer.executor.mdc.MdcUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class MdcFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }


    //Set UUID for each request. Need for logback conf
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MdcUtil.setupRequestId(UUID.randomUUID().toString());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
