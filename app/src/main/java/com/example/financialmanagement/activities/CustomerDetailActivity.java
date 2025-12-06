package com.example.financialmanagement.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.utils.CurrencyFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CustomerDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CUSTOMER = "extra_customer";

    private Customer customer;

    // Views
    private TextView tvAvatar, tvName, tvCode, tvStatus;
    private TextView tvEmail, tvPhone, tvAddress;
    private TextView tvType, tvTaxId, tvCreditLimit, tvPaymentTerms, tvNotes;
    private LinearLayout layoutEmail, layoutPhone, layoutAddress, layoutTax;
    private MaterialCardView cardNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        // Get customer from intent
        customer = (Customer) getIntent().getSerializableExtra(EXTRA_CUSTOMER);
        if (customer == null) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        displayCustomerInfo();
    }

    private void initViews() {
        // Header
        tvAvatar = findViewById(R.id.tv_avatar);
        tvName = findViewById(R.id.tv_name);
        tvCode = findViewById(R.id.tv_code);
        tvStatus = findViewById(R.id.tv_status);

        // Contact
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        layoutEmail = findViewById(R.id.layout_email);
        layoutPhone = findViewById(R.id.layout_phone);
        layoutAddress = findViewById(R.id.layout_address);

        // Business
        tvType = findViewById(R.id.tv_type);
        tvTaxId = findViewById(R.id.tv_tax_id);
        tvCreditLimit = findViewById(R.id.tv_credit_limit);
        tvPaymentTerms = findViewById(R.id.tv_payment_terms);
        layoutTax = findViewById(R.id.layout_tax);

        // Notes
        tvNotes = findViewById(R.id.tv_notes);
        cardNotes = findViewById(R.id.card_notes);

        // FAB
        FloatingActionButton fabEdit = findViewById(R.id.fab_edit);
        fabEdit.setOnClickListener(v -> editCustomer());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayCustomerInfo() {
        // Avatar
        String initial = customer.getName() != null && !customer.getName().isEmpty()
                ? customer.getName().substring(0, 1).toUpperCase()
                : "?";
        tvAvatar.setText(initial);

        // Name & Code
        tvName.setText(customer.getName() != null ? customer.getName() : "");
        tvCode.setText("Mã: " + (customer.getCustomerCode() != null ? customer.getCustomerCode() : "N/A"));

        // Status
        String status = customer.getStatus() != null ? customer.getStatus() : "active";
        switch (status.toLowerCase()) {
            case "active":
                tvStatus.setText("Hoạt động");
                tvStatus.setTextColor(Color.parseColor("#10B981"));
                tvStatus.setBackgroundResource(R.drawable.bg_status_active);
                break;
            case "inactive":
                tvStatus.setText("Ngừng hoạt động");
                tvStatus.setTextColor(Color.parseColor("#6B7280"));
                tvStatus.setBackgroundResource(R.drawable.bg_status_inactive);
                break;
            case "prospect":
                tvStatus.setText("Tiềm năng");
                tvStatus.setTextColor(Color.parseColor("#F59E0B"));
                tvStatus.setBackgroundResource(R.drawable.bg_status_prospect);
                break;
        }

        // Email
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            tvEmail.setText(customer.getEmail());
            layoutEmail.setVisibility(View.VISIBLE);
        } else {
            layoutEmail.setVisibility(View.GONE);
        }

        // Phone
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            tvPhone.setText(customer.getPhone());
            layoutPhone.setVisibility(View.VISIBLE);
        } else {
            layoutPhone.setVisibility(View.GONE);
        }

        // Address
        String fullAddress = customer.getFullAddress();
        if (fullAddress != null && !fullAddress.isEmpty()) {
            tvAddress.setText(fullAddress);
            layoutAddress.setVisibility(View.VISIBLE);
        } else {
            layoutAddress.setVisibility(View.GONE);
        }

        // Type
        String type = customer.getCustomerType() != null ? customer.getCustomerType() : "individual";
        switch (type.toLowerCase()) {
            case "company":
                tvType.setText("Công ty");
                break;
            case "government":
                tvType.setText("Cơ quan nhà nước");
                break;
            default:
                tvType.setText("Cá nhân");
        }

        // Tax ID
        if (customer.getTaxId() != null && !customer.getTaxId().isEmpty()) {
            tvTaxId.setText(customer.getTaxId());
            layoutTax.setVisibility(View.VISIBLE);
        } else {
            layoutTax.setVisibility(View.GONE);
        }

        // Credit Limit
        Double creditLimit = customer.getCreditLimit();
        tvCreditLimit.setText(creditLimit != null ? CurrencyFormatter.format(creditLimit) : "0 ₫");

        // Payment Terms
        Integer paymentTerms = customer.getPaymentTerms();
        tvPaymentTerms.setText(paymentTerms != null ? paymentTerms + " ngày" : "30 ngày");

        // Notes
        if (customer.getNotes() != null && !customer.getNotes().isEmpty()) {
            tvNotes.setText(customer.getNotes());
            cardNotes.setVisibility(View.VISIBLE);
        } else {
            cardNotes.setVisibility(View.GONE);
        }
    }

    private void editCustomer() {
        Intent intent = new Intent(this, CustomerFormActivity.class);
        intent.putExtra(CustomerFormActivity.EXTRA_MODE, CustomerFormActivity.MODE_EDIT);
        intent.putExtra(CustomerFormActivity.EXTRA_CUSTOMER, customer);
        startActivityForResult(intent, 1002);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Reload customer data or finish
            setResult(RESULT_OK);
            finish();
        }
    }
}
