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
import com.example.financialmanagement.adapters.CustomersAdapter;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.services.CustomerService;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.utils.ApiDebugger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Customers Fragment - Màn hình quản lý khách hàng
 * Bước 1 trong quy trình: Tạo khách hàng → Dự án → Báo giá → Hóa đơn → Chi phí → Báo cáo
 * Hiển thị danh sách khách hàng và cho phép thao tác CRUD
 */
public class CustomersFragment extends Fragment implements CustomersAdapter.CustomerClickListener {

    private RecyclerView rvCustomers;
    private CustomersAdapter customersAdapter;
    private CustomerService customerService;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadCustomers();
        
        return view;
    }

    private void initializeViews(View view) {
        rvCustomers = view.findViewById(R.id.rv_customers);
        customerService = new CustomerService(getContext());
        authManager = new AuthManager(getContext());
    }

    private void setupRecyclerView() {
        customersAdapter = new CustomersAdapter(new ArrayList<>(), this);
        rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustomers.setAdapter(customersAdapter);
    }

    private void loadCustomers() {
        // Check authentication first
        if (!authManager.isLoggedIn()) {
            ApiDebugger.logAuth("User not logged in, cannot load customers", false);
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem khách hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Load customers based on user role
        Map<String, Object> params = new HashMap<>();
        
        // Add role-based filtering
        String userRole = authManager.getUserRole();
        if (userRole != null) {
            switch (userRole.toLowerCase()) {
                case "admin":
                    // Admin can see all customers
                    ApiDebugger.logAuth("Loading all customers for Admin", true);
                    break;
                case "manager":
                    // Manager can see customers for their projects
                    params.put("manager_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading manager customers for user: " + authManager.getUserId(), true);
                    break;
                case "sales":
                    // Sales can see customers they created
                    params.put("created_by", authManager.getUserId());
                    ApiDebugger.logAuth("Loading sales customers for user: " + authManager.getUserId(), true);
                    break;
                default:
                    // Default: show customers for user's projects
                    params.put("user_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading user customers for user: " + authManager.getUserId(), true);
                    break;
            }
        }
        
        customerService.getAllCustomers(params, new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        customersAdapter.updateCustomers(customers);
                        ApiDebugger.logResponse(200, "Success", "Customers loaded: " + customers.size());
                        
                        // Show role-based message
                        String roleMessage = getRoleBasedMessage(userRole, customers.size());
                        if (roleMessage != null) {
                            Toast.makeText(getContext(), roleMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onSuccess(Customer customer) {
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
                            Toast.makeText(getContext(), "Lỗi tải khách hàng: " + error, Toast.LENGTH_SHORT).show();
                        }
                        
                        ApiDebugger.logError("loadCustomers", new Exception(error));
                    });
                }
            }
        });
    }
    
    private String getRoleBasedMessage(String userRole, int customerCount) {
        if (userRole == null) return null;
        
        switch (userRole.toLowerCase()) {
            case "admin":
                return "Admin: Đã tải " + customerCount + " khách hàng (tất cả)";
            case "manager":
                return "Manager: Đã tải " + customerCount + " khách hàng (dự án quản lý)";
            case "sales":
                return "Sales: Đã tải " + customerCount + " khách hàng (đã tạo)";
            default:
                return "Đã tải " + customerCount + " khách hàng";
        }
    }

    @Override
    public void onCustomerClick(Customer customer) {
        // Navigate to customer detail
        // TODO: Implement navigation to customer detail activity
        Toast.makeText(getContext(), "Xem chi tiết khách hàng: " + customer.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCustomerEdit(Customer customer) {
        // Navigate to edit customer
        // TODO: Implement navigation to edit customer activity
        Toast.makeText(getContext(), "Chỉnh sửa khách hàng: " + customer.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCustomerDelete(Customer customer) {
        // Delete customer
        // TODO: Implement delete customer functionality
        Toast.makeText(getContext(), "Xóa khách hàng: " + customer.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCustomerCreateProject(Customer customer) {
        // Navigate to create project for this customer
        // TODO: Implement navigation to create project activity
        Toast.makeText(getContext(), "Tạo dự án cho khách hàng: " + customer.getName(), Toast.LENGTH_SHORT).show();
    }
}
