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

import java.io.File;
import java.io.FileOutputStream;

public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TextView textView;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Loader loader = new Loader();
            loader.execute(this);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textView = (TextView) findViewById(R.id.textView);
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
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

            } // for

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            textView.setText(txt);
        }
    }
}
