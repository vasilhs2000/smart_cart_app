package com.example.smart_basket_app;

public class Offers {
    private Long id;
    private String offerDiscount;
    private float offerPrice;

    public void setProductID(Long id) {
        this.id = id;
    }

    public void setOfferDiscount(String offerDiscount) {
        this.offerDiscount = offerDiscount;
    }

    public void setOfferPrice(float offerPrice) {
        this.offerPrice = offerPrice;
    }

    public Long getId() {
        return this.id;
    }

    public String getOfferDiscount() {
        return this.offerDiscount;
    }

    public float getOfferPrice() {
        return this.offerPrice;
    }
}
