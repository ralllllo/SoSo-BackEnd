package com.soso.domain.member.services;

import com.soso.domain.file.dto.FileSaveDto;
import com.soso.domain.file.services.FileService;
import com.soso.domain.member.dao.MemberDAO;
import com.soso.domain.member.dto.PasswordChangeDTO;
import com.soso.domain.member.dto.SignUpDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private MemberDAO memberDAO;

    @Autowired
    private FileService fileService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    
    @Transactional(rollbackFor = Exception.class)
    public String changePassword(Long userSeq, PasswordChangeDTO passwordData) throws Exception {
        logger.info("비밀번호 변경 요청: userSeq={}", userSeq);

        
        String encodedPassword = memberDAO.getPasswordByUserSeq(userSeq);
        
        if (encodedPassword == null) {
            return "isNotPw";
        }

        
        if (!passwordEncoder.matches(passwordData.getCurrentPassword(), encodedPassword)) {
           return "difPw";
        }

        
        String newEncodedPassword = passwordEncoder.encode(passwordData.getNewPassword());
        int result = memberDAO.updatePassword(userSeq, newEncodedPassword);

        if (result == 0) {
            return "fail";
        }
        return "success";
        
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void signUp(SignUpDto signUpDto, MultipartFile exteriorImg, MultipartFile interiorImg) throws Exception {

        
        if (memberDAO.countByUserId(signUpDto.getUserId()) > 0) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        
        signUpDto.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        if (signUpDto.getOpenDate() != null && !signUpDto.getOpenDate().isEmpty()) {
            signUpDto.setFormattedOpenDate(LocalDate.parse(signUpDto.getOpenDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        
        int userResult = memberDAO.insertUser(signUpDto);
        if (userResult == 0) {
            throw new RuntimeException("계정 정보 저장 실패");
        }

        
        int generatedUserSeq = signUpDto.getUserSeq();
        logger.info("확보된 user_seq: {}", generatedUserSeq);

        
        int storeResult = memberDAO.insertStore(signUpDto);
        if (storeResult == 0) {
            throw new RuntimeException("매장 정보 저장 실패");
        }

        
        int generatedStoreSeq = signUpDto.getStoreSeq();
        logger.info("확보된 store_seq: {}", generatedStoreSeq);

        
        processImageUpload(exteriorImg, generatedUserSeq, generatedStoreSeq, "exterior_image");

        
        processImageUpload(interiorImg, generatedUserSeq, generatedStoreSeq, "interior_image");

        logger.info("회원가입 및 매장/파일 등록 완료: userSeq={}, storeSeq={}", generatedUserSeq, generatedStoreSeq);
    }

    
    private void processImageUpload(MultipartFile file, int userSeq, int storeSeq, String typePrefix) throws Exception {
        if (file != null && !file.isEmpty()) {
            
            String gcsUrl = fileService.uploadToGcsAndGetUrlWithBoardSeq(file, userSeq, "STORE_IMAGE", storeSeq);

            logger.info("파일 등록 완료: type={}, url={}", typePrefix, gcsUrl);
        }
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void deleteMember(int userSeq) {
        logger.info("회원 탈퇴 요청: userSeq={}", userSeq);
        
        
        memberDAO.deleteStoresByKey(userSeq);
        
        
        int result = memberDAO.deleteUser(userSeq);
        if (result == 0) {
            throw new RuntimeException("회원 정보 삭제 실패 (대상 없음)");
        }
        
        logger.info("회원 탈퇴 처리 완료: userSeq={}", userSeq);
    }

    
    public boolean isIdDuplicated(String userId) {
        return memberDAO.countByUserId(userId) > 0;
    }

    public boolean isNicknameDuplicated(String nickname) {
        return memberDAO.countByNickname(nickname) > 0;
    }

    public boolean isEmailDuplicated(String email) {
        return memberDAO.countByEmail(email) > 0;
    }
}
