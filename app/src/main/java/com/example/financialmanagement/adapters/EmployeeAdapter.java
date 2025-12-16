package com.example.financialmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.activities.EmployeeDetailActivity;
import com.example.financialmanagement.models.Employee;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    public interface OnEmployeeClickListener {
        void onEmployeeClick(Employee employee);
    }

    private Context context;
    private List<Employee> employees;
    private OnEmployeeClickListener listener;

    public EmployeeAdapter(Context context, List<Employee> employees) {
        this.context = context;
        this.employees = employees;
    }

    public void setOnEmployeeClickListener(OnEmployeeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.tvName.setText(employee.getFullName());
        holder.tvEmail.setText(employee.getEmail());
        holder.tvPosition.setText(employee.getPosition() != null ? employee.getPosition().getTitle() : "N/A");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmployeeClick(employee);
            } else {
                if (employee != null && employee.getId() != null) {
                    // Try to get Activity from view context
                    android.app.Activity activity = null;
                    Context viewContext = v.getContext();
                    
                    // Check if view context is Activity
                    if (viewContext instanceof android.app.Activity) {
                        activity = (android.app.Activity) viewContext;
                    } 
                    // Check if stored context is Activity
                    else if (context instanceof android.app.Activity) {
                        activity = (android.app.Activity) context;
                    }
                    // Try to get Activity from FragmentActivity
                    else if (viewContext instanceof androidx.fragment.app.FragmentActivity) {
                        activity = (androidx.fragment.app.FragmentActivity) viewContext;
                    }
                    
                    if (activity != null) {
                        Intent intent = new Intent(activity, EmployeeDetailActivity.class);
                        intent.putExtra("employee_id", employee.getId());
                        activity.startActivity(intent);
                    } else {
                        // Last resort: use context with FLAG_ACTIVITY_NEW_TASK
                        Intent intent = new Intent(context, EmployeeDetailActivity.class);
                        intent.putExtra("employee_id", employee.getId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            android.util.Log.e("EmployeeAdapter", "Error starting EmployeeDetailActivity: " + e.getMessage(), e);
                            android.widget.Toast.makeText(context, "Không thể mở chi tiết nhân viên", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    android.widget.Toast.makeText(context, "Thông tin nhân viên không hợp lệ", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return employees != null ? employees.size() : 0;
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPosition;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_employee_name);
            tvEmail = itemView.findViewById(R.id.tv_employee_email);
            tvPosition = itemView.findViewById(R.id.tv_employee_position);
        }
    }
}
