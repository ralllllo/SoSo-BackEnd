package com.soso.domain.file.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.soso.domain.file.dto.FileSaveDto;

@Repository
public class FileDAO {
	@Autowired
    private SqlSessionTemplate sqlSession;

    private static final String NAMESPACE = "com.soso.domain.file.repository.FileRepository";

    public int insertFile(FileSaveDto fileSaveDto) {
        return sqlSession.insert(NAMESPACE + ".insertFile", fileSaveDto);
    }

    public java.util.List<com.soso.domain.file.dto.FileSaveDto> getFilesByUserAndCategory(Integer userSeq, String category) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("userSeq", userSeq);
        params.put("category", category);
        return sqlSession.selectList(NAMESPACE + ".getFilesByUserAndCategory", params);
    }

    
    public java.util.List<com.soso.domain.file.dto.FileSaveDto> getFilesByBoardSeqAndCategory(Integer boardSeq, String category) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("boardSeq", boardSeq);
        params.put("category", category);
        return sqlSession.selectList(NAMESPACE + ".getFilesByBoardSeqAndCategory", params);
    }

    public int deleteFile(String sysname) {
        return sqlSession.delete(NAMESPACE + ".deleteFile", sysname);
    }
}
