package com.example.financialmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Product;
import com.example.financialmanagement.models.ProductCategory;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.ProductApi;
import com.example.financialmanagement.network.api.ProductCategoryApi;
import com.example.financialmanagement.utils.ErrorHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditProductActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "com.example.financialmanagement.EXTRA_PRODUCT_ID";

    private TextInputEditText etName, etSku, etPrice, etUnit, etDescription;
    private Spinner spinnerCategory;
    private MaterialButton btnSave;
    
    private ProductApi productApi;
    private ProductCategoryApi categoryApi;
    private String productId;
    private List<ProductCategory> categories = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        checkIntent();
        loadCategories();
    }

    private void initializeViews() {
        etName = findViewById(R.id.et_product_name);
        etSku = findViewById(R.id.et_product_sku);
        etPrice = findViewById(R.id.et_product_price);
        etUnit = findViewById(R.id.et_product_unit);
        etDescription = findViewById(R.id.et_product_description);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSave = findViewById(R.id.btn_save_product);

        productApi = ApiClient.getRetrofit(this).create(ProductApi.class);
        categoryApi = ApiClient.getRetrofit(this).create(ProductCategoryApi.class);

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void checkIntent() {
        if (getIntent().hasExtra(EXTRA_PRODUCT_ID)) {
            String id = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
            if (id != null && !id.isEmpty()) {
                productId = id;
                setTitle("Chỉnh sửa sản phẩm");
                loadProductDetails(productId);
            } else {
                setTitle("Thêm sản phẩm mới");
            }
        } else {
            setTitle("Thêm sản phẩm mới");
        }
    }

    private void loadCategories() {
        categoryApi.getAllCategories().enqueue(new Callback<List<ProductCategory>>() {
            @Override
            public void onResponse(Call<List<ProductCategory>> call, Response<List<ProductCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("Chọn loại sản phẩm");
                    for (ProductCategory cat : categories) {
                        categoryNames.add(cat.getName());
                    }
                    
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddEditProductActivity.this,
                            android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    Toast.makeText(AddEditProductActivity.this, ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductCategory>> call, Throwable t) {
                Toast.makeText(AddEditProductActivity.this, ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetails(String id) {
        productApi.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    etName.setText(product.getName());
                    etSku.setText(product.getSku());
                    etUnit.setText(product.getUnit());
                    etDescription.setText(product.getDescription());
                    if (product.getUnitPrice() != null) {
                        etPrice.setText(String.valueOf(product.getUnitPrice()));
                    }
                    
                    // Set spinner selection based on category_id
                    if (product.getCategoryId() != null) {
                        for (int i = 0; i < categories.size(); i++) {
                            if (categories.get(i).getId().equals(product.getCategoryId())) {
                                spinnerCategory.setSelection(i + 1); // +1 because of default item
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(AddEditProductActivity.this, "Lỗi tải sản phẩm: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(AddEditProductActivity.this, "Lỗi tải sản phẩm: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String sku = etSku.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        
        if (name.isEmpty()) {
            etName.setError("Vui lòng nhập tên sản phẩm");
            return;
        }

        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setUnit(unit);
        product.setDescription(desc);
        
        if (!priceStr.isEmpty()) {
            product.setUnitPrice(Double.parseDouble(priceStr));
        }

        // Set category_id instead of category name
        if (spinnerCategory.getSelectedItemPosition() > 0) {
            ProductCategory selectedCategory = categories.get(spinnerCategory.getSelectedItemPosition() - 1);
            product.setCategoryId(selectedCategory.getId());
            product.setCategory(selectedCategory.getName()); // Also set name for display purposes
        }

        if (productId != null) {
            product.setId(productId);
            productApi.updateProduct(productId, product).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(AddEditProductActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddEditProductActivity.this, "Cập nhật thất bại: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Toast.makeText(AddEditProductActivity.this, "Lỗi kết nối: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            productApi.createProduct(product).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(AddEditProductActivity.this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddEditProductActivity.this, "Thêm mới thất bại: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Toast.makeText(AddEditProductActivity.this, "Lỗi kết nối: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
