package com.nitap.attende;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ttv.face.FaceEngine;
import com.ttv.facerecog.DBHelper;
import com.ttv.facerecog.R;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    public static FaceEngine faceEngine;
    public static int check;

    private Context mycontext;
    private DBHelper mydb;
    public static ArrayList userLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, Objects.toString(check), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}