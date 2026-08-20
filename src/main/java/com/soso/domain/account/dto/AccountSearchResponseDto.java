package com.soso.domain.account.dto;

import java.time.LocalDate;


public class AccountSearchResponseDto {
    private int storeSeq;
    private int userSeq;
    private String bizNumber;
    private String companyName;
    private String ceoName;
    private LocalDate openingDate;
    private Integer zonecode;
    private String address1;
    private String address2;
    private String phone;  
    private String email;  

    
    public AccountSearchResponseDto() {
    }

    
    public AccountSearchResponseDto(int storeSeq, int userSeq, String bizNumber, String companyName, 
                                   String ceoName, LocalDate openingDate, Integer zonecode, 
                                   String address1, String address2) {
        this.storeSeq = storeSeq;
        this.userSeq = userSeq;
        this.bizNumber = bizNumber;
        this.companyName = companyName;
        this.ceoName = ceoName;
        this.openingDate = openingDate;
        this.zonecode = zonecode;
        this.address1 = address1;
        this.address2 = address2;
    }

    
    public AccountSearchResponseDto(int storeSeq, int userSeq, String bizNumber, String companyName, 
                                   String ceoName, LocalDate openingDate, Integer zonecode, 
                                   String address1, String address2, String phone, String email) {
        this.storeSeq = storeSeq;
        this.userSeq = userSeq;
        this.bizNumber = bizNumber;
        this.companyName = companyName;
        this.ceoName = ceoName;
        this.openingDate = openingDate;
        this.zonecode = zonecode;
        this.address1 = address1;
        this.address2 = address2;
        this.phone = phone;
        this.email = email;
    }

    
    public int getStoreSeq() {
        return storeSeq;
    }

    public void setStoreSeq(int storeSeq) {
        this.storeSeq = storeSeq;
    }

    public int getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }

    public String getBizNumber() {
        return bizNumber;
    }

    public void setBizNumber(String bizNumber) {
        this.bizNumber = bizNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCeoName() {
        return ceoName;
    }

    public void setCeoName(String ceoName) {
        this.ceoName = ceoName;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    public Integer getZonecode() {
        return zonecode;
    }

    public void setZonecode(Integer zonecode) {
        this.zonecode = zonecode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "AccountSearchResponseDto{" +
                "storeSeq=" + storeSeq +
                ", userSeq=" + userSeq +
                ", bizNumber='" + bizNumber + '\'' +
                ", companyName='" + companyName + '\'' +
                ", ceoName='" + ceoName + '\'' +
                ", openingDate=" + openingDate +
                ", zonecode=" + zonecode +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
