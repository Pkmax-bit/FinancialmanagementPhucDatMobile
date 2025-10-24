package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.ProjectExpense;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Expenses Adapter - Adapter cho danh sách chi phí
 * Hiển thị danh sách chi phí trong RecyclerView
 */
public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder> {
    
    private List<ProjectExpense> expenses;
    private ExpenseClickListener clickListener;
    private SimpleDateFormat dateFormat;
    
    public ExpensesAdapter(List<ProjectExpense> expenses, ExpenseClickListener clickListener) {
        this.expenses = expenses;
        this.clickListener = clickListener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ProjectExpense expense = expenses.get(position);
        holder.bind(expense);
    }
    
    @Override
    public int getItemCount() {
        return expenses.size();
    }
    
    /**
     * Cập nhật danh sách chi phí
     */
    public void updateExpenses(List<ProjectExpense> newExpenses) {
        this.expenses = newExpenses;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder cho Expense
     */
    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvDescription, tvAmount, tvStatus, tvDate, tvRole, tvProjectName;
        
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvRole = itemView.findViewById(R.id.tv_role);
            tvProjectName = itemView.findViewById(R.id.tv_project_name);
            
            // Set click listeners
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onExpenseClick(expenses.get(getAdapterPosition()));
                    }
                }
            });
            
            // Set long click listener for context menu
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (clickListener != null) {
                        // TODO: Show context menu with edit/delete options
                        clickListener.onExpenseEdit(expenses.get(getAdapterPosition()));
                    }
                    return true;
                }
            });
        }
        
        public void bind(ProjectExpense expense) {
            tvDescription.setText(expense.getDescription());
            tvAmount.setText(CurrencyFormatter.format(expense.getAmount() != null ? expense.getAmount() : 0));
            tvStatus.setText(expense.getStatusDisplayName());
            tvRole.setText(expense.getRoleDisplayName());
            
            // Set date
            if (expense.getExpenseDate() != null) {
                tvDate.setText(dateFormat.format(expense.getExpenseDate()));
            } else {
                tvDate.setText("N/A");
            }
            
            // Set project name
            if (expense.getProject() != null) {
                tvProjectName.setText(expense.getProject().getName());
            } else {
                tvProjectName.setText("N/A");
            }
            
            // Set status color
            setStatusColor(expense.getStatus());
        }
        
        private void setStatusColor(String status) {
            int colorRes;
            switch (status) {
                case "approved":
                    colorRes = R.color.status_approved;
                    break;
                case "pending":
                    colorRes = R.color.status_pending;
                    break;
                case "rejected":
                    colorRes = R.color.status_rejected;
                    break;
                case "paid":
                    colorRes = R.color.status_paid;
                    break;
                default:
                    colorRes = R.color.status_default;
                    break;
            }
            tvStatus.setTextColor(itemView.getContext().getResources().getColor(colorRes));
        }
    }
    
    /**
     * Expense Click Listener Interface
     */
    public interface ExpenseClickListener {
        void onExpenseClick(ProjectExpense expense);
        void onExpenseEdit(ProjectExpense expense);
        void onExpenseDelete(ProjectExpense expense);
    }
}
