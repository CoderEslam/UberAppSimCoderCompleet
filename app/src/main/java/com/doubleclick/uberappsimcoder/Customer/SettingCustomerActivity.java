package com.doubleclick.uberappsimcoder.Customer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class SettingCustomerActivity extends AppCompatActivity {

    private Button Conferm,Cancel;
    private EditText NameEdit, PhoneEdit;
    private String UserId ;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDataBase;
    private String mName;
    private String mPhone;
    private ImageView ImageProfile,ImageProfileEdit;
    private int RequestCode = 1;
    private Uri ResulteUri;
    private String mProfileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_customer);
        Conferm = findViewById(R.id.ConfermBtn);
        Cancel = findViewById(R.id.CancelBtn);
        NameEdit = findViewById(R.id.NameEdit);
        PhoneEdit = findViewById(R.id.PhoneEdit);
        ImageProfile = findViewById(R.id.ImageProfile);
        ImageProfileEdit = findViewById(R.id.ImageProfileEdit);
        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        mCustomerDataBase  = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(UserId);
        getUserInfromation();

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Conferm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveUserInfromation();
            }
        });

        ImageProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,RequestCode);
            }
        });
    }

    private void SaveUserInfromation() {
        mName = NameEdit.getText().toString();
        mPhone = PhoneEdit.getText().toString();
        Map<String,Object> UserInfo = new HashMap();
        UserInfo.put("Name",mName);
        UserInfo.put("Phone",mPhone);
        mCustomerDataBase.updateChildren(UserInfo);

        if (ResulteUri!=null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ProfileImage").child(UserId);
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),ResulteUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
//            byte[] date = byteArrayOutputStream.toByteArray();
//            UploadTask uploadTask = filePath.putBytes(date);
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
                          mCustomerDataBase.updateChildren(newMap);
                            Toast.makeText(SettingCustomerActivity.this,"Update Done",Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingCustomerActivity.this,"Failure "+e,Toast.LENGTH_LONG).show();
                    finish();
                }
            });

        }

    }

    private void getUserInfromation(){
        mCustomerDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map <String,Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("Name")!=null){
                        mName = map.get("Name").toString();
                        NameEdit.setText(""+mName);
                    }
                    if (map.get("Phone")!=null){
                        mPhone = map.get("Phone").toString();
                        PhoneEdit.setText(""+mPhone);
                    }
                    if (map.get("ProfileImage")!=null){
                        mProfileUri = map.get("ProfileImage").toString();
                        Glide.with(getApplication()).load(mProfileUri).centerCrop().into(ImageProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RequestCode&&resultCode== Activity.RESULT_OK&&data!=null){
            ResulteUri=data.getData();
            ImageProfile.setImageURI(ResulteUri);
        }
    }
}