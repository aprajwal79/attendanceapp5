package com.nitap.attende.pages;

import static android.os.SystemClock.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitap.attende.Adapters.RollListAdapter;
import com.nitap.attende.ClickListener;
import com.nitap.attende.model.RollListModel;
import com.ttv.facerecog.R;
import com.ttv.facerecog.databinding.ActivityDisplayAttendanceBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DisplayAttendance extends AppCompatActivity implements ClickListener {

    RollListModel  rollListModel;
    ArrayList<RollListModel> absenteesList = new ArrayList<>();
    ArrayList<RollListModel> presentList = new ArrayList<>();
    List<String> rollList;
    String sectionId, subject;
    String sectionCode;
    RecyclerView rollView;


    ActivityDisplayAttendanceBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDisplayAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        rollView = findViewById(R.id.absentees_list);
        rollList = intent.getStringArrayListExtra("rollList");
        sectionId = intent.getStringExtra("sectionId");
        sectionCode = intent.getStringExtra("sectionCode");
        subject = intent.getStringExtra("subject");

        Toast.makeText(this, sectionCode, Toast.LENGTH_SHORT).show();

        getAttendance();

        binding.submitAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });



    }

    void getAttendance(){

        DatabaseReference secRef = FirebaseDatabase.getInstance().getReference().child("sections");
        final String[] strtRoll = new String[1];
        final String[] endRoll = new String[1];

        secRef.child(sectionCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    strtRoll[0] = snapshot.child("startRollno").getValue().toString();
                    endRoll[0] = snapshot.child("endRollno").getValue().toString();

                    int start = Integer.valueOf(strtRoll[0]);
                    int end = Integer.valueOf(endRoll[0]);

                    int size = rollList.size();

                    if(size >0){

                        for(int i = start; i <= end; i++){

                            RollListModel rollListModel = new RollListModel();

                            if(!rollList.contains(String.valueOf(i))){

                                rollListModel.setRoll_num(String.valueOf(i));

                                absenteesList.add(rollListModel);

                            }else if(rollList.contains(String.valueOf(i))){
                                rollListModel.setRoll_num(String.valueOf(i));

                                presentList.add(rollListModel);
                            }


                        }

                        LinearLayoutManager layoutManager = new LinearLayoutManager(DisplayAttendance.this);
                        rollView.setLayoutManager(layoutManager);

                        RollListAdapter adapter = new RollListAdapter(absenteesList, DisplayAttendance.this, DisplayAttendance.this);
                        rollView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(DisplayAttendance.this, "The given List is empty", Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Toast.makeText(DisplayAttendance.this, "Section Not Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    public void giveAttendanceTo(int position) {

        RollListModel model = new RollListModel();

        model = absenteesList.get(position);
        absenteesList.remove(position);
        presentList.add(model);
//        rollView.setLayoutManager(new LinearLayoutManager(this));
//        rollView.setHasFixedSize(true);

        rollView.getAdapter().notifyDataSetChanged();

    }


    public void uploadData(){

        final DatabaseReference attendance_ref = FirebaseDatabase.getInstance().getReference().child("attendance_record");

        final String saveCurrentDate, saveCurrentMonth, saveCurrentTime;


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM,dd yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentMonth = new SimpleDateFormat("MMMM");
        saveCurrentMonth = currentMonth.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hmma");
        saveCurrentTime = currentTime.format(calendar.getTime());

        String sessionId = saveCurrentDate+saveCurrentTime;


        final HashMap sessionMap = new HashMap<>();


        sessionMap.put("session_id", sectionId+ "" + saveCurrentTime );
        sessionMap.put("date", saveCurrentDate);
        sessionMap.put("absentees", absenteesList);
        sessionMap.put("presentess", presentList);


        attendance_ref.child(saveCurrentMonth).child(subject).child(sectionCode).child(saveCurrentDate).child(sessionId).updateChildren(sessionMap)
                           .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    Toast.makeText(DisplayAttendance.this, "Submitted Sucessfully", Toast.LENGTH_SHORT).show();

                                }
                            });


    }
}