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
import com.example.financialmanagement.adapters.BudgetAdapter;
import com.example.financialmanagement.models.Budget;
import com.example.financialmanagement.services.BudgetService;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.utils.ApiDebugger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Budget Fragment - Màn hình quản lý ngân sách dự án
 * Bước 4 trong quy trình: Tạo khách hàng → Dự án → Báo giá → Ngân sách → Hóa đơn → Chi phí → Báo cáo
 * Hiển thị ngân sách dự án và cho phép phân bổ chi phí theo danh mục
 */
public class BudgetFragment extends Fragment implements BudgetAdapter.BudgetClickListener {

    private RecyclerView rvBudgets;
    private BudgetAdapter budgetAdapter;
    private BudgetService budgetService;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadBudgets();
        
        return view;
    }

    private void initializeViews(View view) {
        rvBudgets = view.findViewById(R.id.rv_budgets);
        budgetService = new BudgetService(getContext());
        authManager = new AuthManager(getContext());
    }

    private void setupRecyclerView() {
        budgetAdapter = new BudgetAdapter(new ArrayList<>(), this);
        rvBudgets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBudgets.setAdapter(budgetAdapter);
    }

    private void loadBudgets() {
        // Check authentication first
        if (!authManager.isLoggedIn()) {
            ApiDebugger.logAuth("User not logged in, cannot load budgets", false);
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem ngân sách", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Load budgets based on user role
        Map<String, Object> params = new HashMap<>();
        
        // Add role-based filtering
        String userRole = authManager.getUserRole();
        if (userRole != null) {
            switch (userRole.toLowerCase()) {
                case "admin":
                    // Admin can see all budgets
                    ApiDebugger.logAuth("Loading all budgets for Admin", true);
                    break;
                case "manager":
                    // Manager can see budgets for their projects
                    params.put("manager_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading manager budgets for user: " + authManager.getUserId(), true);
                    break;
                case "accountant":
                    // Accountant can see all budgets for financial management
                    ApiDebugger.logAuth("Loading all budgets for Accountant", true);
                    break;
                default:
                    // Default: show budgets for user's projects
                    params.put("user_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading user budgets for user: " + authManager.getUserId(), true);
                    break;
            }
        }
        
        budgetService.getAllBudgets(params, new BudgetService.BudgetCallback() {
            @Override
            public void onSuccess(List<Budget> budgets) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        budgetAdapter.updateBudgets(budgets);
                        ApiDebugger.logResponse(200, "Success", "Budgets loaded: " + budgets.size());
                        
                        // Show role-based message
                        String roleMessage = getRoleBasedMessage(userRole, budgets.size());
                        if (roleMessage != null) {
                            Toast.makeText(getContext(), roleMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onSuccess(Budget budget) {
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
                            Toast.makeText(getContext(), "Lỗi tải ngân sách: " + error, Toast.LENGTH_SHORT).show();
                        }
                        
                        ApiDebugger.logError("loadBudgets", new Exception(error));
                    });
                }
            }
        });
    }
    
    private String getRoleBasedMessage(String userRole, int budgetCount) {
        if (userRole == null) return null;
        
        switch (userRole.toLowerCase()) {
            case "admin":
                return "Admin: Đã tải " + budgetCount + " ngân sách (tất cả)";
            case "manager":
                return "Manager: Đã tải " + budgetCount + " ngân sách (dự án quản lý)";
            case "accountant":
                return "Accountant: Đã tải " + budgetCount + " ngân sách (quản lý tài chính)";
            default:
                return "Đã tải " + budgetCount + " ngân sách";
        }
    }

    @Override
    public void onBudgetClick(Budget budget) {
        // Navigate to budget detail
        // TODO: Implement navigation to budget detail activity
        Toast.makeText(getContext(), "Xem chi tiết ngân sách: " + budget.getProjectName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBudgetEdit(Budget budget) {
        // Navigate to edit budget
        // TODO: Implement navigation to edit budget activity
        Toast.makeText(getContext(), "Chỉnh sửa ngân sách: " + budget.getProjectName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBudgetDelete(Budget budget) {
        // Delete budget
        // TODO: Implement delete budget functionality
        Toast.makeText(getContext(), "Xóa ngân sách: " + budget.getProjectName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBudgetAllocate(Budget budget) {
        // Allocate budget to categories
        // TODO: Implement budget allocation functionality
        Toast.makeText(getContext(), "Phân bổ ngân sách: " + budget.getProjectName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBudgetTrack(Budget budget) {
        // Track budget vs actual
        // TODO: Implement budget tracking functionality
        Toast.makeText(getContext(), "Theo dõi ngân sách: " + budget.getProjectName(), Toast.LENGTH_SHORT).show();
    }
}
