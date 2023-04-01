package com.nitap.attende;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nitap.attende.models.Teacher;
import com.ttv.facerecog.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("teachers").child("aprajwal79@gmail?com");
        /*
                courseRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                assert teacher != null;
                Toast.makeText(TestActivity.this, teacher.sectionInfos.get(0).sectionFullName, Toast.LENGTH_SHORT).show();
            }
        });
         */


    }
}