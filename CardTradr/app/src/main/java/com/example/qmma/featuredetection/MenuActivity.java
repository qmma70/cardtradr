package com.example.qmma.featuredetection;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MenuActivity extends AppCompatActivity {
    private Button btnTakePic;
    private Spinner spinner;
    static final int REQUEST_TAKE_PHOTO = 2;

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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newfile));
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnTakePic = (Button) findViewById(R.id.btnTakePic);
        spinner = (Spinner) findViewById(R.id.cardSelectSpinner);

        if (getIntent().hasExtra("AUTO")) {
            takePic();
        }
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            String type = (String) spinner.getSelectedItem();
            i.putExtra("Type", type);
            startActivity(i);
        } else {
            Log.e("CARD", "photo not saved.");
        }
    }
}
