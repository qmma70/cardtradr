package com.example.qmma.featuredetection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class MenuActivity extends AppCompatActivity {
    private Button btnTakePic;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnTakePic = (Button) findViewById(R.id.btnTakePic);
        spinner = (Spinner) findViewById(R.id.cardSelectSpinner);
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                String type = (String) spinner.getSelectedItem();
                i.putExtra("Type", type);
                startActivity(i);
            }
        });
    }
}
