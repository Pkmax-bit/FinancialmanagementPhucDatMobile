package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.ProductDetailAdapter;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.models.Product;
import com.example.financialmanagement.services.InvoiceService;
import com.example.financialmanagement.services.QuoteService;
import com.example.financialmanagement.utils.ApiDebugger;

import java.util.ArrayList;
import java.util.List;

public class RevenueDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_ID = "id";
    public static final String TYPE_INVOICE = "invoice";
    public static final String TYPE_QUOTE = "quote";
    
    private String type;
    private String id;
    private Invoice invoice;
    private Quote quote;
    
    // UI Components
    private Toolbar toolbar;
    private TextView tvTitle, tvCustomerName, tvCustomerEmail, tvCustomerPhone;
    private TextView tvTotalAmount, tvStatus, tvCreatedDate, tvDueDate;
    private TextView tvNotes, tvPaymentMethod, tvTaxRate, tvDiscount;
    private RecyclerView rvProducts;
    
    // Services
    private InvoiceService invoiceService;
    private QuoteService quoteService;
    
    // Adapter
    private ProductDetailAdapter productAdapter;
    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_detail);
        
        // Get intent data
        type = getIntent().getStringExtra(EXTRA_TYPE);
        id = getIntent().getStringExtra(EXTRA_ID);
        
        if (type == null || id == null) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initializeViews();
        setupToolbar();
        initializeServices();
        loadData();
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tv_title);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvCustomerEmail = findViewById(R.id.tv_customer_email);
        tvCustomerPhone = findViewById(R.id.tv_customer_phone);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvStatus = findViewById(R.id.tv_status);
        tvCreatedDate = findViewById(R.id.tv_created_date);
        tvDueDate = findViewById(R.id.tv_due_date);
        tvNotes = findViewById(R.id.tv_notes);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvTaxRate = findViewById(R.id.tv_tax_rate);
        tvDiscount = findViewById(R.id.tv_discount);
        rvProducts = findViewById(R.id.rv_products);
        
        // Setup RecyclerView
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductDetailAdapter(products);
        rvProducts.setAdapter(productAdapter);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(type.equals(TYPE_INVOICE) ? "Chi tiết hóa đơn" : "Chi tiết báo giá");
        }
    }
    
    private void initializeServices() {
        invoiceService = new InvoiceService(this);
        quoteService = new QuoteService(this);
    }
    
    private void loadData() {
        if (type.equals(TYPE_INVOICE)) {
            loadInvoiceDetails();
        } else {
            loadQuoteDetails();
        }
    }
    
    private void loadInvoiceDetails() {
        ApiDebugger.logRequest("GET", "Invoice Detail", null, "Invoice ID: " + id);
        
        invoiceService.getInvoiceById(id, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(Invoice invoice) {
                RevenueDetailActivity.this.invoice = invoice;
                displayInvoiceDetails();
            }
            
            @Override
            public void onSuccess(List<Invoice> invoices) {
                // Not used here
            }
            
            @Override
            public void onSuccess() {
                // Not used here
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(RevenueDetailActivity.this, "Lỗi tải dữ liệu: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void loadQuoteDetails() {
        ApiDebugger.logRequest("GET", "Quote Detail", null, "Quote ID: " + id);
        
        quoteService.getQuoteById(id, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(Quote quote) {
                RevenueDetailActivity.this.quote = quote;
                displayQuoteDetails();
            }
            
            @Override
            public void onSuccess(List<Quote> quotes) {
                // Not used here
            }
            
            @Override
            public void onSuccess() {
                // Not used here
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(RevenueDetailActivity.this, "Lỗi tải dữ liệu: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void displayInvoiceDetails() {
        if (invoice == null) return;
        
        runOnUiThread(() -> {
            tvTitle.setText("Hóa đơn #" + invoice.getInvoiceNumber());
            tvCustomerName.setText(invoice.getCustomer() != null ? invoice.getCustomer().getName() : "N/A");
            tvCustomerEmail.setText(invoice.getCustomer() != null ? invoice.getCustomer().getEmail() : "N/A");
            tvCustomerPhone.setText(invoice.getCustomer() != null ? invoice.getCustomer().getPhone() : "N/A");
            tvTotalAmount.setText(String.format("%,.0f VNĐ", invoice.getTotal()));
            tvStatus.setText(getStatusDisplayName(invoice.getStatus()));
            tvCreatedDate.setText(invoice.getCreatedAt() != null ? invoice.getCreatedAt().toString() : "N/A");
            tvDueDate.setText(invoice.getDueDate() != null ? invoice.getDueDate().toString() : "N/A");
            tvNotes.setText(invoice.getDescription() != null ? invoice.getDescription() : "N/A");
            tvPaymentMethod.setText("Chuyển khoản");
            tvTaxRate.setText("10.0%");
            tvDiscount.setText("0.0%");
            
            // Load products
            if (invoice.getProducts() != null) {
                products.clear();
                products.addAll(invoice.getProducts());
                productAdapter.notifyDataSetChanged();
            }
        });
    }
    
    private void displayQuoteDetails() {
        if (quote == null) return;
        
        runOnUiThread(() -> {
            tvTitle.setText("Báo giá #" + quote.getQuoteNumber());
            tvCustomerName.setText(quote.getCustomer() != null ? quote.getCustomer().getName() : "N/A");
            tvCustomerEmail.setText(quote.getCustomer() != null ? quote.getCustomer().getEmail() : "N/A");
            tvCustomerPhone.setText(quote.getCustomer() != null ? quote.getCustomer().getPhone() : "N/A");
            tvTotalAmount.setText(String.format("%,.0f VNĐ", quote.getTotal()));
            tvStatus.setText(getStatusDisplayName(quote.getStatus()));
            tvCreatedDate.setText(quote.getCreatedAt() != null ? quote.getCreatedAt().toString() : "N/A");
            tvDueDate.setText(quote.getValidUntil() != null ? quote.getValidUntil().toString() : "N/A");
            tvNotes.setText(quote.getDescription() != null ? quote.getDescription() : "N/A");
            tvPaymentMethod.setText("N/A");
            tvTaxRate.setText("10.0%");
            tvDiscount.setText("0.0%");
            
            // Load products
            if (quote.getProducts() != null) {
                products.clear();
                products.addAll(quote.getProducts());
                productAdapter.notifyDataSetChanged();
            }
        });
    }
    
    private String getStatusDisplayName(String status) {
        if (status == null) return "Không xác định";
        
        switch (status.toLowerCase()) {
            case "draft": return "Nháp";
            case "sent": return "Đã gửi";
            case "approved": return "Đã duyệt";
            case "rejected": return "Từ chối";
            case "paid": return "Đã thanh toán";
            case "overdue": return "Quá hạn";
            case "cancelled": return "Đã hủy";
            default: return status;
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
