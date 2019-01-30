package com.arman.armaNote.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// import com.arman.armaNote.config.JwtAuthenticationFilter;
// import com.arman.armaNote.model.Constants;

@Controller
public class LogoutController {
	// place this code somewhere else bcz /token in the beginning is not a good idea
    @RequestMapping(value = "/logmeout", method = RequestMethod.POST)
    public ResponseEntity<Boolean> logout(HttpServletRequest req, HttpServletResponse response) throws AuthenticationException {
    	
    	/*
    	String header = JwtAuthenticationFilter.getCookie(req, "Authorization") != null ? 
        					JwtAuthenticationFilter.getCookie(req, "Authorization").getValue() : null;        
        String authToken = null;
        if (header != null && header.startsWith(Constants.TOKEN_PREFIX)) {
        	authToken = header.replace(Constants.TOKEN_PREFIX,"");
        }
        */

        Cookie cookie = new Cookie("Authorization","");
        cookie.setMaxAge(0);
        cookie.setValue(null);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        
        ResponseEntity<Boolean> responseEntity = new ResponseEntity<Boolean>(true, HttpStatus.OK);
        return responseEntity;

    }
}
