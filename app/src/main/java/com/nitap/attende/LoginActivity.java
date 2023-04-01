package com.nitap.attende;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.nitap.attende.models.Section;
import com.nitap.attende.models.Student;
import com.nitap.attende.models.Class;
import com.nitap.attende.models.StudentConfiguration;
import com.nitap.attende.models.Teacher;
import com.nitap.attende.models.TeacherConfiguration;
import com.nitap.attende.pages.HomeActivity;
import com.ttv.face.FaceFeatureInfo;
import com.ttv.face.FaceResult;
import com.ttv.facerecog.DBHelper;
import com.ttv.facerecog.FaceEntity;
import com.ttv.facerecog.ImageRotator;
import com.ttv.facerecog.R;
import com.ttv.facerecog.Utils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import kotlin.jvm.internal.Intrinsics;


public class LoginActivity extends AppCompatActivity {

    public static boolean hasLeft = false;
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    String email;
    Set<String> sections = new ArraySet<String>();
    Set<String> teacherEmailIds = new LinkedHashSet<String>() { };
    Set<String> adminEmailIds = new LinkedHashSet<String>() { };
    static String rollno, sectionCode;
    static Student student;
    static Section section;
    static Class class1;
    static Teacher teacher;
    static FaceFeatureInfo faceFeatureInfo;
    Button btnRegister,submitButton;
    private DBHelper mydb ;
    public static ArrayList userLists;
    public static String facetagForFaceInfo ;
    public static int searchIdForFaceInfo ;
    LinearLayout signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hasLeft = false;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();

        mAuth = FirebaseAuth.getInstance();

        signInButton = findViewById(R.id.google_btn);

        signInButton.setOnClickListener(v -> {
            signIn();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

        }
        updateUI(currentUser);

    }

    private void signIn() {
        signOut(getApplicationContext());
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


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


        //Toast.makeText(this, "UPDATE UI CA
        // LLED", Toast.LENGTH_SHORT).show();
        // FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null) {
            String email = currentUser.getEmail();
           // Toast.makeText(this, "EMAIL: "+email, Toast.LENGTH_SHORT).show();
            String[] contents = Objects.requireNonNull(email).split("@");
           // Toast.makeText(this, contents[0]+"$"+contents[1], Toast.LENGTH_SHORT).show();
            if (contents.length == 2 && Objects.equals(contents[1], "student.nitandhra.ac.in")) {
                // USERTYPE STUDENT
                if (!Objects.equals(MyUtils.getString(this, "STUDENTCONFIG"), "EMPTY")) {
                    MyUtils.removeString(this,"TEACHERCONFIG");
                    MyUtils.removeString(this,"ADMINCONFIG");
                    MyUtils.removeString(this,"TEACHERCONFIGBUILDER");
                    MyUtils.removeString(this,"ADMINCONFIGBUILDER");
                    MyUtils.removeString(this,"STUDENTCONFIGBUILDER");
                    /*MyUtils.removeString(this,"USERTYPE");
                    MyUtils.removeString(this,"EMAIL");
                    MyUtils.removeString(this,"NAME");*/
                    assert MyUtils.getStudentConfiguration(getApplicationContext()).student.email!=null;

                    hasLeft = true;
                    startActivity(new Intent(this,HomeActivity.class));
                    finish();
                } else {


                    MyUtils.removeAll(getApplicationContext());
                    StudentConfiguration firstStudentConfig = new StudentConfiguration();
                    String updatedStudentConfig = MyUtils.getStringFromObject(firstStudentConfig);
                    MyUtils.saveString(getApplicationContext(),"STUDENTCONFIGBUILDER",updatedStudentConfig);
                    rollno = contents[0];
                    sectionCode = rollno.substring(0,4);
                    assert MyUtils.getStudentConfigurationBuilder(getApplicationContext())!=null;
                    checkIfStudentExists(rollno);
                    //Toast.makeText(getApplicationContext(), "sectionCode " +sectionCode, Toast.LENGTH_SHORT).show();

                }

            } else{
                // USERTYPE FACULTY AND ADMIN
                MyUtils.removeString(this,"STUDENTCONFIG");
                MyUtils.removeString(this,"STUDENTCONFIGBUILDER");
                MyUtils.removeString(this,"TEACHERCONFIGBUILDER");
                MyUtils.removeString(this,"ADMINCONFIGBUILDER");

                if (!Objects.equals(MyUtils.getString(this, "TEACHERCONFIG"), "EMPTY")) {
                    MyUtils.removeString(this,"ADMINCONFIG");
                    hasLeft = true;
                    startActivity(new Intent(this,HomeActivity.class));
                    finish();
                } else if (!Objects.equals(MyUtils.getString(this, "ADMINCONFIG"), "EMPTY")) {
                    MyUtils.removeString(this,"TEACHERCONFIG");
                    hasLeft = true;
                    startActivity(new Intent(this,HomeActivity.class));
                    finish();
                } else {
                    checkIfUserIsTeacher(email);

                    getTeacherEmailIds();
                    getAdminEmailIds();

                    /*
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
                     */


                }


            }



        } else {
            Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
        }







    }

    private void checkIfUserIsTeacher(String email) {
        String teacherId = email.replace(".","?");
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("teachers").child(teacherId);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    //TODO:  Now teacher exists, download teacher object and save jsonString
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    TeacherConfiguration tConfig = new TeacherConfiguration();
                    tConfig.teacher= teacher;
                    String tString = MyUtils.getStringFromObject(tConfig);
                    MyUtils.removeAll(getApplicationContext());
                    MyUtils.saveString(getApplicationContext(),"TEACHERCONFIG",tString);
                    assert MyUtils.getTeacherConfiguration(getApplicationContext())!=null;
                    hasLeft = true;
                    startActivity(new Intent(getApplicationContext(),TeacherDashboardActivity.class));
                    finish();
                } else {
                   checkIfUserIsAdmin(email);
                }
                courseRef.removeEventListener(this);
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
                courseRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkIfSectionExists(String rollno) {
        String sectionId = rollno.substring(0,4);
        Toast.makeText(this,"sec id " + sectionId, Toast.LENGTH_SHORT).show();
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("sections").child(sectionId);
        courseRef.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "section exists", Toast.LENGTH_SHORT).show();
                    // TODO: Now section details exist, hence get section details, then class details and redirect to photo upload
                    section = snapshot.getValue(Section.class);
                    String studentConfigBuilder = MyUtils.getString(getApplicationContext(),"STUDENTCONFIGBUILDER");
                    StudentConfiguration studentConfigurationBuilder = MyUtils.getStudentConfigurationBuilder(getApplicationContext());//(StudentConfiguration) MyUtils.getObjectFromString(studentConfigBuilder,StudentConfiguration.class);
                    assert studentConfigurationBuilder != null;
                    studentConfigurationBuilder.section = section;
                    String updatedStudentConfig = MyUtils.getStringFromObject(studentConfigurationBuilder);
                    MyUtils.saveString(getApplicationContext(),"STUDENTCONFIGBUILDER",updatedStudentConfig);
                    assert MyUtils.getStudentConfigurationBuilder(getApplicationContext()).section!=null;
                    checkIfClassExists(studentConfigurationBuilder.section.classId);

                } else {
                    // TODO: Now section details not found, display error
                    MyUtils.removeAll(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Section not found, contact admin", Toast.LENGTH_SHORT).show();

                }
                courseRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkIfClassExists(String classId) {
        if(classId == null) {
            Toast.makeText(this, "Class id null", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    // TODO: Now section and class exist,save class details and register new student
                    class1 = snapshot.getValue(Class.class);
                    String studentConfigBuilder = MyUtils.getString(getApplicationContext(),"STUDENTCONFIGBUILDER");
                    StudentConfiguration studentConfigurationBuilder = MyUtils.getStudentConfigurationBuilder(getApplicationContext());//(StudentConfiguration) MyUtils.getObjectFromString(studentConfigBuilder,StudentConfiguration.class);
                    assert studentConfigurationBuilder != null;
                    studentConfigurationBuilder.class1 = class1;
                    String updatedConfigString = MyUtils.getStringFromObject(studentConfigurationBuilder);
                    MyUtils.saveString(getApplicationContext(),"STUDENTCONFIGBUILDER",updatedConfigString);
                    assert MyUtils.getStudentConfigurationBuilder(getApplicationContext()).class1!=null;
                    initialiseStudentCredentials();
                    /*
                    hasLeft = true;
                    startActivity(new Intent(LoginActivity.this, FaceRecognitionActivity.class));
                    finish();
                    */
                    /*
                    String finalConfigString = MyUtils.getStringFromObject(studentConfigBuilder);
                    MyUtils.saveString(getApplicationContext(),"STUDENTCONFIG",finalConfigString);
                    MyUtils.removeString(getApplicationContext(),"STUDENTCONFIGBUILDER");
                    hasLeft = true;
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();*/

                } else {
                    // TODO: Now section found but no class found, display error message
                    MyUtils.removeAll(getApplicationContext());
                    Toast.makeText(LoginActivity.this, "Class not found, contact admin", Toast.LENGTH_SHORT).show();
                }
                courseRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initialiseStudentCredentials() {

        String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        assert email != null;
        String[] contents = email.split("@");
        String rollno = contents[0];
        facetagForFaceInfo = rollno;
        Student student1 = new Student();
        student1.rollno = rollno;
        student1.email =email;
        student1.name = mAuth.getCurrentUser().getDisplayName();
        student1.sectionId = rollno.substring(0,4);
        StudentConfiguration updatedConfig = MyUtils.getStudentConfigurationBuilder(getApplicationContext());
        assert updatedConfig != null;
        updatedConfig.student = student1;
        String updatedString = MyUtils.getStringFromObject(updatedConfig);
        MyUtils.saveString(getApplicationContext(),"STUDENTCONFIGBUILDER",updatedString);
        assert MyUtils.getStudentConfigurationBuilder(getApplicationContext()) != null;
        //setContentView(R.layout.activity_face_recognition);
        //btnRegister = findViewById(R.id.upload_btn);
        //submitButton =findViewById(R.id.button_next);
        //btnRegister.setEnabled(true);
        //submitButton.setEnabled(false);
        /*
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction("android.intent.action.PICK");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        */
        Toast.makeText(getApplicationContext(), "Upload photo to complete the registration", Toast.LENGTH_SHORT).show();
        assert MyUtils.getStudentConfigurationBuilder(getApplicationContext()) != null;
        hasLeft = true;
        startActivity(new Intent(this,FaceRecognitionActivity.class));
        finish();
    }

    private void fetchSectionDetails(String rollno) {
        String sectionId = rollno.substring(0,4);
        Toast.makeText(this, "section id "+ sectionId, Toast.LENGTH_SHORT).show();
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("sections").child(sectionId);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    // TODO: CONTINUE HERE
                    // TODO: Now section details exist, hence get section details, then class details and register face engine
                    section = snapshot.getValue(Section.class);
                    //String studentConfigBuilder = MyUtils.getString(getApplicationContext(),"STUDENTCONFIGBUILDER");
                    StudentConfiguration studentConfigurationBuilder = MyUtils.getStudentConfigurationBuilder(getApplicationContext());//(StudentConfiguration) MyUtils.getObjectFromString(studentConfigBuilder,StudentConfiguration.class);
                    assert studentConfigurationBuilder != null;
                    studentConfigurationBuilder.section = section;
                    String updatedStudentConfig = MyUtils.getStringFromObject(studentConfigurationBuilder);
                    MyUtils.saveString(getApplicationContext(),"STUDENTCONFIGBUILDER",updatedStudentConfig);
                    fetchClassDetails(studentConfigurationBuilder.section.classId);

                } else {
                    // TODO: Now section details not found, display error
                    MyUtils.removeAll(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Section not found, contact admin", Toast.LENGTH_SHORT).show();

                }
                courseRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchClassDetails(String classId) {
        if(classId == null) {
            Toast.makeText(this, "Class id null", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    // TODO: Now section and class exist,save class details and register new student
                    class1 = snapshot.getValue(Class.class);
                    String studentConfigBuilder = MyUtils.getString(getApplicationContext(),"STUDENTCONFIGBUILDER");
                    StudentConfiguration studentConfigurationBuilder = MyUtils.getStudentConfigurationBuilder(getApplicationContext()); //(StudentConfiguration) MyUtils.getObjectFromString(studentConfigBuilder,StudentConfiguration.class);
                    assert studentConfigurationBuilder.section != null;
                    studentConfigurationBuilder.class1 = class1;
                    String faceInfoString = studentConfigurationBuilder.student.faceFeatureInfoString;
                    FaceFeatureInfo faceFeatureInfo1 = MyUtils.getFaceFeatureInfo(getApplicationContext(),faceInfoString);
                    assert faceFeatureInfo1!=null;
                    MainActivity.faceEngine.registerFaceFeature(faceFeatureInfo1);
                    String finalConfigString = MyUtils.getStringFromObject(studentConfigurationBuilder);
                    MyUtils.saveString(getApplicationContext(),"STUDENTCONFIG",finalConfigString);
                    MyUtils.removeString(getApplicationContext(),"STUDENTCONFIGBUILDER");
                    //assert MyUtils.getStudentConfiguration(getApplicationContext()).student.email != null;

                    assert MyUtils.getStudentConfiguration(getApplicationContext()).student.faceFeatureInfoString != null;
                    hasLeft = true;
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    finish();

                } else {
                    // TODO: Now section found but no class found, display error message
                    MyUtils.removeAll(getApplicationContext());
                    Toast.makeText(LoginActivity.this, "Class not found, contact admin", Toast.LENGTH_SHORT).show();
                }
                courseRef.removeEventListener(this);
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
                    //TODO: Now student details already exist, hence get student object and store it;
                    student = snapshot.getValue(Student.class);
                    StudentConfiguration studentConfiguration = new StudentConfiguration();
                    studentConfiguration.student = student;
                   // String featureInfoString = student.faceFeatureInfoString;
                   // faceFeatureInfo = (FaceFeatureInfo)
                    // 676(featureInfoString,FaceFeatureInfo.class);
                   // MainActivity.faceEngine.registerFaceFeature(faceFeatureInfo);
                    String updatedStudentConfig = MyUtils.getStringFromObject(studentConfiguration);
                    MyUtils.saveString(getApplicationContext(),"STUDENTCONFIGBUILDER",updatedStudentConfig);
                    assert MyUtils.getStudentConfigurationBuilder(getApplicationContext()).student!=null;
                    fetchSectionDetails(rollno);
                } else {
                    //TODO: Now student credentials are not found, ask for credentials and upload, also save jsonString
                    assert rollno!=null;
                   checkIfSectionExists(rollno);
                }
                courseRef.removeEventListener(this);
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
                courseRef.removeEventListener(this);
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
                courseRef.removeEventListener(this);
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
                courseRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Toast.makeText(this, Arrays.toString(teacherEmailIds.toArray()), Toast.LENGTH_SHORT).show();
    }

    public static void signOut(Context context) {
        FirebaseAuth userAuth;
        GoogleSignInClient mGoogleSigninClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSigninClient = GoogleSignIn.getClient(context, gso);
        FirebaseAuth.getInstance().signOut();
        mGoogleSigninClient.signOut();
    }

    void display(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }










}