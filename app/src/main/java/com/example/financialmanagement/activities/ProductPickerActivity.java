package com.example.financialmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.ProductAdapter;
import com.example.financialmanagement.models.Product;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.ProductApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductPickerActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_PRODUCT_NAME = "extra_product_name";
    public static final String EXTRA_PRODUCT_PRICE = "extra_product_price";
    public static final String EXTRA_PRODUCT_UNIT = "extra_product_unit";
    
    public static final String EXTRA_MULTI_SELECT_MODE = "extra_multi_select_mode";
    public static final String EXTRA_SELECTED_PRODUCTS_JSON = "extra_selected_products_json";

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private EditText etSearch;
    private FloatingActionButton fabDone;
    private List<Product> allProducts = new ArrayList<>();
    private boolean isMultiSelectMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_picker);

        isMultiSelectMode = getIntent().getBooleanExtra(EXTRA_MULTI_SELECT_MODE, false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (isMultiSelectMode) {
            getSupportActionBar().setTitle("Chọn nhiều sản phẩm");
        } else {
            getSupportActionBar().setTitle("Chọn sản phẩm");
        }

        rvProducts = findViewById(R.id.rv_products);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tv_empty);
        etSearch = findViewById(R.id.et_search);
        fabDone = findViewById(R.id.fab_done);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(new ArrayList<>(), this);
        adapter.setMultiSelectMode(isMultiSelectMode);
        rvProducts.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if (isMultiSelectMode) {
            fabDone.setVisibility(View.VISIBLE);
            fabDone.setOnClickListener(v -> finishMultiSelect());
        } else {
            fabDone.setVisibility(View.GONE);
        }

        loadProducts();
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        ProductApi productApi = ApiClient.getRetrofit(this).create(ProductApi.class);
        Map<String, Object> params = new HashMap<>();
        // Fetch all products
        Call<List<Product>> call = productApi.getAllProducts(params);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    updateList(allProducts);
                } else {
                    Toast.makeText(ProductPickerActivity.this, "Không thể tải danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductPickerActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : allProducts) {
            String name = product.getName() != null ? product.getName().toLowerCase() : "";
            String sku = product.getSku() != null ? product.getSku().toLowerCase() : "";
            
            if (name.contains(query.toLowerCase()) || sku.contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        updateList(filteredList);
    }

    private void updateList(List<Product> products) {
        adapter.updateProducts(products);
        if (products.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }
    }

    private void finishMultiSelect() {
        try {
            List<Product> selectedProducts = adapter.getSelectedProducts();
            if (selectedProducts.isEmpty()) {
                Toast.makeText(this, "Chưa chọn sản phẩm nào", Toast.LENGTH_SHORT).show();
                return;
            }

            Gson gson = new Gson();
            String json = gson.toJson(selectedProducts);
            
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_SELECTED_PRODUCTS_JSON, json);
            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi chọn sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onProductClick(Product product) {
        // Only handles single click if NOT multi-select mode
        if (!isMultiSelectMode) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_PRODUCT_ID, product.getId());
            resultIntent.putExtra(EXTRA_PRODUCT_NAME, product.getName());
            resultIntent.putExtra(EXTRA_PRODUCT_PRICE, product.getUnitPrice());
            resultIntent.putExtra(EXTRA_PRODUCT_UNIT, product.getUnit());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
