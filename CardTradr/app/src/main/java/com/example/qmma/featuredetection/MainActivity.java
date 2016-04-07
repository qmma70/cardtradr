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

import java.io.File;
import java.io.IOException;

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

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

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
        //takePictureIntent.putExtra("outputX", 800);
        //takePictureIntent.putExtra("outputY", 600);
        //takePictureIntent.putExtra("scale", true);
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
                String input1 = picsDir + File.separator + String.valueOf(i) + ".jpg";
                int similarity = CVCompare.compare(input1, descriptors2);
                if (similarity > 20 && similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    bestMatchIndex = i;
                }
            } // for
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (bestMatchIndex >= 0) {
                CurrencyToUSD ct = new CurrencyToUSD(ImageData.values[bestMatchIndex]);
                ct.execute(new String[] {ImageData.types[bestMatchIndex]});
                text.setText("This is a " + ImageData.files_names[bestMatchIndex] + ".");
            } else {
                text.setText("No match.");
            }
            progress.dismiss();
        }
    }
}
