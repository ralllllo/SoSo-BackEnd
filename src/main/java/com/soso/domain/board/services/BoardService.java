package com.soso.domain.board.services;

import com.soso.domain.board.dao.BoardDAO;
import com.soso.domain.board.dto.BoardInsertRequestDto;
import com.soso.domain.board.dto.BoardResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class BoardService {

    @Autowired
    private BoardDAO boardDAO;

    public List<BoardResponseDto> getBoardsByType(String boardType) {
        return boardDAO.getBoardsByType(boardType);
    }

    public List<BoardResponseDto> getMyInquiries(int userSeq) {
        return boardDAO.getMyInquiries(userSeq);
    }

    @Transactional
    public boolean registerInquiry(BoardInsertRequestDto dto) {
        return boardDAO.insertInquiry(dto) > 0;
    }
}