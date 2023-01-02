package com.cadenza.ecommerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SearchProductActivity extends AppCompatActivity {

    private Button SearchBtn;
    private EditText inputText;
    private RecyclerView searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        SearchBtn = findViewById(R.id.search_btn);
        inputText = findViewById(R.id.search_product_name);
        searchList = findViewById(R.id.search_List);

        searchList.setLayoutManager(new LinearLayoutManager(SearchProductActivity.this));


    }
}