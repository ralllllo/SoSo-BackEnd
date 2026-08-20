package com.soso.domain.member.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.soso.domain.member.dao.MemberDAO;
import com.soso.domain.member.dto.BizValidateDto;
import com.soso.domain.mypage.dto.BusinessMultiProfileDTO;

@Service
public class BizValidationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private MemberDAO memberDao;
    
    @Value("${api.public-data.service-key}")
    private String serviceKey;

    
    
    
    public boolean validateBusiness(String bNo, String startDt, String pNm, String bNm) {
        String cleanBNo = bNo.replaceAll("-", "");
        String cleanStartDt = startDt.replaceAll("-", "");
        
        
        return executeNtsApi(cleanBNo, cleanStartDt, pNm, bNm, true);
    }

    
    
    
    public boolean validateBusiness(String bNo, String startDt, String pNm, String bNm, boolean isMultiProfile) {
        String cleanBNo = bNo.replaceAll("-", "");
        String cleanStartDt = startDt.replaceAll("-", "");
        
        
        return executeNtsApi(cleanBNo, cleanStartDt, pNm, bNm, !isMultiProfile);
    }

    
    
    
    public boolean validateBusiness(BusinessMultiProfileDTO dto) {
        String cleanBNo = dto.getB_no().replaceAll("-", "");
        String cleanStartDt = dto.getStart_dt().replaceAll("-", "");
        
        
        return executeNtsApi(cleanBNo, cleanStartDt, dto.getP_nm(), dto.getB_nm(), false);
    }

    
    
    
    private boolean executeNtsApi(String cleanBNo, String cleanStartDt, String pNm, String bNm, boolean checkDuplicate) {
        
        if (checkDuplicate) {
            int count = memberDao.countByBizNo(cleanBNo);
            if (count > 0) {
                throw new IllegalArgumentException("DUPLICATED_BIZ_NO"); 
            }
        }
        
        
        URI uri = UriComponentsBuilder
                .fromUriString("https://api.odcloud.kr/api/nts-businessman/v1/validate")
                .queryParam("serviceKey", serviceKey)
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        BizValidateDto.Request.BizInfo bizInfo = new BizValidateDto.Request.BizInfo(
                cleanBNo, cleanStartDt, pNm, bNm, "", "", ""
        );

        List<BizValidateDto.Request.BizInfo> list = new ArrayList<>();
        list.add(bizInfo);
        
        BizValidateDto.Request requestBody = new BizValidateDto.Request(list);
        HttpEntity<BizValidateDto.Request> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<BizValidateDto.Response> response = 
                    restTemplate.postForEntity(uri, entity, BizValidateDto.Response.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                BizValidateDto.Response resBody = response.getBody();
                if (resBody.getData() != null && !resBody.getData().isEmpty()) {
                    String validCode = resBody.getData().get(0).getValid();
                    return "01".equals(validCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}