package com.soso.domain.mypage.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.soso.domain.mypage.dto.BusinessMultiProfileDTO;
import com.soso.domain.mypage.dto.BusinessMypageDTO;
import com.soso.domain.mypage.dto.BusinessUpdateDTO;
import com.soso.domain.mypage.dto.UserNotificationSettingDTO;

@Repository
public class BusinessMypageDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String NAMESPACE = "com.soso.domain.mypage.repository.BusinessMypageRepository";

    public BusinessMypageDTO getBusinessInfo(Long userSeq, Long storeSeq) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("user_seq", userSeq);
        params.put("storeSeq", storeSeq);
        return sqlSession.selectOne(NAMESPACE + ".getBusinessInfo", params);
    }

    
    public java.util.List<BusinessMypageDTO> getAllStores(Long userSeq) {
        return sqlSession.selectList(NAMESPACE + ".getAllStoresBySeq", userSeq);
    }

    public int updateUser(BusinessUpdateDTO updateDto) {
        return sqlSession.update(NAMESPACE + ".updateUser", updateDto);
    }

    public int updateStore(BusinessUpdateDTO updateDto) {
        return sqlSession.update(NAMESPACE + ".updateStore", updateDto);
    }

    
    public int insertStore(BusinessMultiProfileDTO registerDto) {
        
        
        return sqlSession.insert(NAMESPACE + ".insertStore", registerDto);
    }

    public java.util.List<UserNotificationSettingDTO> getUserNotificationSettings(Long storeSeq) {
        return sqlSession.selectList(NAMESPACE + ".getUserNotificationSettings", storeSeq);
    }

    public int updateUserAlertSettings(java.util.Map<String, Object> params) {
        return sqlSession.update(NAMESPACE + ".updateUserAlertSettings", params);
    }

    public int deleteUserNotificationSettings(Long storeSeq) {
        return sqlSession.delete(NAMESPACE + ".deleteUserNotificationSettings", storeSeq);
    }

    public int insertUserNotificationSetting(UserNotificationSettingDTO setting) {
        return sqlSession.insert(NAMESPACE + ".insertUserNotificationSetting", setting);
    }
}
