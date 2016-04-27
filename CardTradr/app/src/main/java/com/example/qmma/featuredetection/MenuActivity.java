package com.example.qmma.featuredetection;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class MenuActivity extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 11;
    static final int REQUEST_TAKE_PHOTO = 2;
    private boolean doubleBackToExitPressedOnce = false;

    //This function is called when btnTakePic is clicked.
    private void takePic() {
        File picsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String input2 = picsDir.getAbsolutePath() + File.separator + "input.jpg";


        File newfile = new File(input2);
        try {
            newfile.createNewFile();
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(), "Could not save photo.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //Create new intent to capture image and start new activity.
        //MenuActivity --> MainActivity
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newfile));
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);




        if (getIntent().hasExtra("AUTO")) {
            takePic();
        }

        ImageButton btn;
        btn = (ImageButton) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //If user selected btnTakePic...
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            } else {
                Log.e("CARD", "photo not saved.");
            }

        //If user selected mapImageButton...
        }
    }



    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_menu_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.location_button){
            Toast.makeText(MenuActivity.this, "Location button pressed", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getApplicationContext(), LocationActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
