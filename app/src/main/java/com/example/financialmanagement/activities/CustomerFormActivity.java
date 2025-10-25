package com.example.financialmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.services.CustomerService;
import com.example.financialmanagement.utils.ApiDebugger;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.ProgressBar;
import java.util.List;

/**
 * Customer Form Activity - Màn hình thêm/sửa khách hàng
 * Hỗ trợ cả tạo mới và cập nhật khách hàng
 */
public class CustomerFormActivity extends AppCompatActivity {

    public static final String EXTRA_CUSTOMER = "customer";
    public static final String EXTRA_MODE = "mode";
    public static final String MODE_CREATE = "create";
    public static final String MODE_EDIT = "edit";

    private TextInputEditText etCustomerCode;
    private TextInputEditText etCustomerName;
    private Spinner spinnerCustomerType;
    private TextInputEditText etCustomerEmail;
    private TextInputEditText etCustomerPhone;
    private TextInputEditText etCustomerAddress;
    private TextInputEditText etCustomerCity;
    private TextInputEditText etCustomerCountry;
    private TextInputEditText etCustomerTaxId;
    private TextInputEditText etCustomerCreditLimit;
    private TextInputEditText etCustomerPaymentTerms;
    private TextInputEditText etCustomerNotes;
    
    // Additional fields for different customer types
    private com.google.android.material.textfield.TextInputLayout layoutCompanyRegistration;
    private com.google.android.material.textfield.TextInputLayout layoutGovernmentCode;
    private com.google.android.material.textfield.TextInputLayout layoutContactPerson;
    private TextInputEditText etCompanyRegistration;
    private TextInputEditText etGovernmentCode;
    private TextInputEditText etContactPerson;
    
    private MaterialButton btnSave;
    private MaterialButton btnCancel;
    private ProgressBar progressBar;
    
    private CustomerService customerService;
    private Customer customer;
    private String mode;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_form);
        
        initializeViews();
        setupToolbar();
        setupForm();
        setupListeners();
    }

    private void initializeViews() {
        etCustomerCode = findViewById(R.id.et_customer_code);
        etCustomerName = findViewById(R.id.et_customer_name);
        spinnerCustomerType = findViewById(R.id.spinner_customer_type);
        etCustomerEmail = findViewById(R.id.et_customer_email);
        etCustomerPhone = findViewById(R.id.et_customer_phone);
        etCustomerAddress = findViewById(R.id.et_customer_address);
        etCustomerCity = findViewById(R.id.et_customer_city);
        etCustomerCountry = findViewById(R.id.et_customer_country);
        etCustomerTaxId = findViewById(R.id.et_customer_tax_id);
        etCustomerCreditLimit = findViewById(R.id.et_customer_credit_limit);
        etCustomerPaymentTerms = findViewById(R.id.et_customer_payment_terms);
        etCustomerNotes = findViewById(R.id.et_customer_notes);
        
        // Initialize additional fields
        layoutCompanyRegistration = findViewById(R.id.layout_company_registration);
        layoutGovernmentCode = findViewById(R.id.layout_government_code);
        layoutContactPerson = findViewById(R.id.layout_contact_person);
        etCompanyRegistration = findViewById(R.id.et_company_registration);
        etGovernmentCode = findViewById(R.id.et_government_code);
        etContactPerson = findViewById(R.id.et_contact_person);
        
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progress_bar);
        
        customerService = new CustomerService(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupForm() {
        // Get mode and customer data
        Intent intent = getIntent();
        mode = intent.getStringExtra(EXTRA_MODE);
        customer = (Customer) intent.getSerializableExtra(EXTRA_CUSTOMER);
        isEditMode = MODE_EDIT.equals(mode);
        
        // Set title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(isEditMode ? "Sửa khách hàng" : "Thêm khách hàng");
        }
        
        // Setup customer type dropdown
        setupCustomerTypeDropdown();
        
        // Populate form if editing
        if (isEditMode && customer != null) {
            populateForm();
        } else {
            // Generate customer code for new customer
            generateCustomerCode();
        }
    }

    private void setupCustomerTypeDropdown() {
        String[] customerTypes = {"Cá nhân", "Công ty", "Cơ quan nhà nước"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, customerTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCustomerType.setAdapter(adapter);
        
        // Set default value
        spinnerCustomerType.setSelection(0); // "Cá nhân"
        
        // Add listener for customer type changes
        spinnerCustomerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = customerTypes[position];
                updateFieldsVisibility(selectedType);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        
        // Initialize with default visibility
        updateFieldsVisibility("Cá nhân");
    }
    
    /**
     * Update field visibility based on customer type
     */
    private void updateFieldsVisibility(String customerType) {
        // Hide all additional fields first
        layoutCompanyRegistration.setVisibility(android.view.View.GONE);
        layoutGovernmentCode.setVisibility(android.view.View.GONE);
        layoutContactPerson.setVisibility(android.view.View.GONE);
        
        // Show relevant fields based on customer type
        switch (customerType) {
            case "Cá nhân":
                // Individual customers - no additional fields
                break;
                
            case "Công ty":
                // Company customers - show company registration and contact person
                layoutCompanyRegistration.setVisibility(android.view.View.VISIBLE);
                layoutContactPerson.setVisibility(android.view.View.VISIBLE);
                break;
                
            case "Cơ quan nhà nước":
                // Government customers - show government code and contact person
                layoutGovernmentCode.setVisibility(android.view.View.VISIBLE);
                layoutContactPerson.setVisibility(android.view.View.VISIBLE);
                break;
        }
    }

    private void populateForm() {
        if (customer == null) return;
        
        etCustomerCode.setText(customer.getCustomerCode());
        etCustomerName.setText(customer.getName());
        // This line is now handled in the populateForm method above
        etCustomerEmail.setText(customer.getEmail());
        etCustomerPhone.setText(customer.getPhone());
        etCustomerAddress.setText(customer.getAddress());
        etCustomerCity.setText(customer.getCity());
        etCustomerCountry.setText(customer.getCountry());
        etCustomerTaxId.setText(customer.getTaxId());
        
        if (customer.getCreditLimit() != null) {
            etCustomerCreditLimit.setText(customer.getCreditLimit().toString());
        }
        
        if (customer.getPaymentTerms() != null) {
            etCustomerPaymentTerms.setText(customer.getPaymentTerms().toString());
        }
        
        etCustomerNotes.setText(customer.getNotes());
        
        // Update field visibility based on customer type
        String customerTypeDisplay = customer.getTypeDisplayName();
        
        // Set spinner selection based on customer type
        String[] customerTypes = {"Cá nhân", "Công ty", "Cơ quan nhà nước"};
        for (int i = 0; i < customerTypes.length; i++) {
            if (customerTypes[i].equals(customerTypeDisplay)) {
                spinnerCustomerType.setSelection(i);
                break;
            }
        }
        
        updateFieldsVisibility(customerTypeDisplay);
        
        // Parse additional information from notes if available
        if (customer.getNotes() != null) {
            String notes = customer.getNotes();
            
            // Extract company registration number
            if (notes.contains("Mã đăng ký KD:")) {
                String[] parts = notes.split("Mã đăng ký KD:");
                if (parts.length > 1) {
                    String regNumber = parts[1].split("\n")[0].trim();
                    etCompanyRegistration.setText(regNumber);
                }
            }
            
            // Extract government code
            if (notes.contains("Mã cơ quan:")) {
                String[] parts = notes.split("Mã cơ quan:");
                if (parts.length > 1) {
                    String govCode = parts[1].split("\n")[0].trim();
                    etGovernmentCode.setText(govCode);
                }
            }
            
            // Extract contact person
            if (notes.contains("Người liên hệ:")) {
                String[] parts = notes.split("Người liên hệ:");
                if (parts.length > 1) {
                    String contactPerson = parts[1].split("\n")[0].trim();
                    etContactPerson.setText(contactPerson);
                }
            }
        }
    }

    private void generateCustomerCode() {
        // Generate a simple customer code (in real app, this would be from server)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String customerCode = "CUST" + timestamp.substring(timestamp.length() - 6);
        etCustomerCode.setText(customerCode);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveCustomer());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveCustomer() {
        if (!validateForm()) {
            return;
        }
        
        showLoading(true);
        
        Customer customerToSave = createCustomerFromForm();
        
        if (isEditMode) {
            updateCustomer(customerToSave);
        } else {
            createCustomer(customerToSave);
        }
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate required fields
        if (etCustomerName.getText().toString().trim().isEmpty()) {
            etCustomerName.setError("Tên khách hàng là bắt buộc");
            isValid = false;
        }
        
        // Validate email format
        String email = etCustomerEmail.getText().toString().trim();
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etCustomerEmail.setError("Email không hợp lệ");
            isValid = false;
        }
        
        // Validate phone format
        String phone = etCustomerPhone.getText().toString().trim();
        if (!phone.isEmpty() && !phone.matches("^[0-9+\\-\\s()]+$")) {
            etCustomerPhone.setError("Số điện thoại không hợp lệ");
            isValid = false;
        }
        
        // Validate additional fields based on customer type
        int selectedPosition = spinnerCustomerType.getSelectedItemPosition();
        String[] customerTypes = {"Cá nhân", "Công ty", "Cơ quan nhà nước"};
        String customerType = customerTypes[selectedPosition];
        if ("Công ty".equals(customerType)) {
            // For companies, contact person is required
            String contactPerson = etContactPerson.getText().toString().trim();
            if (contactPerson.isEmpty()) {
                etContactPerson.setError("Người liên hệ là bắt buộc cho công ty");
                isValid = false;
            }
        } else if ("Cơ quan nhà nước".equals(customerType)) {
            // For government agencies, contact person is required
            String contactPerson = etContactPerson.getText().toString().trim();
            if (contactPerson.isEmpty()) {
                etContactPerson.setError("Người liên hệ là bắt buộc cho cơ quan nhà nước");
                isValid = false;
            }
        }
        
        return isValid;
    }

    private Customer createCustomerFromForm() {
        Customer customerData = new Customer();
        
        if (isEditMode && customer != null) {
            customerData.setId(customer.getId());
        }
        
        customerData.setCustomerCode(etCustomerCode.getText().toString().trim());
        customerData.setName(etCustomerName.getText().toString().trim());
        customerData.setType(getCustomerTypeValue());
        customerData.setEmail(etCustomerEmail.getText().toString().trim());
        customerData.setPhone(etCustomerPhone.getText().toString().trim());
        customerData.setAddress(etCustomerAddress.getText().toString().trim());
        customerData.setCity(etCustomerCity.getText().toString().trim());
        customerData.setCountry(etCustomerCountry.getText().toString().trim());
        customerData.setTaxId(etCustomerTaxId.getText().toString().trim());
        customerData.setNotes(etCustomerNotes.getText().toString().trim());
        customerData.setStatus("active");
        
        // Add additional fields based on customer type
        String customerType = getCustomerTypeValue();
        switch (customerType) {
            case "company":
                // For companies, store company registration number in notes or a custom field
                String companyReg = etCompanyRegistration.getText().toString().trim();
                String contactPerson = etContactPerson.getText().toString().trim();
                if (!companyReg.isEmpty()) {
                    customerData.setNotes(customerData.getNotes() + "\nMã đăng ký KD: " + companyReg);
                }
                if (!contactPerson.isEmpty()) {
                    customerData.setNotes(customerData.getNotes() + "\nNgười liên hệ: " + contactPerson);
                }
                break;
                
            case "government":
                // For government agencies, store government code
                String governmentCode = etGovernmentCode.getText().toString().trim();
                String govContactPerson = etContactPerson.getText().toString().trim();
                if (!governmentCode.isEmpty()) {
                    customerData.setNotes(customerData.getNotes() + "\nMã cơ quan: " + governmentCode);
                }
                if (!govContactPerson.isEmpty()) {
                    customerData.setNotes(customerData.getNotes() + "\nNgười liên hệ: " + govContactPerson);
                }
                break;
        }
        
        // Parse numeric fields
        try {
            String creditLimitText = etCustomerCreditLimit.getText().toString().trim();
            if (!creditLimitText.isEmpty()) {
                customerData.setCreditLimit(Double.parseDouble(creditLimitText));
            }
        } catch (NumberFormatException e) {
            // Keep null if invalid
        }
        
        try {
            String paymentTermsText = etCustomerPaymentTerms.getText().toString().trim();
            if (!paymentTermsText.isEmpty()) {
                customerData.setPaymentTerms(Integer.parseInt(paymentTermsText));
            }
        } catch (NumberFormatException e) {
            // Keep null if invalid
        }
        
        return customerData;
    }

    private String getCustomerTypeValue() {
        int selectedPosition = spinnerCustomerType.getSelectedItemPosition();
        String[] customerTypes = {"Cá nhân", "Công ty", "Cơ quan nhà nước"};
        String displayName = customerTypes[selectedPosition];
        
        switch (displayName) {
            case "Cá nhân": return "individual";
            case "Công ty": return "company";
            case "Cơ quan nhà nước": return "government";
            default: return "individual";
        }
    }

    private void createCustomer(Customer customerData) {
        customerService.createCustomer(customerData, new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {
                // Not used
            }
            
            @Override
            public void onSuccess(Customer customer) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(CustomerFormActivity.this, "Tạo khách hàng thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(CustomerFormActivity.this, "Lỗi tạo khách hàng: " + error, Toast.LENGTH_LONG).show();
                    ApiDebugger.logError("createCustomer", new Exception(error));
                });
            }
        });
    }

    private void updateCustomer(Customer customerData) {
        customerService.updateCustomer(customerData.getId(), customerData, new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {
                // Not used
            }
            
            @Override
            public void onSuccess(Customer customer) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(CustomerFormActivity.this, "Cập nhật khách hàng thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(CustomerFormActivity.this, "Lỗi cập nhật khách hàng: " + error, Toast.LENGTH_LONG).show();
                    ApiDebugger.logError("updateCustomer", new Exception(error));
                });
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.GONE);
        btnSave.setEnabled(!show);
        btnCancel.setEnabled(!show);
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
