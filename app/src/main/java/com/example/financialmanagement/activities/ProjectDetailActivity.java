package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.QuotesAdapter;
import com.example.financialmanagement.adapters.InvoicesAdapter;
import com.example.financialmanagement.adapters.ExpensesAdapter;
import com.example.financialmanagement.adapters.ProjectDetailPagerAdapter;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.models.ProjectExpense;
import com.example.financialmanagement.services.ProjectService;
import com.example.financialmanagement.services.QuoteService;
import com.example.financialmanagement.services.InvoiceService;
import com.example.financialmanagement.services.ExpenseService;
import com.example.financialmanagement.utils.ApiDebugger;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ProjectDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_PROJECT_ID = "project_id";
    public static final String EXTRA_PROJECT_NAME = "project_name";
    
    private String projectId;
    private String projectName;
    private Project project;
    
    // UI Components
    private Toolbar toolbar;
    private TextView tvProjectName, tvProjectStatus, tvProjectDescription;
    private TextView tvTotalRevenue, tvTotalQuotes, tvTotalInvoices, tvTotalExpenses;
    private TextView tvVariancePercentage, tvVarianceAmount;
    
    // Tab Layout
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProjectDetailPagerAdapter pagerAdapter;
    
    // Services
    private ProjectService projectService;
    private QuoteService quoteService;
    private InvoiceService invoiceService;
    private ExpenseService expenseService;
    
    // Data
    private List<Quote> projectQuotes = new ArrayList<>();
    private List<Invoice> projectInvoices = new ArrayList<>();
    private List<ProjectExpense> projectExpenses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        
        // Get intent data
        projectId = getIntent().getStringExtra(EXTRA_PROJECT_ID);
        projectName = getIntent().getStringExtra(EXTRA_PROJECT_NAME);
        
        if (projectId == null) {
            Toast.makeText(this, "Dữ liệu dự án không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initializeViews();
        setupToolbar();
        initializeServices();
        setupTabs();
        loadProjectData();
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvProjectName = findViewById(R.id.tv_project_name);
        tvProjectStatus = findViewById(R.id.tv_project_status);
        tvProjectDescription = findViewById(R.id.tv_project_description);
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        tvTotalQuotes = findViewById(R.id.tv_total_quotes);
        tvTotalInvoices = findViewById(R.id.tv_total_invoices);
        tvTotalExpenses = findViewById(R.id.tv_total_expenses);
        tvVariancePercentage = findViewById(R.id.tv_variance_percentage);
        tvVarianceAmount = findViewById(R.id.tv_variance_amount);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(projectName != null ? projectName : "Chi tiết dự án");
        }
    }
    
    private void initializeServices() {
        projectService = new ProjectService(this);
        quoteService = new QuoteService(this);
        invoiceService = new InvoiceService(this);
        expenseService = new ExpenseService(this);
    }
    
    private void setupTabs() {
        pagerAdapter = new ProjectDetailPagerAdapter(this, projectId);
        viewPager.setAdapter(pagerAdapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Báo giá");
                    break;
                case 1:
                    tab.setText("Hóa đơn");
                    break;
                case 2:
                    tab.setText("Chi phí");
                    break;
                case 3:
                    tab.setText("Tổng quan");
                    break;
            }
        }).attach();
    }
    
    private void loadProjectData() {
        ApiDebugger.logAuth("Loading project data for project: " + projectId, true);
        
        // Load project details
        loadProjectDetails();
        
        // Load project statistics
        loadProjectStatistics();
        
        // Load project data for tabs with delay to ensure fragments are initialized
        new android.os.Handler().postDelayed(() -> {
            loadProjectQuotes();
            loadProjectInvoices();
            loadProjectExpenses();
        }, 500);
    }
    
    private void loadProjectDetails() {
        ApiDebugger.logRequest("GET", "Project Details", null, "Project ID: " + projectId);
        
        projectService.getProjectById(projectId, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                // Not used
            }
            
            @Override
            public void onSuccess(Project project) {
                ProjectDetailActivity.this.project = project;
                runOnUiThread(() -> {
                    tvProjectName.setText(project.getName());
                    tvProjectStatus.setText(getStatusDisplayName(project.getStatus()));
                    tvProjectDescription.setText(project.getDescription());
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProjectDetailActivity.this, "Lỗi tải thông tin dự án: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void loadProjectStatistics() {
        // Load total revenue
        loadTotalRevenue();
        
        // Load quotes count
        loadQuotesCount();
        
        // Load invoices count
        loadInvoicesCount();
        
        // Load expenses and variance
        loadExpensesAndVariance();
    }
    
    private void loadTotalRevenue() {
        Map<String, Object> params = new HashMap<>();
        params.put("project_id", projectId);
        params.put("limit", 1000);
        
        ApiDebugger.logRequest("GET", "Project Revenue", null, params);
        
        invoiceService.getAllInvoices(params, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                runOnUiThread(() -> {
                    double totalRevenue = 0;
                    if (invoices != null) {
                        for (Invoice invoice : invoices) {
                            if (invoice.getTotal() != null) {
                                totalRevenue += invoice.getTotal();
                            }
                        }
                    }
                    tvTotalRevenue.setText(String.format("%,.0f VNĐ", totalRevenue));
                });
            }
            
            @Override
            public void onSuccess(Invoice invoice) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvTotalRevenue.setText("0 VNĐ");
                });
            }
        });
    }
    
    private void loadQuotesCount() {
        Map<String, Object> params = new HashMap<>();
        params.put("project_id", projectId);
        params.put("limit", 1000);
        
        ApiDebugger.logRequest("GET", "Project Quotes Count", null, params);
        
        quoteService.getAllQuotes(params, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                runOnUiThread(() -> {
                    int count = quotes != null ? quotes.size() : 0;
                    tvTotalQuotes.setText(String.valueOf(count));
                });
            }
            
            @Override
            public void onSuccess(Quote quote) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvTotalQuotes.setText("0");
                });
            }
        });
    }
    
    private void loadInvoicesCount() {
        Map<String, Object> params = new HashMap<>();
        params.put("project_id", projectId);
        params.put("limit", 1000);
        
        ApiDebugger.logRequest("GET", "Project Invoices Count", null, params);
        
        invoiceService.getAllInvoices(params, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                runOnUiThread(() -> {
                    int count = invoices != null ? invoices.size() : 0;
                    tvTotalInvoices.setText(String.valueOf(count));
                });
            }
            
            @Override
            public void onSuccess(Invoice invoice) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvTotalInvoices.setText("0");
                });
            }
        });
    }
    
    private void loadExpensesAndVariance() {
        Map<String, Object> params = new HashMap<>();
        params.put("project_id", projectId);
        params.put("limit", 1000);
        
        ApiDebugger.logRequest("GET", "Project Expenses", null, params);
        
        expenseService.getAllExpenses(params, new ExpenseService.ExpenseCallback() {
            @Override
            public void onSuccess(List<ProjectExpense> expenses) {
                runOnUiThread(() -> {
                    if (expenses != null) {
                        projectExpenses = expenses;
                        
                        // Calculate totals
                        double plannedTotal = 0;
                        double actualTotal = 0;
                        
                        for (ProjectExpense expense : expenses) {
                            if (expense.getPlannedAmount() != null) {
                                plannedTotal += expense.getPlannedAmount();
                            }
                            if (expense.getActualAmount() != null) {
                                actualTotal += expense.getActualAmount();
                            }
                        }
                        
                        // Update UI
                        tvTotalExpenses.setText(String.format("%,.0f VNĐ", actualTotal));
                        
                        // Calculate variance
                        double variance = actualTotal - plannedTotal;
                        double variancePercentage = plannedTotal > 0 ? (variance / plannedTotal) * 100 : 0;
                        
                        tvVarianceAmount.setText(String.format("%,.0f VNĐ", variance));
                        tvVariancePercentage.setText(String.format("%.1f%%", variancePercentage));
                        
                        // Set color based on variance
                        if (variance > 0) {
                            tvVarianceAmount.setTextColor(getResources().getColor(R.color.error_color));
                            tvVariancePercentage.setTextColor(getResources().getColor(R.color.error_color));
                        } else if (variance < 0) {
                            tvVarianceAmount.setTextColor(getResources().getColor(R.color.success_color));
                            tvVariancePercentage.setTextColor(getResources().getColor(R.color.success_color));
                        } else {
                            tvVarianceAmount.setTextColor(getResources().getColor(R.color.text_secondary));
                            tvVariancePercentage.setTextColor(getResources().getColor(R.color.text_secondary));
                        }
                    } else {
                        tvTotalExpenses.setText("0 VNĐ");
                        tvVarianceAmount.setText("0 VNĐ");
                        tvVariancePercentage.setText("0%");
                    }
                });
            }
            
            @Override
            public void onSuccess(ProjectExpense expense) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvTotalExpenses.setText("0 VNĐ");
                    tvVarianceAmount.setText("0 VNĐ");
                    tvVariancePercentage.setText("0%");
                });
            }
        });
    }
    
    private void loadProjectQuotes() {
        Map<String, Object> params = new HashMap<>();
        params.put("project_id", projectId);
        params.put("limit", 1000);
        params.put("sort", "created_at");
        params.put("order", "desc");
        
        ApiDebugger.logRequest("GET", "Project Quotes", null, params);
        
        quoteService.getAllQuotes(params, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                runOnUiThread(() -> {
                    projectQuotes = quotes != null ? quotes : new ArrayList<>();
                    ApiDebugger.logAuth("Project quotes loaded: " + projectQuotes.size(), true);
                    // Update pager adapter with quotes data
                    if (pagerAdapter != null) {
                        pagerAdapter.updateQuotes(projectQuotes);
                        ApiDebugger.logAuth("Pager adapter updated with quotes", true);
                    } else {
                        ApiDebugger.logAuth("Pager adapter is null!", false);
                    }
                    
                    // Force refresh fragments
                    refreshFragments();
                });
            }
            
            @Override
            public void onSuccess(Quote quote) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    projectQuotes = new ArrayList<>();
                    if (pagerAdapter != null) {
                        pagerAdapter.updateQuotes(projectQuotes);
                    }
                });
            }
        });
    }
    
    private void loadProjectInvoices() {
        Map<String, Object> params = new HashMap<>();
        params.put("project_id", projectId);
        params.put("limit", 1000);
        params.put("sort", "created_at");
        params.put("order", "desc");
        
        ApiDebugger.logRequest("GET", "Project Invoices", null, params);
        
        invoiceService.getAllInvoices(params, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                runOnUiThread(() -> {
                    projectInvoices = invoices != null ? invoices : new ArrayList<>();
                    ApiDebugger.logAuth("Project invoices loaded: " + projectInvoices.size(), true);
                    // Update pager adapter with invoices data
                    if (pagerAdapter != null) {
                        pagerAdapter.updateInvoices(projectInvoices);
                        ApiDebugger.logAuth("Pager adapter updated with invoices", true);
                    } else {
                        ApiDebugger.logAuth("Pager adapter is null!", false);
                    }
                    
                    // Force refresh fragments
                    refreshFragments();
                });
            }
            
            @Override
            public void onSuccess(Invoice invoice) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    projectInvoices = new ArrayList<>();
                    if (pagerAdapter != null) {
                        pagerAdapter.updateInvoices(projectInvoices);
                    }
                });
            }
        });
    }
    
    private void loadProjectExpenses() {
        Map<String, Object> params = new HashMap<>();
        params.put("project_id", projectId);
        params.put("limit", 1000);
        params.put("sort", "created_at");
        params.put("order", "desc");
        
        ApiDebugger.logRequest("GET", "Project Expenses", null, params);
        
        expenseService.getAllExpenses(params, new ExpenseService.ExpenseCallback() {
            @Override
            public void onSuccess(List<ProjectExpense> expenses) {
                runOnUiThread(() -> {
                    projectExpenses = expenses != null ? expenses : new ArrayList<>();
                    ApiDebugger.logAuth("Project expenses loaded: " + projectExpenses.size(), true);
                    // Update pager adapter with expenses data
                    if (pagerAdapter != null) {
                        pagerAdapter.updateExpenses(projectExpenses);
                        ApiDebugger.logAuth("Pager adapter updated with expenses", true);
                    } else {
                        ApiDebugger.logAuth("Pager adapter is null!", false);
                    }
                    
                    // Force refresh fragments
                    refreshFragments();
                });
            }
            
            @Override
            public void onSuccess(ProjectExpense expense) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    projectExpenses = new ArrayList<>();
                    if (pagerAdapter != null) {
                        pagerAdapter.updateExpenses(projectExpenses);
                    }
                });
            }
        });
    }
    
    private String getStatusDisplayName(String status) {
        if (status == null) return "Không xác định";
        
        switch (status.toLowerCase()) {
            case "active": return "Đang hoạt động";
            case "completed": return "Hoàn thành";
            case "on_hold": return "Tạm dừng";
            case "cancelled": return "Đã hủy";
            case "planning": return "Đang lập kế hoạch";
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
    
    /**
     * Force refresh all fragments with current data
     */
    private void refreshFragments() {
        if (pagerAdapter != null) {
            if (projectQuotes != null) {
                pagerAdapter.updateQuotes(projectQuotes);
            }
            if (projectInvoices != null) {
                pagerAdapter.updateInvoices(projectInvoices);
            }
            if (projectExpenses != null) {
                pagerAdapter.updateExpenses(projectExpenses);
            }
        }
    }
}
