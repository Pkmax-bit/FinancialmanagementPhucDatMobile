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
import java.util.ArrayList;
import java.util.List;

/**
 * Expenses Fragment - Màn hình quản lý chi phí
 * Hiển thị danh sách chi phí và cho phép thao tác CRUD
 */
public class ExpensesFragment extends Fragment implements ExpensesAdapter.ExpenseClickListener {

    private RecyclerView rvExpenses;
    private ExpensesAdapter expensesAdapter;
    private ExpenseService expenseService;

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
    }

    private void setupRecyclerView() {
        expensesAdapter = new ExpensesAdapter(new ArrayList<>(), this);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(expensesAdapter);
    }

    private void loadExpenses() {
        expenseService.getAllExpenses(new ExpenseService.ExpenseCallback() {
            @Override
            public void onSuccess(List<ProjectExpense> expenses) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        expensesAdapter.updateExpenses(expenses);
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
                    });
                }
            }
        });
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
