package com.nitap.attende;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nitap.attende.model.Student;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

public class MyUtils {

    public static Student student;
/*
    public static MyStudent convertStudentToMyStudent(Student student) {
        assert student!=null;
        MyStudent myStudent = new MyStudent();
        myStudent.rollno = student.rollno;
        myStudent.regno = student.regno;
        myStudent.year = student.year;
        myStudent.sem = student.sem;
        myStudent.name = student.name;
        myStudent.degree = student.degree;
        myStudent.branch = student.branch;
        myStudent.section = student.section;
        myStudent.email = student.email;
        myStudent.deviceHash =student.deviceHash;
        myStudent.sectionId =student.sectionId;
        myStudent.faceFeatureInfo = MyUtils.student.getFaceFeatureInfo().getFeatureData();

        myStudent.courses = Blob.fromBytes(MyUtils.getBytesArrFromStringArr(student.courses));



        return myStudent;
    }
*/

    /*
    public static Student convertMyStudentToStudent(MyStudent mystudent) {
        assert mystudent!=null;
        Student student = new Student();
        student.rollno = mystudent.rollno;
        student.regno = mystudent.regno;
        student.year = mystudent.year;
        student.sem = mystudent.sem;
        student.name = mystudent.name;
        student.degree = mystudent.degree;
        student.branch = mystudent.branch;
        student.section = mystudent.section;
        student.email = mystudent.email;
        student.deviceHash = mystudent.deviceHash;
        student.sectionId = mystudent.sectionId;

        student.faceFeatureInfo = MyFaceInfo.convertMyFaceInfoToFaceFeatureInfo(mystudent.myFaceInfo);

        student.courses = MyUtils.getStringArrFromBytesArr(mystudent.courses.toBytes());

        return student;
    }
*/
    public static byte[] getBytesArrFromStringArr(String[] strs) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<strs.length; i++) {
            sb.append(strs[i]);
            if (i != strs.length-1) {
                sb.append("*.*"); //concatenate by this splitter
            }
        }
        return sb.toString().getBytes();
    }

    public static String[] getStringArrFromBytesArr(byte[] bytes) {
        String entire = new String(bytes);
        return entire.split("\\*\\.\\*");
    }

    public static String getStringFromStudent(Student student) {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        String json = gson.toJson(student);
        return json;
    }


    public static Student getStudentFromString(String jsonString) {
       ObjectMapper mapper = new ObjectMapper();
        Student student = null;
        try {
            student = mapper.readValue(jsonString, Student.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
            if (student == null){ return null; }
            else  { return student; }
    }

    public static void saveString(Context context,String key,String value) {
        SharedPreferences prefs = context.getSharedPreferences("application",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getString(Context context,String key) {
        SharedPreferences prefs = context.getSharedPreferences("application",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String value = prefs.getString(key, "EMPTY");
        editor.commit();
        return value;
    }
    public static void removeString(Context context,String key) {
        SharedPreferences prefs = context.getSharedPreferences("application", Context.MODE_PRIVATE);
        prefs.edit().remove(key).commit();
    }

    public static String[] getSectionInfo(String sectionId)  {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("SECTION-INFO").child(sectionId);
        String[] info = {"q","q","q","q","q","q"};
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> iterable = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = iterable.iterator();
                iterable.forEach(dataSnapshot -> {

                    // Toast.makeText(LoginActivity.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                    if (Objects.equals(dataSnapshot.getKey(), "DEGREE"))
                        info[0] = dataSnapshot.getValue(String.class);
                    if (Objects.equals(dataSnapshot.getKey(), "BRANCH"))
                        info[1] = dataSnapshot.getValue(String.class);
                    if (Objects.equals(dataSnapshot.getKey(), "YEAR"))
                        info[2] = dataSnapshot.getValue(String.class);
                    if (Objects.equals(dataSnapshot.getKey(), "SEM"))
                        info[3] = dataSnapshot.getValue(String.class);
                    if (Objects.equals(dataSnapshot.getKey(), "SECTION"))
                        info[4] = dataSnapshot.getValue(String.class);
                    if (Objects.equals(dataSnapshot.getKey(), "COURSES"))
                        info[5] = dataSnapshot.getValue(String.class);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        int conf = 0;
        while (true) {
            if (conf>5)
                break;
            else if (info[conf] == "q") {
                try{
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                conf++;
            }
        }
        return info;

    }

    public static String convertStringArraytoString(String[] courseArray) {
        String result = courseArray[0];
        for (int i = 1;i<courseArray.length;i++) {
            result = result + "&" + courseArray[i];
        }
        return result;
    }

    public static String[] convertStringtoStringArray(String courseString) {
        return courseString.split("&");
    }

}
