package com.nitap.attende;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ttv.facerecog.R;

public class AddTeacherActivity extends AppCompatActivity {

    Button addTeacher ;
    EditText a,b,c,d,e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        addTeacher =findViewById(R.id.btnAddTeacher);
        a=findViewById(R.id.e1);
        b=findViewById(R.id.e2);
        c=findViewById(R.id.e3);
        d =findViewById(R.id.e4);
        a=findViewById(R.id.e5);

        addTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "NOT YET IMPLEMENTED", Toast.LENGTH_SHORT).show();
            }
        });

    }


}