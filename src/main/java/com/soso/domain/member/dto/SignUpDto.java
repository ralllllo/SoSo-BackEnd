package com.soso.domain.member.dto;

import java.time.LocalDate;


public class SignUpDto {
    
    private Integer userSeq;    
    private String userId;      
    private String password;    
    private String name;        
    private String nickname;    
    private String email;       
    private String phone;       
    private String userType;    

    
    private Integer storeSeq;   
    private String bizNo;       
    private String corpName;    
    private String ceoName;
    private String openDate;    
    private String repName;		
    

	private Integer zipCode;    
    private String address;     
    private String detailAddress; 
    private String storeImage;  
    
    
    private LocalDate formattedOpenDate; 

    public SignUpDto() {}

    
    public String getRepName() {
		return repName;
	}

	public void setRepName(String repName) {
		this.repName = repName;
	}
    public Integer getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(Integer userSeq) {
        this.userSeq = userSeq;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getStoreSeq() {
        return storeSeq;
    }

    public void setStoreSeq(Integer storeSeq) {
        this.storeSeq = storeSeq;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }
    
    public String getCeoName() {
    	return ceoName;
    }
    
    public void setCeoName(String ceoName) {
    	this.ceoName = ceoName;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public LocalDate getFormattedOpenDate() {
        return formattedOpenDate;
    }

    public void setFormattedOpenDate(LocalDate formattedOpenDate) {
        this.formattedOpenDate = formattedOpenDate;
    }

    @Override
    public String toString() {
        return "SignUpDto{" +
                "userSeq=" + userSeq +
                ", userId='" + userId + '\'' +
                ", bizNo='" + bizNo + '\'' +
                ", storeImage='" + storeImage + '\'' +
                '}';
    }
}
