package com.example.qmma.featuredetection.EbaySearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.example.qmma.featuredetection.EbaySearch.Model.SearchEvent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import de.greenrobot.event.EventBus;

/**
 * Created by S_And on 2016/4/22 0022.
 */
public class SearchClient {
    private String SEARCH_URL = "http://open.api.ebay.com/shopping?";
    ProgressDialog progressDialog;
    Context context;
    public SearchClient(Context context) {
        this.context = context;
    }
    public void searchItem (RequestParams params) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(context, SEARCH_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    EventBus.getDefault().post(new SearchEvent("success", response));
                } catch (Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
}
