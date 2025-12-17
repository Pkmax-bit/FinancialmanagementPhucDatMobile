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
import com.example.financialmanagement.adapters.InvoicesAdapter;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.services.InvoiceService;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.utils.ApiDebugger;
import android.content.Intent;
import com.example.financialmanagement.activities.InvoiceDetailActivity;
import com.example.financialmanagement.activities.AddEditInvoiceActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Invoices Fragment - Màn hình quản lý hóa đơn
 * Bước 6 trong quy trình: Tạo khách hàng → Dự án → Báo giá → Hóa đơn → Chi phí → Báo cáo
 * Hiển thị danh sách hóa đơn và cho phép thao tác CRUD với payment tracking
 */
public class InvoicesFragment extends Fragment implements InvoicesAdapter.InvoiceClickListener {

    private RecyclerView rvInvoices;
    private InvoicesAdapter invoicesAdapter;
    private InvoiceService invoiceService;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoices, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadInvoices();
        
        return view;
    }

    private void initializeViews(View view) {
        rvInvoices = view.findViewById(R.id.rv_invoices);
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAddInvoice = view.findViewById(R.id.fab_add_invoice);
        
        fabAddInvoice.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getContext(), com.example.financialmanagement.activities.AddEditInvoiceActivity.class);
            startActivity(intent);
        });

        invoiceService = new InvoiceService(getContext());
        authManager = new AuthManager(getContext());
    }

    private void setupRecyclerView() {
        invoicesAdapter = new InvoicesAdapter(new ArrayList<>(), this);
        rvInvoices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInvoices.setAdapter(invoicesAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInvoices();
    }

    private void loadInvoices() {
        // Check authentication first
        if (!authManager.isLoggedIn()) {
            ApiDebugger.logAuth("User not logged in, cannot load invoices", false);
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem hóa đơn", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Load invoices based on user role
        Map<String, Object> params = new HashMap<>();
        
        // Add role-based filtering
        String userRole = authManager.getUserRole();
        if (userRole != null) {
            switch (userRole.toLowerCase()) {
                case "admin":
                    // Admin can see all invoices
                    ApiDebugger.logAuth("Loading all invoices for Admin", true);
                    break;
                case "manager":
                    // Manager can see invoices for their projects
                    params.put("manager_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading manager invoices for user: " + authManager.getUserId(), true);
                    break;
                case "accountant":
                    // Accountant can see all invoices for financial management
                    ApiDebugger.logAuth("Loading all invoices for Accountant", true);
                    break;
                default:
                    // Default: show invoices for user's projects
                    params.put("user_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading user invoices for user: " + authManager.getUserId(), true);
                    break;
            }
        }
        
        invoiceService.getAllInvoices(params, new InvoiceService.InvoiceCallback() {
            @Override
            public void onSuccess(List<Invoice> invoices) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        invoicesAdapter.updateInvoices(invoices);
                        ApiDebugger.logResponse(200, "Success", "Invoices loaded: " + invoices.size());
                        
                        // Show role-based message
                        String roleMessage = getRoleBasedMessage(userRole, invoices.size());
                        if (roleMessage != null) {
                            Toast.makeText(getContext(), roleMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onSuccess(Invoice invoice) {
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
                            Toast.makeText(getContext(), "Lỗi tải hóa đơn: " + error, Toast.LENGTH_SHORT).show();
                        }
                        
                        ApiDebugger.logError("loadInvoices", new Exception(error));
                    });
                }
            }
        });
    }
    
    private String getRoleBasedMessage(String userRole, int invoiceCount) {
        if (userRole == null) return null;
        
        switch (userRole.toLowerCase()) {
            case "admin":
                return "Admin: Đã tải " + invoiceCount + " hóa đơn (tất cả)";
            case "manager":
                return "Manager: Đã tải " + invoiceCount + " hóa đơn (dự án quản lý)";
            case "accountant":
                return "Accountant: Đã tải " + invoiceCount + " hóa đơn (quản lý tài chính)";
            default:
                return "Đã tải " + invoiceCount + " hóa đơn";
        }
    }

    @Override
    public void onInvoiceClick(Invoice invoice) {
        // Navigate to invoice detail
        Intent intent = new Intent(getContext(), InvoiceDetailActivity.class);
        intent.putExtra(InvoiceDetailActivity.EXTRA_INVOICE_ID, invoice.getId());
        startActivity(intent);
    }

    @Override
    public void onInvoiceEdit(Invoice invoice) {
        // Navigate to edit invoice
        Intent intent = new Intent(getContext(), AddEditInvoiceActivity.class);
        intent.putExtra("invoice_id", invoice.getId());
        startActivity(intent);
    }

    @Override
    public void onInvoiceDelete(Invoice invoice) {
        // Delete invoice with confirmation
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa hóa đơn " + invoice.getInvoiceNumber() + "?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                invoiceService.deleteInvoice(invoice.getId(), new InvoiceService.InvoiceCallback() {
                    @Override
                    public void onSuccess(List<Invoice> invoices) {}
                    
                    @Override
                    public void onSuccess(Invoice inv) {}
                    
                    @Override
                    public void onSuccess() {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Đã xóa hóa đơn", Toast.LENGTH_SHORT).show();
                                loadInvoices();
                            });
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    @Override
    public void onInvoiceMarkAsPaid(Invoice invoice) {
        // Mark invoice as paid
        // TODO: Implement mark invoice as paid functionality
        Toast.makeText(getContext(), "Đánh dấu hóa đơn đã thanh toán: " + invoice.getInvoiceNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInvoiceSendToCustomer(Invoice invoice) {
        // Send invoice to customer
        // TODO: Implement send invoice to customer functionality
        Toast.makeText(getContext(), "Gửi hóa đơn cho khách hàng: " + invoice.getInvoiceNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInvoiceAddPayment(Invoice invoice) {
        // Add payment to invoice
        // TODO: Implement add payment functionality
        Toast.makeText(getContext(), "Thêm thanh toán cho hóa đơn: " + invoice.getInvoiceNumber(), Toast.LENGTH_SHORT).show();
    }
}
