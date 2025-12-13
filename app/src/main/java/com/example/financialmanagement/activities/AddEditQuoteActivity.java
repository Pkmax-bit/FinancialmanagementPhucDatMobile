package com.example.financialmanagement.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.CreateQuoteItemAdapter;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.SalesApi;
import com.example.financialmanagement.services.QuoteService;
import com.example.financialmanagement.utils.CurrencyFormatter;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditQuoteActivity extends AppCompatActivity implements CreateQuoteItemAdapter.OnItemChangeListener {

    // Request Codes
    private static final int REQUEST_CODE_PICK_CUSTOMER = 1001;
    private static final int REQUEST_CODE_PICK_PRODUCT = 1002;
    private static final int REQUEST_CODE_PICK_PROJECT = 1003;
    private static final int REQUEST_CODE_PICK_EMPLOYEE = 1004;

    // UI Components
    private Button btnSelectCustomer;
    private Button btnSelectProject;
    private Button btnSelectEmployee;
    private TextInputEditText etQuoteDate, etExpiryDate;
    private RecyclerView rvItems;
    private Button btnAddProduct, btnCancel, btnSave;
    private TextView tvSubtotal, tvTotalAmount, tvTaxAmount;
    private EditText etTaxRate, etNotes;

    // Data
    private CreateQuoteItemAdapter adapter;
    private List<Quote.QuoteItem> quoteItems = new ArrayList<>();
    private Quote currentQuote = new Quote();
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    // Tax Rate default
    private double taxRate = 10.0;
    
    // Edit mode
    private boolean isEditMode = false;
    private String quoteIdToEdit = null;
    private QuoteService quoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_quote);

        // Check if editing
        quoteIdToEdit = getIntent().getStringExtra("quote_id");
        isEditMode = quoteIdToEdit != null && !quoteIdToEdit.isEmpty();
        
        initViews();
        setupListeners();
        
        if (isEditMode) {
            // Load quote data for editing
            loadQuoteForEdit();
        } else {
            // Setup defaults for new quote
            setupDefaults();
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        btnSelectCustomer = findViewById(R.id.btn_select_customer);
        btnSelectProject = findViewById(R.id.btn_select_project);
        btnSelectEmployee = findViewById(R.id.btn_select_employee);
        etQuoteDate = findViewById(R.id.et_quote_date);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        rvItems = findViewById(R.id.rv_items);
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvTaxAmount = findViewById(R.id.tv_tax_amount);
        etTaxRate = findViewById(R.id.et_tax_rate);
        etNotes = findViewById(R.id.et_notes);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CreateQuoteItemAdapter(quoteItems, this);
        rvItems.setAdapter(adapter);
        
        // Initialize service
        quoteService = new QuoteService(this);
        
        // Update toolbar title
        if (isEditMode) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sửa báo giá");
            }
        }
    }

    private void setupListeners() {
        // Customer Picker
        btnSelectCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerPickerActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PICK_CUSTOMER);
        });

        // Project Picker
        btnSelectProject.setOnClickListener(v -> {
            if (currentQuote.getCustomerId() == null) {
                Toast.makeText(this, "Vui lòng chọn khách hàng trước", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ProjectPickerActivity.class);
            intent.putExtra(ProjectPickerActivity.EXTRA_CUSTOMER_ID, currentQuote.getCustomerId());
            startActivityForResult(intent, REQUEST_CODE_PICK_PROJECT);
        });

        // Employee Picker
        btnSelectEmployee.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployeePickerActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PICK_EMPLOYEE);
        });

        // Product Picker
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductPickerActivity.class);
            intent.putExtra(ProductPickerActivity.EXTRA_MULTI_SELECT_MODE, true);
            startActivityForResult(intent, REQUEST_CODE_PICK_PRODUCT);
        });

        // Date Pickers
        etQuoteDate.setOnClickListener(v -> showDatePicker(etQuoteDate, true));
        etExpiryDate.setOnClickListener(v -> showDatePicker(etExpiryDate, false));

        // Tax Rate Change
        etTaxRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    taxRate = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    taxRate = 0;
                }
                calculateTotals();
            }
        });

        // Buttons
        btnCancel.setOnClickListener(v -> finish());
        
        Button btnSaveDraft = findViewById(R.id.btn_save_draft);
        btnSaveDraft.setOnClickListener(v -> saveQuote("draft"));

        btnSave.setOnClickListener(v -> saveQuote("sent"));
    }

    private void setupDefaults() {
        // Default Dates
        Date now = new Date();
        etQuoteDate.setText(dateFormat.format(now));
        currentQuote.setIssueDate(now);
        currentQuote.setQuoteDate(now);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 30); // Valid for 30 days
        Date expiry = cal.getTime();
        etExpiryDate.setText(dateFormat.format(expiry));
        currentQuote.setValidUntil(expiry);
        currentQuote.setExpiryDate(expiry);

        // Default Tax
        etTaxRate.setText("10");
        
        // Defaults
        currentQuote.setStatus("draft");
        currentQuote.setCurrency("VND");

        // Disable project picker initially
        btnSelectProject.setEnabled(false);
        btnSelectCustomer.setHint("Chọn khách hàng...");
        btnSelectProject.setHint("Chọn dự án...");
    }

    private void showDatePicker(TextView target, boolean isIssueDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                Date selectedDate = calendar.getTime();
                target.setText(dateFormat.format(selectedDate));
                
                if (isIssueDate) {
                    currentQuote.setIssueDate(selectedDate);
                    currentQuote.setQuoteDate(selectedDate);
                } else {
                    currentQuote.setValidUntil(selectedDate);
                    currentQuote.setExpiryDate(selectedDate);
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_PICK_CUSTOMER) {
                String id = data.getStringExtra(CustomerPickerActivity.EXTRA_CUSTOMER_ID);
                String name = data.getStringExtra(CustomerPickerActivity.EXTRA_CUSTOMER_NAME);
                
                // If customer changes, reset project
                if (currentQuote.getCustomerId() != null && !currentQuote.getCustomerId().equals(id)) {
                    currentQuote.setProjectId(null);
                    btnSelectProject.setText("");
                    btnSelectProject.setHint("Chọn dự án...");
                }
                
                currentQuote.setCustomerId(id);
                btnSelectCustomer.setText(name != null ? name : "Khách hàng");
                
                // Enable project picker
                btnSelectProject.setEnabled(true);
                
            } else if (requestCode == REQUEST_CODE_PICK_PROJECT) {
                String id = data.getStringExtra(ProjectPickerActivity.EXTRA_PROJECT_ID);
                String name = data.getStringExtra(ProjectPickerActivity.EXTRA_PROJECT_NAME);
                
                currentQuote.setProjectId(id);
                btnSelectProject.setText(name != null ? name : "Dự án");
                
            } else if (requestCode == REQUEST_CODE_PICK_PRODUCT) {
                try {
                    boolean hasJson = data.hasExtra(ProductPickerActivity.EXTRA_SELECTED_PRODUCTS_JSON);
                    
                    if (hasJson) {
                        String json = data.getStringExtra(ProductPickerActivity.EXTRA_SELECTED_PRODUCTS_JSON);
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<com.example.financialmanagement.models.Product>>(){}.getType();
                        List<com.example.financialmanagement.models.Product> products = gson.fromJson(json, type);
                        
                        if (products != null && !products.isEmpty()) {
                            for (com.example.financialmanagement.models.Product p : products) {
                                addQuoteItem(p.getId(), p.getName(), p.getUnitPrice() != null ? p.getUnitPrice() : 0, p.getUnit() != null ? p.getUnit() : "");
                            }
                        } else {
                            Toast.makeText(this, "Không có sản phẩm nào được chọn (List empty)", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Fallback to single select if needed
                        String id = data.getStringExtra(ProductPickerActivity.EXTRA_PRODUCT_ID);
                        String name = data.getStringExtra(ProductPickerActivity.EXTRA_PRODUCT_NAME);
                        double price = data.getDoubleExtra(ProductPickerActivity.EXTRA_PRODUCT_PRICE, 0);
                        String unit = data.getStringExtra(ProductPickerActivity.EXTRA_PRODUCT_UNIT);
                        addQuoteItem(id, name, price, unit != null ? unit : "");
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi khi xử lý sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(this, "Lỗi khi xử lý sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_PICK_EMPLOYEE) {
                String id = data.getStringExtra(EmployeePickerActivity.EXTRA_EMPLOYEE_ID);
                String name = data.getStringExtra(EmployeePickerActivity.EXTRA_EMPLOYEE_NAME);

                currentQuote.setEmployeeInChargeId(id);
                btnSelectEmployee.setText(name != null ? name : "Chọn nhân viên...");
            }
        }
    }

    private void addQuoteItem(String productId, String name, double price, String unit) {
        Quote.QuoteItem newItem = new Quote.QuoteItem();
        newItem.setProductServiceId(productId);
        newItem.setNameProduct(name);
        newItem.setUnitPrice(price);
        newItem.setQuantity(1.0);
        newItem.setUnit(unit);
        newItem.setTotalPrice(price * 1.0);
        // Default description to name
        newItem.setDescription(name);

        quoteItems.add(newItem);
        adapter.notifyItemInserted(quoteItems.size() - 1);
        calculateTotals();
    }

    @Override
    public void onItemRemove(int position) {
        quoteItems.remove(position);
        adapter.notifyItemRemoved(position);
        calculateTotals();
    }

    @Override
    public void onItemQuantityChanged(int position, double newQuantity) {
        // Deprecated callback, logic moved to onItemUpdated
        calculateTotals();
    }

    @Override
    public void onItemUpdated(int position) {
        // Called when any field (Quantity, Length, Width, Area) changes
        // Recalculate Totals
        calculateTotals();
        
        // Note: Adapter manages row fields binding/updates internally.
        // We only check totals for the entire quote here.
    }

    private void calculateTotals() {
        double subtotal = 0;
        for (Quote.QuoteItem item : quoteItems) {
            if (item.getTotalPrice() != null) {
                subtotal += item.getTotalPrice();
            }
        }

        currentQuote.setSubtotal(subtotal);
        currentQuote.setTaxRate(taxRate);
        
        double taxAmount = subtotal * (taxRate / 100);
        currentQuote.setTaxAmount(taxAmount);
        
        double total = subtotal + taxAmount;
        currentQuote.setTotalAmount(total);

        tvSubtotal.setText(CurrencyFormatter.format(subtotal));
        tvTaxAmount.setText(CurrencyFormatter.format(taxAmount));
        tvTotalAmount.setText(CurrencyFormatter.format(total));
    }

    private void loadQuoteForEdit() {
        Toast.makeText(this, "Đang tải thông tin báo giá...", Toast.LENGTH_SHORT).show();
        
        quoteService.getQuoteById(quoteIdToEdit, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                // Not used
            }

            @Override
            public void onSuccess(Quote quote) {
                runOnUiThread(() -> {
                    currentQuote = quote;
                    fillFormWithQuoteData(quote);
                });
            }

            @Override
            public void onSuccess() {
                // Not used
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AddEditQuoteActivity.this, "Lỗi tải báo giá: " + error, Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }
    
    private void fillFormWithQuoteData(Quote quote) {
        // Set quote ID
        currentQuote.setId(quote.getId());
        
        // Customer
        if (quote.getCustomerId() != null) {
            currentQuote.setCustomerId(quote.getCustomerId());
            if (quote.getCustomer() != null && quote.getCustomer().getName() != null) {
                btnSelectCustomer.setText(quote.getCustomer().getName());
            } else {
                btnSelectCustomer.setText("Khách hàng");
            }
            btnSelectProject.setEnabled(true);
        }
        
        // Project
        if (quote.getProjectId() != null) {
            currentQuote.setProjectId(quote.getProjectId());
            if (quote.getProject() != null && quote.getProject().getName() != null) {
                btnSelectProject.setText(quote.getProject().getName());
            } else {
                btnSelectProject.setText("Dự án");
            }
        }
        
        // Employee
        if (quote.getEmployeeInChargeId() != null) {
            currentQuote.setEmployeeInChargeId(quote.getEmployeeInChargeId());
            btnSelectEmployee.setText("Nhân viên");
        }
        
        // Dates
        if (quote.getIssueDate() != null) {
            etQuoteDate.setText(dateFormat.format(quote.getIssueDate()));
            currentQuote.setIssueDate(quote.getIssueDate());
            currentQuote.setQuoteDate(quote.getIssueDate());
        } else if (quote.getQuoteDate() != null) {
            etQuoteDate.setText(dateFormat.format(quote.getQuoteDate()));
            currentQuote.setIssueDate(quote.getQuoteDate());
            currentQuote.setQuoteDate(quote.getQuoteDate());
        }
        
        if (quote.getValidUntil() != null) {
            etExpiryDate.setText(dateFormat.format(quote.getValidUntil()));
            currentQuote.setValidUntil(quote.getValidUntil());
            currentQuote.setExpiryDate(quote.getValidUntil());
        } else if (quote.getExpiryDate() != null) {
            etExpiryDate.setText(dateFormat.format(quote.getExpiryDate()));
            currentQuote.setValidUntil(quote.getExpiryDate());
            currentQuote.setExpiryDate(quote.getExpiryDate());
        }
        
        // Tax Rate
        if (quote.getTaxRate() != null) {
            taxRate = quote.getTaxRate();
            etTaxRate.setText(String.format(Locale.getDefault(), "%.1f", taxRate));
        }
        
        // Notes/Description
        if (quote.getDescription() != null) {
            etNotes.setText(quote.getDescription());
        }
        
        // Items
        if (quote.getItems() != null && !quote.getItems().isEmpty()) {
            quoteItems.clear();
            quoteItems.addAll(quote.getItems());
            adapter.notifyDataSetChanged();
        }
        
        // Calculate totals
        calculateTotals();
        
        Toast.makeText(this, "Đã tải thông tin báo giá", Toast.LENGTH_SHORT).show();
    }

    private void saveQuote(String status) {
        // Validation
        if (currentQuote.getCustomerId() == null) {
            Toast.makeText(this, "Vui lòng chọn khách hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quoteItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare Data
        currentQuote.setItems(quoteItems);
        currentQuote.setDescription(etNotes.getText().toString());
        currentQuote.setStatus(status);
        
        if (isEditMode) {
            // Update existing quote
            updateQuote();
        } else {
            // Create new quote
            createQuote(status);
        }
    }
    
    private void createQuote(String status) {
        // Quote number is generated by backend if not unique check logic, 
        // but here we send one.
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        currentQuote.setQuoteNumber("QUO-M-" + timeStamp);
        
        // Call API
        SalesApi salesApi = ApiClient.getSalesApi(this);
        Call<Quote> call = salesApi.createQuote(currentQuote);

        Toast.makeText(this, status.equals("draft") ? "Đang lưu nháp..." : "Đang tạo và gửi...", Toast.LENGTH_SHORT).show();
        btnSave.setEnabled(false);
        findViewById(R.id.btn_save_draft).setEnabled(false);

        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                btnSave.setEnabled(true);
                findViewById(R.id.btn_save_draft).setEnabled(true);
                if (response.isSuccessful()) {
                    String msg = status.equals("draft") ? "Đã lưu nháp báo giá!" : "Tạo và gửi báo giá thành công!";
                    Toast.makeText(AddEditQuoteActivity.this, msg, Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddEditQuoteActivity.this, "Lỗi: " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                btnSave.setEnabled(true);
                findViewById(R.id.btn_save_draft).setEnabled(true);
                Toast.makeText(AddEditQuoteActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void updateQuote() {
        Toast.makeText(this, "Đang cập nhật báo giá...", Toast.LENGTH_SHORT).show();
        btnSave.setEnabled(false);
        findViewById(R.id.btn_save_draft).setEnabled(false);
        
        quoteService.updateQuote(quoteIdToEdit, currentQuote, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                // Not used
            }

            @Override
            public void onSuccess(Quote quote) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    findViewById(R.id.btn_save_draft).setEnabled(true);
                    Toast.makeText(AddEditQuoteActivity.this, "Cập nhật báo giá thành công!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onSuccess() {
                // Not used
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    findViewById(R.id.btn_save_draft).setEnabled(true);
                    Toast.makeText(AddEditQuoteActivity.this, "Lỗi cập nhật: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
