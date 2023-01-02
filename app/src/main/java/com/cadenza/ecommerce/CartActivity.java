package com.cadenza.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cadenza.ecommerce.Model.Cart;
import com.cadenza.ecommerce.Prevalent.Prevalent;
import com.cadenza.ecommerce.ViewHolders.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Button NextProcessBtn;
    private TextView txtToalAmount , txtMsg1;

    private int overTotalPrice = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        NextProcessBtn = findViewById(R.id.next_process_btn);
        recyclerView = findViewById(R.id.cart_List);
        txtToalAmount = findViewById(R.id.total_Price);
        txtMsg1 = findViewById(R.id.msg1);


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);




        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(CartActivity.this, ComformFinalOrderActivity.class);
                intent.putExtra("Total price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });

        txtToalAmount.setText("Total Price : " + String.valueOf(overTotalPrice));

    }


    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

        final DatabaseReference CartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
            new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(CartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products"),Cart.class)
                    .build();

        FirebaseRecyclerAdapter<Cart , CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {

                holder.txtProductName.setText("Product Name : "+model.getPname());
                holder.txtProductPrice.setText("Price : " + model.getPrice()+"$");
                holder.txtProductQuantity.setText("Quantity : " +model.getQuantity());


                int oneTotalPrice = ((Integer.valueOf(model.getPrice())))*Integer.valueOf(model.getQuantity());


                overTotalPrice =  overTotalPrice + oneTotalPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[]{
                                "Edit",
                                "Remove"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            if(i ==0){
                                Intent intent = new Intent(CartActivity.this ,ProductDetailActivity.class);
                                intent.putExtra("pid",model.getPid());
                                startActivity(intent);
                            }

                            if(i ==1){
                                CartListRef.child("User View")
                                        .child(Prevalent.currentOnlineUser.getPhone())
                                        .child("Products")
                                        .child(model.getPid())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    Toast.makeText(CartActivity.this,"Item Removedd SuccessFully ",Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(CartActivity.this ,HomeActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            }

                            }
                        });

                        builder.show();
                    }
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout , parent , false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void CheckOrderState(){

        DatabaseReference orderRef;

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if(datasnapshot.exists()){
                    String shippingState = datasnapshot.child("state").getValue().toString();
                    String userName = datasnapshot.child("pname").getValue().toString();

                    if(shippingState.equals("shipped")){

                        txtToalAmount.setText("Dear " +userName +"\n order is shipped successfully.");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this,"you can purchase more products,once you recieved your first ofinal rder",Toast.LENGTH_LONG).show();


                    }else if(shippingState.equals("Not shipped")){

                        txtToalAmount.setText("Shipping State = Not shipped");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this,"you can purchase more products,once you recieved your first ofinal rder",Toast.LENGTH_LONG).show();


                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
}