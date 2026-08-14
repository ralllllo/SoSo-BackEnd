package com.soso.domain.mypage.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.soso.domain.file.dao.FileDAO;
import com.soso.domain.file.dto.FileSaveDto;
import com.soso.domain.file.services.FileService;
import com.soso.domain.member.dao.MemberDAO;
import com.soso.domain.member.services.BizValidationService;
import com.soso.domain.mypage.dao.BusinessMypageDAO;
import com.soso.domain.mypage.dto.BusinessMultiProfileDTO;
import com.soso.domain.mypage.dto.BusinessMypageDTO;
import com.soso.domain.mypage.dto.BusinessUpdateDTO;
import com.soso.domain.mypage.dto.BusinessNotificationSettingsDTO;
import com.soso.domain.mypage.dto.UserNotificationSettingDTO;
import java.util.HashMap;
import java.util.Map;

@Service
public class BusinessMypageService {

    @Autowired
    private BusinessMypageDAO businessMypageDAO;

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private BizValidationService bizValidationService; 

    
    public BusinessMypageDTO getBusinessMypage(Long user_Seq, Long storeSeq) {
        BusinessMypageDTO dto = businessMypageDAO.getBusinessInfo(user_Seq, storeSeq);
        
        if (dto != null && dto.getProfileImageUrl() != null && !dto.getProfileImageUrl().isEmpty()) {
            
            
        }
        
        return dto;
    }

    
    public List<BusinessMypageDTO> getAllStores(Long userSeq) {
        return businessMypageDAO.getAllStores(userSeq);
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateBusinessProfile(BusinessUpdateDTO updateDto) throws Exception {
        Long userSeq = updateDto.getUserSeq();
        Long storeSeq = updateDto.getStoreSeq(); 

        
        if (memberDAO.countByNicknameExcludingSelf(updateDto.getNickname(), userSeq.intValue()) > 0) {
            return "duplNickname";
        }

        
        if (memberDAO.countByEmailExcludingSelf(updateDto.getEmail(), userSeq.intValue()) > 0) {
            return "duplEmail";
        }

        
        businessMypageDAO.updateUser(updateDto);
        businessMypageDAO.updateStore(updateDto); 

        
        
        List<FileSaveDto> existingFiles = fileDAO.getFilesByBoardSeqAndCategory(storeSeq.intValue(), "STORE_IMAGE");

        
        MultipartFile exteriorImg = updateDto.getExteriorImg();
        if (exteriorImg != null && !exteriorImg.isEmpty()) {
            String oldSysName = (existingFiles.size() > 0) ? existingFiles.get(0).getSysname() : null;
            
            
            fileService.updateFileWithBoardSeq(exteriorImg, userSeq.intValue(), "STORE_IMAGE", oldSysName, storeSeq.intValue());
        }

        
        MultipartFile interiorImg = updateDto.getInteriorImg();
        if (interiorImg != null && !interiorImg.isEmpty()) {
            String oldSysName = (existingFiles.size() > 1) ? existingFiles.get(1).getSysname() : null;
            fileService.updateFileWithBoardSeq(interiorImg, userSeq.intValue(), "STORE_IMAGE", oldSysName, storeSeq.intValue());
        }

        return "success";
    }

    
    @Transactional(rollbackFor = Exception.class)
    public String registerMultiProfile(BusinessMultiProfileDTO registerDto, MultipartFile exteriorImg, MultipartFile interiorImg) throws Exception {
        
        
        
        
        boolean isValid = bizValidationService.validateBusiness(registerDto);
        
        if (!isValid) {
            
            return "invalidBizInfo";
        }

        
        
        int result = businessMypageDAO.insertStore(registerDto);

        if (result > 0) {
            
            Long storeSeq = registerDto.getStoreSeq();
            Integer userSeq = registerDto.getUserSeq().intValue();

            
            if (exteriorImg != null && !exteriorImg.isEmpty()) {
                fileService.uploadToGcsAndGetUrlWithBoardSeq(exteriorImg, userSeq, "STORE_IMAGE", storeSeq.intValue());
            }

            
            if (interiorImg != null && !interiorImg.isEmpty()) {
                fileService.uploadToGcsAndGetUrlWithBoardSeq(interiorImg, userSeq, "STORE_IMAGE", storeSeq.intValue());
            }

            return "success";
        }
        
        
        return "fail";
    }

    
    public BusinessNotificationSettingsDTO getNotificationSettings(Long userSeq, Long storeSeq) {
        BusinessMypageDTO profile = businessMypageDAO.getBusinessInfo(userSeq, storeSeq);
        String alertStockYn = "N";
        String alertExpiryYn = "N";
        String alertOrderYn = "N";
        
        if (profile != null) {
            alertStockYn = profile.getAlertStockYn() != null ? profile.getAlertStockYn() : "N";
            alertExpiryYn = profile.getAlertExpiryYn() != null ? profile.getAlertExpiryYn() : "N";
            alertOrderYn = profile.getAlertOrderYn() != null ? profile.getAlertOrderYn() : "N";
        }
        
        List<UserNotificationSettingDTO> settings = businessMypageDAO.getUserNotificationSettings(storeSeq);
        return new BusinessNotificationSettingsDTO(alertStockYn, alertExpiryYn, alertOrderYn, settings);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void updateNotificationSettings(Long userSeq, Long storeSeq, BusinessNotificationSettingsDTO settingsDto) throws Exception {
        
        Map<String, Object> userParams = new HashMap<>();
        userParams.put("userSeq", userSeq);
        userParams.put("alertStockYn", settingsDto.getAlertStockYn());
        userParams.put("alertExpiryYn", settingsDto.getAlertExpiryYn());
        userParams.put("alertOrderYn", settingsDto.getAlertOrderYn());
        businessMypageDAO.updateUserAlertSettings(userParams);

        
        businessMypageDAO.deleteUserNotificationSettings(storeSeq);
        
        if (settingsDto.getSettings() != null) {
            for (UserNotificationSettingDTO setting : settingsDto.getSettings()) {
                setting.setStoreSeq(storeSeq);
                businessMypageDAO.insertUserNotificationSetting(setting);
            }
        }
    }
}
