package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.MaterialAdjustmentRule;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.ProductRuleApi;
import com.example.financialmanagement.utils.ErrorHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditProductRuleActivity extends AppCompatActivity {

    public static final String EXTRA_RULE_ID = "com.example.financialmanagement.EXTRA_RULE_ID";

    private TextInputEditText etName, etValue, etDescription;
    private Spinner spinnerType;
    private MaterialButton btnSave;
    
    private ProductRuleApi ruleApi;
    private String ruleId;
    
    private static final String[] RULE_TYPES = {"FIXED", "PERCENTAGE"};
    private static final String[] RULE_TYPE_LABELS = {"Cố định (Fixed)", "Phần trăm (%)"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product_rule);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();
        checkIntent();
    }

    private void initializeViews() {
        etName = findViewById(R.id.et_rule_name);
        etValue = findViewById(R.id.et_rule_value);
        etDescription = findViewById(R.id.et_rule_description);
        spinnerType = findViewById(R.id.spinner_rule_type);
        btnSave = findViewById(R.id.btn_save_rule);

        ruleApi = ApiClient.getRetrofit(this).create(ProductRuleApi.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, RULE_TYPE_LABELS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveRule());
    }

    private void checkIntent() {
        if (getIntent().hasExtra(EXTRA_RULE_ID)) {
            ruleId = getIntent().getStringExtra(EXTRA_RULE_ID);
            setTitle("Chỉnh sửa quy tắc");
            loadRuleDetails(ruleId);
        } else {
            setTitle("Thêm quy tắc mới");
        }
    }

    private void loadRuleDetails(String id) {
        ruleApi.getRuleById(id).enqueue(new Callback<MaterialAdjustmentRule>() {
            @Override
            public void onResponse(Call<MaterialAdjustmentRule> call, Response<MaterialAdjustmentRule> response) {
                if(response.isSuccessful() && response.body() != null) {
                    MaterialAdjustmentRule rule = response.body();
                    etName.setText(rule.getName());
                    etDescription.setText(rule.getDescription());
                    if(rule.getAdjustmentValue() != null) {
                        etValue.setText(String.valueOf(rule.getAdjustmentValue()));
                    }
                    
                    if(rule.getAdjustmentType() != null) {
                        for(int i=0; i<RULE_TYPES.length; i++) {
                            if(RULE_TYPES[i].equalsIgnoreCase(rule.getAdjustmentType())) {
                                spinnerType.setSelection(i);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(AddEditProductRuleActivity.this, "Lỗi tải thông tin: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<MaterialAdjustmentRule> call, Throwable t) {
                 Toast.makeText(AddEditProductRuleActivity.this, "Lỗi tải thông tin: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRule() {
        String name = etName.getText().toString().trim();
        String valStr = etValue.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        
        if (name.isEmpty()) {
            etName.setError("Vui lòng nhập tên quy tắc");
            return;
        }

        MaterialAdjustmentRule rule = new MaterialAdjustmentRule();
        rule.setName(name);
        rule.setDescription(desc);
        
        if (!valStr.isEmpty()) {
            rule.setAdjustmentValue(Double.parseDouble(valStr));
        }
        
        rule.setAdjustmentType(RULE_TYPES[spinnerType.getSelectedItemPosition()]);

        if (ruleId != null) {
            // Update
            rule.setId(ruleId);
            ruleApi.updateRule(ruleId, rule).enqueue(new Callback<MaterialAdjustmentRule>() {
                 @Override
                 public void onResponse(Call<MaterialAdjustmentRule> call, Response<MaterialAdjustmentRule> response) {
                     if (response.isSuccessful()) {
                         Toast.makeText(AddEditProductRuleActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                         setResult(RESULT_OK);
                         finish();
                     } else {
                         Toast.makeText(AddEditProductRuleActivity.this, "Lỗi: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                     }
                 }
                 @Override
                 public void onFailure(Call<MaterialAdjustmentRule> call, Throwable t) {
                     Toast.makeText(AddEditProductRuleActivity.this, "Lỗi: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
                 }
            });
        } else {
            // Create
            ruleApi.createRule(rule).enqueue(new Callback<MaterialAdjustmentRule>() {
                 @Override
                 public void onResponse(Call<MaterialAdjustmentRule> call, Response<MaterialAdjustmentRule> response) {
                     if (response.isSuccessful()) {
                         Toast.makeText(AddEditProductRuleActivity.this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
                         setResult(RESULT_OK);
                         finish();
                     } else {
                         Toast.makeText(AddEditProductRuleActivity.this, "Lỗi: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                     }
                 }
                 @Override
                 public void onFailure(Call<MaterialAdjustmentRule> call, Throwable t) {
                     Toast.makeText(AddEditProductRuleActivity.this, "Lỗi: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
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
