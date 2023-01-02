package com.cadenza.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cadenza.ecommerce.Model.Users;
import com.cadenza.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton , loginNowButton;
    private ProgressDialog loadingBar;
    private String parentDb = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginNowButton = (Button)findViewById(R.id.main_login_btn);
        joinNowButton = (Button)findViewById(R.id.main_join_now_btn);





        loadingBar = new ProgressDialog(this);
        Paper.init(this);

        loginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });





        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if(UserPhoneKey != "" && UserPasswordKey != ""){

            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){

                AllowAccess(UserPhoneKey , UserPasswordKey);

                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("Please wait .....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


            }
        }
    }

    private void AllowAccess(final String phone,final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                // Toast.makeText(LoginActivity.this,"81",Toast.LENGTH_LONG).show();

                if(datasnapshot.child(parentDb).child(phone).exists()){

                    Users usersData = datasnapshot.child(parentDb).child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone)){

                        if(usersData.getPassword().equals(password)){

                            Toast.makeText(MainActivity.this,"Please wait ..you are alredy logged in ",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);

                        }else{

                            Toast.makeText(MainActivity.this,"Password is incorrect",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }

                }else{

                    Toast.makeText(MainActivity.this,"Account with This Number Not Match",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
}