package com.soso.global.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.soso.global.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenValidator implements HandlerInterceptor{
	
	@Autowired
	private JWTUtil jwt;
	
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception{
		
		
		
		if(request.getMethod().equalsIgnoreCase("OPTIONS")) {
			response.setStatus(HttpServletResponse.SC_OK);
			return true;
		}
		
		
	    String path = request.getRequestURI();

	    if(path.startsWith("/ai/")) {
	        return true;
	    }
		
		
		
		String authHeader = request.getHeader("Authorization");
		
		
		if(authHeader != null && authHeader.startsWith("Bearer")) { 
			String token = authHeader.substring(7); 
 
			
			try {
				Long user_seq = jwt.getUserSeq(token); 
				String user_type = jwt.getUserType(token);
				request.setAttribute("user_seq", user_seq);
				request.setAttribute("userSeq", user_seq); 
				request.setAttribute("user_type", user_type);
				request.setAttribute("userType", user_type); 
				return true;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		return false; 
	}

}
