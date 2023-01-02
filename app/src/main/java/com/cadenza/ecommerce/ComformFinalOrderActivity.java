package com.cadenza.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cadenza.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ComformFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
    private Button confirmOrderBtn;

    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comform_final_order);

        totalAmount = getIntent().getStringExtra("Total price");

        nameEditText = findViewById(R.id.shipmentName);
        addressEditText = findViewById(R.id.shipment_Address);
        cityEditText = findViewById(R.id.shipment_city);
        phoneEditText = findViewById(R.id.shipment_phoneNumber);
        confirmOrderBtn = findViewById(R.id.comform_final_order);

        Toast.makeText(ComformFinalOrderActivity.this,totalAmount,Toast.LENGTH_LONG).show();



        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                check();
            }
        });


    }

    private void check() {

        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(ComformFinalOrderActivity.this,"Please Enter Your Full Name",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(ComformFinalOrderActivity.this,"Please Enter Shiping Address",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(ComformFinalOrderActivity.this,"Please Enter Shiping City",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(ComformFinalOrderActivity.this,"Please Enter Contact Number",Toast.LENGTH_LONG).show();
        }
        else{
            ConfirmOrder();
        }


    }

    private void ConfirmOrder() {

        String saveCurrentTime , saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM,dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH,mm,ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUser.getPhone());


        final HashMap<String , Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount",totalAmount);
        orderMap.put("pname",nameEditText.getText().toString());
        orderMap.put("phone",phoneEditText.getText().toString());
        orderMap.put("address",addressEditText.getText().toString());
        orderMap.put("city" , cityEditText.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time" , saveCurrentTime);
        orderMap.put("state" , "Not shipped");

        ordersRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(ComformFinalOrderActivity.this,"Your Final Order has been Placed Sucessfully",Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(ComformFinalOrderActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }
                            });
                }
            }
        });


    }
}