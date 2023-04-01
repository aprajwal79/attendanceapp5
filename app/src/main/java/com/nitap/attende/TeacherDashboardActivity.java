package com.nitap.attende;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nitap.attende.models.SectionInfo;
import com.nitap.attende.models.Teacher;
import com.nitap.attende.models.TeacherConfiguration;
import com.ttv.facerecog.R;

import java.util.ArrayList;

public class TeacherDashboardActivity extends AppCompatActivity {

    TeacherConfiguration tConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // tConfig = MyUtils.getTeacherConfiguration(this);
        tConfig = new TeacherConfiguration();
        tConfig.teacher = new Teacher();
        tConfig.teacher.courses = new ArrayList<>();
        tConfig.teacher.sectionInfos = new ArrayList<SectionInfo>();
        tConfig.teacher.name = "PRAJWAL A";
        tConfig.teacher.teacherId = "aprajwal79@gmail?com";
        tConfig.teacher.branch = "CSE";
        tConfig.teacher.email = "aprajwal79@gmail.com";
        tConfig.teacher.courses.add("CS251");
        tConfig.teacher.courses.add("CS252");
        tConfig.teacher.courses.add("CS253");
        SectionInfo s1 =new SectionInfo();
        s1.endRollno = "421275";
        s1.max = "75";
        s1.sectionFullName = "BTECHCSE22B";
        s1.startRollno="421201";
        s1.sectionId="4212";
        tConfig.teacher.sectionInfos.add(s1);
        SectionInfo s2 =new SectionInfo();
        s2.endRollno = "421175";
        s2.max = "75";
        s2.sectionFullName = "BTECHCSE22A";
        s2.startRollno="421101";
        s2.sectionId="4211";
        tConfig.teacher.sectionInfos.add(s2);

        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("teachers").child("aprajwal79@gmail?com");
        courseRef.setValue(tConfig.teacher).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(TeacherDashboardActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
            }
        });


        assert tConfig!=null;
        Toast.makeText(this, tConfig.teacher.email, Toast.LENGTH_SHORT).show();

        TextView fname,lname;
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        String[] contents = tConfig.teacher.name.split(" ");
        fname.setText(contents[0]);
        lname.setText(contents[1]);


    }
}