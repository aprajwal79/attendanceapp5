package com.nitap.attende.pages;

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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.nitap.attende.LoginActivity;
import com.ttv.face.FaceFeatureInfo;
import com.ttv.face.FaceResult;
import com.ttv.facerecog.CameraActivity;
import com.ttv.facerecog.DBHelper;
import com.ttv.facerecog.FaceEntity;
import com.ttv.facerecog.ImageRotator;
import com.ttv.facerecog.MainActivity;
import com.ttv.facerecog.R;
import com.ttv.facerecog.Utils;
import com.ttv.facerecog.databinding.ActivityHomeBinding;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import kotlin.jvm.internal.Intrinsics;


public class HomeActivity extends AppCompatActivity {

    FirebaseAuth userAuth;
    ActivityHomeBinding binding;
    private GoogleSignInClient mGoogleSigninClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSigninClient = GoogleSignIn.getClient(this, gso);



        binding.aboutUsBtn.setOnClickListener(v -> {
            signOut();
        });

        binding.attendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "VERIFY CLICKED", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, 2);


            }



        });


    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == -1) {
            Intrinsics.checkNotNull(data);
            int verifyResult = data.getIntExtra("verifyResult", Toast.LENGTH_SHORT);
            String recogName = data.getStringExtra("verifyName");
            if (verifyResult == 1) {
                Toast.makeText((Context)this, (CharSequence)("Verify succeed! " + recogName), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeActivity.this, AttendanceActivity.class));
            } else {
                Toast.makeText((Context)this, (CharSequence)"Verify failed!", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

        ////////

    }




    void signOut(){
        userAuth.getInstance().signOut();
        mGoogleSigninClient.signOut();

        Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
        Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(logoutIntent);
    }

}