package com.soso.domain.account.dto;


public class PartnerDetailDto {
    private int storeSeq;
    private String companyName;
    private String ceoName;
    private String bizNumber;
    private String address1;
    private String address2;
    private String phone;
    private String email;

    public PartnerDetailDto() {
    }

    public PartnerDetailDto(int storeSeq, String companyName, String ceoName, String bizNumber, String address1, String address2, String phone, String email) {
        this.storeSeq = storeSeq;
        this.companyName = companyName;
        this.ceoName = ceoName;
        this.bizNumber = bizNumber;
        this.address1 = address1;
        this.address2 = address2;
        this.phone = phone;
        this.email = email;
    }

    public int getStoreSeq() { return storeSeq; }
    public void setStoreSeq(int storeSeq) { this.storeSeq = storeSeq; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCeoName() { return ceoName; }
    public void setCeoName(String ceoName) { this.ceoName = ceoName; }

    public String getBizNumber() { return bizNumber; }
    public void setBizNumber(String bizNumber) { this.bizNumber = bizNumber; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
