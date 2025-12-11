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
import com.example.financialmanagement.adapters.CreateInvoiceItemAdapter;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.SalesApi;
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

public class AddEditInvoiceActivity extends AppCompatActivity implements CreateInvoiceItemAdapter.OnItemChangeListener {

    // Request Codes
    private static final int REQUEST_CODE_PICK_CUSTOMER = 1001;
    private static final int REQUEST_CODE_PICK_PRODUCT = 1002;
    private static final int REQUEST_CODE_PICK_PROJECT = 1003;

    // UI Components
    private TextView tvCustomerName, tvProjectName;
    private View containerCustomer, containerProject;
    private TextInputEditText etIssueDate, etDueDate;
    private RecyclerView rvItems;
    private Button btnAddProduct, btnCancel, btnSave;
    private TextView tvSubtotal, tvTotalAmount, tvTaxAmount;
    private EditText etTaxRate, etNotes;
    private android.widget.AutoCompleteTextView tvInvoiceType;
    private TextInputEditText etPaymentTerms;

    // Data
    private CreateInvoiceItemAdapter adapter;
    private List<Invoice.InvoiceItem> invoiceItems = new ArrayList<>();
    private Invoice currentInvoice = new Invoice();
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    // Tax Rate default
    private double taxRate = 10.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_invoice);

        initViews();
        setupListeners();
        setupDefaults();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvCustomerName = findViewById(R.id.tv_customer_name);
        containerCustomer = findViewById(R.id.container_customer);
        tvProjectName = findViewById(R.id.tv_project_name);
        containerProject = findViewById(R.id.container_project);
        
        etIssueDate = findViewById(R.id.et_issue_date);
        etDueDate = findViewById(R.id.et_due_date);
        rvItems = findViewById(R.id.rv_items);
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvTaxAmount = findViewById(R.id.tv_tax_amount);
        etTaxRate = findViewById(R.id.et_tax_rate);
        etNotes = findViewById(R.id.et_notes);
        tvInvoiceType = findViewById(R.id.tv_invoice_type);
        etPaymentTerms = findViewById(R.id.et_payment_terms);

        // Setup dropdown for Invoice Type
        String[] invoiceTypes = new String[]{"Hóa đơn thường", "Hóa đơn định kỳ", "Hóa đơn tạm", "Phiếu ghi có"};
        android.widget.ArrayAdapter<String> typeAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, invoiceTypes);
        tvInvoiceType.setAdapter(typeAdapter);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CreateInvoiceItemAdapter(invoiceItems, this);
        rvItems.setAdapter(adapter);
    }

    private void setupListeners() {
        // Customer Picker
        containerCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerPickerActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PICK_CUSTOMER);
        });

        // Project Picker
        containerProject.setOnClickListener(v -> {
             if (currentInvoice.getCustomerId() == null) {
                Toast.makeText(this, "Vui lòng chọn khách hàng trước", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ProjectPickerActivity.class);
            intent.putExtra(ProjectPickerActivity.EXTRA_CUSTOMER_ID, currentInvoice.getCustomerId());
            startActivityForResult(intent, REQUEST_CODE_PICK_PROJECT);
        });

        // Product Picker
        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductPickerActivity.class);
            intent.putExtra(ProductPickerActivity.EXTRA_MULTI_SELECT_MODE, true);
            startActivityForResult(intent, REQUEST_CODE_PICK_PRODUCT);
        });

        // Date Pickers
        etIssueDate.setOnClickListener(v -> showDatePicker(etIssueDate, true));
        etDueDate.setOnClickListener(v -> showDatePicker(etDueDate, false));

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
        btnSaveDraft.setOnClickListener(v -> saveInvoice("draft"));
        
        btnSave.setOnClickListener(v -> saveInvoice("sent"));
    }

    private void setupDefaults() {
        // Default Dates
        Date now = new Date();
        etIssueDate.setText(dateFormat.format(now));
        currentInvoice.setIssueDate(now);
        currentInvoice.setInvoiceNumber("INV-M-" + System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 30); // Default due date 30 days
        Date due = cal.getTime();
        etDueDate.setText(dateFormat.format(due));
        currentInvoice.setDueDate(due);

        // Default Tax
        etTaxRate.setText("10");
        
        // Defaults
        currentInvoice.setStatus("draft");
        currentInvoice.setCurrency("VND");
        
        // Hide project picker initially
        containerProject.setVisibility(View.GONE);
    }

    private void showDatePicker(TextView target, boolean isIssueDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                Date selectedDate = calendar.getTime();
                target.setText(dateFormat.format(selectedDate));
                
                if (isIssueDate) {
                    currentInvoice.setIssueDate(selectedDate);
                } else {
                    currentInvoice.setDueDate(selectedDate);
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
                if (currentInvoice.getCustomerId() != null && !currentInvoice.getCustomerId().equals(id)) {
                    currentInvoice.setProjectId(null);
                    tvProjectName.setText("Chọn dự án...");
                }
                
                currentInvoice.setCustomerId(id);
                // Also set Customer object if model requires, by backend usually only needs ID.
                tvCustomerName.setText(name != null ? name : "Khách hàng");
                
                // Show project picker
                containerProject.setVisibility(View.VISIBLE);
                
            } else if (requestCode == REQUEST_CODE_PICK_PROJECT) {
                String id = data.getStringExtra(ProjectPickerActivity.EXTRA_PROJECT_ID);
                String name = data.getStringExtra(ProjectPickerActivity.EXTRA_PROJECT_NAME);
                
                currentInvoice.setProjectId(id);
                tvProjectName.setText(name != null ? name : "Dự án");
                
            } else if (requestCode == REQUEST_CODE_PICK_PRODUCT) {
                try {
                    if (data.hasExtra(ProductPickerActivity.EXTRA_SELECTED_PRODUCTS_JSON)) {
                        String json = data.getStringExtra(ProductPickerActivity.EXTRA_SELECTED_PRODUCTS_JSON);
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<com.example.financialmanagement.models.Product>>(){}.getType();
                        List<com.example.financialmanagement.models.Product> products = gson.fromJson(json, type);
                        
                        if (products != null) {
                            for (com.example.financialmanagement.models.Product p : products) {
                                addInvoiceItem(p.getId(), p.getName(), p.getUnitPrice() != null ? p.getUnitPrice() : 0, p.getUnit() != null ? p.getUnit() : "");
                            }
                        }
                    } else {
                        String id = data.getStringExtra(ProductPickerActivity.EXTRA_PRODUCT_ID);
                        String name = data.getStringExtra(ProductPickerActivity.EXTRA_PRODUCT_NAME);
                        double price = data.getDoubleExtra(ProductPickerActivity.EXTRA_PRODUCT_PRICE, 0);
                        String unit = data.getStringExtra(ProductPickerActivity.EXTRA_PRODUCT_UNIT);
                        addInvoiceItem(id, name, price, unit != null ? unit : "");
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi khi thêm sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void addInvoiceItem(String productId, String name, double price, String unit) {
        Invoice.InvoiceItem newItem = new Invoice.InvoiceItem();
        newItem.setProductServiceId(productId);
        newItem.setNameProduct(name);
        newItem.setUnitPrice(price);
        newItem.setQuantity(1.0);
        newItem.setUnit(unit);
        newItem.setTotalPrice(price * 1.0);
        newItem.setDescription(name);

        invoiceItems.add(newItem);
        adapter.notifyItemInserted(invoiceItems.size() - 1);
        calculateTotals();
    }

    @Override
    public void onItemRemove(int position) {
        invoiceItems.remove(position);
        adapter.notifyItemRemoved(position);
        calculateTotals();
    }

    @Override
    public void onItemQuantityChanged(int position, double newQuantity) {
        if (position >= 0 && position < invoiceItems.size()) {
            Invoice.InvoiceItem item = invoiceItems.get(position);
            item.setQuantity(newQuantity);
            if (item.getUnitPrice() != null) {
                item.setTotalPrice(newQuantity * item.getUnitPrice());
            }
            adapter.notifyItemChanged(position);
            calculateTotals();
        }
    }

    @Override
    public void onItemUpdated(int position) {
        // Called when any field (Quantity, Length, Width, Area) changes
        // Recalculate Totals
        calculateTotals();
    }

    private void calculateTotals() {
        double subtotal = 0;
        for (Invoice.InvoiceItem item : invoiceItems) {
            if (item.getTotalPrice() != null) {
                subtotal += item.getTotalPrice();
            }
        }

        currentInvoice.setSubtotal(subtotal);
        currentInvoice.setTaxRate(taxRate);
        
        double taxAmount = subtotal * (taxRate / 100);
        currentInvoice.setTaxAmount(taxAmount);
        
        double total = subtotal + taxAmount;
        currentInvoice.setTotalAmount(total);

        tvSubtotal.setText(CurrencyFormatter.format(subtotal));
        tvTaxAmount.setText(CurrencyFormatter.format(taxAmount));
        tvTotalAmount.setText(CurrencyFormatter.format(total));
    }

    private void saveInvoice(String status) {
        // Validation
        if (currentInvoice.getCustomerId() == null) {
            Toast.makeText(this, "Vui lòng chọn khách hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (invoiceItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare Data
        currentInvoice.setItems(invoiceItems);
        currentInvoice.setDescription(etNotes.getText().toString());
        currentInvoice.setTitle("Hóa đơn mới");
        currentInvoice.setStatus(status);
        
        // Map UI invoice type to backend value code
        String uiType = tvInvoiceType.getText().toString();
        String typeCode = "standard";
        if (uiType.equals("Hóa đơn định kỳ")) typeCode = "recurring";
        else if (uiType.equals("Hóa đơn tạm")) typeCode = "proforma";
        else if (uiType.equals("Phiếu ghi có")) typeCode = "credit_note";
        currentInvoice.setInvoiceType(typeCode);
        
        currentInvoice.setPaymentTerms(etPaymentTerms.getText().toString());

        // Unique ID logic
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        currentInvoice.setInvoiceNumber("INV-M-" + timeStamp);

        // Call API
        SalesApi salesApi = ApiClient.getSalesApi(this);
        Call<Invoice> call = salesApi.createInvoice(currentInvoice);

        Toast.makeText(this, status.equals("draft") ? "Đang lưu nháp..." : "Đang tạo và gửi...", Toast.LENGTH_SHORT).show();
        btnSave.setEnabled(false);
        findViewById(R.id.btn_save_draft).setEnabled(false);

        call.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                btnSave.setEnabled(true);
                findViewById(R.id.btn_save_draft).setEnabled(true);
                if (response.isSuccessful()) {
                    String msg = status.equals("draft") ? "Đã lưu nháp hóa đơn!" : "Tạo và gửi hóa đơn thành công!";
                    Toast.makeText(AddEditInvoiceActivity.this, msg, Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddEditInvoiceActivity.this, "Lỗi: " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                btnSave.setEnabled(true);
                findViewById(R.id.btn_save_draft).setEnabled(true);
                Toast.makeText(AddEditInvoiceActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
