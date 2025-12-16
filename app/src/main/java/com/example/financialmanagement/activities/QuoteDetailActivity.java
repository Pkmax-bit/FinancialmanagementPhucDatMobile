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
import com.example.financialmanagement.adapters.QuoteItemDetailAdapter;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.services.QuoteService;
import com.example.financialmanagement.utils.CurrencyFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Quote Detail Activity - Màn hình xem chi tiết báo giá
 */
public class QuoteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_QUOTE_ID = "quote_id";

    private String quoteId;
    private Quote quote;

    // UI Components
    private Toolbar toolbar;
    private TextView tvQuoteNumber;
    private TextView tvStatus;
    private TextView tvTotalAmount;
    private TextView tvSubtotal;
    private TextView tvTaxAmount;
    private TextView tvCustomerName;
    private TextView tvCustomerEmail;
    private TextView tvCustomerPhone;
    private TextView tvProjectName;
    private TextView tvIssueDate;
    private TextView tvValidUntil;
    private TextView tvDescription;
    private TextView tvTotalAmountSummary;
    private RecyclerView rvQuoteItems;
    private QuoteItemDetailAdapter quoteItemAdapter;
    private android.view.View layoutFooter;
    private android.widget.Button btnApprove;

    // Services
    private QuoteService quoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_detail);

        // Get intent data
        quoteId = getIntent().getStringExtra(EXTRA_QUOTE_ID);
        if (quoteId == null || quoteId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy báo giá", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupToolbar();
        initializeServices();
        setupActions();
        loadQuoteDetail();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvQuoteNumber = findViewById(R.id.tv_quote_number);
        tvStatus = findViewById(R.id.tv_status);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTaxAmount = findViewById(R.id.tv_tax_amount);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvCustomerEmail = findViewById(R.id.tv_customer_email);
        tvCustomerPhone = findViewById(R.id.tv_customer_phone);
        tvProjectName = findViewById(R.id.tv_project_name);
        tvIssueDate = findViewById(R.id.tv_issue_date);
        tvValidUntil = findViewById(R.id.tv_valid_until);
        tvDescription = findViewById(R.id.tv_description);
        tvTotalAmountSummary = findViewById(R.id.tv_total_amount_summary);
        rvQuoteItems = findViewById(R.id.rv_quote_items);
        layoutFooter = findViewById(R.id.layout_footer);
        btnApprove = findViewById(R.id.btn_approve);
        
        // Setup RecyclerView for quote items
        if (rvQuoteItems != null) {
            rvQuoteItems.setLayoutManager(new LinearLayoutManager(this));
            quoteItemAdapter = new QuoteItemDetailAdapter(null);
            rvQuoteItems.setAdapter(quoteItemAdapter);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết báo giá");
        }
    }

    private void initializeServices() {
        quoteService = new QuoteService(this);
    }

    private void loadQuoteDetail() {
        quoteService.getQuoteById(quoteId, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                // Not used
            }

            @Override
            public void onSuccess(Quote quote) {
                QuoteDetailActivity.this.quote = quote;
                // Log detailed quote data for debugging
                android.util.Log.d("QuoteDetailActivity", "=== Quote Data Received ===");
                android.util.Log.d("QuoteDetailActivity", "Quote ID: " + quote.getId());
                android.util.Log.d("QuoteDetailActivity", "Quote Number: " + quote.getQuoteNumber());
                android.util.Log.d("QuoteDetailActivity", "Status: " + quote.getStatus());
                android.util.Log.d("QuoteDetailActivity", "Total: " + quote.getTotalAmount());
                if (quote.getCustomer() != null) {
                    android.util.Log.d("QuoteDetailActivity", "Customer: " + quote.getCustomer().getName() + " (ID: " + quote.getCustomer().getId() + ")");
                } else {
                    android.util.Log.w("QuoteDetailActivity", "Customer is NULL");
                }
                if (quote.getProject() != null) {
                    android.util.Log.d("QuoteDetailActivity", "Project: " + quote.getProject().getName() + " (ID: " + quote.getProject().getId() + ")");
                } else {
                    android.util.Log.w("QuoteDetailActivity", "Project is NULL");
                }
                List<Quote.QuoteItem> items = quote.getItems();
                android.util.Log.d("QuoteDetailActivity", "Items count: " + (items != null ? items.size() : 0));
                if (items != null && !items.isEmpty()) {
                    // Use available fields from Quote.QuoteItem (nameProduct or description)
                    String name = items.get(0).getNameProduct() != null
                            ? items.get(0).getNameProduct()
                            : items.get(0).getDescription();
                    android.util.Log.d("QuoteDetailActivity",
                            "First item: " + name + " - Qty: " + items.get(0).getQuantity());
                }
                android.util.Log.d("QuoteDetailActivity", "==========================");
                runOnUiThread(() -> displayQuoteDetails());
            }

            @Override
            public void onSuccess() {
                // Not used
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // Check if it's a validation error - don't close immediately
                    if (error != null && error.contains("validation")) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(QuoteDetailActivity.this);
                        builder.setTitle("Lỗi Validation")
                                .setMessage("Backend không chấp nhận status 'approved'. Đây là lỗi từ server. Bạn có muốn thử lại không?")
                                .setPositiveButton("Thử lại", (dialog, which) -> {
                                    loadQuoteDetail();
                                })
                                .setNegativeButton("Đóng", (dialog, which) -> {
                                    finish();
                                })
                                .setCancelable(false)
                                .show();
                    } else {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(QuoteDetailActivity.this);
                        builder.setTitle("Lỗi")
                                .setMessage("Không thể tải chi tiết báo giá:\n" + error)
                                .setPositiveButton("Thử lại", (dialog, which) -> {
                                    loadQuoteDetail();
                                })
                                .setNegativeButton("Đóng", (dialog, which) -> {
                                    finish();
                                })
                                .setCancelable(false)
                                .show();
                    }
                });
            }
        });
    }

    private void displayQuoteDetails() {
        if (quote == null) {
            Toast.makeText(this, "Không thể tải thông tin báo giá", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Quote Number
        if (tvQuoteNumber != null) {
            tvQuoteNumber.setText(quote.getQuoteNumber() != null ? quote.getQuoteNumber() : "Chưa có số báo giá");
        }

        // Status
        if (tvStatus != null) {
            String statusText = getStatusText(quote.getStatus());
            tvStatus.setText(statusText);
            try {
                tvStatus.setBackgroundResource(getStatusBackground(quote.getStatus()));
            } catch (Exception e) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_active);
            }
        }

        // Amounts
        if (tvTotalAmount != null) {
            Double totalAmount = quote.getTotalAmount();
            if (totalAmount != null) {
                tvTotalAmount.setText(CurrencyFormatter.format(totalAmount.doubleValue()));
            } else {
                tvTotalAmount.setText("0 ₫");
            }
        }

        if (tvTotalAmountSummary != null) {
            Double totalAmount = quote.getTotalAmount();
            if (totalAmount != null) {
                tvTotalAmountSummary.setText(CurrencyFormatter.format(totalAmount.doubleValue()));
            } else {
                tvTotalAmountSummary.setText("0 ₫");
            }
        }

        if (tvSubtotal != null) {
            Double subtotal = quote.getSubtotal();
            if (subtotal != null) {
                tvSubtotal.setText(CurrencyFormatter.format(subtotal.doubleValue()));
            } else {
                tvSubtotal.setText("0 ₫");
            }
        }

        if (tvTaxAmount != null) {
            Double taxAmount = quote.getTaxAmount();
            if (taxAmount != null) {
                tvTaxAmount.setText(CurrencyFormatter.format(taxAmount.doubleValue()));
            } else {
                tvTaxAmount.setText("0 ₫");
            }
        }

        // Customer Information
        if (tvCustomerName != null) {
            if (quote.getCustomer() != null && quote.getCustomer().getName() != null) {
                tvCustomerName.setText(quote.getCustomer().getName());
            } else {
                tvCustomerName.setText("Chưa có khách hàng");
            }
        }

        if (tvCustomerEmail != null) {
            if (quote.getCustomer() != null && quote.getCustomer().getEmail() != null) {
                tvCustomerEmail.setText(quote.getCustomer().getEmail());
            } else {
                tvCustomerEmail.setText("N/A");
            }
        }

        if (tvCustomerPhone != null) {
            if (quote.getCustomer() != null && quote.getCustomer().getPhone() != null) {
                tvCustomerPhone.setText(quote.getCustomer().getPhone());
            } else {
                tvCustomerPhone.setText("N/A");
            }
        }

        // Project Information
        if (tvProjectName != null) {
            if (quote.getProject() != null && quote.getProject().getName() != null) {
                tvProjectName.setText(quote.getProject().getName());
            } else {
                tvProjectName.setText("Chưa có dự án");
            }
        }

        // Dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        
        if (tvIssueDate != null) {
            try {
                Date issueDate = quote.getIssueDate();
                if (issueDate == null) {
                    issueDate = quote.getQuoteDate();
                }
                if (issueDate != null) {
                    tvIssueDate.setText(dateFormat.format(issueDate));
                } else {
                    tvIssueDate.setText("N/A");
                }
            } catch (Exception e) {
                tvIssueDate.setText("N/A");
            }
        }

        if (tvValidUntil != null) {
            try {
                Date validUntil = quote.getValidUntil();
                if (validUntil == null) {
                    validUntil = quote.getExpiryDate();
                }
                if (validUntil != null) {
                    tvValidUntil.setText(dateFormat.format(validUntil));
                } else {
                    tvValidUntil.setText("N/A");
                }
            } catch (Exception e) {
                tvValidUntil.setText("N/A");
            }
        }

        // Description/Notes
        if (tvDescription != null) {
            String notes = quote.getNotes();
            if (notes == null || notes.isEmpty()) {
                notes = quote.getDescription();
            }
            if (notes != null && !notes.isEmpty()) {
                tvDescription.setText(notes);
                android.view.View llNotes = findViewById(R.id.ll_notes);
                if (llNotes != null) {
                    llNotes.setVisibility(android.view.View.VISIBLE);
                }
            } else {
                android.view.View llNotes = findViewById(R.id.ll_notes);
                if (llNotes != null) {
                    llNotes.setVisibility(android.view.View.GONE);
                }
            }
        }

        // Display quote items
        displayQuoteItems();
        
        // Show/Hide approve button
        updateFooterVisibility();
    }

    private void updateFooterVisibility() {
        if (quote != null && layoutFooter != null && btnApprove != null) {
            String status = quote.getStatus();
            boolean canApprove = !"approved".equalsIgnoreCase(status) && 
                                !"accepted".equalsIgnoreCase(status) && 
                                !"cancelled".equalsIgnoreCase(status) && 
                                !"rejected".equalsIgnoreCase(status);
            
            if (canApprove) {
                layoutFooter.setVisibility(android.view.View.VISIBLE);
            } else {
                layoutFooter.setVisibility(android.view.View.GONE);
            }
        }
    }

    private void setupActions() {
        if (btnApprove != null) {
            btnApprove.setOnClickListener(v -> showApproveConfirmation());
        }
    }

    private void showApproveConfirmation() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Xác nhận duyệt")
            .setMessage("Bạn có chắc chắn muốn duyệt báo giá này không?")
            .setPositiveButton("Duyệt", (dialog, which) -> handleApproveQuote())
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void handleApproveQuote() {
        if (quote == null) return;
        
        Toast.makeText(this, "Đang xử lý...", Toast.LENGTH_SHORT).show();
        
        quoteService.approveQuote(quote.getId(), new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(Quote updatedQuote) {
                runOnUiThread(() -> {
                    Toast.makeText(QuoteDetailActivity.this, "Đã duyệt báo giá và tạo hóa đơn thành công", Toast.LENGTH_SHORT).show();
                    if (updatedQuote != null) {
                        quote = updatedQuote;
                    } else {
                        quote.setStatus("approved");
                    }
                    displayQuoteDetails();
                });
            }

            @Override
            public void onSuccess(java.util.List<Quote> quotes) {}

            @Override
            public void onSuccess() {}

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(QuoteDetailActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void displayQuoteItems() {
        if (quote == null || quoteItemAdapter == null) {
            android.util.Log.w("QuoteDetailActivity", "Quote or adapter is null");
            return;
        }

        List<Quote.QuoteItem> items = quote.getItems();
        int itemCount = items != null ? items.size() : 0;
        android.util.Log.d("QuoteDetailActivity", "=== Quote Items Debug ===");
        android.util.Log.d("QuoteDetailActivity", "Quote ID: " + quote.getId());
        android.util.Log.d("QuoteDetailActivity", "Quote Number: " + quote.getQuoteNumber());
        android.util.Log.d("QuoteDetailActivity", "Items count: " + itemCount);
        android.util.Log.d("QuoteDetailActivity", "Items list is null: " + (items == null));
        if (items != null) {
            android.util.Log.d("QuoteDetailActivity", "Items list is empty: " + items.isEmpty());
        }
        android.util.Log.d("QuoteDetailActivity", "=========================");
        
        if (items != null && !items.isEmpty()) {
            android.util.Log.d("QuoteDetailActivity", "Displaying " + itemCount + " items");
            quoteItemAdapter.updateItems(items);
            if (rvQuoteItems != null) {
                rvQuoteItems.setVisibility(android.view.View.VISIBLE);
            }
            
            // Hide "no items" message if exists
            android.view.View tvNoItems = findViewById(R.id.tv_no_items);
            if (tvNoItems != null) {
                tvNoItems.setVisibility(android.view.View.GONE);
            }
        } else {
            android.util.Log.w("QuoteDetailActivity", "Quote has NO items - showing empty state");
            // Show RecyclerView with empty state or hide it
            if (rvQuoteItems != null) {
                rvQuoteItems.setVisibility(android.view.View.GONE);
            }
            
            // Show "no items" message if exists
            android.view.View tvNoItems = findViewById(R.id.tv_no_items);
            if (tvNoItems != null) {
                tvNoItems.setVisibility(android.view.View.VISIBLE);
                android.util.Log.d("QuoteDetailActivity", "Showing 'no items' message");
            } else {
                android.util.Log.w("QuoteDetailActivity", "tv_no_items view not found in layout");
            }
        }
    }

    private String getStatusText(String status) {
        if (status == null) return "Nháp";
        switch (status.toLowerCase()) {
            case "draft":
                return "Nháp";
            case "sent":
                return "Đã gửi";
            case "viewed":
                return "Đã xem";
            case "approved":
                return "Đã duyệt";
            case "rejected":
                return "Từ chối";
            case "expired":
                return "Hết hạn";
            case "closed":
                return "Đã đóng";
            case "converted":
                return "Đã chuyển đổi";
            default:
                return status;
        }
    }

    private int getStatusBackground(String status) {
        if (status == null) return R.drawable.bg_status_draft;
        switch (status.toLowerCase()) {
            case "draft":
                return R.drawable.bg_status_draft;
            case "sent":
            case "viewed":
                return R.drawable.bg_status_active;
            case "approved":
                return R.drawable.bg_status_success;
            case "rejected":
            case "expired":
                return R.drawable.bg_status_error;
            case "closed":
            case "converted":
                return R.drawable.bg_status_draft;
            default:
                return R.drawable.bg_status_draft;
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
