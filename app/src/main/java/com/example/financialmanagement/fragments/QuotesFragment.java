package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.QuotesAdapter;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.services.QuoteService;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.utils.ApiDebugger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quotes Fragment - Màn hình quản lý báo giá
 * Bước 3 trong quy trình: Tạo khách hàng → Dự án → Báo giá → Hóa đơn → Chi phí → Báo cáo
 * Hiển thị danh sách báo giá và cho phép thao tác CRUD với approval workflow
 */
public class QuotesFragment extends Fragment implements QuotesAdapter.QuoteClickListener {

    private RecyclerView rvQuotes;
    private QuotesAdapter quotesAdapter;
    private QuoteService quoteService;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadQuotes();
        
        return view;
    }

    private void initializeViews(View view) {
        rvQuotes = view.findViewById(R.id.rv_quotes);
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAddQuote = view.findViewById(R.id.fab_add_quote);
        
        fabAddQuote.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getContext(), com.example.financialmanagement.activities.AddEditQuoteActivity.class);
            startActivity(intent);
        });

        quoteService = new QuoteService(getContext());
        authManager = new AuthManager(getContext());
    }

    private void setupRecyclerView() {
        quotesAdapter = new QuotesAdapter(new ArrayList<>(), this);
        rvQuotes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuotes.setAdapter(quotesAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadQuotes();
    }

    private void loadQuotes() {
        // Check authentication first
        if (!authManager.isLoggedIn()) {
            ApiDebugger.logAuth("User not logged in, cannot load quotes", false);
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem báo giá", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Load quotes based on user role
        Map<String, Object> params = new HashMap<>();
        
        // Add role-based filtering
        String userRole = authManager.getUserRole();
        if (userRole != null) {
            switch (userRole.toLowerCase()) {
                case "admin":
                    // Admin can see all quotes
                    ApiDebugger.logAuth("Loading all quotes for Admin", true);
                    break;
                case "manager":
                    // Manager can see quotes for their projects
                    params.put("manager_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading manager quotes for user: " + authManager.getUserId(), true);
                    break;
                case "sales":
                    // Sales can see quotes they created
                    params.put("created_by", authManager.getUserId());
                    ApiDebugger.logAuth("Loading sales quotes for user: " + authManager.getUserId(), true);
                    break;
                default:
                    // Default: show quotes for user's projects
                    params.put("user_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading user quotes for user: " + authManager.getUserId(), true);
                    break;
            }
        }
        
        quoteService.getAllQuotes(params, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        quotesAdapter.updateQuotes(quotes);
                        ApiDebugger.logResponse(200, "Success", "Quotes loaded: " + quotes.size());
                        
                        // Show role-based message
                        String roleMessage = getRoleBasedMessage(userRole, quotes.size());
                        if (roleMessage != null) {
                            Toast.makeText(getContext(), roleMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onSuccess(Quote quote) {
                // Not used in this context
            }

            @Override
            public void onSuccess() {
                // Not used in this context
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Check if it's an authentication error
                        if (error.contains("403") || error.contains("401") || error.contains("Not authenticated")) {
                            ApiDebugger.logAuth("Authentication failed, redirecting to login", false);
                            Toast.makeText(getContext(), "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                            
                            // Clear auth data and redirect to login
                            authManager.logout();
                        } else {
                            Toast.makeText(getContext(), "Lỗi tải báo giá: " + error, Toast.LENGTH_SHORT).show();
                        }
                        
                        ApiDebugger.logError("loadQuotes", new Exception(error));
                    });
                }
            }
        });
    }
    
    private String getRoleBasedMessage(String userRole, int quoteCount) {
        if (userRole == null) return null;
        
        switch (userRole.toLowerCase()) {
            case "admin":
                return "Admin: Đã tải " + quoteCount + " báo giá (tất cả)";
            case "manager":
                return "Manager: Đã tải " + quoteCount + " báo giá (dự án quản lý)";
            case "sales":
                return "Sales: Đã tải " + quoteCount + " báo giá (đã tạo)";
            default:
                return "Đã tải " + quoteCount + " báo giá";
        }
    }

    @Override
    public void onQuoteClick(Quote quote) {
        // Navigate to quote detail
        onQuoteViewDetails(quote);
    }

    @Override
    public void onQuoteEdit(Quote quote) {
        // Navigate to edit quote
        android.content.Intent intent = new android.content.Intent(getContext(), com.example.financialmanagement.activities.AddEditQuoteActivity.class);
        intent.putExtra("quote_id", quote.getId());
        startActivity(intent);
    }

    @Override
    public void onQuoteDelete(Quote quote) {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Xóa báo giá")
                .setMessage("Bạn có chắc chắn muốn xóa báo giá " + (quote.getQuoteNumber() != null ? quote.getQuoteNumber() : "") + "?\n\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteQuote(quote);
                })
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onQuoteApprove(Quote quote) {
        // Approve quote
        approveQuote(quote);
    }

    @Override
    public void onQuoteConvertToInvoice(Quote quote) {
        // Convert quote to invoice
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Chuyển đổi báo giá thành hóa đơn")
                .setMessage("Bạn có chắc chắn muốn chuyển báo giá " + quote.getQuoteNumber() + " thành hóa đơn?")
                .setPositiveButton("Chuyển đổi", (dialog, which) -> {
                    convertQuoteToInvoice(quote);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onQuoteSendToCustomer(Quote quote) {
        // Send quote to customer
        Toast.makeText(getContext(), "Tính năng gửi báo giá đang được phát triển", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuoteViewDetails(Quote quote) {
        // Navigate to quote detail
        android.content.Intent intent = new android.content.Intent(getContext(), com.example.financialmanagement.activities.QuoteDetailActivity.class);
        intent.putExtra(com.example.financialmanagement.activities.QuoteDetailActivity.EXTRA_QUOTE_ID, quote.getId());
        startActivity(intent);
    }

    @Override
    public void onQuoteReview(Quote quote) {
        // Review quote - same as approve
        approveQuote(quote);
    }

    @Override
    public void onQuoteExportPDF(Quote quote) {
        // Export quote to PDF
        Toast.makeText(getContext(), "Tính năng xuất PDF đang được phát triển", Toast.LENGTH_SHORT).show();
    }

    private void deleteQuote(Quote quote) {
        if (quote.getId() == null || quote.getId().isEmpty()) {
            Toast.makeText(getContext(), "Không thể xóa báo giá: ID không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading
        Toast.makeText(getContext(), "Đang xóa báo giá...", Toast.LENGTH_SHORT).show();
        
        quoteService.deleteQuote(quote.getId(), new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                // Not used
            }

            @Override
            public void onSuccess(Quote quote) {
                // Not used
            }

            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đã xóa báo giá thành công", Toast.LENGTH_SHORT).show();
                        loadQuotes();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        String errorMessage = "Lỗi xóa báo giá";
                        if (error != null && !error.isEmpty()) {
                            errorMessage += ": " + error;
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void approveQuote(Quote quote) {
        quoteService.approveQuote(quote.getId(), new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                // Not used
            }

            @Override
            public void onSuccess(Quote quote) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đã duyệt báo giá thành công", Toast.LENGTH_SHORT).show();
                        loadQuotes();
                    });
                }
            }

            @Override
            public void onSuccess() {
                // Not used
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi duyệt báo giá: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void convertQuoteToInvoice(Quote quote) {
        quoteService.convertToInvoice(quote.getId(), new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                // Not used
            }

            @Override
            public void onSuccess(Quote quote) {
                // Not used
            }

            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đã chuyển đổi báo giá thành hóa đơn thành công", Toast.LENGTH_SHORT).show();
                        loadQuotes();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi chuyển đổi báo giá: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}
