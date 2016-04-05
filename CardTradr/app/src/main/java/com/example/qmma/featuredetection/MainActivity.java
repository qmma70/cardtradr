package com.example.qmma.featuredetection;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView text;
    ImageView imageView;

    private String input1, input2, picsDir;

    static final int REQUEST_TAKE_PHOTO = 2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);
        imageView = (ImageView) findViewById(R.id.imageView);



        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        File picsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        this.picsDir = picsDir.getAbsolutePath();
        input1 = picsDir.getAbsolutePath() + File.separator + "1.jpg";
        input2 = picsDir.getAbsolutePath() + File.separator + "2.jpg";

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }


        Bitmap bm_in1 = BitmapFactory.decodeResource(getResources(),
                R.drawable.a1);
        FileOutputStream out;
        try {
            out = new FileOutputStream(input1);
            bm_in1.compress(Bitmap.CompressFormat.PNG, 100, out);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Could not read images in database.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not read images in database.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.e("CARD", "photo saved.");
            int similarity = CVCompare.compare(input1, input2, this.picsDir);
            text.setText("Similarity: " + String.valueOf(similarity));
            //File imgFile = new File(this.picsDir + "out.png");
            //Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //imageView.setImageBitmap(bm);
        } else {
            Log.e("CARD", "photo not saved.");
        }
    }

}
