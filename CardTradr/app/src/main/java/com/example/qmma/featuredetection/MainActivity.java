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
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView text;
    Button button;

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
        takePictureIntent.putExtra("outputX", 800);
        takePictureIntent.putExtra("outputY", 600);
        takePictureIntent.putExtra("scale", true);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int maxSimilarity = 0;
            int bestMatchIndex = -1;
            for(int i = 0; i < ImageData.files.length; i++) {
                String input1 = picsDir + File.separator + String.valueOf(i) + ".jpg";
                int similarity = CVCompare.compare(input1, input2, this.picsDir);
                if (similarity > 20 && similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    bestMatchIndex = i;
                }
            } // for

            if (bestMatchIndex >= 0) {
                CurrencyToUSD ct = new CurrencyToUSD(ImageData.values[bestMatchIndex]);
                ct.execute(new String[] {ImageData.types[bestMatchIndex]});
                try {
                    text.setText("This is a " + ImageData.files_names[bestMatchIndex] + ". It is equal to " + ct.get() + " USD (Bloomberg).");
                } catch (Exception e) {
                    //
                    e.printStackTrace();
                }
            } else {
                text.setText("No match.");
            }
        } else {
            Log.e("CARD", "photo not saved.");
        }
    }

}
