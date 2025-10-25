package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.ExpensesAdapter;
import com.example.financialmanagement.models.ProjectExpense;

import java.util.ArrayList;
import java.util.List;

public class ProjectExpensesFragment extends Fragment {
    
    private static final String ARG_PROJECT_ID = "project_id";
    
    private String projectId;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private ExpensesAdapter expensesAdapter;
    private List<ProjectExpense> expenses = new ArrayList<>();
    
    public static ProjectExpensesFragment newInstance(String projectId) {
        ProjectExpensesFragment fragment = new ProjectExpensesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, projectId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectId = getArguments().getString(ARG_PROJECT_ID);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_expenses, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        tvEmpty = view.findViewById(R.id.tv_empty);
        
        setupRecyclerView();
        
        return view;
    }
    
    private void setupRecyclerView() {
        expensesAdapter = new ExpensesAdapter(expenses, new ExpensesAdapter.ExpenseClickListener() {
            @Override
            public void onExpenseClick(ProjectExpense expense) {
                // Handle expense click - could open expense detail
            }
            
            @Override
            public void onExpenseDelete(ProjectExpense expense) {
                // Handle delete expense
            }
            
            @Override
            public void onExpenseEdit(ProjectExpense expense) {
                // Handle edit expense
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(expensesAdapter);
    }
    
    public void updateExpenses(List<ProjectExpense> newExpenses) {
        System.out.println("ProjectExpensesFragment: updateExpenses called with " + (newExpenses != null ? newExpenses.size() : "null") + " expenses");
        
        if (newExpenses != null) {
            expenses.clear();
            expenses.addAll(newExpenses);
        } else {
            expenses.clear();
        }
        
        System.out.println("ProjectExpensesFragment: expenses list now has " + expenses.size() + " items");
        
        if (expensesAdapter != null) {
            expensesAdapter.updateExpenses(expenses);
            System.out.println("ProjectExpensesFragment: adapter updated");
        } else {
            System.out.println("ProjectExpensesFragment: expensesAdapter is null!");
        }
        
        if (tvEmpty != null) {
            tvEmpty.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
            System.out.println("ProjectExpensesFragment: empty view visibility set to " + (expenses.isEmpty() ? "VISIBLE" : "GONE"));
        } else {
            System.out.println("ProjectExpensesFragment: tvEmpty is null!");
        }
    }
}
