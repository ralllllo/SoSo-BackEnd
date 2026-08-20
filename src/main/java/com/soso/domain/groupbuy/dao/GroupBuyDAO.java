package com.soso.domain.groupbuy.dao;

import com.soso.domain.groupbuy.dto.GroupBuyDTO;
import com.soso.domain.groupbuy.dto.GroupBuyParticipantDTO;
import com.soso.domain.groupbuy.dto.ParticipantInfoDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.soso.domain.groupbuy.dto.GroupBuyDTO;

@Repository
public class GroupBuyDAO {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private final String NAMESPACE = "com.soso.domain.groupbuy.dao.GroupBuyDAO.";

    
    public void insertGroupBuy(GroupBuyDTO dto) {
        sqlSession.insert(NAMESPACE + "insertGroupBuy", dto);
    }

    
    public List<GroupBuyDTO> selectGroupBuys(String userType, Integer userSeq, String filter) {
        Map<String, Object> params = new HashMap<>();
        params.put("userType", userType);
        params.put("userSeq", userSeq);
        params.put("filter", filter);
        return sqlSession.selectList(NAMESPACE + "selectGroupBuys", params);
    }

    
    public List<GroupBuyDTO> selectMyParticipatedGroups(int userSeq) {
        return sqlSession.selectList(NAMESPACE + "selectMyParticipatedGroups", userSeq);
    }

    
    public List<GroupBuyDTO> selectMyCompletedGroups(int userSeq) {
        return sqlSession.selectList(NAMESPACE + "selectMyCompletedGroups", userSeq);
    }

    
    public List<GroupBuyDTO> selectMyCreatedGroups(int userSeq) {
        return sqlSession.selectList(NAMESPACE + "selectMyCreatedGroups", userSeq);
    }

    
    public int countMyParticipatedGroups(int userSeq) {
        return sqlSession.selectOne(NAMESPACE + "countMyParticipatedGroups", userSeq);
    }

    
    public int countMyCreatedGroups(int userSeq) {
        return sqlSession.selectOne(NAMESPACE + "countMyCreatedGroups", userSeq);
    }

    
    public int countCompletedGroupBuys(int userSeq) {
        return sqlSession.selectOne(NAMESPACE + "countCompletedGroupBuys", userSeq);
    }

    
    public GroupBuyDTO selectGroupBuyBySeq(int groupBuySeq) {
        return sqlSession.selectOne(NAMESPACE + "selectGroupBuyBySeq", groupBuySeq);
    }

    
    public int updateCurrentParticipantsCount(int groupBuySeq) {
        return sqlSession.update(NAMESPACE + "updateCurrentParticipantsCount", groupBuySeq);
    }

    
    public void insertParticipant(GroupBuyParticipantDTO dto) {
        sqlSession.insert(NAMESPACE + "insertParticipant", dto);
    }

    
    public int updateGroupBuyStatus(int groupBuySeq, String status, int userSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupBuySeq", groupBuySeq);
        params.put("status", status);
        params.put("userSeq", userSeq);
        return sqlSession.update(NAMESPACE + "updateGroupBuyStatus", params);
    }

    
    public List<ParticipantInfoDTO> selectParticipantsByGroupBuySeq(int groupBuySeq) {
        return sqlSession.selectList(NAMESPACE + "selectParticipantsByGroupBuySeq", groupBuySeq);
    }

    
    public int checkChatAccess(int groupBuySeq, int userSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupBuySeq", groupBuySeq);
        params.put("userSeq", userSeq);
        return sqlSession.selectOne(NAMESPACE + "checkChatAccess", params);
    }

    public List<GroupBuyDTO> getGroupBuyListByUser(int userSeq, Integer storeSeq) {
        Map<String, Object> params = new HashMap<>();
        params.put("userSeq", userSeq);
        params.put("storeSeq", storeSeq);
        return sqlSession.selectList(NAMESPACE+"getGroupBuyListByUser", params);
    }
}
