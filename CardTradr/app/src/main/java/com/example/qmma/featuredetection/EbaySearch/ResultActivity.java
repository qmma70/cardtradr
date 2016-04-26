package com.example.qmma.featuredetection.EbaySearch;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qmma.featuredetection.EbaySearch.Model.Item;
import com.example.qmma.featuredetection.EbaySearch.Model.ItemWrapper;
import com.example.qmma.featuredetection.R;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    class MyListener implements AdapterView.OnItemClickListener {
        private List<Item> items;
        public MyListener(List<Item> items) {
            this.items = items;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            webView.loadUrl(items.get(position).getViewUrl());
        }
    }
    private TextView txtAverage;
    private ListView listViewResult;
    private WebView webView;
    private List<Item> listItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtAverage = (TextView) findViewById(R.id.txtAve);
        listViewResult = (ListView)findViewById(R.id.listViewResult);
        webView = (WebView) findViewById(R.id.webView);
        listItem = new ArrayList<>();

        if (getIntent().hasExtra("SearchResults")) {
            ItemWrapper itemWrapper = (ItemWrapper) getIntent().getSerializableExtra("SearchResults");
            listItem = itemWrapper.getItemList();
        }
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(this);
        SearchResultAdapter searchResultAdapter = new SearchResultAdapter(contextWeakReference.get(),listItem);
        listViewResult.setAdapter(searchResultAdapter);
        MyListener listener = new MyListener(listItem);
        listViewResult.setOnItemClickListener(listener);
        double average = 0;
        for (int i = 0; i < listItem.size(); i++) {
            //Log.e("CARD", String.valueOf(listItem.get(i).getDoublePrice()));
            average += listItem.get(i).getDoublePrice();
        }
        average /= listItem.size();
        //Log.e("CARD", String.valueOf(average));
        txtAverage.setText("Average price: $ " + new DecimalFormat("##.##").format(average));

    }

    @Override
    public void onResume(){
        super.onResume();

    }
}

