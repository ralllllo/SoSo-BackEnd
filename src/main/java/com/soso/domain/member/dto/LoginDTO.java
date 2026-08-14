package com.soso.domain.member.dto;

public class LoginDTO {
	
	private String id;
	private String pw;
	private Long user_seq;
	public Long getUser_seq() {
		return user_seq;
	}
	public void setUser_seq(Long user_seq) {
		this.user_seq = user_seq;
	}
	private String user_type;    
	
	public LoginDTO() {}
	
	
	public LoginDTO(String id, String pw, Long user_seq, String user_type) {
		super();
		this.id = id;
		this.pw = pw;
		this.user_seq = user_seq;
		this.user_type = user_type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
}
