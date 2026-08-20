package com.soso.domain.board.controllers;

import com.soso.domain.board.dto.BoardInsertRequestDto;
import com.soso.domain.board.dto.BoardResponseDto;
import com.soso.domain.board.services.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBoardsByType(@RequestParam("type") String type) {
        logger.info("게시판 목록 조회 요청: type={}", type);
        
        List<BoardResponseDto> results = boardService.getBoardsByType(type);
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("count", results.size());
        
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/my-inquiries")
    public ResponseEntity<Map<String, Object>> getMyInquiries(@RequestParam("userSeq") int userSeq) {
        logger.info("내 문의 목록 조회 요청: userSeq={}", userSeq);
        
        List<BoardResponseDto> results = boardService.getMyInquiries(userSeq);
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("count", results.size());
        
        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/inquiry")
    public ResponseEntity<Map<String, Object>> submitInquiry(@RequestBody BoardInsertRequestDto dto) {
        logger.info("1:1 문의 등록 요청: userSeq={}, csType={}", dto.getUserSeq(), dto.getCsType());
        
        boolean success = boardService.registerInquiry(dto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", success ? "success" : "fail");
        response.put("message", success ? "문의가 성공적으로 접수되었습니다." : "문의 접수에 실패했습니다.");
        
        return ResponseEntity.ok(response);
    }
}