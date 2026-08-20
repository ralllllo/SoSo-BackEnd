package com.soso.domain.notification.dao;

import com.soso.domain.notification.dto.NotificationDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String NAMESPACE = "com.soso.mappers.notification.NotificationMapper.";

    public void createTableIfNotExists() {
        sqlSession.update(NAMESPACE + "createTableIfNotExists");
    }

    public void insertNotification(NotificationDTO notification) {
        sqlSession.insert(NAMESPACE + "insertNotification", notification);
    }

    public List<NotificationDTO> selectRecentNotifications(int storeSeq) {
        return sqlSession.selectList(NAMESPACE + "selectRecentNotifications", storeSeq);
    }

    
    public void updateNotificationRead(int notificationSeq) {
        sqlSession.update(NAMESPACE + "updateNotificationRead", notificationSeq);
    }

    
    public String selectCompanyNameByStoreSeq(int storeSeq) {
        return sqlSession.selectOne(NAMESPACE + "selectCompanyNameByStoreSeq", storeSeq);
    }

    
    public Integer selectStoreSeqByUserSeq(long userSeq) {
        return sqlSession.selectOne(NAMESPACE + "selectStoreSeqByUserSeq", userSeq);
    }

    
    public List<Integer> selectPartnerStoreSeqsByBusinessSeq(int businessSeq) {
        return sqlSession.selectList(NAMESPACE + "selectPartnerStoreSeqsByBusinessSeq", businessSeq);
    }

    
    public java.util.Map<String, Object> selectStoreOwnerInfo(int storeSeq) {
        return sqlSession.selectOne(NAMESPACE + "selectStoreOwnerInfo", storeSeq);
    }

    
    public String checkNotificationEnabled(int storeSeq, String notificationType) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("storeSeq", storeSeq);
        params.put("notificationType", notificationType);
        return sqlSession.selectOne(NAMESPACE + "checkNotificationEnabled", params);
    }
}
