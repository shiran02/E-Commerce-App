package com.cadenza.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cadenza.ecommerce.Model.Products;
import com.cadenza.ecommerce.Prevalent.Prevalent;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.FloatingActionButton;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    private FloatingActionButton addTocartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice , productDescription , productName;
    private String productID = "" , state = "Normal";
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productID = getIntent().getStringExtra("pid");

       // addTocartBtn = (FloatingActionButton) findViewById(R.id.add_product_to_cart);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        productImage = (ImageView) findViewById(R.id.product_image_deail);
        productPrice = (TextView) findViewById(R.id.product_price_deails);
        productDescription = (TextView) findViewById(R.id.product_description_deail);
        productName = (TextView) findViewById(R.id.product_name_deails);
        addToCartButton = (Button) findViewById(R.id.cart_Btn);

        getProductDetail(productID);



        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(state.equals("Order shipped") || state.equals("Order placed")){

                    Toast.makeText(ProductDetailActivity.this, "You can add purchase more products,once your order is shipped or confirmed.", Toast.LENGTH_SHORT).show();

                }else{

                    addToCartList();

                }



            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

    }

    private void addToCartList() {

        String saveCurrentTime , saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM,dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH,mm,ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
//
        final HashMap<String , Object> cartMap = new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time" , saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount" , "");



        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Toast.makeText(ProductDetailActivity.this, "111", Toast.LENGTH_LONG).show();


                                            if (task.isSuccessful()) {

                                                Toast.makeText(ProductDetailActivity.this, "Add to Cart List  Successfully", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                                                startActivity(intent);

                                            }else{
                                                Toast.makeText(ProductDetailActivity.this, "Add to Cart List  UnSuccessfully", Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    });
                        }else{
                            Toast.makeText(ProductDetailActivity.this, "Add to Cart List  UnSuccessfully", Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }

    private void getProductDetail(String productID) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if(datasnapshot.exists()){

                    Products products =  datasnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void CheckOrderState(){

        DatabaseReference orderRef;

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if(datasnapshot.exists()){

                    String shippingState = datasnapshot.child("state").getValue().toString();

                    if(shippingState.equals("shipped")){

                        state = "Order shipped";

                    }else if(shippingState.equals("Not shipped")){

                        state = "Order placed";

                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}