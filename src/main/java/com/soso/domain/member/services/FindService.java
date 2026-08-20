package com.soso.domain.member.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.soso.domain.member.dao.FindDAO;
import com.soso.domain.member.dto.FindDTO;

@Service
public class FindService {
	
	@Autowired
	private FindDAO FindDAO;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	private Map<String, String> codeStorage = new HashMap<>();
	
	public boolean findId(FindDTO dto) {
		
		int count = FindDAO.findId(dto);
		
		if(count == 0) {
			return false;
		}
		
		
		String code = String.valueOf((int)(Math.random() * 900000) + 100000);
		
		
		codeStorage.put(dto.getEmail(), code);
		
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(dto.getEmail());
		message.setSubject("[SoSo] 아이디 찾기 인증번호");
		message.setText("인증번호는 " + code + " 입니다.");

		mailSender.send(message);
		
		return true;
	}
	
	
	public String checkCode(FindDTO dto) {
		
		
		String savedCode = codeStorage.get(dto.getEmail());
		
		if(savedCode == null) {
			return null;
		}
		
		if(!savedCode.equals(dto.getCode())) {
			return null;
		}
		
		
		String foundId = FindDAO.getIdByEmail(dto.getEmail());
		
		return foundId;
	}
	
	
	public boolean updatePassword(FindDTO dto) {
	    
	
	   String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

	
	dto.setNewPassword(encodedPassword);

	
	int result = FindDAO.updatePassword(dto);
	    
	
	return result > 0;
	}
	
	
	public boolean findPw(FindDTO dto) {
		
		int count = FindDAO.findPw(dto);
		
		if(count == 0) {
			return false;
		}
		
		
		String code = String.valueOf((int)(Math.random() * 900000) + 100000);
		
		
		codeStorage.put(dto.getEmail(), code);
		
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(dto.getEmail());
		message.setSubject("[SoSo] 비밀번호 찾기 인증번호");
		message.setText("인증번호는 " + code + " 입니다.");

		mailSender.send(message);
		
		return true;
	}
	
	
	public boolean checkCodeForPassword(FindDTO dto) {
		
		
		String savedCode = codeStorage.get(dto.getEmail());
		
		if(savedCode == null) {
			return false;
		}
		
		if(!savedCode.equals(dto.getCode())) {
			return false;
		}
		
		return true;
	}

}
