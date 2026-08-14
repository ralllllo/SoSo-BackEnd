package com.soso.domain.mypage.dto;

public class PartnerWithdrawalDTO {
	private Integer userSeq;          
    private String withdrawReason; 

    
    public PartnerWithdrawalDTO() {
    }

    
    public PartnerWithdrawalDTO(Integer userSeq, String withdrawReason) {
        this.userSeq = userSeq;
        this.withdrawReason = withdrawReason;
    }

    
    public Integer getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }

    public String getWithdrawReason() {
        return withdrawReason;
    }

    public void setWithdrawReason(String withdrawReason) {
        this.withdrawReason = withdrawReason;
    }

    
    @Override
    public String toString() {
        return "WithdrawalDTO{" +
                "userSeq=" + userSeq +
                ", withdrawReason='" + withdrawReason + '\'' +
                '}';
    }
}
