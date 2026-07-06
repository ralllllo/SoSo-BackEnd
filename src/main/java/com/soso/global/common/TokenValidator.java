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
	
	// 컨트롤러 실행 전에 자동으로 실행됨.
	// 여기서 클라이언트로 돌아갈지, 컨트롤러로 돌아갈지 결정함
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception{
		
		// CORS 때문에 브라우저가 보내는 사전 요청은 그냥 통과시킴
		// 요청메서드가 options이면 통과 시켜주기
		if(request.getMethod().equalsIgnoreCase("OPTIONS")) {
			response.setStatus(HttpServletResponse.SC_OK);
			return true;
		}
		
		// RAG 테스트 API는 토큰 검사 없이 통과
	    String path = request.getRequestURI();

	    if(path.startsWith("/ai/")) {
	        return true;
	    }
		
		// 프론트에서 보낸 헤더를 꺼냄
		// 예) Authorization: Bearer eyJhbGciOi...
		String authHeader = request.getHeader("Authorization");
		// 토큰이 있는지 확인
		// authHeader.startsWith("Bearer ") 공백까지 확인하는 게 안전
		if(authHeader != null && authHeader.startsWith("Bearer")) { // 정상적인 토큰이 들어왔다면
			String token = authHeader.substring(7); // Bearer 7글자를 잘라내고 실제 토큰만 꺼냄
 // 토큰이 잘 넘어오는지 확인
			
			try {
				Long user_seq = jwt.getUserSeq(token); // 토큰이 정상이라면 seq를 request에 저장하고 컨트롤러로 보냄
				String user_type = jwt.getUserType(token);
				request.setAttribute("user_seq", user_seq);
				request.setAttribute("userSeq", user_seq); // groupbuy 등에서 사용하는 이름으로도 세팅
				request.setAttribute("user_type", user_type);
				request.setAttribute("userType", user_type); // groupbuy 등에서 사용하는 이름으로 세팅
				return true;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		return false; // 토큰이 애초에 없거나 Bearer로 시작하지 않는다면 false, 다시 돌려보냄 
	}

}
