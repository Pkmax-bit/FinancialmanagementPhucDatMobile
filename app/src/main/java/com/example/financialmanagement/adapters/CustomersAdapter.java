package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Customer;
import java.util.List;

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
        return new CustomerViewHolder(view, clickListener, customers);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer);
    }

    @Override
    public int getItemCount() {
        return customers != null ? customers.size() : 0;
    }

    public void updateCustomers(List<Customer> newCustomers) {
        this.customers = newCustomers;
        notifyDataSetChanged();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar;
        TextView tvCustomerName;
        ImageButton btnMenu;
        List<Customer> customers;
        CustomerClickListener listener;

        CustomerViewHolder(@NonNull View itemView, CustomerClickListener listener, List<Customer> customers) {
            super(itemView);
            this.customers = customers;
            this.listener = listener;
            
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            btnMenu = itemView.findViewById(R.id.btn_menu);

            // Click on item to view details
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && customers != null) {
                        listener.onCustomerClick(customers.get(position));
                    }
                }
            });
        }

        void bind(Customer customer) {
            // Avatar - first letter of name
            String initial = customer.getName() != null && !customer.getName().isEmpty() 
                ? customer.getName().substring(0, 1).toUpperCase() 
                : "?";
            tvAvatar.setText(initial);
            
            // Name
            tvCustomerName.setText(customer.getName() != null ? customer.getName() : "");
            
            // 3-dot menu button
            btnMenu.setOnClickListener(v -> showPopupMenu(v, customer));
        }
        
        private void showPopupMenu(View anchor, Customer customer) {
            PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
            popup.inflate(R.menu.menu_customer_item);
            
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_view) {
                    if (listener != null) listener.onCustomerClick(customer);
                    return true;
                } else if (id == R.id.action_edit) {
                    if (listener != null) listener.onCustomerEdit(customer);
                    return true;
                } else if (id == R.id.action_delete) {
                    if (listener != null) listener.onCustomerDelete(customer);
                    return true;
                }
                return false;
            });
            
            popup.show();
        }
    }
}
