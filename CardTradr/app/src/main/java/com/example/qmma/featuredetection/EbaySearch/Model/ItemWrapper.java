package com.example.qmma.featuredetection.EbaySearch.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by S_And on 2016/4/22 0022.
 */
public class ItemWrapper implements Serializable {
    List<Item> itemList;
    public  ItemWrapper (List itemList) {
        this.itemList = itemList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
