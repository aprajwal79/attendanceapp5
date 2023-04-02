package com.nitap.attende;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nitap.attende.models.MyConfiguration;
import com.nitap.attende.models.MyStudent;
import com.nitap.attende.models.Section;
import com.nitap.attende.models.Student;
import com.nitap.attende.models.Class;
import com.nitap.attende.models.StudentConfiguration;
import com.nitap.attende.models.Teacher;
import com.nitap.attende.models.TeacherConfiguration;
import com.nitap.attende.pages.HomeActivity;
import com.ttv.face.FaceFeatureInfo;
import com.ttv.facerecog.DBHelper;
import com.ttv.facerecog.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


public class LoginActivity extends AppCompatActivity {

    long mFileDownloadedId;
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
    public static Uri myUri;
    public static String myRollno;
    public static boolean shouldTrain = false;
    public static boolean isEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hasLeft = false;
        MyUtils.removeConfigurationBuilder(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null) {
            isEnabled = true;
            } else {
            isEnabled = false;
        }

        signInButton = findViewById(R.id.google_btn);

        signInButton.setOnClickListener(v -> {
            if(isEnabled) {
                isEnabled = false;
                display("Verifying your details, please wait");
                signIn();
            } else {
                display("Processing, try again later");
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }

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
            Toast.makeText(getApplicationContext(), "Got Google account", Toast.LENGTH_SHORT).show();
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
                isEnabled = true;
                //updateUI(null);
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
                            //updateUI(null);
                            isEnabled = true;
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

       /*
        if (currentUser!=null) {
            String email = currentUser.getEmail();
           // Toast.makeText(this, "EMAIL: "+email, Toast.LENGTH_SHORT).show();
            String[] contents = Objects.requireNonNull(email).split("@");
           // Toast.makeText(this, contents[0]+"$"+contents[1], Toast.LENGTH_SHORT).show();
            if (contents.length == 2 && Objects.equals(contents[1], "student.nitandhra.ac.in")) {
                // USERTYPE STUDENT
                if (MyUtils.getConfiguration(this).student!=null) {
                    MyUtils.removeString(this,"TEACHERCONFIG");
                    MyUtils.removeString(this,"ADMINCONFIG");
                    MyUtils.removeString(this,"TEACHERCONFIGBUILDER");
                    MyUtils.removeString(this,"ADMINCONFIGBUILDER");
                    MyUtils.removeString(this,"STUDENTCONFIGBUILDER");
                    MyUtils.removeString(this,"USERTYPE");
                    MyUtils.removeString(this,"EMAIL");
                    MyUtils.removeString(this,"NAME");
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



                }


            }



        } else {
            Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
        }
        */

        MyConfiguration myConfiguration = MyUtils.getConfiguration(this);

        if(myConfiguration==null){
            //determine email and register
            String email = currentUser.getEmail();
            String[] contents = Objects.requireNonNull(email).split("@");
            if (contents.length == 2 && Objects.equals(contents[1], "student.nitandhra.ac.in")) {
                checkIfStudentExists();
            } else {
                //checkIfUserIsTeacher();
            }




        }else if(myConfiguration.student!=null && myConfiguration.teacher==null  && myConfiguration.admin==null){
            hasLeft = true;
            startActivity(new Intent(this,HomeActivity.class));
            finish();

        }else if(myConfiguration.student==null && myConfiguration.teacher!=null  && myConfiguration.admin==null) {
            hasLeft = true;
            startActivity(new Intent(this,TeacherDashboardActivity.class));
            finish();

        }else if(myConfiguration.student==null && myConfiguration.teacher==null  && myConfiguration.admin!=null){
            hasLeft = true;
            startActivity(new Intent(this,AdminActivity.class));
            finish();

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

    private void checkIfSectionExists() {
        String sectionId = mAuth.getCurrentUser().getEmail().split("@")[0].substring(0,4);
        Toast.makeText(this,"sec id " + sectionId, Toast.LENGTH_SHORT).show();
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("sections").child(sectionId);
        courseRef.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "section exists", Toast.LENGTH_SHORT).show();
                    // TODO: Now section details exist, hence get section details, then class details and redirect to photo upload
                    Section section = snapshot.getValue(Section.class);
                    MyConfiguration myConfiguration = new MyConfiguration();
                    myConfiguration.student = new Student();
                    myConfiguration.teacher =null;
                    myConfiguration.admin = null;

                    myConfiguration.student.max = section.max;
                    myConfiguration.student.startRollno = section.startRollno;
                    myConfiguration.student.endRollno = section.endRollno;
                    myConfiguration.student.sectionId = section.sectionId;
                    myConfiguration.student.sectionName = section.sectionName;
                    myConfiguration.student.classId = section.classId;

                    MyUtils.saveConfigurationBuilder(getApplicationContext(),myConfiguration);
                    checkIfClassExists();


                } else {
                    // TODO: Now section details not found, display error
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

    private void checkIfClassExists() {
        MyConfiguration myConfiguration = MyUtils.getConfigurationBuilder(this);
        String classId = myConfiguration.student.classId;

        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "class exists", Toast.LENGTH_SHORT).show();
                    // TODO: Now section and class exist,save class details and register new student
                    Class class1 = snapshot.getValue(Class.class);
                    myConfiguration.student.courses = class1.courses;
                    myConfiguration.student.degree = class1.degree;
                    myConfiguration.student.branch = class1.branch;
                    myConfiguration.student.year = class1.year;
                    myConfiguration.student.sem = class1.sem;

                    MyUtils.saveConfigurationBuilder(getApplicationContext(),myConfiguration);

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
                    //MyUtils.removeAll(getApplicationContext());
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

        String email = mAuth.getCurrentUser().getEmail();

        MyConfiguration myConfiguration = MyUtils.getConfigurationBuilder(this);
        myConfiguration.student.email = mAuth.getCurrentUser().getEmail();
        myConfiguration.student.rollno = mAuth.getCurrentUser().getEmail().split("@")[0];
        myConfiguration.student.regno = null;
        myConfiguration.student.name = mAuth.getCurrentUser().getDisplayName();

        MyUtils.saveConfigurationBuilder(getApplicationContext(),myConfiguration);

        uploadPhoto();

        /*
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

    }

    private void uploadPhoto() {
        Toast.makeText(getApplicationContext(), "Upload photo to complete the registration", Toast.LENGTH_SHORT).show();
        hasLeft = true;
        rollno = mAuth.getCurrentUser().getEmail().split("@")[0];
        startActivity(new Intent(this,FaceRecognitionActivity.class));
        finish();
    }

    private void fetchSectionDetails() {
        String sectionId = mAuth.getCurrentUser().getEmail().split("@")[0].substring(0,4);

        Toast.makeText(this, "section id "+ sectionId, Toast.LENGTH_SHORT).show();
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("sections").child(sectionId);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // TODO: Now section details exist, hence get section details, then class details and register face engine
                    Section section = snapshot.getValue(Section.class);
                    MyConfiguration myConfiguration = MyUtils.getConfigurationBuilder(getApplicationContext());
                    myConfiguration.student.sectionName = section.sectionName;
                    myConfiguration.student.classId = section.classId;
                    myConfiguration.student.max = section.max;
                    myConfiguration.student.startRollno = section.startRollno;
                    myConfiguration.student.endRollno = section.endRollno;
                    MyUtils.saveConfigurationBuilder(getApplicationContext(),myConfiguration);

                    fetchClassDetails();
                courseRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchClassDetails() {
        MyConfiguration myConfiguration = MyUtils.getConfigurationBuilder(getApplicationContext());
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("classes").child(myConfiguration.student.classId);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // TODO: Now section and class exist,save class details and register new student
                    Class class1 = snapshot.getValue(Class.class);
                    myConfiguration.student.degree = class1.degree;
                    myConfiguration.student.branch = class1.branch;
                    myConfiguration.student.year = class1.year;
                    myConfiguration.student.sem = class1.sem;
                    myConfiguration.student.courses = class1.courses;
                    MyUtils.saveConfigurationBuilder(getApplicationContext(),myConfiguration);

                    downloadPhoto();

                courseRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void downloadPhoto() {
        String rollno = MyUtils.getConfigurationBuilder(this).student.rollno;
        String url = MyUtils.getConfigurationBuilder(this).student.photoUrl;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("students").child(rollno);

        downloadFile(getApplicationContext(),rollno,".jpeg", Environment.DIRECTORY_DOWNLOADS,url);
    }

    private void registerFace(String rollno) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("students").child(rollno);
/*
        try{
            File localFile = File.createTempFile("images", "jpeg");

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Toast.makeText(getApplicationContext(), "Download success", Toast.LENGTH_SHORT).show();
                    Context var10000 =  getApplicationContext(); //MainActivity.context;
                    Uri var10001 = Uri.fromFile(localFile);
                    Bitmap var20 = null;
                    try {
                        var20 = ImageRotator.getCorrectlyOrientedImage(getApplicationContext(), var10001);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    Bitmap bitmap = var20;
                    List var21 = MainActivity.faceEngine.detectFace(bitmap);
                    final List faceResults = var21;
                    Collection var6 = (Collection) faceResults;
                    if (var6.size() == 1) {
                        MainActivity.faceEngine.extractFeature(bitmap, true, faceResults);
                        Rect cropRect = Utils.getBestRect(bitmap.getWidth(), bitmap.getHeight(), ((FaceResult)faceResults.get(0)).rect);
                        final Bitmap headImg = Utils.crop(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), 120, 120);
                        String s = rollno;
                        DBHelper var9 = MainActivity.mydb;
                        int user_id = var9.insertUser(s, headImg, ((FaceResult)faceResults.get(0)).feature);
                        FaceEntity face = new FaceEntity(user_id, s, headImg, ((FaceResult)faceResults.get(0)).feature);
                        com.ttv.facerecog.MainActivity.Companion.getUserLists().add(face);
                        FaceFeatureInfo faceFeatureInfo = new FaceFeatureInfo(user_id, ((FaceResult)faceResults.get(0)).feature);
                        MainActivity.faceEngine.registerFaceFeature(faceFeatureInfo);

                        Toast.makeText(getApplicationContext(), "ALL SUCCESS", Toast.LENGTH_SHORT).show();













                    }
                    else {
                        var6 = (Collection)faceResults;
                        if (var6.size() > 1) {
                            Toast.makeText(getApplicationContext(), (CharSequence)"Multiple face detected!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), (CharSequence)"No face detected!", Toast.LENGTH_SHORT).show();
                        }
                    }







                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Toast.makeText(getApplicationContext(),"Download Failed",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Throwable e ){
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
*/      storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                display("got download url");
                downloadFile(getApplicationContext(),rollno,".jpeg", Environment.DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                display("failed to get download url");
            }
        });


/*
        //assert bitmap!=null;
        List var21 = MainActivity.faceEngine.detectFace(bitmap);
        final List faceResults = var21;
        Collection var6 = (Collection)faceResults;
        if (var6.size() == 1) {
            MainActivity.faceEngine.extractFeature(bitmap, true, faceResults);
            Rect cropRect = Utils.getBestRect(bitmap.getWidth(), bitmap.getHeight(), ((FaceResult)faceResults.get(0)).rect);
            final Bitmap headImg = Utils.crop(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), 120, 120);
            String s = studentConfigurationBuilder.student.rollno;
            DBHelper var9 = MainActivity.mydb;
            int user_id = var9.insertUser(s, headImg, ((FaceResult)faceResults.get(0)).feature);
            FaceEntity face = new FaceEntity(user_id, s, headImg, ((FaceResult)faceResults.get(0)).feature);
            com.ttv.facerecog.MainActivity.Companion.getUserLists().add(face);
            FaceFeatureInfo faceFeatureInfo = new FaceFeatureInfo(user_id, ((FaceResult)faceResults.get(0)).feature);
            MainActivity.faceEngine.registerFaceFeature(faceFeatureInfo);

            Toast.makeText((Context) getApplicationContext(), (CharSequence)"Register succeed!", Toast.LENGTH_SHORT).show();
*/
        }

    BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadedID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadedID == mFileDownloadedId) {
                String rollno = MyUtils.getConfigurationBuilder(getApplicationContext()).student.rollno;
                display("Please ReUpload the same image "+rollno+".jpg to continue");

                hasLeft=true;
                startActivity(new Intent(getApplicationContext(),ReUploadActivity.class));
                finish();
            }
        }
    };

    public void trainFace(Uri uri) {
        myUri = uri;
        myRollno = rollno;
        startActivity(new Intent(getApplicationContext(),MainActivity2.class));

        /*

        display("Training the face");
        Uri var10001 = uri;
        Bitmap var20 = null;
        try {
            var20 = ImageRotator.getCorrectlyOrientedImage(MainActivity.context, var10001);
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        Bitmap bitmap = var20;
        assert bitmap!=null;
        List var21 = MainActivity.faceEngine.detectFace(bitmap);
        final List faceResults = var21;
        Collection var6 = (Collection) faceResults;
        if (var6.size() == 1) {
            MainActivity.faceEngine.extractFeature(bitmap, true, faceResults);
            Rect cropRect = Utils.getBestRect(bitmap.getWidth(), bitmap.getHeight(), ((FaceResult)faceResults.get(0)).rect);
            final Bitmap headImg = Utils.crop(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), 120, 120);
            String s = rollno;
            DBHelper var9 = MainActivity.mydb;
            int user_id = var9.insertUser(s, headImg, ((FaceResult)faceResults.get(0)).feature);
            FaceEntity face = new FaceEntity(user_id, s, headImg, ((FaceResult)faceResults.get(0)).feature);
            com.ttv.facerecog.MainActivity.Companion.getUserLists().add(face);
            FaceFeatureInfo faceFeatureInfo = new FaceFeatureInfo(user_id, ((FaceResult)faceResults.get(0)).feature);
            MainActivity.faceEngine.registerFaceFeature(faceFeatureInfo);

            Toast.makeText(getApplicationContext(), "ALL SUCCESS", Toast.LENGTH_SHORT).show();













        }
        else {
            var6 = (Collection)faceResults;
            if (var6.size() > 1) {
                Toast.makeText(getApplicationContext(), (CharSequence)"Multiple face detected!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), (CharSequence)"No face detected!", Toast.LENGTH_SHORT).show();
            }
        }

         */








    }

    private void downloadFile(Context applicationContext, String filename, String extension, String directoryDownloads, String url) {

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
       // request.setDestinationInExternalFilesDir(applicationContext,"directory",filename+extension);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename + extension);
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        mFileDownloadedId = downloadManager.enqueue(request);
        applicationContext.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }




    private void checkIfStudentExists() {
        String[] contents = mAuth.getCurrentUser().getEmail().split("@");
        String rollno = contents[0];
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("students").child(rollno);
        courseRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    //TODO: Now student details already exist, hence get student object and store it;
                    MyStudent student = snapshot.getValue(MyStudent.class);
                    MyConfiguration myConfiguration = new MyConfiguration();
                    myConfiguration.student = new Student();
                    myConfiguration.teacher = null;
                    myConfiguration.admin = null;

                    myConfiguration.student.email = student.email;
                    myConfiguration.student.rollno = student.rollno;
                    myConfiguration.student.regno = student.regno;
                    myConfiguration.student.name = student.name;
                    myConfiguration.student.deviceHash = student.deviceHash;
                    myConfiguration.student.sectionId = student.sectionId;
                    myConfiguration.student.faceFeatureInfoString = student.faceFeatureInfoString;
                    myConfiguration.student.photoUrl = student.photoUrl;
                    MyUtils.saveConfigurationBuilder(getApplicationContext(),myConfiguration);

                    fetchSectionDetails();

                } else {
                    //TODO: Now student credentials are not found, ask for credentials and upload, also save jsonString
                    checkIfSectionExists();
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

    @Override
    public void onResume() {
        super.onResume();

    }







}
