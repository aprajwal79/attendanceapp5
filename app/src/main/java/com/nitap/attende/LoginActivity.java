package com.nitap.attende;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitap.attende.pages.AttendanceActivity;
import com.nitap.attende.pages.HomeActivity;
import com.nitap.attende.pages.TakeAttendance;
import com.ttv.facerecog.R;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


public class LoginActivity extends AppCompatActivity {

    public static boolean hasLeft = false;
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    String email;
    Set<String> sections = new ArraySet<String>();
    Set<String> teacherEmailIds = new LinkedHashSet<String>() { };
    Set<String> adminEmailIds = new LinkedHashSet<String>() { };
    String rollno, sectionCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();

        mAuth = FirebaseAuth.getInstance();

        LinearLayout signInButton  = findViewById(R.id.google_btn);

        signInButton.setOnClickListener(v -> {
            signIn();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {
        signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN && mAuth.getCurrentUser()==null)  {
            Toast.makeText(getApplicationContext(), "Processing, please wait", Toast.LENGTH_SHORT).show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account == null){
                    Toast.makeText(this, "Got the account", Toast.LENGTH_SHORT).show();
                }
                 email = account.getEmail();
                firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, ""+ e, Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
       if (currentUser == null) {
           return;
       }
       if (hasLeft) {
           return;
       }

       startActivity(new Intent(LoginActivity.this, AttendanceActivity.class));
       finish();


        //Toast.makeText(this, "UPDATE UI CA
        // LLED", Toast.LENGTH_SHORT).show();
        // FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null) {
            String email = currentUser.getEmail();
            String[] contents = Objects.requireNonNull(email).split("@");
            if (contents.length == 2 && Objects.equals(contents[1], "student.nitandhra.ac.in")) {
                // USERTYPE STUDENT
                if (!Objects.equals(MyUtils.getString(this, "STUDENT"), "EMPTY")) {
                    MyUtils.saveString(this,"USERTYPE","STUDENT");
                    MyUtils.saveString(this,"EMAIL",email);
                    MyUtils.saveString(getApplicationContext(),"NAME", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                    MyUtils.removeString(this,"TEACHER");
                    MyUtils.removeString(this,"ADMIN");
                    hasLeft = true;
                    startActivity(new Intent(this,HomeActivity.class));
                    finish();
                } else {
                    rollno = contents[0];
                    sectionCode = rollno.substring(0,4);
                    checkIfSectionExists(rollno);
                    //Toast.makeText(getApplicationContext(), "sectionCode " +sectionCode, Toast.LENGTH_SHORT).show();

                }

            } else{
                // USERTYPE FACULTY AND ADMIN
                if (!Objects.equals(MyUtils.getString(this, "TEACHER"), "EMPTY")) {
                    MyUtils.saveString(this,"USERTYPE","TEACHER");
                    MyUtils.saveString(this,"EMAIL",email);
                    MyUtils.saveString(getApplicationContext(),"NAME", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                    MyUtils.removeString(this,"STUDENT");
                    MyUtils.removeString(this,"ADMIN");
                    hasLeft = true;
                    startActivity(new Intent(this,HomeActivity.class));
                    finish();
                } else if (!Objects.equals(MyUtils.getString(this, "ADMIN"), "EMPTY")) {
                    MyUtils.saveString(this,"USERTYPE","ADMIN");
                    MyUtils.saveString(this,"EMAIL",email);
                    MyUtils.saveString(getApplicationContext(),"NAME", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                    MyUtils.removeString(this,"STUDENT");
                    MyUtils.removeString(this,"TEACHER");
                    hasLeft = true;
                    startActivity(new Intent(this,HomeActivity.class));
                    finish();
                } else {
                    checkIfUserIsTeacher(email);

                    getTeacherEmailIds();
                    getAdminEmailIds();

                    if (teacherEmailIds.contains(email)) {
                        MyUtils.saveString(this,"USERTYPE","TEACHER");
                        MyUtils.saveString(this,"EMAIL",email);
                        MyUtils.saveString(getApplicationContext(),"NAME", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                        hasLeft =true;
                        startActivity(new Intent(this,FaceRecognitionActivity.class));
                        finish();
                    } else if (adminEmailIds.contains(email)) {
                        MyUtils.saveString(this,"USERTYPE","ADMIN");
                        MyUtils.saveString(this,"EMAIL",email);
                        MyUtils.saveString(getApplicationContext(),"NAME", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                        hasLeft=true;
                        startActivity(new Intent(this,FaceRecognitionActivity.class));
                        finish();
                    } else {
                        ///oo
                        //mGoogleSignInClient.signOut();
                        // mAuth.signOut();
                        Toast.makeText(this, "Account Unauthorised, Try Again", Toast.LENGTH_SHORT).show();
                    }


                }


            }



        } else {
            Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
        }







    }

    private void checkIfUserIsTeacher(String email) {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("OBJECTS").child("TEACHERS").child(email);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //TODO:  Now teacher exists, download teacher object and save jsonString

                } else {
                   checkIfUserIsAdmin(email);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfUserIsAdmin(String email) {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("OBJECTS").child("ADMINS").child(email);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //TODO:  Now admin exists, download admin object and save jsonString
                    MyUtils.saveString(getApplicationContext(),"USERTYPE","ADMIN");
                    MyUtils.saveString(getApplicationContext(),"EMAIL",email);
                    MyUtils.saveString(getApplicationContext(),"ADMIN", email);
                    MyUtils.removeString(getApplicationContext(),"STUDENT");
                    MyUtils.removeString(getApplicationContext(),"TEACHER");
                    hasLeft = true;
                    startActivity(new Intent(getApplicationContext(),AdminActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Account not authorised, try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkIfSectionExists(String rollno) {
        String sectionCode = rollno.substring(0,4);
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("SECTIONS").child(sectionCode);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Now section exists, hence check if student is already registered in the section
                    checkIfStudentExists(rollno);
                } else {
                    Toast.makeText(LoginActivity.this, "Section not found, contact admin", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkIfStudentExists(String rollno) {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("students").child(rollno);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // TODO: Now student details already exist, hence get jsonString and store it;
                    String jsonString = snapshot.getValue(String.class);
                    MyUtils.saveString(getApplicationContext(),"USERTYPE","STUDENT");
                    MyUtils.saveString(getApplicationContext(),"EMAIL",email);
                    MyUtils.saveString(getApplicationContext(),"STUDENT",jsonString);
                    hasLeft=true;
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();
                } else {
                    // TODO: Now student credentials are not found, ask for credentials and upload, also save jsonString
                    MyUtils.saveString(getApplicationContext(),"USERTYPE","STUDENT");
                    MyUtils.saveString(getApplicationContext(),"EMAIL",email);
                    hasLeft=true;
                    startActivity(new Intent(getApplicationContext(),FaceRecognitionActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
        private void getListOfSections() {
            DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("SECTION-INFO");
            courseRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NewApi")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterable<DataSnapshot> iterable = snapshot.getChildren();
                    Iterator<DataSnapshot> iterator = iterable.iterator();
                    iterable.forEach(dataSnapshot -> {

                        Toast.makeText(LoginActivity.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();

                        sections.add(dataSnapshot.getKey());
                        Toast.makeText(getApplicationContext(), Arrays.toString(sections.toArray()), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), Arrays.toString(sections.toArray()), Toast.LENGTH_SHORT).show();

                        if (sections.contains(sectionCode)) {
                            MyUtils.saveString(getApplicationContext(),"USERTYPE","STUDENT");
                            MyUtils.saveString(getApplicationContext(),"EMAIL",email);
                            MyUtils.saveString(getApplicationContext(),"NAME", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                            MyUtils.removeString(getApplicationContext(),"TEACHER");
                            MyUtils.removeString(getApplicationContext(),"ADMIN");
                            hasLeft=true;
                            startActivity(new Intent(getApplicationContext(),FaceRecognitionActivity.class));
                            finish();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    */
    private void getListOfSections() {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("OBJECTS").child("4212");
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sections.add(snapshot.getKey());
                    if(sections.contains("4212")) {
                        Toast.makeText(LoginActivity.this, "'4212' found in snapshot", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "'4212' not found in snapshot", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "SNAPSHOT DOES NOT EXIST", Toast.LENGTH_SHORT).show();
                }


                /*
                Iterable<DataSnapshot> iterable = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = iterable.iterator();
                iterable.forEach(dataSnapshot -> {

                    Toast.makeText(LoginActivity.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();

                    sections.add(dataSnapshot.getKey());
                    Toast.makeText(getApplicationContext(), Arrays.toString(sections.toArray()), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), Arrays.toString(sections.toArray()), Toast.LENGTH_SHORT).show();

                    if (sections.contains(sectionCode)) {
                        MyUtils.saveString(getApplicationContext(),"USERTYPE","STUDENT");
                        MyUtils.saveString(getApplicationContext(),"EMAIL",email);
                        MyUtils.saveString(getApplicationContext(),"NAME", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                        MyUtils.removeString(getApplicationContext(),"TEACHER");
                        MyUtils.removeString(getApplicationContext(),"ADMIN");
                        hasLeft=true;
                        startActivity(new Intent(getApplicationContext(),FaceRecognitionActivity.class));
                        finish();
                    }
                });
                */

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTeacherEmailIds() {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("OBJECTS").child("TEACHERS");
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> iterable = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = iterable.iterator();
                iterable.forEach(dataSnapshot -> {

                    Toast.makeText(LoginActivity.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();

                    sections.add(dataSnapshot.getKey());
                    Toast.makeText(getApplicationContext(), Arrays.toString(sections.toArray()), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), Arrays.toString(sections.toArray()), Toast.LENGTH_SHORT).show();

                    if (sections.contains(sectionCode)) {
                        MyUtils.saveString(getApplicationContext(),"USERTYPE","STUDENT");
                        MyUtils.saveString(getApplicationContext(),"EMAIL",email);
                        MyUtils.saveString(getApplicationContext(),"NAME", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                        MyUtils.removeString(getApplicationContext(),"TEACHER");
                        MyUtils.removeString(getApplicationContext(),"ADMIN");
                        hasLeft=true;
                        startActivity(new Intent(getApplicationContext(),FaceRecognitionActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getAdminEmailIds() {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("OBJECTS").child("TEACHERS");
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    teacherEmailIds.add(Objects.requireNonNull(dataSnapshot.getKey()).replace("?","."));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Toast.makeText(this, Arrays.toString(teacherEmailIds.toArray()), Toast.LENGTH_SHORT).show();
    }


    void signOut() {
        FirebaseAuth userAuth;
        GoogleSignInClient mGoogleSigninClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSigninClient = GoogleSignIn.getClient(this, gso);
        //userAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signOut();
        mGoogleSigninClient.signOut();
    }

}