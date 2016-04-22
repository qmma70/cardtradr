package com.example.qmma.featuredetection.EbaySearch;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.qmma.featuredetection.EbaySearch.Model.Item;
import com.example.qmma.featuredetection.EbaySearch.Model.ItemWrapper;
import com.example.qmma.featuredetection.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listViewResult;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        listViewResult = (ListView)findViewById(R.id.listViewResult);
        List<Item> listItem = new ArrayList<>();
        if (getIntent().hasExtra("SearchResults")) {
            ItemWrapper itemWrapper = (ItemWrapper) getIntent().getSerializableExtra("SearchResults");
            listItem = itemWrapper.getItemList();
        }
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(this);
        SearchResultAdapter searchResultAdapter = new SearchResultAdapter(contextWeakReference.get(),listItem);
        listViewResult.setAdapter(searchResultAdapter);
    }
}
