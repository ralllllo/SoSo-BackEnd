package com.soso.domain.member.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soso.domain.member.services.BizValidationService;

@RestController
@RequestMapping("/api/biz")

public class BizApiController {

    private final BizValidationService bizValidationService;

    public BizApiController(BizValidationService bizValidationService) {
        this.bizValidationService = bizValidationService;
    }
    @GetMapping("/check")
    public ResponseEntity<String> checkBusiness(
            @RequestParam String bNo,
            @RequestParam String startDt,
            @RequestParam String pNm,
            @RequestParam String bNm,
            @RequestParam(required = false, defaultValue = "false") boolean isMultiProfile) {

        
        boolean isValid = bizValidationService.validateBusiness(bNo, startDt, pNm, bNm, isMultiProfile);

        if (isValid) {
            return ResponseEntity.ok("인증 성공: 정상적인 사업자 정보입니다.");
        } else {
            return ResponseEntity.badRequest().body("인증 실패: 국세청 정보와 일치하지 않거나 유효하지 않은 사업자입니다.");
        }
    }
}