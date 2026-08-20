package com.soso.domain.account.dto;


public class ItemResponseDto {
    private int itemSeq;
    private int categorySeq;
    private String categoryName;
    private String itemCode;
    private String itemName;
    private String spec;
    private int unitPrice;
    private String itemImage;

    public ItemResponseDto() {
    }

    public ItemResponseDto(int itemSeq, int categorySeq, String categoryName, String itemCode, String itemName, String spec, int unitPrice, String itemImage) {
        this.itemSeq = itemSeq;
        this.categorySeq = categorySeq;
        this.categoryName = categoryName;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.spec = spec;
        this.unitPrice = unitPrice;
        this.itemImage = itemImage;
    }

    public int getItemSeq() {
        return itemSeq;
    }

    public void setItemSeq(int itemSeq) {
        this.itemSeq = itemSeq;
    }

    public int getCategorySeq() {
        return categorySeq;
    }

    public void setCategorySeq(int categorySeq) {
        this.categorySeq = categorySeq;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }
}
