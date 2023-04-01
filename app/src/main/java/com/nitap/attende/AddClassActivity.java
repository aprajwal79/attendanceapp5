package com.nitap.attende;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitap.attende.models.Class;
import com.nitap.attende.models.StudentConfiguration;
import com.nitap.attende.pages.HomeActivity;
import com.ttv.face.FaceFeatureInfo;
import com.ttv.facerecog.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AddClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        Button addClassBtn = findViewById(R.id.b6);
        EditText degree,branch,year,sem,courses;
        degree = findViewById(R.id.e1);
        branch = findViewById(R.id.e2);
        year = findViewById(R.id.e3);
        sem = findViewById(R.id.e4);
        courses = findViewById(R.id.e5);
        addClassBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                Class class1 = new Class();
                class1.degree = degree.getText().toString();
                class1.branch = branch.getText().toString();
                class1.year = year.getText().toString();
                class1.sem = sem.getText().toString();
                class1.classId = class1.degree+class1.branch+class1.year+class1.sem;

                String[] arr = courses.getText().toString().split(",");
                class1.courses=new ArrayList<>(Arrays.asList(arr));

                DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("classes").child(class1.classId);
                courseRef.setValue(class1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Class added successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to add class", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}