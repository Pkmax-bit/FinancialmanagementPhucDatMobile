package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.util.List;

/**
 * Invoices Adapter - Adapter cho danh sách hóa đơn
 * Hiển thị thông tin hóa đơn với các thao tác CRUD và payment tracking
 */
public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.InvoiceViewHolder> {

    private List<Invoice> invoices;
    private InvoiceClickListener clickListener;

    public interface InvoiceClickListener {
        void onInvoiceClick(Invoice invoice);
        void onInvoiceEdit(Invoice invoice);
        void onInvoiceDelete(Invoice invoice);
        void onInvoiceMarkAsPaid(Invoice invoice);
        void onInvoiceSendToCustomer(Invoice invoice);
        void onInvoiceAddPayment(Invoice invoice);
    }

    public InvoicesAdapter(List<Invoice> invoices, InvoiceClickListener clickListener) {
        this.invoices = invoices;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice invoice = invoices.get(position);
        holder.bind(invoice, clickListener);
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public void updateInvoices(List<Invoice> newInvoices) {
        this.invoices = newInvoices;
        notifyDataSetChanged();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInvoiceNumber;
        private TextView tvInvoiceTitle;
        private TextView tvInvoiceStatus;
        private TextView tvInvoiceTotal;
        private TextView tvInvoiceBalance;
        private TextView tvInvoiceCustomer;
        private TextView tvInvoiceProject;
        private TextView tvInvoiceDueDate;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceNumber = itemView.findViewById(R.id.tv_invoice_number);
            tvInvoiceTitle = itemView.findViewById(R.id.tv_invoice_title);
            tvInvoiceStatus = itemView.findViewById(R.id.tv_invoice_status);
            tvInvoiceTotal = itemView.findViewById(R.id.tv_invoice_total);
            tvInvoiceBalance = itemView.findViewById(R.id.tv_invoice_balance);
            tvInvoiceCustomer = itemView.findViewById(R.id.tv_invoice_customer);
            tvInvoiceProject = itemView.findViewById(R.id.tv_invoice_project);
            tvInvoiceDueDate = itemView.findViewById(R.id.tv_invoice_due_date);
        }

        public void bind(Invoice invoice, InvoiceClickListener clickListener) {
            // Basic information
            tvInvoiceNumber.setText(invoice.getInvoiceNumber());
            tvInvoiceTitle.setText(invoice.getTitle());
            tvInvoiceStatus.setText(getInvoiceStatusText(invoice.getStatus()));
            
            // Financial information
            if (invoice.getTotal() != null) {
                tvInvoiceTotal.setText(CurrencyFormatter.format(invoice.getTotal()));
            } else {
                tvInvoiceTotal.setText("Chưa có tổng tiền");
            }
            
            if (invoice.getBalance() != null) {
                tvInvoiceBalance.setText("Còn lại: " + CurrencyFormatter.format(invoice.getBalance()));
            } else {
                tvInvoiceBalance.setText("Chưa có số dư");
            }
            
            // Related information
            if (invoice.getCustomer() != null) {
                tvInvoiceCustomer.setText(invoice.getCustomer().getName());
            } else {
                tvInvoiceCustomer.setText("Chưa có khách hàng");
            }
            
            if (invoice.getProject() != null) {
                tvInvoiceProject.setText(invoice.getProject().getName());
            } else {
                tvInvoiceProject.setText("Chưa có dự án");
            }
            
            // Due date
            if (invoice.getDueDate() != null) {
                tvInvoiceDueDate.setText("Hạn thanh toán: " + invoice.getDueDate().toString());
            } else {
                tvInvoiceDueDate.setText("Chưa thiết lập hạn thanh toán");
            }
            
            // Click listeners
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onInvoiceClick(invoice);
                }
            });
        }

        private String getInvoiceStatusText(String status) {
            if (status == null) return "Draft";
            switch (status.toLowerCase()) {
                case "draft":
                    return "Nháp";
                case "sent":
                    return "Đã gửi";
                case "paid":
                    return "Đã thanh toán";
                case "overdue":
                    return "Quá hạn";
                case "cancelled":
                    return "Đã hủy";
                default:
                    return status;
            }
        }
    }
}

