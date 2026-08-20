package com.soso.domain.groupbuy.services;

import com.soso.domain.groupbuy.dao.GroupBuyDAO;
import com.soso.domain.groupbuy.dto.GroupBuyDTO;
import com.soso.domain.groupbuy.dto.GroupBuyParticipantDTO;
import com.soso.domain.groupbuy.dto.ParticipantInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;

@Service
public class GroupBuyService {

    @Autowired
    private GroupBuyDAO groupBuyDAO;

    
    @Transactional
    public void createGroupBuy(GroupBuyDTO groupBuyDTO) {
        
        groupBuyDAO.insertGroupBuy(groupBuyDTO);
        
        
        GroupBuyParticipantDTO participantDTO = new GroupBuyParticipantDTO();
        participantDTO.setGroupBuySeq(groupBuyDTO.getGroupBuySeq());
        participantDTO.setUserSeq(groupBuyDTO.getUserSeq());
        
        groupBuyDAO.insertParticipant(participantDTO);
    }

    
    public List<GroupBuyDTO> getGroupBuys(String userType, Integer userSeq, String filter) {
        return groupBuyDAO.selectGroupBuys(userType, userSeq, filter);
    }

    
    public List<GroupBuyDTO> getMyParticipatedGroups(int userSeq) {
        return groupBuyDAO.selectMyParticipatedGroups(userSeq);
    }

    
    public List<GroupBuyDTO> getMyCompletedGroups(int userSeq) {
        return groupBuyDAO.selectMyCompletedGroups(userSeq);
    }

    
    public List<GroupBuyDTO> getMyCreatedGroups(int userSeq) {
        return groupBuyDAO.selectMyCreatedGroups(userSeq);
    }

    
    public int getMyParticipatedGroupsCount(int userSeq) {
        return groupBuyDAO.countMyParticipatedGroups(userSeq);
    }

    
    public int getMyCreatedGroupsCount(int userSeq) {
        return groupBuyDAO.countMyCreatedGroups(userSeq);
    }

    
    public int getCompletedGroupBuysCount(int userSeq) {
        return groupBuyDAO.countCompletedGroupBuys(userSeq);
    }

    
    public GroupBuyDTO getGroupBuyDetail(int groupBuySeq) {
        return groupBuyDAO.selectGroupBuyBySeq(groupBuySeq);
    }

    
    @Transactional
    public void joinGroupBuy(int groupBuySeq, int userSeq) {
        
        GroupBuyDTO targetGroup = groupBuyDAO.selectGroupBuyBySeq(groupBuySeq);
        
        if (targetGroup == null) {
            throw new IllegalArgumentException("존재하지 않는 공동구매입니다.");
        }
        
        if (!"RECRUITING".equals(targetGroup.getStatus())) {
            throw new IllegalStateException("모집 중인 공동구매가 아닙니다.");
        }

        if (targetGroup.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("모집 마감일이 지났습니다.");
        }
        
        if (targetGroup.getCurrentParticipants() >= targetGroup.getTargetParticipants()) {
            throw new IllegalStateException("모집 인원이 이미 마감되었습니다.");
        }

        
        int updatedRows = groupBuyDAO.updateCurrentParticipantsCount(groupBuySeq);
        if (updatedRows == 0) {
            throw new IllegalStateException("동시 접속으로 인해 모집 인원이 마감되었습니다.");
        }

        
        GroupBuyParticipantDTO participantDTO = new GroupBuyParticipantDTO();
        participantDTO.setGroupBuySeq(groupBuySeq);
        participantDTO.setUserSeq(userSeq);
        
        groupBuyDAO.insertParticipant(participantDTO);
    }

    
    public void updateGroupBuyStatus(int groupBuySeq, String status, int userSeq) {
        int result = groupBuyDAO.updateGroupBuyStatus(groupBuySeq, status, userSeq);
        if (result == 0) {
            throw new IllegalArgumentException("상태 업데이트 권한이 없거나 존재하지 않는 그룹입니다.");
        }
    }

    
    public List<ParticipantInfoDTO> getParticipants(int groupBuySeq) {
        return groupBuyDAO.selectParticipantsByGroupBuySeq(groupBuySeq);
    }

    
    public boolean validateChatAccess(int groupBuySeq, int userSeq) {
        int count = groupBuyDAO.checkChatAccess(groupBuySeq, userSeq);
        return count > 0;
    }
    public List<GroupBuyDTO> getGroupBuyListByUser(int userSeq, Integer storeSeq) {
        return groupBuyDAO.getGroupBuyListByUser(userSeq, storeSeq);
    }
}
