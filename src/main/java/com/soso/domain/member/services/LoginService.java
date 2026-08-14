package com.soso.domain.member.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.soso.domain.member.dao.LoginDAO;
import com.soso.domain.member.dto.LoginDTO;

@Service
public class LoginService {
	
	@Autowired
	private LoginDAO LoginDAO;
	
	@Autowired
	private PasswordEncoder PasswordEncoder;
	
	public Map<String, Object> toLogin(LoginDTO dto) {
		
		
		Map<String, Object> member = LoginDAO.toLogin(dto);
		
		Map<String, Object> resultMap = new HashMap<>();
		
		if(member == null) {
			return null;
		}
		
		
		String DBPassword = (String)member.get("password");
		
		
		String status = (String)member.get("status");
		if ("WITHDRAWN".equals(status)) {
			
			resultMap.put("status", "isWithDraw");
			resultMap.put("message", "탈퇴 처리된 계정입니다. 해당 계정으로는 로그인할 수 없습니다.");
			return resultMap; 
		}
		
		
		boolean isMatch = PasswordEncoder.matches(dto.getPw(), DBPassword);
		
		
		if(!isMatch) {
			return null;
		}
		
		
		return member;
	}
	public List<Integer> getStoreListByUserSeq(Long userSeq) {
	    return LoginDAO.getStoreListByUserSeq(userSeq);
	}
}
