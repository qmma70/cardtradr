package com.example.qmma.featuredetection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;

public class InfoActivity extends AppCompatActivity {
    ImageView img;
    TextView txt, txt2, txt3, txt4, txt5, txt6;
    private String dir, file1, file2;
    private String confidence;
    Bitmap outBM;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        img = (ImageView) findViewById(R.id.imageView);
        txt = (TextView) findViewById(R.id.text);
        txt2 = (TextView) findViewById(R.id.text2);
        txt3 = (TextView) findViewById(R.id.text3);
        txt4 = (TextView) findViewById(R.id.text4);
        txt5 = (TextView) findViewById(R.id.text5);
        txt6 = (TextView) findViewById(R.id.text6);

        Intent i = getIntent();
        dir = i.getStringExtra("DIR");
        file1 = i.getStringExtra("FILE1");
        file2 = i.getStringExtra("FILE2");
        confidence = i.getStringExtra("CONFIDENCE");

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(dir + File.separator + "out.png")), "image/*");
                startActivity(intent);
            }
        });

        progress = ProgressDialog.show(this, "Calculating",
                "Drawing comparison graph...", true);
        Task task = new Task();
        task.execute();

    }

    private class Task extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            CVCompare.compareWithOutput(file1, file2, dir);
            File imgFile = new  File(dir + File.separator + "out.png");
            outBM = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txt.setText("Confidence value: " + confidence);
            txt2.setText("Matching keypoints:");
            txt3.setText("Feature Detector: ORB");
            txt4.setText("Matcher: Hamming/Brute Force");
            txt5.setText("Classifier: kNN");
            txt6.setText("Filter Rate: " + String.valueOf(CVCompare.rate));
            img.setImageBitmap(outBM);
            progress.dismiss();
        }
    }
}
