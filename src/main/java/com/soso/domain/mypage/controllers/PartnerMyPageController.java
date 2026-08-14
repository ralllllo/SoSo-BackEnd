package com.soso.domain.mypage.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.soso.domain.mypage.dto.PartnerProfileDTO;
import com.soso.domain.mypage.dto.PartnerUpdateDTO;
import com.soso.domain.mypage.dto.PartnerWithdrawalDTO;
import com.soso.domain.mypage.services.PartnerMyPageService;

import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/member/partner")
public class PartnerMyPageController {

    @Autowired
    private PartnerMyPageService myPageService;

    @GetMapping("/profile")
    public ResponseEntity<PartnerProfileDTO> getPartnerProfile(@RequestAttribute("user_seq") Long user_seq) {
        PartnerProfileDTO profile = myPageService.getPartnerProfile(user_seq);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updatePartnerProfile(
            @RequestAttribute("user_seq") Long userSeq,
            @RequestPart("updateData") String updateData,
            @RequestPart(value = "exteriorImg", required = false) MultipartFile exteriorImg,
            @RequestPart(value = "interiorImg", required = false) MultipartFile interiorImg) {

        Map<String, String> response = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PartnerUpdateDTO updateDto = objectMapper.readValue(updateData, PartnerUpdateDTO.class);
            updateDto.setUserSeq(userSeq.intValue());

            String result = myPageService.updatePartnerProfile(updateDto, exteriorImg, interiorImg);

            if(result.equals("duplNickname")) {
            	response.put("status", "duplNickname");
            	 response.put("message", "이미 사용 중인 닉네임입니다. 다시 입력해주세요");
            }
            if(result.equals("duplEmail")) {
            	response.put("status", "duplEmail");
            	 response.put("message", "이미 사용 중인 이메일입니다. 다시 입력해주세요");
            }
            if(result.equals("success")) {
            	response.put("status", "success");
            	 response.put("message", "업체 정보 수정이 완료되었습니다.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PatchMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdrawMember(
            @RequestAttribute("user_seq") Long userSeq,
            @RequestBody PartnerWithdrawalDTO withdrawalData) { 
        Map<String, String> response = new HashMap<>();
        try {
            withdrawalData.setUserSeq(userSeq.intValue());
            
            myPageService.withdrawMember(withdrawalData);
            
            response.put("status", "success");
            response.put("message", "회원 탈퇴가 정상적으로 처리되었습니다.");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "탈퇴 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
