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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;
import android .content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.File;
import java.io.IOException;

public class MenuActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int PLACE_PICKER_REQUEST = 11;
    static final int REQUEST_TAKE_PHOTO = 2;
    private boolean doubleBackToExitPressedOnce = false;

    //---------------------------------------------------------------------------
    //Related to Google Places API
    // source:http://www.truiton.com/2015/04/using-new-google-places-api-android/
    private static final String LOG_TAG = "PlacesAPIActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_REQUEST_CODE = 100;
    //---------------------------------------------------------------------------

    //Keep for testing purposes, but remove later.
    //To do: possibly use current location here?
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

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

    //This function is called when the mapImageButton is clicked.
    //It launches the PlacePicker available through Google Places API.
    //Source: http://www.truiton.com/2015/04/using-new-google-places-api-android/
    private void openMap(){
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            Intent intent = intentBuilder.build(MenuActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        mGoogleApiClient = new GoogleApiClient.Builder(MenuActivity.this)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .build();

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
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(this, data);
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
            } else {
                Log.e("CARD", "place picker failed");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.map_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent NextScreen = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(NextScreen);
        return super.onOptionsItemSelected(item);
    }
}
