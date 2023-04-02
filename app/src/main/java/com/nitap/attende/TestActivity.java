package com.nitap.attende;

<<<<<<< HEAD
import static android.os.SystemClock.sleep;

=======
>>>>>>> f5dd250ddbf31052e32f4d1ca97c88e1851a057a
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

<<<<<<< HEAD
import com.google.android.material.snackbar.Snackbar;
=======
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
>>>>>>> f5dd250ddbf31052e32f4d1ca97c88e1851a057a
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
<<<<<<< HEAD
import com.google.firebase.database.ValueEventListener;
import com.nitap.attende.model.Section;
import com.ttv.facerecog.R;

import java.util.ArrayList;
=======
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nitap.attende.models.Teacher;
import com.ttv.facerecog.R;

import java.io.File;
>>>>>>> f5dd250ddbf31052e32f4d1ca97c88e1851a057a

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

<<<<<<< HEAD
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
=======
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("students");
        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
>>>>>>> f5dd250ddbf31052e32f4d1ca97c88e1851a057a


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

