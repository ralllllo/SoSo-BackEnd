package com.soso.domain.member.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soso.domain.member.dto.LoginDTO;
import com.soso.domain.member.services.LoginService;
import com.soso.global.util.JWTUtil;
import com.soso.global.util.RedisService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private JWTUtil jwt;
	
	@Autowired
	private LoginService LoginServ;

	@Autowired
	private RedisService redisService;
	
	@Value("${jwt.refresh-expiration}")
	private Long refreshExpiration;
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> toLogin(@RequestBody LoginDTO dto, HttpServletResponse response) {
		
		
		
	    
	    
	    Map<String, Object> member = LoginServ.toLogin(dto);
	    
		
		if(member != null) { 
			
			if ("isWithDraw".equals(member.get("status"))) {
				return ResponseEntity.ok(member);
			}

			
			Map<String, Object> result = new HashMap<>();
			
			
			
			Long userSeq = Long.parseLong(String.valueOf(member.get("user_seq")));
			String userType = String.valueOf(member.get("user_type"));
			String token = jwt.createToken(userSeq, userType);
			
			
			String refreshToken = jwt.createRefreshToken(userSeq, userType);
			redisService.setValuesWithTimeout("RT:" + userSeq, refreshToken, refreshExpiration);
			
			
			ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
					.maxAge(refreshExpiration / 1000)
					.path("/")
					.secure(true) 
					.sameSite("Strict")
					.httpOnly(true)
					.build();
			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
			
			
			member.remove("password");
			
			
			result.put("token", token); 
			result.put("user_seq", userSeq); 
			result.put("user_type", member.get("user_type")); 
			result.put("user_nickname", member.get("nickname"));
			result.put("company_name", member.get("company_name"));
			
			List<Integer> storeList = LoginServ.getStoreListByUserSeq(userSeq);
			if (storeList != null && !storeList.isEmpty()) {
				result.put("selectedStoreSeq", storeList.get(0));
			}
			
			
			return ResponseEntity.ok(result);
		}
		
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	@PostMapping("/reissue")
	public ResponseEntity<Map<String, Object>> reissue(
			@CookieValue(value = "refreshToken", required = false) String refreshToken,
			HttpServletResponse response) {
		
		if (refreshToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			
			jwt.validation(refreshToken);
			
			Long userSeq = jwt.getUserSeq(refreshToken);
			String userType = jwt.getUserType(refreshToken);
			
			
			String savedToken = redisService.getValues("RT:" + userSeq);
			if (savedToken == null || !savedToken.equals(refreshToken)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			
			
			String newAccessToken = jwt.createToken(userSeq, userType);
			
			
			String newRefreshToken = jwt.createRefreshToken(userSeq, userType);
			redisService.setValuesWithTimeout("RT:" + userSeq, newRefreshToken, refreshExpiration);
			
			
			ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
					.maxAge(refreshExpiration / 1000)
					.path("/")
					.secure(true) 
					.sameSite("Strict")
					.httpOnly(true)
					.build();
			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
			
			Map<String, Object> result = new HashMap<>();
			result.put("token", newAccessToken);
			
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			try {
				String token = authHeader.substring(7);
				Long userSeq = jwt.getUserSeq(token);
				
				
				redisService.deleteValues("RT:" + userSeq);
			} catch (Exception e) {
				
			}
		}
		
		
		ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
				.maxAge(0)
				.path("/")
				.secure(true)
				.sameSite("Strict")
				.httpOnly(true)
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		
		return ResponseEntity.ok().build();
	}
}
