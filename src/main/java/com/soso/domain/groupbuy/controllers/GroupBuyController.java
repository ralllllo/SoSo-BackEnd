package com.soso.domain.groupbuy.controllers;

import com.soso.domain.groupbuy.dto.GroupBuyDTO;
import com.soso.domain.groupbuy.dto.ParticipantInfoDTO;
import com.soso.domain.groupbuy.services.GroupBuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/group-buys")
public class GroupBuyController {

    @Autowired
    private GroupBuyService groupBuyService;

    
    @PostMapping
    public ResponseEntity<?> createGroupBuy(@RequestBody GroupBuyDTO groupBuyDTO,
                                            @RequestAttribute("userSeq") Integer userSeq,
                                            @RequestAttribute("userType") String userType) {
        
        groupBuyDTO.setUserSeq(userSeq);
        
        groupBuyDTO.setCreatorType(userType);
        
        groupBuyService.createGroupBuy(groupBuyDTO);
        
        return ResponseEntity.ok().body(Map.of("message", "공동구매가 성공적으로 등록되었습니다."));
    }

    
    @GetMapping
    public ResponseEntity<List<GroupBuyDTO>> getGroupBuys(
            @RequestParam(required = false) String filter,
            @RequestAttribute("userType") String userType,
            @RequestAttribute("userSeq") Integer userSeq) {
        
        List<GroupBuyDTO> list = groupBuyService.getGroupBuys(userType, userSeq, filter);
        return ResponseEntity.ok(list);
    }

    
    @GetMapping("/participated")
    public ResponseEntity<List<GroupBuyDTO>> getMyParticipatedGroups(
            @RequestAttribute("userSeq") Integer userSeq) {
        
        List<GroupBuyDTO> list = groupBuyService.getMyParticipatedGroups(userSeq);
        return ResponseEntity.ok(list);
    }

    
    @GetMapping("/completed")
    public ResponseEntity<List<GroupBuyDTO>> getMyCompletedGroups(
            @RequestAttribute("userSeq") Integer userSeq) {
        
        List<GroupBuyDTO> list = groupBuyService.getMyCompletedGroups(userSeq);
        return ResponseEntity.ok(list);
    }

    
    @GetMapping("/created")
    public ResponseEntity<List<GroupBuyDTO>> getMyCreatedGroups(
            @RequestAttribute("userSeq") Integer userSeq) {
        
        List<GroupBuyDTO> list = groupBuyService.getMyCreatedGroups(userSeq);
        return ResponseEntity.ok(list);
    }

    
    @GetMapping("/participated/count")
    public ResponseEntity<Map<String, Integer>> getMyParticipatedGroupsCount(
            @RequestAttribute("userSeq") Integer userSeq) {
        
        int count = groupBuyService.getMyParticipatedGroupsCount(userSeq);
        return ResponseEntity.ok(Map.of("count", count));
    }

    
    @GetMapping("/created/count")
    public ResponseEntity<Map<String, Integer>> getMyCreatedGroupsCount(
            @RequestAttribute("userSeq") Integer userSeq) {
        
        int count = groupBuyService.getMyCreatedGroupsCount(userSeq);
        return ResponseEntity.ok(Map.of("count", count));
    }

    
    @GetMapping("/completed/count")
    public ResponseEntity<Map<String, Integer>> getCompletedGroupBuysCount(
            @RequestAttribute("userSeq") Integer userSeq) {
        
        int count = groupBuyService.getCompletedGroupBuysCount(userSeq);
        return ResponseEntity.ok(Map.of("count", count));
    }

    
    @GetMapping("/{groupBuySeq}")
    public ResponseEntity<GroupBuyDTO> getGroupBuyDetail(@PathVariable("groupBuySeq") int groupBuySeq) {
        GroupBuyDTO detail = groupBuyService.getGroupBuyDetail(groupBuySeq);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    
    @PostMapping("/{groupBuySeq}/join")
    public ResponseEntity<?> joinGroupBuy(@PathVariable("groupBuySeq") int groupBuySeq,
                                          @RequestAttribute("userSeq") Integer userSeq) {
        try {
            groupBuyService.joinGroupBuy(groupBuySeq, userSeq);
            return ResponseEntity.ok().body(Map.of("message", "공동구매 참여가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    
    @PatchMapping("/{groupBuySeq}/status")
    public ResponseEntity<?> updateGroupBuyStatus(@PathVariable("groupBuySeq") int groupBuySeq,
                                                  @RequestBody Map<String, String> requestBody,
                                                  @RequestAttribute("userSeq") Integer userSeq) {

        String status = requestBody.get("status");
        try {
            groupBuyService.updateGroupBuyStatus(groupBuySeq, status, userSeq);
            return ResponseEntity.ok().body(Map.of("message", "상태가 성공적으로 변경되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    
    @GetMapping("/{groupBuySeq}/participants")
    public ResponseEntity<?> getParticipants(@PathVariable("groupBuySeq") int groupBuySeq,
                                             @RequestAttribute("userType") String userType) {
        if (!"PARTNER".equals(userType)) {
            return ResponseEntity.status(403).body(Map.of("error", "거래처 권한이 필요합니다."));
        }
        
        List<ParticipantInfoDTO> participants = groupBuyService.getParticipants(groupBuySeq);
        return ResponseEntity.ok(participants);
    }
    @GetMapping("/history")
    public ResponseEntity<List<GroupBuyDTO>> getGroupBuyHistory(
            HttpServletRequest request,
            @RequestParam(required = false) Integer storeSeq) {
        Long userSeqLong = (Long) request.getAttribute("user_seq");
        if (userSeqLong == null) {
            return ResponseEntity.status(401).build();
        }
        int userSeq = userSeqLong.intValue();
        
        List<GroupBuyDTO> list = groupBuyService.getGroupBuyListByUser(userSeq, storeSeq);
        return ResponseEntity.ok(list);
    }
}
