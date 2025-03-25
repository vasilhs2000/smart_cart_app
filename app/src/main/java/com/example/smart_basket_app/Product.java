package com.example.smart_basket_app;

public class Product {
    private Long id;
    private String productName;
    private float productWeight;
    private float productPrice;
    private String productImage;
    private int categoryId;

    public void setProductID(Long id){
        this.id = id;
    }
    public void setProductName(String productName){
        this.productName = productName;
    }
    public void setProductWeight(float productWeight){
        this.productWeight = productWeight;
    }
    public void setProductPrice(float productPrice){
        this.productPrice = productPrice;
    }
    public void setProductImage(String product_image){
        this.productImage = product_image;
    }
    public void setCategoryId(int category_id){ this.categoryId = category_id;}
    public Long getId(){
        return this.id;
    }
    public String getProductName(){
        return this.productName;
    }
    public float getProductWeight(){
        return this.productWeight;
    }
    public float getProductPrice(){
        return this.productPrice;
    }
    public String getProductImage(){
        return  this.productImage;
    }
    public int getCategoryId(){ return this.categoryId; }
}

