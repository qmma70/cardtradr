package com.example.qmma.featuredetection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qmma.featuredetection.EbaySearch.Model.Item;
import com.example.qmma.featuredetection.EbaySearch.Model.ItemWrapper;
import com.example.qmma.featuredetection.EbaySearch.Model.SearchEvent;
import com.example.qmma.featuredetection.EbaySearch.ResultActivity;
import com.example.qmma.featuredetection.EbaySearch.searchClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    TextView text;
    Button button;
    ProgressDialog progress;

    int maxSimilarity = 0;
    int bestMatchIndex = -1;
    boolean noMatch;

    Stack<Integer> stack_index;
    Stack<Integer> stack_similarity;

    private String input2, picsDir;
    TextView txtRunnerups0, txtRunnerups1, txtRunnerups2;
    TextView txtP;

    //Ebay Params
    String APP_ID = "MarkBeni-CardTrad-PRD-4e7ff2694-0d8e0c78";
    String CALL_NAME = "FindItems";
    String RESPONSE_ENCODING = "JSON";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.details_menuitem) {
            if (noMatch) {
                Toast.makeText(getApplicationContext(), "No match to show.", Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
            intent.putExtra("DIR", picsDir);
            intent.putExtra("FILE1", picsDir + File.separator + bestMatchIndex + ".jpg");
            intent.putExtra("FILE2", picsDir + File.separator + "input.jpg");
            intent.putExtra("CONFIDENCE", String.valueOf(maxSimilarity));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.button);
        txtRunnerups0 = (TextView) findViewById(R.id.p1);
        txtRunnerups1 = (TextView) findViewById(R.id.p2);
        txtRunnerups2 = (TextView) findViewById(R.id.p3);
        txtP = (TextView) findViewById(R.id.txtP);

        stack_similarity = new Stack<Integer>();
        stack_index = new Stack<Integer>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra("AUTO", "TRUE");
                startActivity(intent);
            }
        });


        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateSearch();
            }
        });

        File picsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        this.picsDir = picsDir.getAbsolutePath();
        input2 = picsDir.getAbsolutePath() + File.separator + "input.jpg";
        progress = ProgressDialog.show(this, "Calculating",
                "Comparing images...", true);
        Task task = new Task();
        task.execute(this);
    }

    // Search Event catching
    public void onEventMainThread (SearchEvent event) {
        if (event.getResult().equals("success")) {
            JSONObject jsonObject = event.getJsonObject();
            List<Item> itemList = new ArrayList<>();
            JSONArray itemArray = jsonObject.optJSONArray("Item");

            if (itemArray.length() > 0) {
                for (int i =0; i < itemArray.length(); i++) {
                    itemList.add(new Item(itemArray.optJSONObject(i)));
                }
            }
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SearchResults", new ItemWrapper(itemList));
            startActivity(intent);
        }
    }

    //Ebay Initiate Search Passing Params
    private void initiateSearch(){
        RequestParams params = new RequestParams();
        params.put("version", "517");
        //params.put("SERVICE-NAME","FindingService");
        params.put("appid",APP_ID);
        params.put("callname", CALL_NAME);
        //params.put("pagination", "10");
        params.put("maxEntries", "10");
        params.put("responseencoding",RESPONSE_ENCODING);
        params.put("QueryKeywords", text.getText().toString().replaceAll("[^A-Za-z]",""));
        params.put("ItemSort", "BestMatch");

        WeakReference<Context> contextWeakReference = new WeakReference<Context>(this);
        searchClient searchClient = new searchClient(contextWeakReference.get());
        searchClient.searchItem(params);
    }

    //Event Bus
    public void onResume () {
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    //Event Bus
    public void onDestroy () {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private class Task extends AsyncTask<MainActivity, Void, Void> {

        MainActivity activity;
        @Override
        protected Void doInBackground(MainActivity... params) {
            activity = params[0];
            DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
            Mat img2 = Imgcodecs.imread(input2);
            Mat descriptors2 = new Mat();
            MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
            detector.detect(img2, keypoints2);
            descriptor.compute(img2, keypoints2, descriptors2);
            for(int i = 0; i < ImageData.files.length; i++) {
                String dataFile1 = picsDir + File.separator + String.valueOf(i) + ".dat";
                Mat descriptors1;
                try {
                    File myFile = new File(dataFile1);
                    FileInputStream fIn = new FileInputStream(myFile);
                    BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                    String aDataRow = "";
                    String gsonD1 = "";
                    while ((aDataRow = myReader.readLine()) != null)
                    {
                        gsonD1 += aDataRow ;
                    }
                    myReader.close();
                    Log.e("CARD", gsonD1);
                    descriptors1 = CVCompare.matFromJson(gsonD1);
                    int similarity = CVCompare.compare(descriptors1, descriptors2);

                    Log.e("CARD", "Similarity = " + String.valueOf(similarity));
                    if (similarity > 20 && similarity > maxSimilarity) {
                        maxSimilarity = similarity;
                        bestMatchIndex = i;
                        if (stack_index.size() > 3) stack_index.pop();
                        if (stack_similarity.size() > 3) stack_similarity.pop();
                        stack_index.push(bestMatchIndex);
                        stack_similarity.push(similarity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // for
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (bestMatchIndex >= 0) {
                txtP.setText("Other possibilities:");
                text.setPaintFlags(text.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                text.setText(ImageData.files_names[bestMatchIndex]);
                noMatch = false;
                int runner_up_size = stack_index.size() - 1;
                stack_index.pop();
                stack_similarity.pop();
                Log.e("CARD", String.valueOf(runner_up_size));
                //Log.e("CARD", String.valueOf(runner_up_size));
                if (runner_up_size > 0) {
                    for(int i = 0; i < runner_up_size; i++) {
                        int index = stack_index.pop();
                        int sim = stack_similarity.pop();
                        double p = (double)sim / maxSimilarity * 100;
                        String listItem = String.valueOf(ImageData.files_names[index]) + " (" + String.valueOf((int)p) + "%)";
                        Log.e("CARD", listItem);
                        if (i == 0) {
                            txtRunnerups0.setPaintFlags(txtRunnerups0.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                            txtRunnerups0.setText(listItem);
                        } else if (i == 1) {
                            txtRunnerups1.setPaintFlags(txtRunnerups1.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                            txtRunnerups1.setText(listItem);
                        } else {
                            txtRunnerups2.setPaintFlags(txtRunnerups2.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                            txtRunnerups2.setText(listItem);
                        }
                    } //for
                } else {
                    txtRunnerups0.setText("No other possibilities found.");
                }
            } else {
                text.setText("No match.");
                noMatch = true;
            }
            progress.dismiss();
        }
    }
}
