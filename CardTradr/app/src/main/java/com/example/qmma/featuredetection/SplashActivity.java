package com.example.qmma.featuredetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private TextView textView;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED) {
            Loader loader = new Loader();
            loader.execute(this);
        } else {
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textView = (TextView) findViewById(R.id.textView);

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            Loader loader = new Loader();
            loader.execute(this);
        }
    }

    private class Loader extends AsyncTask<SplashActivity, Void, Void> {
        private SplashActivity activity;
        private String txt;
        @Override
        protected Void doInBackground(SplashActivity... params) {
            activity = params[0];

            String picsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            int total = ImageData.files.length;
            for(int i = 0; i < ImageData.files.length; i++) {
                txt = "Copying files (" + String.valueOf(i + 1) + "/" + String.valueOf(total) + ")...";
                publishProgress();
                String input1 = picsDir + File.separator + String.valueOf(i) + ".jpg";
                if (! new File(input1).exists()) {
                    Bitmap bm_in1 = BitmapFactory.decodeResource(getResources(),
                            ImageData.files[i]);
                    FileOutputStream out;
                    try {
                        out = new FileOutputStream(input1);
                        bm_in1.compress(Bitmap.CompressFormat.PNG, 100, out);
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                       e.printStackTrace();
                    }
                }
                // Extract key points and write to i.dat
                String dataFile1 = picsDir + File.separator + String.valueOf(i) + ".dat";
                if (! new File(dataFile1).exists()) {
                    FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
                    DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);

                    Mat img1 = Imgcodecs.imread(input1);
                    Mat descriptors1 = new Mat();
                    MatOfKeyPoint keypoints1 = new MatOfKeyPoint();

                    detector.detect(img1, keypoints1);
                    descriptor.compute(img1, keypoints1, descriptors1);

                    String gsonD1 = CVCompare.matToJson(descriptors1);

                    File myFile = new File(dataFile1);
                    try {
                        myFile.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(myFile);
                        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                        myOutWriter.append(gsonD1);
                        myOutWriter.close();
                        fOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } // for
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(activity, MenuActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            textView.setText(txt);
        }
    }
}
