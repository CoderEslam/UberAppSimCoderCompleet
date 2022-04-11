package com.doubleclick.uberappsimcoder.Driver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.doubleclick.uberappsimcoder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;

public class DriverSettingActivity extends AppCompatActivity {

    private EditText mNameEdit,mPhoneEdit,mCarEdit;
    private Button mConfirmbtn,mCanclebtn;
    private ImageView profileImage,ImageProfileEdit;
    private int RequestCode = 1;
    private String mName,mCar,mPhone;
    private String mProfileUri;
    private String UserId ;
    private String mService;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDataBase;
    private Uri ResulteUri;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_setting);

        mNameEdit = findViewById(R.id.NameEdit);
        mPhoneEdit = findViewById(R.id.PhoneEdit);
        mCarEdit = findViewById(R.id.CarEdit);
        mConfirmbtn = findViewById(R.id.ConfermBtn);
        mCanclebtn = findViewById(R.id.CancelBtn);
        profileImage = findViewById(R.id.ImageProfile);
        ImageProfileEdit = findViewById(R.id.ImageProfileEdit);
        radioGroup = findViewById(R.id.RadioGroupServes);
        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        mDriverDataBase  = FirebaseDatabase.getInstance().getReference().child("User").child("Drivers").child(UserId);
        getUserInfromation();
        ImageProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,RequestCode);
            }
        });

        mCanclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mConfirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveUserInfromation();
            }
        });

    }

    private void SaveUserInfromation() {
        mName = mNameEdit.getText().toString();
        mCar = mCarEdit.getText().toString();
        mPhone = mPhoneEdit.getText().toString();
        int selectedId = radioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = findViewById(selectedId);
        if (radioButton.getText()==null){
            return;
        }
        mService = radioButton.getText().toString();
        Map UserInfo = new HashMap();
        UserInfo.put("Name",mName);
        UserInfo.put("Car",mCar);
        UserInfo.put("Phone",mPhone);
        UserInfo.put("Service",mService);
        mDriverDataBase.updateChildren(UserInfo);

        if (ResulteUri!=null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ProfileImage").child(UserId);
//
            UploadTask uploadTask = filePath.putFile(ResulteUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
//                          Uri downloadUri = uri;
                            Map newMap = new HashMap();
                            newMap.put("ProfileImage",uri.toString());
                            mDriverDataBase.updateChildren(newMap);
                            Toast.makeText(DriverSettingActivity.this,"Update Done",Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DriverSettingActivity.this,"Failure "+e,Toast.LENGTH_LONG).show();
                    finish();
                }
            });

        }

    }

    private void getUserInfromation(){
        mDriverDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                    Map <String,Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("Name")!=null){
                        mName = map.get("Name").toString();
                        mNameEdit.setText(""+mName);
                    }
                    if (map.get("Car")!=null){
                        mCar = map.get("Car").toString();
                        mCarEdit.setText(""+mCar);
                    }
                    if (map.get("Phone")!=null){
                        mPhone = map.get("Phone").toString();
                        mPhoneEdit.setText(""+mPhone);
                    }
                    if (map.get("Service")!=null){
                        mService = map.get("Service").toString();
                        switch (mService){
                            case "UperX":
                                radioGroup.check(R.id.UperX);
                                break;
                            case "UperXL":
                                radioGroup.check(R.id.UperXL);
                                break;
                            case "UperBlack":
                                radioGroup.check(R.id.UperBlack);
                                break;
                        }
                    }
                    if (map.get("ProfileImage")!=null){
                        mProfileUri = map.get("ProfileImage").toString();
                        Glide.with(getApplication()).load(mProfileUri).centerCrop().into(profileImage);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RequestCode&&resultCode== Activity.RESULT_OK&&data!=null){
            ResulteUri=data.getData();
            profileImage.setImageURI(ResulteUri);
        }
    }
}