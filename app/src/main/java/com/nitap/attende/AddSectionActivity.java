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
import com.nitap.attende.models.Section;
import com.nitap.attende.models.StudentConfiguration;
import com.nitap.attende.pages.HomeActivity;
import com.ttv.face.FaceFeatureInfo;
import com.ttv.facerecog.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AddSectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_section);
        Button addSectionBtn = findViewById(R.id.btnAddSection);
        EditText degree,branch,year,sem,sectionName,startRollNo,endRollNo;
        degree = findViewById(R.id.e1);
        branch = findViewById(R.id.e2);
        year = findViewById(R.id.e3);
        sem = findViewById(R.id.e4);
        sectionName = findViewById(R.id.e5);
        startRollNo = findViewById(R.id.e6);
        endRollNo = findViewById(R.id.e7);


        addSectionBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                String classId =
                             degree.getText().toString()
                            +branch.getText().toString()
                            +year.getText().toString()
                            +sem.getText().toString();
                Section section = new Section();
                section.startRollno = startRollNo.getText().toString();
                section.endRollno = endRollNo.getText().toString();
                 int max = (Integer.parseInt(section.endRollno) - Integer.parseInt(section.startRollno) + 1);
                 section.max = Integer.toString(max);
                 section.sectionId = section.startRollno.substring(0,4);
                 section.sectionName = sectionName.getText().toString();
                 //section.teacherEmails = new ArrayList<>();
                // section.teacherEmails.add("test1");

                DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId);
                            courseRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        DatabaseReference courseRef1 = FirebaseDatabase.getInstance().getReference().child("sections").child(section.sectionId);
                                        courseRef1.setValue(section).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "Added Section successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddSectionActivity.this, "Failed to add section", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Class not found, Add corresponding class first", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


            }
        });
    }
}