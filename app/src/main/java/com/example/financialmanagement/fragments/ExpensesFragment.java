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
import com.example.financialmanagement.adapters.ExpensesAdapter;
import com.example.financialmanagement.models.ProjectExpense;
import com.example.financialmanagement.services.ExpenseService;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.utils.ApiDebugger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Expenses Fragment - Màn hình quản lý chi phí dự án
 * Tập trung vào việc quản lý chi phí thực tế của dự án web Phúc Đạt
 * Hiển thị danh sách chi phí theo role và cho phép thao tác CRUD
 */
public class ExpensesFragment extends Fragment implements ExpensesAdapter.ExpenseClickListener {

    private RecyclerView rvExpenses;
    private ExpensesAdapter expensesAdapter;
    private ExpenseService expenseService;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadExpenses();
        
        return view;
    }

    private void initializeViews(View view) {
        rvExpenses = view.findViewById(R.id.rv_expenses);
        expenseService = new ExpenseService(getContext());
        authManager = new AuthManager(getContext());
    }

    private void setupRecyclerView() {
        expensesAdapter = new ExpensesAdapter(new ArrayList<>(), this);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(expensesAdapter);
    }

    private void loadExpenses() {
        // Load expenses based on user role
        Map<String, Object> params = new HashMap<>();
        
        // Add role-based filtering
        String userRole = authManager.getUserRole();
        if (userRole != null) {
            switch (userRole.toLowerCase()) {
                case "admin":
                    // Admin can see all expenses
                    ApiDebugger.logAuth("Loading all expenses for Admin", true);
                    break;
                case "manager":
                    // Manager can see expenses for their projects
                    params.put("manager_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading manager expenses for user: " + authManager.getUserId(), true);
                    break;
                case "employee":
                default:
                    // Employee can only see their own expenses
                    params.put("created_by", authManager.getUserId());
                    ApiDebugger.logAuth("Loading employee expenses for user: " + authManager.getUserId(), true);
                    break;
            }
        }
        
        expenseService.getAllExpenses(params, new ExpenseService.ExpenseCallback() {
            @Override
            public void onSuccess(List<ProjectExpense> expenses) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        expensesAdapter.updateExpenses(expenses);
                        ApiDebugger.logResponse(200, "Success", "Expenses loaded: " + expenses.size());
                        
                        // Show role-based message
                        String roleMessage = getRoleBasedMessage(userRole, expenses.size());
                        if (roleMessage != null) {
                            Toast.makeText(getContext(), roleMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onSuccess(ProjectExpense expense) {
                // Not used in this context
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi tải chi phí: " + error, Toast.LENGTH_SHORT).show();
                        ApiDebugger.logError("loadExpenses", new Exception(error));
                    });
                }
            }
        });
    }
    
    private String getRoleBasedMessage(String userRole, int expenseCount) {
        if (userRole == null) return null;
        
        switch (userRole.toLowerCase()) {
            case "admin":
                return "Admin: Đã tải " + expenseCount + " chi phí (tất cả dự án)";
            case "manager":
                return "Manager: Đã tải " + expenseCount + " chi phí (dự án quản lý)";
            case "employee":
                return "Employee: Đã tải " + expenseCount + " chi phí (của bạn)";
            default:
                return "Đã tải " + expenseCount + " chi phí";
        }
    }

    @Override
    public void onExpenseClick(ProjectExpense expense) {
        // Navigate to expense detail
        // TODO: Implement navigation to expense detail activity
        Toast.makeText(getContext(), "Xem chi tiết chi phí: " + expense.getDescription(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onExpenseEdit(ProjectExpense expense) {
        // Navigate to edit expense
        // TODO: Implement navigation to edit expense activity
        Toast.makeText(getContext(), "Chỉnh sửa chi phí: " + expense.getDescription(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onExpenseDelete(ProjectExpense expense) {
        // Show confirmation dialog and delete expense
        // TODO: Implement delete confirmation dialog
        Toast.makeText(getContext(), "Xóa chi phí: " + expense.getDescription(), Toast.LENGTH_SHORT).show();
    }
}
