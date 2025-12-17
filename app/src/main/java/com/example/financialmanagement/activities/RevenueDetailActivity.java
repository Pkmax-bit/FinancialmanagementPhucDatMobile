package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.financialmanagement.R;
import com.example.financialmanagement.utils.CurrencyFormatter;

/**
 * Revenue Detail Activity - Màn hình chi tiết doanh thu
 */
public class RevenueDetailActivity extends AppCompatActivity {

    public static final String EXTRA_REVENUE_TYPE = "revenue_type";
    public static final String EXTRA_REVENUE_ID = "revenue_id";
    public static final String EXTRA_TYPE = "revenue_type";  // Alias for compatibility
    public static final String EXTRA_ID = "revenue_id";      // Alias for compatibility
    public static final String TYPE_QUOTE = "quote";
    public static final String TYPE_INVOICE = "invoice";

    private Toolbar toolbar;
    private String revenueType;
    private String revenueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_detail);

        // Get intent data
        revenueType = getIntent().getStringExtra(EXTRA_REVENUE_TYPE);
        revenueId = getIntent().getStringExtra(EXTRA_REVENUE_ID);

        initializeViews();
        setupToolbar();
        
        // Redirect to appropriate detail activity
        redirectToDetailActivity();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết doanh thu");
        }
    }

    private void redirectToDetailActivity() {
        if (revenueType == null || revenueId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        android.content.Intent intent;
        if (TYPE_QUOTE.equals(revenueType)) {
            intent = new android.content.Intent(this, QuoteDetailActivity.class);
            intent.putExtra(QuoteDetailActivity.EXTRA_QUOTE_ID, revenueId);
        } else if (TYPE_INVOICE.equals(revenueType)) {
            intent = new android.content.Intent(this, InvoiceDetailActivity.class);
            intent.putExtra(InvoiceDetailActivity.EXTRA_INVOICE_ID, revenueId);
        } else {
            Toast.makeText(this, "Loại doanh thu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        startActivity(intent);
        finish();
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

