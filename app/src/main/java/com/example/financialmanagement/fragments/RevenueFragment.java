package com.example.financialmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.activities.RevenueDetailActivity;
import com.example.financialmanagement.adapters.QuotesAdapter;
import com.example.financialmanagement.adapters.InvoicesAdapter;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.services.QuoteService;
import com.example.financialmanagement.services.InvoiceService;
import com.example.financialmanagement.services.ProjectService;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.utils.CurrencyFormatter;
import com.example.financialmanagement.utils.ApiDebugger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Revenue Fragment - Màn hình doanh thu
 * Hiển thị báo giá và hóa đơn
 */
public class RevenueFragment extends Fragment {

    private TextView tvTotalRevenue, tvTotalQuotes, tvTotalInvoices, tvProjectInfo;
    private TextView tvViewAllQuotes, tvViewAllInvoices;
    private RecyclerView rvQuotes, rvInvoices;
    private QuotesAdapter quotesAdapter;
    private InvoicesAdapter invoicesAdapter;
    private QuoteService quoteService;
    private InvoiceService invoiceService;
    private ProjectService projectService;
    private AuthManager authManager;
    private List<Project> userProjects;
    
    // Toggle state
    private boolean showingAllQuotes = false;
    private boolean showingAllInvoices = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_revenue, container, false);
            
            initializeViews(view);
            setupRecyclerViews();
            setupToggleButtons();
            loadRevenueData();
            
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi tải Doanh thu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return inflater.inflate(R.layout.fragment_revenue, container, false);
        }
    }

    private void initializeViews(View view) {
        try {
            tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
            tvTotalQuotes = view.findViewById(R.id.tv_total_quotes);
            tvTotalInvoices = view.findViewById(R.id.tv_total_invoices);
            tvProjectInfo = view.findViewById(R.id.tv_project_info);
            tvViewAllQuotes = view.findViewById(R.id.tv_view_all_quotes);
            tvViewAllInvoices = view.findViewById(R.id.tv_view_all_invoices);
            rvQuotes = view.findViewById(R.id.rv_quotes);
            rvInvoices = view.findViewById(R.id.rv_invoices);
            
            // Check for null views
            if (tvTotalRevenue == null) {
                throw new RuntimeException("tvTotalRevenue not found");
            }
            if (tvTotalQuotes == null) {
                throw new RuntimeException("tvTotalQuotes not found");
            }
            if (tvTotalInvoices == null) {
                throw new RuntimeException("tvTotalInvoices not found");
            }
            if (rvQuotes == null) {
                throw new RuntimeException("rvQuotes not found");
            }
            if (rvInvoices == null) {
                throw new RuntimeException("rvInvoices not found");
            }
            
            // Initialize services
            if (getContext() != null) {
                quoteService = new QuoteService(getContext());
                invoiceService = new InvoiceService(getContext());
                projectService = new ProjectService(getContext());
                authManager = new AuthManager(getContext());
                userProjects = new ArrayList<>();
            } else {
                throw new RuntimeException("Context is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khởi tạo views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerViews() {
        try {
            // Setup Quotes RecyclerView
            quotesAdapter = new QuotesAdapter(new ArrayList<>(), new QuotesAdapter.QuoteClickListener() {
                @Override
                public void onQuoteClick(Quote quote) {
                    // Navigate to quote detail
                    Intent intent = new Intent(getContext(), RevenueDetailActivity.class);
                    intent.putExtra(RevenueDetailActivity.EXTRA_TYPE, RevenueDetailActivity.TYPE_QUOTE);
                    intent.putExtra(RevenueDetailActivity.EXTRA_ID, quote.getId());
                    startActivity(intent);
                }
                
                @Override
                public void onQuoteEdit(Quote quote) {
                    // Handle quote edit
                }
                
                @Override
                public void onQuoteDelete(Quote quote) {
                    // Handle quote delete
                }
                
                @Override
                public void onQuoteApprove(Quote quote) {
                    // Handle quote approve
                }
                
                @Override
                public void onQuoteConvertToInvoice(Quote quote) {
                    // Handle quote convert to invoice
                }
                
                @Override
                public void onQuoteSendToCustomer(Quote quote) {
                    // Handle quote send to customer
                }
            });
            rvQuotes.setLayoutManager(new LinearLayoutManager(getContext()));
            rvQuotes.setAdapter(quotesAdapter);
            
            // Setup Invoices RecyclerView
            invoicesAdapter = new InvoicesAdapter(new ArrayList<>(), new InvoicesAdapter.InvoiceClickListener() {
                @Override
                public void onInvoiceClick(Invoice invoice) {
                    // Navigate to invoice detail
                    Intent intent = new Intent(getContext(), RevenueDetailActivity.class);
                    intent.putExtra(RevenueDetailActivity.EXTRA_TYPE, RevenueDetailActivity.TYPE_INVOICE);
                    intent.putExtra(RevenueDetailActivity.EXTRA_ID, invoice.getId());
                    startActivity(intent);
                }
                
                @Override
                public void onInvoiceEdit(Invoice invoice) {
                    // Handle invoice edit
                }
                
                @Override
                public void onInvoiceDelete(Invoice invoice) {
                    // Handle invoice delete
                }
                
                @Override
                public void onInvoiceMarkAsPaid(Invoice invoice) {
                    // Handle invoice mark as paid
                }
                
                @Override
                public void onInvoiceSendToCustomer(Invoice invoice) {
                    // Handle invoice send to customer
                }
                
                @Override
                public void onInvoiceAddPayment(Invoice invoice) {
                    // Handle invoice add payment
                }
            });
            rvInvoices.setLayoutManager(new LinearLayoutManager(getContext()));
            rvInvoices.setAdapter(invoicesAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi setup RecyclerView: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void setupToggleButtons() {
        // Setup Quotes toggle
        tvViewAllQuotes.setOnClickListener(v -> {
            if (showingAllQuotes) {
                // Show recent quotes only
                showingAllQuotes = false;
                tvViewAllQuotes.setText("Xem tất cả");
                loadRecentQuotes();
            } else {
                // Show all quotes
                showingAllQuotes = true;
                tvViewAllQuotes.setText("Thu gọn");
                loadAllQuotes();
            }
        });
        
        // Setup Invoices toggle
        tvViewAllInvoices.setOnClickListener(v -> {
            if (showingAllInvoices) {
                // Show recent invoices only
                showingAllInvoices = false;
                tvViewAllInvoices.setText("Xem tất cả");
                loadRecentInvoices();
            } else {
                // Show all invoices
                showingAllInvoices = true;
                tvViewAllInvoices.setText("Thu gọn");
                loadAllInvoices();
            }
        });
    }
    
    private void loadRevenueData() {
        try {
            // Check authentication first
            if (!authManager.isLoggedIn()) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để xem doanh thu", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Load user projects first
            loadUserProjects();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi tải dữ liệu doanh thu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void loadUserProjects() {
        // Load projects based on user role
        Map<String, Object> params = new HashMap<>();
        String userRole = authManager.getUserRole();
        String userId = authManager.getUserId();
        
        if (userRole != null) {
            switch (userRole.toLowerCase()) {
                case "admin":
                    // Admin can see all projects
                    ApiDebugger.logAuth("Loading all projects for Admin", true);
                    break;
                case "manager":
                    // Manager can see projects they manage
                    params.put("manager_id", userId);
                    ApiDebugger.logAuth("Loading manager projects for user: " + userId, true);
                    break;
                case "employee":
                case "workshop_employee":
                default:
                    // Employee can see projects assigned to them
                    params.put("user_id", userId);
                    ApiDebugger.logAuth("Loading employee projects for user: " + userId, true);
                    break;
            }
        }
        
        ApiDebugger.logRequest("GET", "User Projects", null, params);
        
        projectService.getAllProjects(params, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        userProjects = projects != null ? projects : new ArrayList<>();
                        
                        // Update project info display
                        updateProjectInfo();
                        
                        // Load revenue data for all projects
                        ApiDebugger.logAuth("Loading revenue stats for " + userProjects.size() + " projects", true);
                        loadRevenueStats();
                        ApiDebugger.logAuth("Loading recent quotes for " + userProjects.size() + " projects", true);
                        loadRecentQuotes();
                        ApiDebugger.logAuth("Loading recent invoices for " + userProjects.size() + " projects", true);
                        loadRecentInvoices();
                        
                        ApiDebugger.logResponse(200, "Success", "User projects loaded: " + userProjects.size());
                    });
                }
            }
            
            @Override
            public void onSuccess(Project project) {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi tải dự án: " + error, Toast.LENGTH_SHORT).show();
                        ApiDebugger.logError("loadUserProjects", new Exception(error));
                    });
                }
            }
        });
    }
    
    private void updateProjectInfo() {
        if (tvProjectInfo != null) {
            String projectInfo = "Dự án của bạn: " + userProjects.size() + " dự án";
            if (userProjects.size() > 0) {
                projectInfo += "\nDự án gần đây: " + userProjects.get(0).getName();
            }
            tvProjectInfo.setText(projectInfo);
            ApiDebugger.logAuth("Project info updated: " + projectInfo, true);
        } else {
            ApiDebugger.logAuth("tvProjectInfo is null", false);
        }
    }

    private void loadRevenueStats() {
        // Load total revenue from invoices
        loadTotalRevenue();
        
        // Load quotes count
        loadTotalQuotes();
        
        // Load invoices count
        loadTotalInvoices();
    }
    
    private void loadTotalRevenue() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000);
        
        ApiDebugger.logRequest("GET", "Total Revenue", null, params);
        
        invoiceService.getAllInvoices(params, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        double totalRevenue = 0;
                        if (invoices != null) {
                            for (Invoice invoice : invoices) {
                                if (invoice.getTotal() != null) {
                                    totalRevenue += invoice.getTotal();
                                }
                            }
                        }
                        tvTotalRevenue.setText(CurrencyFormatter.format(totalRevenue));
                        ApiDebugger.logResponse(200, "Success", "Total revenue: " + totalRevenue);
                    });
                }
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalRevenue.setText("0 ₫");
                        ApiDebugger.logError("loadTotalRevenue", new Exception(error));
                        Toast.makeText(getContext(), "Lỗi tải doanh thu: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    
    private void loadTotalQuotes() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000);
        
        ApiDebugger.logRequest("GET", "Total Quotes", null, params);
        
        quoteService.getAllQuotes(params, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        int quoteCount = quotes != null ? quotes.size() : 0;
                        tvTotalQuotes.setText(String.valueOf(quoteCount));
                        ApiDebugger.logResponse(200, "Success", "Total quotes: " + quoteCount);
                    });
                }
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalQuotes.setText("0");
                        ApiDebugger.logError("loadTotalQuotes", new Exception(error));
                    });
                }
            }
        });
    }
    
    private void loadTotalInvoices() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000);
        
        ApiDebugger.logRequest("GET", "Total Invoices", null, params);
        
        invoiceService.getAllInvoices(params, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        int invoiceCount = invoices != null ? invoices.size() : 0;
                        tvTotalInvoices.setText(String.valueOf(invoiceCount));
                        ApiDebugger.logResponse(200, "Success", "Total invoices: " + invoiceCount);
                    });
                }
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalInvoices.setText("0");
                        ApiDebugger.logError("loadTotalInvoices", new Exception(error));
                    });
                }
            }
        });
    }

    private void loadRecentQuotes() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 5);
        params.put("sort", "created_at");
        params.put("order", "desc");
        
        // Filter by user's projects
        if (userProjects != null && !userProjects.isEmpty()) {
            List<String> projectIds = new ArrayList<>();
            for (Project project : userProjects) {
                projectIds.add(project.getId());
            }
            params.put("project_id", String.join(",", projectIds));
        }
        
        ApiDebugger.logRequest("GET", "Recent Quotes", null, params);
        ApiDebugger.logAuth("Recent Quotes params: " + params.toString(), true);
        
        quoteService.getAllQuotes(params, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (quotes != null) {
                            quotesAdapter.updateQuotes(quotes);
                            ApiDebugger.logResponse(200, "Success", "Recent quotes loaded: " + quotes.size());
                        } else {
                            quotesAdapter.updateQuotes(new ArrayList<>());
                            ApiDebugger.logResponse(200, "Success", "Recent quotes loaded: 0 (null response)");
                        }
                    });
                }
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi tải báo giá gần đây: " + error, Toast.LENGTH_SHORT).show();
                        ApiDebugger.logError("loadRecentQuotes", new Exception(error));
                    });
                }
            }
        });
    }
    
    private void loadRecentInvoices() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 5);
        params.put("sort", "created_at");
        params.put("order", "desc");
        
        // Filter by user's projects
        if (userProjects != null && !userProjects.isEmpty()) {
            List<String> projectIds = new ArrayList<>();
            for (Project project : userProjects) {
                projectIds.add(project.getId());
            }
            params.put("project_id", String.join(",", projectIds));
        }
        
        ApiDebugger.logRequest("GET", "Recent Invoices", null, params);
        ApiDebugger.logAuth("Recent Invoices params: " + params.toString(), true);
        
        invoiceService.getAllInvoices(params, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (invoices != null) {
                            invoicesAdapter.updateInvoices(invoices);
                            ApiDebugger.logResponse(200, "Success", "Recent invoices loaded: " + invoices.size());
                        } else {
                            invoicesAdapter.updateInvoices(new ArrayList<>());
                            ApiDebugger.logResponse(200, "Success", "Recent invoices loaded: 0 (null response)");
                        }
                    });
                }
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi tải hóa đơn gần đây: " + error, Toast.LENGTH_SHORT).show();
                        ApiDebugger.logError("loadRecentInvoices", new Exception(error));
                    });
                }
            }
        });
    }
    
    private void loadAllQuotes() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000);
        params.put("sort", "created_at");
        params.put("order", "desc");
        
        // Filter by user's projects
        if (userProjects != null && !userProjects.isEmpty()) {
            List<String> projectIds = new ArrayList<>();
            for (Project project : userProjects) {
                projectIds.add(project.getId());
            }
            params.put("project_id", String.join(",", projectIds));
        }
        
        ApiDebugger.logRequest("GET", "All Quotes", null, params);
        
        quoteService.getAllQuotes(params, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        quotesAdapter.updateQuotes(quotes);
                        ApiDebugger.logResponse(200, "Success", "All quotes loaded: " + quotes.size());
                    });
                }
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi tải tất cả báo giá: " + error, Toast.LENGTH_SHORT).show();
                        ApiDebugger.logError("loadAllQuotes", new Exception(error));
                    });
                }
            }
        });
    }
    
    private void loadAllInvoices() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000);
        params.put("sort", "created_at");
        params.put("order", "desc");
        
        // Filter by user's projects
        if (userProjects != null && !userProjects.isEmpty()) {
            List<String> projectIds = new ArrayList<>();
            for (Project project : userProjects) {
                projectIds.add(project.getId());
            }
            params.put("project_id", String.join(",", projectIds));
        }
        
        ApiDebugger.logRequest("GET", "All Invoices", null, params);
        
        invoiceService.getAllInvoices(params, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        invoicesAdapter.updateInvoices(invoices);
                        ApiDebugger.logResponse(200, "Success", "All invoices loaded: " + invoices.size());
                    });
                }
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi tải tất cả hóa đơn: " + error, Toast.LENGTH_SHORT).show();
                        ApiDebugger.logError("loadAllInvoices", new Exception(error));
                    });
                }
            }
        });
    }
}
