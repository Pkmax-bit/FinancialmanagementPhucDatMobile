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
import com.example.financialmanagement.adapters.InvoiceItemDetailAdapter;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.services.InvoiceService;
import com.example.financialmanagement.utils.CurrencyFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Invoice Detail Activity - Màn hình xem chi tiết hóa đơn
 */
public class InvoiceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_INVOICE_ID = "invoice_id";

    private String invoiceId;
    private Invoice invoice;

    // UI Components
    private Toolbar toolbar;
    private TextView tvInvoiceNumber;
    private TextView tvStatus;
    private TextView tvPaymentStatus;
    private TextView tvTotalAmount;
    private TextView tvSubtotal;
    private TextView tvTaxAmount;
    private TextView tvPaidAmount;
    private TextView tvBalance;
    private TextView tvCustomerName;
    private TextView tvCustomerEmail;
    private TextView tvCustomerPhone;
    private TextView tvProjectName;
    private TextView tvIssueDate;
    private TextView tvDueDate;
    private TextView tvNotes;
    private TextView tvTotalAmountSummary;
    private RecyclerView rvInvoiceItems;
    private InvoiceItemDetailAdapter invoiceItemAdapter;

    // Services
    private InvoiceService invoiceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        // Get intent data
        invoiceId = getIntent().getStringExtra(EXTRA_INVOICE_ID);
        if (invoiceId == null || invoiceId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupToolbar();
        initializeServices();
        loadInvoiceDetail();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvInvoiceNumber = findViewById(R.id.tv_invoice_number);
        tvStatus = findViewById(R.id.tv_status);
        tvPaymentStatus = findViewById(R.id.tv_payment_status);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTaxAmount = findViewById(R.id.tv_tax_amount);
        tvPaidAmount = findViewById(R.id.tv_paid_amount);
        tvBalance = findViewById(R.id.tv_balance);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvCustomerEmail = findViewById(R.id.tv_customer_email);
        tvCustomerPhone = findViewById(R.id.tv_customer_phone);
        tvProjectName = findViewById(R.id.tv_project_name);
        tvIssueDate = findViewById(R.id.tv_issue_date);
        tvDueDate = findViewById(R.id.tv_due_date);
        tvNotes = findViewById(R.id.tv_notes);
        tvTotalAmountSummary = findViewById(R.id.tv_total_amount_summary);
        rvInvoiceItems = findViewById(R.id.rv_invoice_items);
        
        // Setup RecyclerView for invoice items
        if (rvInvoiceItems != null) {
            rvInvoiceItems.setLayoutManager(new LinearLayoutManager(this));
            invoiceItemAdapter = new InvoiceItemDetailAdapter(null);
            rvInvoiceItems.setAdapter(invoiceItemAdapter);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết hóa đơn");
        }
    }

    private void initializeServices() {
        invoiceService = new InvoiceService(this);
    }

    private void loadInvoiceDetail() {
        invoiceService.getInvoiceById(invoiceId, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                // Not used
            }

            @Override
            public void onSuccess(Invoice inv) {
                InvoiceDetailActivity.this.invoice = inv;
                // Log detailed invoice data for debugging
                android.util.Log.d("InvoiceDetailActivity", "=== Invoice Data Received ===");
                android.util.Log.d("InvoiceDetailActivity", "Invoice ID: " + inv.getId());
                android.util.Log.d("InvoiceDetailActivity", "Invoice Number: " + inv.getInvoiceNumber());
                android.util.Log.d("InvoiceDetailActivity", "Status: " + inv.getStatus());
                android.util.Log.d("InvoiceDetailActivity", "Total: " + inv.getTotalAmount());
                if (inv.getCustomer() != null) {
                    android.util.Log.d("InvoiceDetailActivity", "Customer: " + inv.getCustomer().getName());
                } else {
                    android.util.Log.w("InvoiceDetailActivity", "Customer is NULL");
                }
                if (inv.getProject() != null) {
                    android.util.Log.d("InvoiceDetailActivity", "Project: " + inv.getProject().getName());
                } else {
                    android.util.Log.w("InvoiceDetailActivity", "Project is NULL");
                }
                List<Invoice.InvoiceItem> items = inv.getItems();
                android.util.Log.d("InvoiceDetailActivity", "Items count: " + (items != null ? items.size() : 0));
                android.util.Log.d("InvoiceDetailActivity", "==========================");
                runOnUiThread(() -> displayInvoiceDetails());
            }

            @Override
            public void onSuccess() {
                // Not used
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(InvoiceDetailActivity.this);
                    builder.setTitle("Lỗi")
                            .setMessage("Không thể tải chi tiết hóa đơn:\n" + error)
                            .setPositiveButton("Thử lại", (dialog, which) -> {
                                loadInvoiceDetail();
                            })
                            .setNegativeButton("Đóng", (dialog, which) -> {
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                });
            }
        });
    }

    private void displayInvoiceDetails() {
        if (invoice == null) {
            Toast.makeText(this, "Không thể tải thông tin hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Invoice Number
        if (tvInvoiceNumber != null) {
            tvInvoiceNumber.setText(invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "Chưa có số hóa đơn");
        }

        // Status
        if (tvStatus != null) {
            String statusText = getStatusText(invoice.getStatus());
            tvStatus.setText(statusText);
            try {
                tvStatus.setBackgroundResource(getStatusBackground(invoice.getStatus()));
            } catch (Exception e) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_active);
            }
        }

        // Payment Status
        if (tvPaymentStatus != null) {
            String paymentStatusText = getPaymentStatusText(invoice.getPaymentStatus());
            tvPaymentStatus.setText(paymentStatusText);
            try {
                tvPaymentStatus.setBackgroundResource(getPaymentStatusBackground(invoice.getPaymentStatus()));
            } catch (Exception e) {
                tvPaymentStatus.setBackgroundResource(R.drawable.bg_status_pending);
            }
        }

        // Amounts
        if (tvTotalAmount != null) {
            Double totalAmount = invoice.getTotalAmount();
            if (totalAmount != null) {
                tvTotalAmount.setText(CurrencyFormatter.format(totalAmount.doubleValue()));
            } else {
                tvTotalAmount.setText("0 ₫");
            }
        }

        if (tvTotalAmountSummary != null) {
            Double totalAmount = invoice.getTotalAmount();
            if (totalAmount != null) {
                tvTotalAmountSummary.setText(CurrencyFormatter.format(totalAmount.doubleValue()));
            } else {
                tvTotalAmountSummary.setText("0 ₫");
            }
        }

        if (tvSubtotal != null) {
            Double subtotal = invoice.getSubtotal();
            if (subtotal != null) {
                tvSubtotal.setText(CurrencyFormatter.format(subtotal.doubleValue()));
            } else {
                tvSubtotal.setText("0 ₫");
            }
        }

        if (tvTaxAmount != null) {
            Double taxAmount = invoice.getTaxAmount();
            if (taxAmount != null) {
                tvTaxAmount.setText(CurrencyFormatter.format(taxAmount.doubleValue()));
            } else {
                tvTaxAmount.setText("0 ₫");
            }
        }

        if (tvPaidAmount != null) {
            Double paidAmount = invoice.getPaidAmount();
            if (paidAmount != null) {
                tvPaidAmount.setText(CurrencyFormatter.format(paidAmount.doubleValue()));
            } else {
                tvPaidAmount.setText("0 ₫");
            }
        }

        if (tvBalance != null) {
            Double balance = invoice.getBalance();
            if (balance != null) {
                tvBalance.setText(CurrencyFormatter.format(balance.doubleValue()));
            } else {
                // Calculate balance if not provided
                Double total = invoice.getTotalAmount();
                Double paid = invoice.getPaidAmount();
                if (total != null && paid != null) {
                    tvBalance.setText(CurrencyFormatter.format(total - paid));
                } else if (total != null) {
                    tvBalance.setText(CurrencyFormatter.format(total));
                } else {
                    tvBalance.setText("0 ₫");
                }
            }
        }

        // Customer Information
        if (tvCustomerName != null) {
            if (invoice.getCustomer() != null && invoice.getCustomer().getName() != null) {
                tvCustomerName.setText(invoice.getCustomer().getName());
            } else {
                tvCustomerName.setText("Chưa có khách hàng");
            }
        }

        if (tvCustomerEmail != null) {
            if (invoice.getCustomer() != null && invoice.getCustomer().getEmail() != null) {
                tvCustomerEmail.setText(invoice.getCustomer().getEmail());
            } else {
                tvCustomerEmail.setText("N/A");
            }
        }

        if (tvCustomerPhone != null) {
            if (invoice.getCustomer() != null && invoice.getCustomer().getPhone() != null) {
                tvCustomerPhone.setText(invoice.getCustomer().getPhone());
            } else {
                tvCustomerPhone.setText("N/A");
            }
        }

        // Project Information
        if (tvProjectName != null) {
            if (invoice.getProject() != null && invoice.getProject().getName() != null) {
                tvProjectName.setText(invoice.getProject().getName());
            } else {
                tvProjectName.setText("Chưa có dự án");
            }
        }

        // Dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        
        if (tvIssueDate != null) {
            try {
                Date issueDate = invoice.getIssueDate();
                if (issueDate != null) {
                    tvIssueDate.setText(dateFormat.format(issueDate));
                } else {
                    tvIssueDate.setText("N/A");
                }
            } catch (Exception e) {
                tvIssueDate.setText("N/A");
            }
        }

        if (tvDueDate != null) {
            try {
                Date dueDate = invoice.getDueDate();
                if (dueDate != null) {
                    tvDueDate.setText(dateFormat.format(dueDate));
                } else {
                    tvDueDate.setText("N/A");
                }
            } catch (Exception e) {
                tvDueDate.setText("N/A");
            }
        }

        // Notes
        if (tvNotes != null) {
            String notes = invoice.getDescription();
            if (notes != null && !notes.isEmpty()) {
                tvNotes.setText(notes);
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

        // Display invoice items
        displayInvoiceItems();
    }

    private void displayInvoiceItems() {
        if (invoice == null || invoiceItemAdapter == null) {
            android.util.Log.w("InvoiceDetailActivity", "Invoice or adapter is null");
            return;
        }

        List<Invoice.InvoiceItem> items = invoice.getItems();
        int itemCount = items != null ? items.size() : 0;
        android.util.Log.d("InvoiceDetailActivity", "=== Invoice Items Debug ===");
        android.util.Log.d("InvoiceDetailActivity", "Invoice ID: " + invoice.getId());
        android.util.Log.d("InvoiceDetailActivity", "Items count: " + itemCount);
        android.util.Log.d("InvoiceDetailActivity", "=========================");
        
        if (items != null && !items.isEmpty()) {
            android.util.Log.d("InvoiceDetailActivity", "Displaying " + itemCount + " items");
            invoiceItemAdapter.updateItems(items);
            if (rvInvoiceItems != null) {
                rvInvoiceItems.setVisibility(android.view.View.VISIBLE);
            }
            
            // Hide "no items" message if exists
            android.view.View tvNoItems = findViewById(R.id.tv_no_items);
            if (tvNoItems != null) {
                tvNoItems.setVisibility(android.view.View.GONE);
            }
        } else {
            android.util.Log.w("InvoiceDetailActivity", "Invoice has NO items - showing empty state");
            if (rvInvoiceItems != null) {
                rvInvoiceItems.setVisibility(android.view.View.GONE);
            }
            
            android.view.View tvNoItems = findViewById(R.id.tv_no_items);
            if (tvNoItems != null) {
                tvNoItems.setVisibility(android.view.View.VISIBLE);
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
            case "paid":
                return "Đã thanh toán";
            case "overdue":
                return "Quá hạn";
            case "cancelled":
                return "Đã hủy";
            case "partial":
                return "Thanh toán một phần";
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
                return R.drawable.bg_status_active;
            case "paid":
                return R.drawable.bg_status_success;
            case "overdue":
            case "cancelled":
                return R.drawable.bg_status_error;
            case "partial":
                return R.drawable.bg_status_pending;
            default:
                return R.drawable.bg_status_draft;
        }
    }

    private String getPaymentStatusText(String status) {
        if (status == null) return "Chưa thanh toán";
        switch (status.toLowerCase()) {
            case "pending":
                return "Chưa thanh toán";
            case "partial":
                return "Thanh toán một phần";
            case "paid":
                return "Đã thanh toán đủ";
            case "overdue":
                return "Quá hạn";
            default:
                return status;
        }
    }

    private int getPaymentStatusBackground(String status) {
        if (status == null) return R.drawable.bg_status_pending;
        switch (status.toLowerCase()) {
            case "pending":
                return R.drawable.bg_status_pending;
            case "partial":
                return R.drawable.bg_status_active;
            case "paid":
                return R.drawable.bg_status_success;
            case "overdue":
                return R.drawable.bg_status_error;
            default:
                return R.drawable.bg_status_pending;
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


