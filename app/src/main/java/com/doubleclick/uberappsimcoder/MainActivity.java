package com.doubleclick.uberappsimcoder;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.doubleclick.uberappsimcoder.Customer.CustomerLoginActivity;
import com.doubleclick.uberappsimcoder.Driver.DriverLoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {
    private Button Customerbtn,Driverbtn;
    private RelativeLayout LayoutRoot;
    private FirebaseAuth mAuth;
    private FirebaseUser users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutRoot = findViewById(R.id.LayoutRoot);
        Customerbtn = findViewById(R.id.btnCustomer);
        Driverbtn = findViewById(R.id.btnDriver);
//        mAuth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        RootRef = database.getReference();
//        users = mAuth.getCurrentUser();

        GetPremassions();

        Customerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(LayoutRoot,"done",Snackbar.LENGTH_LONG).show();
                Intent CustomerIntent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                startActivity(CustomerIntent);
//                finish();
//                ShowRegisterDialog();
            }
        });

        Driverbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent DriverIntent = new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(DriverIntent);
//                finish();
//                ShowSignInDialog();
            }
        });

    }

    public void GetPremassions(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Customerbtn.setEnabled(true);
                        Driverbtn.setEnabled(true);
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Customerbtn.setEnabled(false);
                        Driverbtn.setEnabled(false);
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }
}