package com.cadenza.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cadenza.ecommerce.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.order_List);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef , AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders , AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder, @SuppressLint("RecyclerView") int position,  AdminOrders model) {

                        holder.userName.setText("Name : "+model.getPname());
                        holder.userPhoneNumber.setText("phone Number : "+model.getPhone());
                        holder.userTotalPrice.setText("Total Price : "+model.getTotalAmount());
                        holder.userDateTime.setText("Date : "+ model.getDate()+" Time :"+model.getTime());
                        holder.userShippingAddress.setText("Shipping Address : "+model.getAddress() + " city :" +model.getCity());

                        holder.ShowOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(AdminNewOrdersActivity.this,AdminNewUserProductActivity.class);
                                intent.putExtra("uid",uID);
                                startActivity(intent);

                            }
                        });


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder bulider = new AlertDialog.Builder(AdminNewOrdersActivity.this);

                                bulider.setTitle("Have you Shipped this Order Product");

                                bulider.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(i==0){

                                            String uID = getRef(position).getKey();
                                            
                                            RemoveOrder(uID);

                                        }else{
                                            finish();
                                        }
                                    }
                                });

                                bulider.show();
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                        return new AdminOrderViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(String uID) {

        orderRef.child(uID).removeValue();
    }

    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder{


        public TextView userName , userPhoneNumber , userTotalPrice , userDateTime , userShippingAddress;
        public Button ShowOrderBtn;

        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userPhoneNumber = itemView.findViewById(R.id.Order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            ShowOrderBtn = itemView.findViewById(R.id.show_all_product_btn);
        }
    }
}