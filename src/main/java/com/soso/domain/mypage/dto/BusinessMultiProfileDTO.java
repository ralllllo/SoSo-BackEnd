package com.soso.domain.mypage.dto;

import java.time.LocalDate;


public class BusinessMultiProfileDTO {
    
    private String b_nm;         
    private String b_no;         
    private String p_nm;         
    private String start_dt;      

    
    private Integer zipcode;     
    private String address1;     
    private String address2;     
    private Long userSeq;        
    private Long storeSeq;       

    public BusinessMultiProfileDTO() {}

    
    public String getB_nm() { return b_nm; }
    public void setB_nm(String b_nm) { this.b_nm = b_nm; }

    public String getB_no() { return b_no; }
    public void setB_no(String b_no) { this.b_no = b_no; }

    public String getP_nm() { return p_nm; }
    public void setP_nm(String p_nm) { this.p_nm = p_nm; }

    public String getStart_dt() { return start_dt; }
    public void setStart_dt(String start_dt) { this.start_dt = start_dt; }

    public Integer getZipcode() { return zipcode; }
    public void setZipcode(Integer zipcode) { this.zipcode = zipcode; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public Long getUserSeq() { return userSeq; }
    public void setUserSeq(Long userSeq) { this.userSeq = userSeq; }

    public Long getStoreSeq() { return storeSeq; }
    public void setStoreSeq(Long storeSeq) { this.storeSeq = storeSeq; }
}
