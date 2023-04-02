package com.nitap.attende;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ttv.facerecog.R;

public class AdminActivity extends AppCompatActivity {

    Button addClass,addSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        addClass = findViewById(R.id.button1);
        addSection = findViewById(R.id.button4);


        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddClassActivity.class));
            }
        });
        addSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddSectionActivity.class));
            }
        });


    }
}