package com.example.qmma.featuredetection.EbaySearch.Model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by S_And on 2016/4/22 0022.
 */
public class Item implements Serializable {
    private String itemId, itemName, imageUrl, shipping, viewUrl, price;
    private String itemCategory, buyingFormat, shippingType;

    public Item (JSONObject jsonObject) {
        itemName = jsonObject.optString("Title");
        itemId = jsonObject.optString("ItemID");
        imageUrl = jsonObject.optString("GalleryURL");
        viewUrl = jsonObject.optString("ViewItemURLForNaturalSearch");
        shipping = "";
        buyingFormat = jsonObject.optString("ListingType");
        itemCategory = jsonObject.optString("Title");
        shippingType = jsonObject.optString("ShippingType");
        JSONObject priceObject = jsonObject.optJSONObject("ConvertedCurrentPrice");
        price = String.valueOf(priceObject.optLong("Value"));
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getBuyingFormat() {
        return buyingFormat;
    }

    public void setBuyingFormat(String buyingFormat) {
        this.buyingFormat = buyingFormat;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }
}
