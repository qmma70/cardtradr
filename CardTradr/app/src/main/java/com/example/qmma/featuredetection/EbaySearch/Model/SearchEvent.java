package com.example.qmma.featuredetection.EbaySearch.Model;

import org.json.JSONObject;
/**
 * Created by S_And on 2016/4/22 0022.
 */
public class SearchEvent {
    private String result;
    private JSONObject jsonObject;

    public SearchEvent (String result, JSONObject jsonObject) {
        this.result = result;
        this.jsonObject = jsonObject;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
