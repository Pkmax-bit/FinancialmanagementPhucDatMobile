package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Budget;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.util.List;

/**
 * Budget Adapter - Adapter cho danh sách ngân sách
 * Hiển thị thông tin ngân sách với các thao tác CRUD và cost allocation
 */
public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private List<Budget> budgets;
    private BudgetClickListener clickListener;

    public interface BudgetClickListener {
        void onBudgetClick(Budget budget);
        void onBudgetEdit(Budget budget);
        void onBudgetDelete(Budget budget);
        void onBudgetAllocate(Budget budget);
        void onBudgetTrack(Budget budget);
    }

    public BudgetAdapter(List<Budget> budgets, BudgetClickListener clickListener) {
        this.budgets = budgets;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgets.get(position);
        holder.bind(budget, clickListener);
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public void updateBudgets(List<Budget> newBudgets) {
        this.budgets = newBudgets;
        notifyDataSetChanged();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBudgetTitle;
        private TextView tvBudgetProject;
        private TextView tvBudgetStatus;
        private TextView tvBudgetTotal;
        private TextView tvBudgetSpent;
        private TextView tvBudgetRemaining;
        private TextView tvBudgetAllocated;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBudgetTitle = itemView.findViewById(R.id.tv_budget_title);
            tvBudgetProject = itemView.findViewById(R.id.tv_budget_project);
            tvBudgetStatus = itemView.findViewById(R.id.tv_budget_status);
            tvBudgetTotal = itemView.findViewById(R.id.tv_budget_total);
            tvBudgetSpent = itemView.findViewById(R.id.tv_budget_spent);
            tvBudgetRemaining = itemView.findViewById(R.id.tv_budget_remaining);
            tvBudgetAllocated = itemView.findViewById(R.id.tv_budget_allocated);
        }

        public void bind(Budget budget, BudgetClickListener clickListener) {
            // Basic information
            tvBudgetTitle.setText(budget.getTitle());
            tvBudgetProject.setText(budget.getProjectName());
            tvBudgetStatus.setText(getBudgetStatusText(budget.getStatus()));
            
            // Financial information
            if (budget.getTotalBudget() != null) {
                tvBudgetTotal.setText("Tổng ngân sách: " + CurrencyFormatter.format(budget.getTotalBudget()));
            } else {
                tvBudgetTotal.setText("Chưa có tổng ngân sách");
            }
            
            if (budget.getSpentAmount() != null) {
                tvBudgetSpent.setText("Đã chi: " + CurrencyFormatter.format(budget.getSpentAmount()));
            } else {
                tvBudgetSpent.setText("Chưa có chi phí");
            }
            
            if (budget.getRemainingBudget() != null) {
                tvBudgetRemaining.setText("Còn lại: " + CurrencyFormatter.format(budget.getRemainingBudget()));
            } else {
                tvBudgetRemaining.setText("Chưa có số dư");
            }
            
            if (budget.getAllocatedBudget() != null) {
                tvBudgetAllocated.setText("Đã phân bổ: " + CurrencyFormatter.format(budget.getAllocatedBudget()));
            } else {
                tvBudgetAllocated.setText("Chưa phân bổ");
            }
            
            // Click listeners
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onBudgetClick(budget);
                }
            });
        }

        private String getBudgetStatusText(String status) {
            if (status == null) return "Draft";
            switch (status.toLowerCase()) {
                case "draft":
                    return "Nháp";
                case "approved":
                    return "Đã duyệt";
                case "active":
                    return "Hoạt động";
                case "completed":
                    return "Hoàn thành";
                default:
                    return status;
            }
        }
    }
}

