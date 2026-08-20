package com.soso.domain.board.dto;


public class BoardInsertRequestDto {
    private int userSeq;
    private String boardType; 
    private String csType;    
    private String title;
    private String content;

    public BoardInsertRequestDto() {}

    public BoardInsertRequestDto(int userSeq, String boardType, String csType, String title, String content) {
        this.userSeq = userSeq;
        this.boardType = boardType;
        this.csType = csType;
        this.title = title;
        this.content = content;
    }

    public int getUserSeq() { return userSeq; }
    public void setUserSeq(int userSeq) { this.userSeq = userSeq; }

    public String getBoardType() { return boardType; }
    public void setBoardType(String boardType) { this.boardType = boardType; }

    public String getCsType() { return csType; }
    public void setCsType(String csType) { this.csType = csType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}