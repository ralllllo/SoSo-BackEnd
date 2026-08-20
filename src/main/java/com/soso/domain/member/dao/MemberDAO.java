package com.soso.domain.member.dao;

import com.soso.domain.member.dto.SignUpDto;
import com.soso.domain.file.dto.FileSaveDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class MemberDAO {

    @Autowired
    private SqlSessionTemplate mybatis;

    private static final String NAMESPACE = "com.soso.domain.member.dao.MemberDAO";

    
    public int insertUser(SignUpDto signUpDto) {
        return mybatis.insert(NAMESPACE + ".insertUser", signUpDto);
    }

    
    public int insertStore(SignUpDto signUpDto) {
        return mybatis.insert(NAMESPACE + ".insertStore", signUpDto);
    }

  
    
    public int deleteStoresByKey(int userSeq) {
        return mybatis.delete(NAMESPACE + ".deleteStoresByKey", userSeq);
    }

    
    public int deleteUser(int userSeq) {
        return mybatis.delete(NAMESPACE + ".deleteUser", userSeq);
    }

    
    public int countByUserId(String userId) {
        return mybatis.selectOne(NAMESPACE + ".countByUserId", userId);
    }

    public int countByNickname(String nickname) {
        return mybatis.selectOne(NAMESPACE + ".countByNickname", nickname);
    }

    public int countByEmail(String email) {
        return mybatis.selectOne(NAMESPACE + ".countByEmail", email);
    }

    public int countByBizNo(String bizNo) {
        return mybatis.selectOne(NAMESPACE + ".countByBizNo", bizNo);
    }

    public int countByNicknameExcludingSelf(String nickname, int userSeq) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("nickname", nickname);
        params.put("userSeq", userSeq);
        return mybatis.selectOne(NAMESPACE + ".countByNicknameExcludingSelf", params);
    }

    public int countByEmailExcludingSelf(String email, int userSeq) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("email", email);
        params.put("userSeq", userSeq);
        return mybatis.selectOne(NAMESPACE + ".countByEmailExcludingSelf", params);
    }

    
    public String getPasswordByUserSeq(Long userSeq) {
        return mybatis.selectOne(NAMESPACE + ".getPasswordByUserSeq", userSeq);
    }

    
    public int updatePassword(Long userSeq, String encodedPassword) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("userSeq", userSeq);
        params.put("encodedPassword", encodedPassword);
        return mybatis.update(NAMESPACE + ".updatePassword", params);
    }
}
