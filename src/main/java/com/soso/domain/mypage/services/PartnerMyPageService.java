package com.soso.domain.mypage.services;

import com.soso.domain.file.dao.FileDAO;
import com.soso.domain.file.dto.FileSaveDto;
import com.soso.domain.file.services.FileService;
import com.soso.domain.member.dao.MemberDAO;
import com.soso.domain.mypage.dao.PartnerMyPageDAO;
import com.soso.domain.mypage.dto.PartnerProfileDTO;
import com.soso.domain.mypage.dto.PartnerUpdateDTO;
import com.soso.domain.mypage.dto.PartnerWithdrawalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PartnerMyPageService {

    @Autowired
    private PartnerMyPageDAO myPageDAO;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private MemberDAO memberDAO;

    public PartnerProfileDTO getPartnerProfile(Long user_seq) {
        return myPageDAO.getPartnerProfile(user_seq);
    }

    @Transactional(rollbackFor = Exception.class)
    public String updatePartnerProfile(PartnerUpdateDTO updateDto, MultipartFile exteriorImg, MultipartFile interiorImg) throws Exception {
        Integer userSeq = updateDto.getUserSeq();

        
        if (memberDAO.countByNicknameExcludingSelf(updateDto.getNickname(), userSeq) > 0) {
            return "duplNickname";
        }

        
        if (memberDAO.countByEmailExcludingSelf(updateDto.getEmail(), userSeq) > 0) {
        	return "duplEmail";
        }

        
        myPageDAO.updateUser(updateDto);
        myPageDAO.updateStore(updateDto);

        
        List<FileSaveDto> existingFiles = fileDAO.getFilesByUserAndCategory(userSeq, "STORE_IMAGE");

        
        if (exteriorImg != null && !exteriorImg.isEmpty()) {
            String oldSysName = (existingFiles.size() > 0) ? existingFiles.get(0).getSysname() : null;
            fileService.updateFile(exteriorImg, userSeq, "STORE_IMAGE", oldSysName);
        }

        
        if (interiorImg != null && !interiorImg.isEmpty()) {
            String oldSysName = (existingFiles.size() > 1) ? existingFiles.get(1).getSysname() : null;
            fileService.updateFile(interiorImg, userSeq, "STORE_IMAGE", oldSysName);
        }
        return "success";
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void withdrawMember(PartnerWithdrawalDTO withdrawalData) throws Exception {
        int result = myPageDAO.withdrawMember(withdrawalData);
        if (result == 0) {
            throw new RuntimeException("회원 탈퇴 처리 중 오류가 발생했습니다. (대상 없음)");
        }
    }
}
