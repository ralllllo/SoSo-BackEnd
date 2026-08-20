package com.soso.domain.account.controllers;

import com.soso.domain.account.dto.AccountRelationRequestDto;
import com.soso.domain.account.dto.AccountRelationResponseDto;
import com.soso.domain.account.dto.AccountSearchResponseDto;
import com.soso.domain.account.dto.ItemResponseDto;
import com.soso.domain.account.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/account")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPartnerStores(@RequestParam("searchTerm") String searchTerm) {
        logger.info("거래처 검색 요청: {}", searchTerm);
        
        List<AccountSearchResponseDto> results = accountService.searchAccounts(searchTerm);
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("count", results.size());
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/all-partners")
    public ResponseEntity<Map<String, Object>> getAllPartnerStores(
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "district", required = false) String district) {
        logger.info("모든 거래처 조회 요청: searchTerm={}, city={}, district={}", searchTerm, city, district);
        
        List<AccountSearchResponseDto> results = accountService.getAllPartnerStores(searchTerm, city, district);
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("count", results.size());
        
        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerAccount(@RequestBody AccountRelationRequestDto relationDto) {
        logger.info("거래처 관계 등록 요청: businessSeq={}, partnerSeq={}", 
                relationDto.getBusinessSeq(), relationDto.getPartnerSeq());
        
        boolean success = accountService.registerAccount(relationDto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", success ? "success" : "fail");
        response.put("message", success ? "거래처가 성공적으로 등록되었습니다." : "거래처 등록에 실패했습니다.");
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getRegisteredAccounts(
            @RequestParam("businessSeq") int businessSeq,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "district", required = false) String district) {
        logger.info("등록된 거래처 목록 조회 요청: businessSeq={}, searchTerm={}, city={}, district={}", 
                businessSeq, searchTerm, city, district);
        
        List<AccountRelationResponseDto> results = accountService.getRegisteredAccounts(businessSeq, searchTerm, city, district);
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("count", results.size());
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getPartnerItems(@RequestParam("partnerSeq") int partnerSeq) {
        logger.info("거래처 품목 조회 요청: partnerSeq={}", partnerSeq);
        
        List<ItemResponseDto> results = accountService.getPartnerItems(partnerSeq);
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("count", results.size());
        
        return ResponseEntity.ok(response);
    }

    
    @DeleteMapping("/{relationSeq}")
    public ResponseEntity<Map<String, Object>> deleteAccount(@PathVariable("relationSeq") int relationSeq) {
        logger.info("거래처 관계 삭제 요청: relationSeq={}", relationSeq);
        
        boolean success = accountService.deleteAccount(relationSeq);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", success ? "success" : "fail");
        response.put("message", success ? "거래처가 성공적으로 삭제되었습니다." : "거래처 삭제에 실패했습니다.");
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/first-store/{userSeq}")
    public ResponseEntity<Map<String, Object>> getFirstStoreSeq(@PathVariable("userSeq") int userSeq) {
        logger.info("첫 번째 매장 시퀀스 조회 요청: userSeq={}", userSeq);
        
        Integer storeSeq = accountService.getFirstStoreSeqByUserSeq(userSeq);
        
        Map<String, Object> response = new HashMap<>();
        response.put("storeSeq", storeSeq);
        
        return ResponseEntity.ok(response);
    }

    
 
    @GetMapping("/my-partners")
    public ResponseEntity<List<AccountSearchResponseDto>> myPartners(
            @RequestParam Long storeSeq
    ) {
        List<AccountSearchResponseDto> list = accountService.myPartners(storeSeq);
        return ResponseEntity.ok(list);
    }
     
    
    @GetMapping("/partner/{partnerSeq}")
    public ResponseEntity<AccountSearchResponseDto> getPartnerDetail(@PathVariable("partnerSeq") int partnerSeq) {
        logger.info("거래처 상세 정보 조회 요청: partnerSeq={}", partnerSeq);
        
        AccountSearchResponseDto result = accountService.getPartnerDetail(partnerSeq);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);

    }
}
