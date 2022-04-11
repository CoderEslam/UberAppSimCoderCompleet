package com.doubleclick.uberappsimcoder.Driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.doubleclick.uberappsimcoder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DriverLoginActivity extends AppCompatActivity {
    private Button registerbtn,SignInbtn;
    private RelativeLayout root;
//    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        registerbtn = findViewById(R.id.registration);
        SignInbtn = findViewById(R.id.login);
//        root = findViewById(R.id.RelativeLayout);
//        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                if (User != null){
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRegisterDialog();
            }
        });

        SignInbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSignInDialog();
            }
        });
    }



    private void ShowRegisterDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("REGISTER");
        builder.setMessage("please use email to register");
        View view = LayoutInflater.from(this).inflate(R.layout.rigisterlayout,null);
        final EditText name = view.findViewById(R.id.registerNameEdit);
        final EditText email = view.findViewById(R.id.registerEmailEdit);
        final EditText password = view.findViewById(R.id.registerPasswordEdit);
        final EditText phone = view.findViewById(R.id.registerPhoneEdit);
        builder.setView(view);

//

        builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(email.getText().toString())) {

                    Toast.makeText(DriverLoginActivity.this, "enter Email", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(name.getText().toString())) {
                    Toast.makeText(DriverLoginActivity.this, "enter name", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(DriverLoginActivity.this, "enter password", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Toast.makeText(DriverLoginActivity.this, "enter phone", Toast.LENGTH_LONG).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(DriverLoginActivity.this, "Failed " + task.getException(), Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        String User_id = mAuth.getCurrentUser().getUid();
                                        DatabaseReference Currnt_User_db = FirebaseDatabase.getInstance().getReference().child("User")
                                                .child("Drivers").child(User_id);
                                        Currnt_User_db.setValue(true);
                                    }
                                }
                            });
                }
//                progressBar.setVisibility(View.VISIBLE);
                SignInbtn.setEnabled(false);
                registerbtn.setEnabled(false);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerbtn.setEnabled(true);
                dialog.dismiss();
            }
        });

        builder.show();


    }


    private void ShowSignInDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SignIn");
        builder.setMessage("please use email to SignIn");
        View view = LayoutInflater.from(this).inflate(R.layout.signinlayout,null);
        final EditText email = view.findViewById(R.id.EmailEdit);
        final EditText password = view.findViewById(R.id.PasswordEdit);
        builder.setView(view);
        builder.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog , int which) {
                if (TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(DriverLoginActivity.this,"enter Email",Toast.LENGTH_LONG)
                            .show();
                }
                if (TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(DriverLoginActivity.this,"enter password",Toast.LENGTH_LONG)
                            .show();
                }
//                progressBar.setVisibility(View.VISIBLE);
//                SignInbtn.setEnabled(false);
//                registerbtn.setEnabled(false);
                else {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
//                                        progressBar.setVisibility(View.INVISIBLE);
                                        startActivity(new Intent(DriverLoginActivity.this, DriverMapsActivity.class));
                                        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        DatabaseReference CustomerId = FirebaseDatabase.getInstance().getReference().child("User").child("Drivers")
                                                .child(driverId).child("CustomerRequest").child("CustomerRideId");
                                        CustomerId.setValue("");
                                    }
                                }
                            });
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SignInbtn.setEnabled(true);
                dialog.dismiss();
            }
        });
        builder.show();

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}