package com.example.qmma.featuredetection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    TextView text;
    Button button;
    ProgressDialog progress;

    private String input2, picsDir;

    static final int REQUEST_TAKE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        File picsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        this.picsDir = picsDir.getAbsolutePath();
        input2 = picsDir.getAbsolutePath() + File.separator + "input.jpg";


        File newfile = new File(input2);
        try {
            newfile.createNewFile();
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(), "Could not save photo.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newfile));
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        progress = ProgressDialog.show(this, "Calculating",
                "Comparing images...", true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Task task = new Task();
            task.execute(this);
        } else {
            Log.e("CARD", "photo not saved.");
        }
    }

    private class Task extends AsyncTask<MainActivity, Void, Void> {
        int maxSimilarity = 0;
        int bestMatchIndex = -1;
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
                text.setText("This is a " + ImageData.files_names[bestMatchIndex] + ".");
            } else {
                text.setText("No match.");
            }
            progress.dismiss();
        }
    }
}
