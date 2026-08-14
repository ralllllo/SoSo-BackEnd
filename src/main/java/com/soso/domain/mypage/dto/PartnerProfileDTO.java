package com.soso.domain.mypage.dto;

import java.util.Date;

public class PartnerProfileDTO {
    
    private String userId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String createdAt;
    
    
    private String bizNumber;
    private String repName;
	private String companyName;
    private Date openingDate;
    private Integer zonecode;
    private String address1;
    private String address2;
    private String ceoName;
    
    
    private String storeSysNames;

    public String getStoreSysNames() {
		return storeSysNames;
	}

	public void setStoreSysNames(String storeSysNames) {
		this.storeSysNames = storeSysNames;
	}

	public PartnerProfileDTO() {}

    
    
    public String getCeoName() {
        return ceoName;
    }

    public void setCeoName(String ceoName) {
        this.ceoName = ceoName;
    }
   
	public String getRepName() {
		return repName;
	}
	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public void setRepName(String repName) {
		this.repName = repName;
	}
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBizNumber() { return bizNumber; }
    public void setBizNumber(String bizNumber) { this.bizNumber = bizNumber; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Date getOpeningDate() { return openingDate; }
    public void setOpeningDate(Date openingDate) { this.openingDate = openingDate; }

    public Integer getZonecode() { return zonecode; }
    public void setZonecode(Integer zonecode) { this.zonecode = zonecode; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }
}
