package com.nitap.attende.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.nitap.attende.MyUtils;
import com.nitap.attende.models.StudentConfiguration;
import com.ttv.facerecog.R;

public class ViewStudentProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_profile);
        StudentConfiguration studentConfiguration = MyUtils.getStudentConfiguration(this);
        TextView name,email,rollno,degree,branch,year,sem,sectionName;
        name = findViewById(R.id.fullname);
        assert studentConfiguration != null;
        name.setText(studentConfiguration.student.name);
        email = findViewById(R.id.email);
        email.setText(studentConfiguration.student.email);
        rollno = findViewById(R.id.rollno);
        rollno.setText(studentConfiguration.student.rollno);
        degree = findViewById(R.id.degree);
        degree.setText(studentConfiguration.class1.degree);
        branch = findViewById(R.id.branch);
        branch.setText(studentConfiguration.class1.branch);
        year = findViewById(R.id.year);
        year.setText(studentConfiguration.class1.year);
        sem = findViewById(R.id.sem);
        sem.setText(studentConfiguration.class1.sem);
        sectionName = findViewById(R.id.sectionName);
        sectionName.setText(studentConfiguration.section.sectionName);






    }
}