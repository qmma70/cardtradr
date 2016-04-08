package com.example.qmma.featuredetection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    TextView text;
    Button button;
    ProgressDialog progress;
    Button test;
    ImageView imageView;
    TextView txtConfidence;

    int maxSimilarity = 0;
    int bestMatchIndex = -1;
    Bitmap outBM;

    private String input2, picsDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
        txtConfidence = (TextView) findViewById(R.id.txtConfidence);
        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra("AUTO", "TRUE");
                startActivity(intent);
            }
        });
        test = (Button) findViewById(R.id.button2);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindItem f = new FindItem();
                f.execute();
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

    @Override
    protected void onResume() {
        super.onResume();
        maxSimilarity = 0;
        bestMatchIndex = -1;
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // for
            CVCompare.compareWithOutput(picsDir + File.separator + bestMatchIndex + ".jpg", picsDir + File.separator + "input.jpg", picsDir);
            File imgFile = new  File(picsDir + File.separator + "out.png");
            outBM = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (bestMatchIndex >= 0) {
                text.setText("This is a " + ImageData.files_names[bestMatchIndex] + ".");
                txtConfidence.setText("Confidence Value: " + String.valueOf(maxSimilarity));
                imageView.setImageBitmap(outBM);
            } else {
                text.setText("No match.");
            }
            progress.dismiss();
        }
    }
}
