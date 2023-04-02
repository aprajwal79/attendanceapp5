package com.nitap.attende;

import static android.os.SystemClock.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitap.attende.model.Section;
import com.ttv.facerecog.R;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        GetValue();
    }


    public void uploadInfo(){

        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Test");

        Section sec = new Section();
        sec.section = "hello";
        sec.branch = "cse";
        sec.courses = new ArrayList<>();
        sec.courses.add("peter");
        sec.courses.add("csdjf");
        sec.courses.add("fhsfj");
        sec.sem = "fourth";
        sec.degree = "B.tech";

        dataRef.setValue(sec);


    }

    void GetValue(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Test");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Section sec = (Section) snapshot.getValue(Section.class);

                Toast.makeText(TestActivity.this, sec.courses.get(0), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}

