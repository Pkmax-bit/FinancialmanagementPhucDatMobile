package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
    private TextView tvProductsCount, tvNoProducts;
    private LinearLayout llProductsContainer;
    private LinearLayout llProduct1, llProduct2, llProduct3;
    private TextView tvProductName1, tvProductDescription1, tvProductQuantity1, tvProductPrice1;
    private TextView tvProductName2, tvProductDescription2, tvProductQuantity2, tvProductPrice2;
    private TextView tvProductName3, tvProductDescription3, tvProductQuantity3, tvProductPrice3;
    
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
        tvProductsCount = findViewById(R.id.tv_products_count);
        tvNoProducts = findViewById(R.id.tv_no_products);
        llProductsContainer = findViewById(R.id.ll_products_container);
        
        // Product layouts
        llProduct1 = findViewById(R.id.ll_product_1);
        llProduct2 = findViewById(R.id.ll_product_2);
        llProduct3 = findViewById(R.id.ll_product_3);
        
        // Product 1
        tvProductName1 = findViewById(R.id.tv_product_name_1);
        tvProductDescription1 = findViewById(R.id.tv_product_description_1);
        tvProductQuantity1 = findViewById(R.id.tv_product_quantity_1);
        tvProductPrice1 = findViewById(R.id.tv_product_price_1);
        
        // Product 2
        tvProductName2 = findViewById(R.id.tv_product_name_2);
        tvProductDescription2 = findViewById(R.id.tv_product_description_2);
        tvProductQuantity2 = findViewById(R.id.tv_product_quantity_2);
        tvProductPrice2 = findViewById(R.id.tv_product_price_2);
        
        // Product 3
        tvProductName3 = findViewById(R.id.tv_product_name_3);
        tvProductDescription3 = findViewById(R.id.tv_product_description_3);
        tvProductQuantity3 = findViewById(R.id.tv_product_quantity_3);
        tvProductPrice3 = findViewById(R.id.tv_product_price_3);
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
            tvTotalAmount.setText(String.format("%,.0f VNĐ", invoice.getTotalAmount()));
            tvStatus.setText(getStatusDisplayName(invoice.getStatus()));
            tvCreatedDate.setText(invoice.getCreatedAt() != null ? invoice.getCreatedAt().toString() : "N/A");
            tvDueDate.setText(invoice.getDueDate() != null ? invoice.getDueDate().toString() : "N/A");
            tvNotes.setText(invoice.getDescription() != null ? invoice.getDescription() : "N/A");
            tvPaymentMethod.setText("Chuyển khoản");
            tvTaxRate.setText("10.0%");
            tvDiscount.setText("0.0%");
            
            // Load products
            // Hiển thị danh sách sản phẩm từ items hoặc products
            products.clear();
            
            // Ưu tiên hiển thị từ items (chi tiết hóa đơn)
            if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
                for (Invoice.InvoiceItem item : invoice.getItems()) {
                    Product product = new Product();
                    product.setNameProduct(item.getNameProduct() != null ? item.getNameProduct() : "Sản phẩm");
                    product.setDescription(item.getDescription() != null ? item.getDescription() : "Không có mô tả");
                    product.setQuantity(item.getQuantity() != null ? item.getQuantity() : 0.0);
                    product.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice() : 0.0);
                    product.setTotalPrice(item.getTotalPrice() != null ? item.getTotalPrice() : 0.0);
                    product.setUnit(item.getUnit() != null ? item.getUnit() : "cái");
                    product.setArea(item.getArea());
                    product.setVolume(item.getVolume());
                    product.setHeight(item.getHeight());
                    product.setLength(item.getLength());
                    product.setDepth(item.getDepth());
                    product.setDiscountRate(item.getDiscountRate());
                    products.add(product);
                }
            } else if (invoice.getProducts() != null && !invoice.getProducts().isEmpty()) {
                // Fallback: sử dụng products nếu có
                products.addAll(invoice.getProducts());
            }
            
            productAdapter.notifyDataSetChanged();
            
            // Hiển thị sản phẩm
            displayProducts(products);
        });
    }
    
    private void displayQuoteDetails() {
        if (quote == null) return;
        
        runOnUiThread(() -> {
            tvTitle.setText("Báo giá #" + quote.getQuoteNumber());
            tvCustomerName.setText(quote.getCustomer() != null ? quote.getCustomer().getName() : "N/A");
            tvCustomerEmail.setText(quote.getCustomer() != null ? quote.getCustomer().getEmail() : "N/A");
            tvCustomerPhone.setText(quote.getCustomer() != null ? quote.getCustomer().getPhone() : "N/A");
            tvTotalAmount.setText(String.format("%,.0f VNĐ", quote.getTotalAmount()));
            tvStatus.setText(getStatusDisplayName(quote.getStatus()));
            tvCreatedDate.setText(quote.getCreatedAt() != null ? quote.getCreatedAt().toString() : "N/A");
            tvDueDate.setText(quote.getValidUntil() != null ? quote.getValidUntil().toString() : "N/A");
            tvNotes.setText(quote.getDescription() != null ? quote.getDescription() : "N/A");
            tvPaymentMethod.setText("N/A");
            tvTaxRate.setText("10.0%");
            tvDiscount.setText("0.0%");
            
            // Load products
            // Hiển thị danh sách sản phẩm từ items hoặc products
            products.clear();
            
            // Ưu tiên hiển thị từ items (chi tiết báo giá)
            if (quote.getItems() != null && !quote.getItems().isEmpty()) {
                for (Quote.QuoteItem item : quote.getItems()) {
                    Product product = new Product();
                    product.setNameProduct(item.getNameProduct() != null ? item.getNameProduct() : "Sản phẩm");
                    product.setDescription(item.getDescription() != null ? item.getDescription() : "Không có mô tả");
                    product.setQuantity(item.getQuantity() != null ? item.getQuantity() : 0.0);
                    product.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice() : 0.0);
                    product.setTotalPrice(item.getTotalPrice() != null ? item.getTotalPrice() : 0.0);
                    product.setUnit(item.getUnit() != null ? item.getUnit() : "cái");
                    product.setArea(item.getArea());
                    product.setVolume(item.getVolume());
                    product.setHeight(item.getHeight());
                    product.setLength(item.getLength());
                    product.setDepth(item.getDepth());
                    product.setDiscountRate(item.getDiscountRate());
                    products.add(product);
                }
            } else if (quote.getProducts() != null && !quote.getProducts().isEmpty()) {
                // Fallback: sử dụng products nếu có
                products.addAll(quote.getProducts());
            }
            
            productAdapter.notifyDataSetChanged();
            
            // Hiển thị sản phẩm
            displayProducts(products);
        });
    }
    
    private void displayProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            // Hiển thị empty state
            tvNoProducts.setVisibility(View.VISIBLE);
            llProduct1.setVisibility(View.GONE);
            llProduct2.setVisibility(View.GONE);
            llProduct3.setVisibility(View.GONE);
            tvProductsCount.setText("0 sản phẩm");
            return;
        }
        
        // Ẩn empty state
        tvNoProducts.setVisibility(View.GONE);
        
        // Hiển thị sản phẩm 1
        if (products.size() > 0) {
            Product product1 = products.get(0);
            tvProductName1.setText(product1.getNameProduct() != null ? product1.getNameProduct() : "Sản phẩm");
            tvProductDescription1.setText(product1.getDescription() != null ? product1.getDescription() : "Không có mô tả");
            tvProductQuantity1.setText(String.format("%.0f x", product1.getQuantity() != null ? product1.getQuantity() : 0));
            tvProductPrice1.setText(String.format("%,.0f VNĐ", product1.getTotalPrice() != null ? product1.getTotalPrice() : 0));
            llProduct1.setVisibility(View.VISIBLE);
        } else {
            llProduct1.setVisibility(View.GONE);
        }
        
        // Hiển thị sản phẩm 2
        if (products.size() > 1) {
            Product product2 = products.get(1);
            tvProductName2.setText(product2.getNameProduct() != null ? product2.getNameProduct() : "Sản phẩm");
            tvProductDescription2.setText(product2.getDescription() != null ? product2.getDescription() : "Không có mô tả");
            tvProductQuantity2.setText(String.format("%.0f x", product2.getQuantity() != null ? product2.getQuantity() : 0));
            tvProductPrice2.setText(String.format("%,.0f VNĐ", product2.getTotalPrice() != null ? product2.getTotalPrice() : 0));
            llProduct2.setVisibility(View.VISIBLE);
        } else {
            llProduct2.setVisibility(View.GONE);
        }
        
        // Hiển thị sản phẩm 3
        if (products.size() > 2) {
            Product product3 = products.get(2);
            tvProductName3.setText(product3.getNameProduct() != null ? product3.getNameProduct() : "Sản phẩm");
            tvProductDescription3.setText(product3.getDescription() != null ? product3.getDescription() : "Không có mô tả");
            tvProductQuantity3.setText(String.format("%.0f x", product3.getQuantity() != null ? product3.getQuantity() : 0));
            tvProductPrice3.setText(String.format("%,.0f VNĐ", product3.getTotalPrice() != null ? product3.getTotalPrice() : 0));
            llProduct3.setVisibility(View.VISIBLE);
        } else {
            llProduct3.setVisibility(View.GONE);
        }
        
        // Cập nhật số lượng sản phẩm
        tvProductsCount.setText(products.size() + " sản phẩm");
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
