package com.arman.armaNote.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {
	
	private final List<String> allowedOrigins = Arrays.asList("http://localhost:4200");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        String origin = request.getHeader("Origin");
        
        ArrayList<String> allowedHeaders = new ArrayList<>();
        allowedHeaders.add("http://localhost:4200");
        
        // We must have allowed headers, allowed origins in our response header otherwise browser gives error when we send set-credentials=true in
        // our request. Reason: since set-credentials makes browser to add cookie in browser sent by server, so it ensures of extra security by the server.

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, "
        		+ "Content-Type, Accept, X-CSRF-TOKEN, Access-Control-Allow-Headers");
        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "180");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
