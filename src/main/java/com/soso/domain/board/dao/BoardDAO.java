package com.soso.domain.board.dao;

import com.soso.domain.board.dto.BoardInsertRequestDto;
import com.soso.domain.board.dto.BoardResponseDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class BoardDAO {

    @Autowired
    private SqlSessionTemplate mybatis;

    private static final String NAMESPACE = "com.soso.domain.board.dao.BoardDAO";

    
    public List<BoardResponseDto> getBoardsByType(String boardType) {
        return mybatis.selectList(NAMESPACE + ".getBoardsByType", boardType);
    }

    
    public List<BoardResponseDto> getMyInquiries(int userSeq) {
        return mybatis.selectList(NAMESPACE + ".getMyInquiries", userSeq);
    }

    
    public int insertInquiry(BoardInsertRequestDto dto) {
        return mybatis.insert(NAMESPACE + ".insertInquiry", dto);
    }
}