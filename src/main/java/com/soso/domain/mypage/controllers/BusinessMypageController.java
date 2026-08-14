package com.soso.domain.mypage.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.soso.domain.mypage.dto.BusinessMultiProfileDTO;
import com.soso.domain.mypage.dto.BusinessMypageDTO;
import com.soso.domain.mypage.dto.BusinessUpdateDTO;
import com.soso.domain.mypage.dto.BusinessNotificationSettingsDTO;
import com.soso.domain.mypage.services.BusinessMypageService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/member/business")
public class BusinessMypageController {

    @Autowired
    private BusinessMypageService businessMypageService;

    @GetMapping("/profile")
    public ResponseEntity<BusinessMypageDTO> getBusinessProfile(
            @RequestAttribute("user_seq") Long user_seq,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long storeSeq) {
        
        
        BusinessMypageDTO profile = businessMypageService.getBusinessMypage(user_seq, storeSeq);
        
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    
    @GetMapping("/stores")
    public ResponseEntity<java.util.List<BusinessMypageDTO>> getAllStores(@RequestAttribute("user_seq") Long user_seq) {
        java.util.List<BusinessMypageDTO> stores = businessMypageService.getAllStores(user_seq);
        return ResponseEntity.ok(stores);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateBusinessProfile(
            @RequestAttribute("user_seq") Long userSeq,
            @ModelAttribute BusinessUpdateDTO updateDto) {

        Map<String, String> response = new HashMap<>();
        try {
            updateDto.setUserSeq(userSeq);
            String result = businessMypageService.updateBusinessProfile(updateDto);

            if ("duplNickname".equals(result)) {
                response.put("status", "duplNickname");
                response.put("message", "이미 사용 중인 닉네임입니다. 다시 입력해주세요");
            } else if ("duplEmail".equals(result)) {
                response.put("status", "duplEmail");
                response.put("message", "이미 사용 중인 이메일입니다. 다시 입력해주세요");
            } else if ("success".equals(result)) {
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

    
    @PostMapping("/multiprofile/register")
    public ResponseEntity<Map<String, String>> registerMultiProfile(
            @RequestAttribute("user_seq") Long userSeq,
            @RequestPart("registerData") String registerData,
            @RequestPart(value = "exteriorImg", required = false) MultipartFile exteriorImg,
            @RequestPart(value = "interiorImg", required = false) MultipartFile interiorImg) {

        Map<String, String> response = new HashMap<>();
        try {
            
            ObjectMapper objectMapper = new ObjectMapper();
            BusinessMultiProfileDTO registerDto = objectMapper.readValue(registerData, BusinessMultiProfileDTO.class);
            
            
            registerDto.setUserSeq(userSeq);

            
            String result = businessMypageService.registerMultiProfile(registerDto, exteriorImg, interiorImg);

            
            if ("success".equals(result)) {
                response.put("status", "success");
                response.put("message", "새로운 매장이 성공적으로 등록되었습니다.");
            } else if ("invalidBizInfo".equals(result)) {
                response.put("status", "error");
                response.put("message", "사업자 정보가 유효하지 않거나 이미 등록된 번호입니다.");
            } else {
                response.put("status", "error");
                response.put("message", "매장 등록에 실패했습니다.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    
    @GetMapping("/notification-settings")
    public ResponseEntity<BusinessNotificationSettingsDTO> getNotificationSettings(
            @RequestAttribute("user_seq") Long userSeq,
            @RequestParam Long storeSeq) {
        
        BusinessNotificationSettingsDTO settings = businessMypageService.getNotificationSettings(userSeq, storeSeq);
        return ResponseEntity.ok(settings);
    }

    
    @PutMapping("/notification-settings")
    public ResponseEntity<Map<String, String>> updateNotificationSettings(
            @RequestAttribute("user_seq") Long userSeq,
            @RequestParam Long storeSeq,
            @RequestBody BusinessNotificationSettingsDTO settingsDto) {
        
        Map<String, String> response = new HashMap<>();
        try {
            businessMypageService.updateNotificationSettings(userSeq, storeSeq, settingsDto);
            response.put("status", "success");
            response.put("message", "알림 설정이 성공적으로 저장되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "알림 설정 저장 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
