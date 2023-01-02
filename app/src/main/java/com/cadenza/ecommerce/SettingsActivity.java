package com.cadenza.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cadenza.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText , userPhoneEditText , addressEditText;
    private TextView profileChangeTextBtn , closeTextBtn , saveTextBtn;


    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storagePofilePrictureRef;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storagePofilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = (CircleImageView)findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText)findViewById(R.id.setting_full_name);
        userPhoneEditText = (EditText)findViewById(R.id.setting_phone_number);
        addressEditText = (EditText)findViewById(R.id.setting_address);
        profileChangeTextBtn = (TextView)findViewById(R.id.profile_image_change_btn);
        closeTextBtn = (TextView)findViewById(R.id.close_setting_btn);
        saveTextBtn = (TextView)findViewById(R.id.update_account_setting_btn);

        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,addressEditText);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker.equals("clicked")){

                    userInforSaved();

                }else{

                    updateOnlyUserInfo();
                }
            }
        });


        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateOnlyUserInfo() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String , Object> userMap = new HashMap<>();

        userMap.put("name" , fullNameEditText.getText().toString());
        userMap.put("address" , addressEditText.getText().toString());
        userMap.put("phoneOrder" , userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


        //progressDialog.dismiss();

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));

        Toast.makeText(SettingsActivity.this,"Profile Updated Successfuly ",Toast.LENGTH_LONG).show();
        finish();
    }

    //-----image Crop -----------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }else{
            Toast.makeText(SettingsActivity.this,"Error , Try Again.",Toast.LENGTH_LONG).show();
            startActivity(new Intent(SettingsActivity.this , SettingsActivity.class));

        }

    }

    //----------------------------------------------------------------------------------------------

    private void userInforSaved() {

        if(TextUtils.isEmpty(fullNameEditText.getText().toString())){

            Toast.makeText(SettingsActivity.this,"Name is Mandentoty ",Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(addressEditText.getText().toString())){

            Toast.makeText(SettingsActivity.this,"Name is Mandentoty ",Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(userPhoneEditText.getText().toString())){

            Toast.makeText(SettingsActivity.this,"Name is Mandentoty ",Toast.LENGTH_LONG).show();

        }else if(checker.equals("clicked")){
            uploadImage();
        }


    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait while we are updating your Account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef  = storagePofilePrictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");


            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()){

                        throw task.getException();
                    }



                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String , Object> userMap = new HashMap<>();

                        userMap.put("name" , fullNameEditText.getText().toString());
                        userMap.put("address" , addressEditText.getText().toString());
                        userMap.put("phoneOrder" , userPhoneEditText.getText().toString());
                        userMap.put("image" , myUrl);

                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));

                        Toast.makeText(SettingsActivity.this,"Profile Updated Successfuly ",Toast.LENGTH_LONG).show();
                        finish();
                    }else{

                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this,"Erra ",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Toast.makeText(SettingsActivity.this,"Images Not Selected ",Toast.LENGTH_LONG).show();
        }
    }

    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEditText, EditText userPhoneEditText, EditText addressEditText) {

        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datanapshot) {

                if(datanapshot.exists()){

                    if(datanapshot.child("image").exists()){
                        String image = datanapshot.child("image").getValue().toString();
                        String name = datanapshot.child("name").getValue().toString();
                        String pass = datanapshot.child("password").getValue().toString();
                        String address = datanapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);

                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(pass);
                        addressEditText.setText(address);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}