package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.ProductCategory;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.ProductCategoryApi;
import com.example.financialmanagement.utils.ErrorHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditProductCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_ID = "com.example.financialmanagement.EXTRA_CATEGORY_ID";

    private TextInputEditText etName, etDescription;
    private MaterialButton btnSave;
    
    private ProductCategoryApi categoryApi;
    private String categoryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product_category);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        checkIntent();
    }

    private void initializeViews() {
        etName = findViewById(R.id.et_category_name);
        etDescription = findViewById(R.id.et_category_description);
        btnSave = findViewById(R.id.btn_save_category);

        categoryApi = ApiClient.getRetrofit(this).create(ProductCategoryApi.class);

        btnSave.setOnClickListener(v -> saveCategory());
    }

    private void checkIntent() {
        if (getIntent().hasExtra(EXTRA_CATEGORY_ID)) {
            categoryId = getIntent().getStringExtra(EXTRA_CATEGORY_ID);
            setTitle("Chỉnh sửa loại sản phẩm");
            loadCategoryDetails(categoryId);
        } else {
            setTitle("Thêm loại sản phẩm mới");
        }
    }

    private void loadCategoryDetails(String id) {
        categoryApi.getCategoryById(id).enqueue(new Callback<ProductCategory>() {
            @Override
            public void onResponse(Call<ProductCategory> call, Response<ProductCategory> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ProductCategory cat = response.body();
                    etName.setText(cat.getName());
                    etDescription.setText(cat.getDescription());
                } else {
                    Toast.makeText(AddEditProductCategoryActivity.this, "Lỗi tải thông tin: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ProductCategory> call, Throwable t) {
                Toast.makeText(AddEditProductCategoryActivity.this, "Lỗi tải thông tin: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCategory() {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        
        if (name.isEmpty()) {
            etName.setError("Vui lòng nhập tên loại sản phẩm");
            return;
        }

        ProductCategory category = new ProductCategory();
        category.setName(name);
        category.setDescription(desc);

        if (categoryId != null) {
            // Update
            category.setId(categoryId);
            categoryApi.updateCategory(categoryId, category).enqueue(new Callback<ProductCategory>() {
                @Override
                public void onResponse(Call<ProductCategory> call, Response<ProductCategory> response) {
                    if (response.isSuccessful()) {
                         Toast.makeText(AddEditProductCategoryActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                         setResult(RESULT_OK);
                         finish();
                    } else {
                        Toast.makeText(AddEditProductCategoryActivity.this, "Lỗi: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ProductCategory> call, Throwable t) {
                    Toast.makeText(AddEditProductCategoryActivity.this, "Lỗi: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create
            categoryApi.createCategory(category).enqueue(new Callback<ProductCategory>() {
                @Override
                public void onResponse(Call<ProductCategory> call, Response<ProductCategory> response) {
                    if (response.isSuccessful()) {
                         Toast.makeText(AddEditProductCategoryActivity.this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
                         setResult(RESULT_OK);
                         finish();
                    } else {
                        Toast.makeText(AddEditProductCategoryActivity.this, "Lỗi: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ProductCategory> call, Throwable t) {
                    Toast.makeText(AddEditProductCategoryActivity.this, "Lỗi: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
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
