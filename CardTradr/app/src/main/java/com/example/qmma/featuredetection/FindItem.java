package com.example.qmma.featuredetection;

import android.os.AsyncTask;
import android.util.Log;

import com.ebay.services.client.ClientConfig;
import com.ebay.services.client.FindingServiceClientFactory;
import com.ebay.services.finding.FindItemsByKeywordsRequest;
import com.ebay.services.finding.FindItemsByKeywordsResponse;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.services.finding.PaginationInput;
import com.ebay.services.finding.SearchItem;
import com.ebay.services.finding.FindingService;
import java.util.List;


/**
 * Created by Mark on 4/7/2016.
 */

public class FindItem extends AsyncTask<Void, Void, Void> {

    public static void find() {
        try {
            // initialize service end-point configuration
            ClientConfig config = new ClientConfig();
            config.setApplicationId("MarkBeni-CardTrad-PRD-4e7ff2694-0d8e0c78");

            //create a service client
            FindingServicePortType serviceClient =
                    FindingServiceClientFactory.getServiceClient(config);

            //create request object
            FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest();
            //set request parameters
            request.setKeywords("harry potter phoenix");
            PaginationInput pi = new PaginationInput();
            pi.setEntriesPerPage(2);
            request.setPaginationInput(pi);

            //call service
            FindItemsByKeywordsResponse result =
                    serviceClient.findItemsByKeywords(request);

            //output result

            Log.e("CARD", result.getAck().toString());
            Log.e("CARD", "Find " + result.getSearchResult().getCount() + " items." );
            List<SearchItem> items = result.getSearchResult().getItem();
            for(SearchItem item : items) {
                Log.e("CARD", item.getTitle());
            }

        } catch (Exception ex) {
            // handle exception if any
            ex.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        find();
        return null;
    }
}
