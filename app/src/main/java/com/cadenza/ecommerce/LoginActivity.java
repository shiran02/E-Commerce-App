package com.cadenza.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cadenza.ecommerce.Model.Users;
import com.cadenza.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText InputName , InputPhoneNumber , InputPassword;
    private ProgressDialog loadingBar;
    private String parentDb = "Users";
    private CheckBox chkBoxRememberMe;

    private TextView AdminLink , NotAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button)findViewById(R.id.login_btn);
        InputPassword = (EditText)findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText)findViewById(R.id.login_phone_number_input);
        loadingBar = new ProgressDialog(this);

        AdminLink = (TextView)findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView)findViewById(R.id.not_admin_panel_link);

        chkBoxRememberMe = (CheckBox)findViewById(R.id.remember_me_chkb);

        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDb = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDb = "Users";
            }
        });


    }

    private void LoginUser() {

        String phone = InputPhoneNumber.getText().toString();
        String Password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){

            Toast.makeText(this,"Please Enter phone",Toast.LENGTH_LONG).show();

        }else if(TextUtils.isEmpty(Password)){

            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_LONG).show();

        }else{

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait,while we are checking the Credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone , Password);
            

        }


    }

    private void AllowAccessToAccount(String phone, String password) {

        if(chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {



                if(datasnapshot.child(parentDb).child(phone).exists()){

                    Toast.makeText(LoginActivity.this,"131" + parentDb,Toast.LENGTH_LONG).show();

                    Users usersData = datasnapshot.child(parentDb).child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone)){

                        if(usersData.getPassword().equals(password)){

                            if(parentDb.equals("Admins")){

                                //Toast.makeText(LoginActivity.this,"139",Toast.LENGTH_LONG).show();

                                Toast.makeText(LoginActivity.this,"welcome admin ,you are Logging Successfully",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,AdminCategoryActivity.class);
                                startActivity(intent);
                            }else if(parentDb.equals("Users")){

                                Toast.makeText(LoginActivity.this,"Logging Successfully",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }

                        }else{

                            Toast.makeText(LoginActivity.this,"Password is incorrect",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }

                }else{

                    Toast.makeText(LoginActivity.this,"Account with This Number Not Match",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

}