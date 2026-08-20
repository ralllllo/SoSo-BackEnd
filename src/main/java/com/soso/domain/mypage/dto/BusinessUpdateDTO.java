package com.soso.domain.mypage.dto;

import org.springframework.web.multipart.MultipartFile;

public class BusinessUpdateDTO {
    
    private String nickname;
    private String phone;
    private String email;
    
    
    private Long storeSeq;       
    private String bizNumber;
    private String companyName;
    private String ceoName;      
    private Integer zonecode;
    private String address1;
    private String address2;

    
    private Long userSeq;

    
    private MultipartFile exteriorImg;
    private MultipartFile interiorImg;

    public BusinessUpdateDTO() {}

    
    public Long getStoreSeq() { return storeSeq; }
    public void setStoreSeq(Long storeSeq) { this.storeSeq = storeSeq; }

    public String getCeoName() { return ceoName; }
    public void setCeoName(String ceoName) { this.ceoName = ceoName; }
    
    public String getBizNumber() { return bizNumber; }
    public void setBizNumber(String bizNumber) { this.bizNumber = bizNumber; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getZonecode() { return zonecode; }
    public void setZonecode(Integer zonecode) { this.zonecode = zonecode; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public Long getUserSeq() { return userSeq; }
    public void setUserSeq(Long userSeq) { this.userSeq = userSeq; }

    public MultipartFile getExteriorImg() { return exteriorImg; }
    public void setExteriorImg(MultipartFile exteriorImg) { this.exteriorImg = exteriorImg; }

    public MultipartFile getInteriorImg() { return interiorImg; }
    public void setInteriorImg(MultipartFile interiorImg) { this.interiorImg = interiorImg; }
}
