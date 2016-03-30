package com.example.qmma.featuredetection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;


import java.io.File;

public class MainActivity extends AppCompatActivity {
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        String inputFileName1="1";
        String inputFileName2="2";
        String inputExtension = "jpg";
        String inputDir = getCacheDir().getAbsolutePath();
        String outputDir = inputDir;
        String inputFilePath1 = inputDir + File.separator + inputFileName1 + "." + inputExtension;
        String inputFilePath2 = inputDir + File.separator + inputFileName2 + "." + inputExtension;

        int similarity = CVCompare.compare(inputFilePath1, inputFilePath2, outputDir);

        text.setText(String.valueOf(similarity));
    }
}
