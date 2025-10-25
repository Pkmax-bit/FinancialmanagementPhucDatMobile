package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.util.List;

/**
 * Customers Adapter - Adapter cho danh sách khách hàng
 * Hiển thị thông tin khách hàng với các thao tác CRUD
 */
public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder> {

    private List<Customer> customers;
    private CustomerClickListener clickListener;

    public interface CustomerClickListener {
        void onCustomerClick(Customer customer);
        void onCustomerEdit(Customer customer);
        void onCustomerDelete(Customer customer);
        void onCustomerCreateProject(Customer customer);
    }

    public CustomersAdapter(List<Customer> customers, CustomerClickListener clickListener) {
        this.customers = customers;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer, clickListener);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void updateCustomers(List<Customer> newCustomers) {
        this.customers = newCustomers;
        notifyDataSetChanged();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName;
        private TextView tvCustomerCode;
        private TextView tvCustomerType;
        private TextView tvCustomerEmail;
        private TextView tvCustomerPhone;
        private TextView tvCustomerCredit;
        private TextView tvCustomerProjects;
        private TextView tvCustomerStatus;
        
        // Action buttons
        private com.google.android.material.button.MaterialButton btnCreateProject;
        private com.google.android.material.button.MaterialButton btnEditCustomer;
        private com.google.android.material.button.MaterialButton btnDeleteCustomer;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvCustomerCode = itemView.findViewById(R.id.tv_customer_code);
            tvCustomerType = itemView.findViewById(R.id.tv_customer_type);
            tvCustomerEmail = itemView.findViewById(R.id.tv_customer_email);
            tvCustomerPhone = itemView.findViewById(R.id.tv_customer_phone);
            tvCustomerCredit = itemView.findViewById(R.id.tv_customer_credit);
            tvCustomerProjects = itemView.findViewById(R.id.tv_customer_projects);
            tvCustomerStatus = itemView.findViewById(R.id.tv_customer_status);
            
            // Initialize action buttons
            btnCreateProject = itemView.findViewById(R.id.btn_create_project);
            btnEditCustomer = itemView.findViewById(R.id.btn_edit_customer);
            btnDeleteCustomer = itemView.findViewById(R.id.btn_delete_customer);
        }

        public void bind(Customer customer, CustomerClickListener clickListener) {
            // Basic information
            tvCustomerName.setText(customer.getName());
            tvCustomerCode.setText(customer.getCustomerCode());
            tvCustomerType.setText(getCustomerTypeText(customer.getCustomerType()));
            tvCustomerEmail.setText(customer.getEmail() != null ? customer.getEmail() : "Chưa có email");
            tvCustomerPhone.setText(customer.getPhone() != null ? customer.getPhone() : "Chưa có SĐT");
            
            // Financial information
            if (customer.getCreditLimit() != null) {
                tvCustomerCredit.setText("Hạn mức: " + CurrencyFormatter.format(customer.getCreditLimit()));
            } else {
                tvCustomerCredit.setText("Chưa thiết lập hạn mức");
            }
            
            // Project count
            if (customer.getProjectCount() != null) {
                tvCustomerProjects.setText(customer.getProjectCount() + " dự án");
            } else {
                tvCustomerProjects.setText("0 dự án");
            }
            
            // Status
            tvCustomerStatus.setText(getCustomerStatusText(customer.getStatus()));
            
            // Click listeners
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onCustomerClick(customer);
                }
            });
            
            // Action button listeners
            btnCreateProject.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onCustomerCreateProject(customer);
                }
            });
            
            btnEditCustomer.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onCustomerEdit(customer);
                }
            });
            
            btnDeleteCustomer.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onCustomerDelete(customer);
                }
            });
        }

        private String getCustomerTypeText(String type) {
            if (type == null) return "Chưa xác định";
            switch (type.toLowerCase()) {
                case "individual":
                    return "Cá nhân";
                case "company":
                    return "Công ty";
                case "government":
                    return "Chính phủ";
                default:
                    return type;
            }
        }

        private String getCustomerStatusText(String status) {
            if (status == null) return "Hoạt động";
            switch (status.toLowerCase()) {
                case "active":
                    return "Hoạt động";
                case "inactive":
                    return "Không hoạt động";
                case "suspended":
                    return "Tạm dừng";
                default:
                    return status;
            }
        }
    }
}
