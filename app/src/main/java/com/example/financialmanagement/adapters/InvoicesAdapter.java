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
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Invoices Adapter - Adapter cho danh sách hóa đơn
 * Hiển thị thông tin hóa đơn với các thao tác CRUD và payment tracking
 */
public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.InvoiceViewHolder> {

    private List<Invoice> invoices;
    private InvoiceClickListener clickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

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
        holder.bind(invoice, clickListener, dateFormat);
    }

    @Override
    public int getItemCount() {
        return invoices != null ? invoices.size() : 0;
    }

    public void updateInvoices(List<Invoice> newInvoices) {
        this.invoices = newInvoices;
        notifyDataSetChanged();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInvoiceNumber;
        private TextView tvInvoiceProject;
        private TextView tvInvoiceStatus;
        private TextView tvInvoiceTotal;
        private TextView tvInvoiceBalance;
        private TextView tvInvoiceCustomer;
        private TextView tvInvoiceDueDate;
        private ImageButton btnInvoiceMenu;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceNumber = itemView.findViewById(R.id.tv_invoice_number);
            tvInvoiceProject = itemView.findViewById(R.id.tv_invoice_project);
            tvInvoiceStatus = itemView.findViewById(R.id.tv_invoice_status);
            tvInvoiceTotal = itemView.findViewById(R.id.tv_invoice_total);
            tvInvoiceBalance = itemView.findViewById(R.id.tv_invoice_balance);
            tvInvoiceCustomer = itemView.findViewById(R.id.tv_invoice_customer);
            tvInvoiceDueDate = itemView.findViewById(R.id.tv_invoice_due_date);
            btnInvoiceMenu = itemView.findViewById(R.id.btn_invoice_menu);
        }

        public void bind(Invoice invoice, InvoiceClickListener clickListener, SimpleDateFormat dateFormat) {
            // Invoice Number
            if (tvInvoiceNumber != null) {
                tvInvoiceNumber.setText(invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "N/A");
            }
            
            // Project Name
            if (tvInvoiceProject != null) {
                if (invoice.getProject() != null && invoice.getProject().getName() != null) {
                    tvInvoiceProject.setText(invoice.getProject().getName());
                } else {
                    tvInvoiceProject.setText("Chưa có dự án");
                }
            }
            
            // Status
            if (tvInvoiceStatus != null) {
                tvInvoiceStatus.setText(getInvoiceStatusText(invoice.getStatus()));
                tvInvoiceStatus.setBackgroundResource(getStatusBackground(invoice.getStatus()));
            }
            
            // Total Amount
            if (tvInvoiceTotal != null) {
                if (invoice.getTotalAmount() != null) {
                    tvInvoiceTotal.setText(CurrencyFormatter.format(invoice.getTotalAmount()));
                } else {
                    tvInvoiceTotal.setText("0 ₫");
                }
            }
            
            // Balance (hidden but kept for compatibility)
            if (tvInvoiceBalance != null) {
                if (invoice.getBalance() != null) {
                    tvInvoiceBalance.setText(CurrencyFormatter.format(invoice.getBalance()));
                } else {
                    tvInvoiceBalance.setText("0 ₫");
                }
            }
            
            // Customer Name
            if (tvInvoiceCustomer != null) {
                if (invoice.getCustomer() != null && invoice.getCustomer().getName() != null) {
                    tvInvoiceCustomer.setText(invoice.getCustomer().getName());
                } else {
                    tvInvoiceCustomer.setText("Chưa có khách hàng");
                }
            }
            
            // Due Date
            if (tvInvoiceDueDate != null) {
                if (invoice.getDueDate() != null) {
                    tvInvoiceDueDate.setText(dateFormat.format(invoice.getDueDate()));
                } else {
                    tvInvoiceDueDate.setText("N/A");
                }
            }
            
            // Click listener for the whole item
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onInvoiceClick(invoice);
                }
            });
            
            // Menu button click listener
            if (btnInvoiceMenu != null) {
                btnInvoiceMenu.setOnClickListener(v -> {
                    showPopupMenu(v, invoice, clickListener);
                });
            }
        }

        private void showPopupMenu(View anchor, Invoice invoice, InvoiceClickListener clickListener) {
            if (clickListener == null) return;
            
            PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
            popup.getMenuInflater().inflate(R.menu.menu_invoice_item, popup.getMenu());
            
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    clickListener.onInvoiceEdit(invoice);
                    return true;
                } else if (id == R.id.action_delete) {
                    clickListener.onInvoiceDelete(invoice);
                    return true;
                } else if (id == R.id.action_mark_paid) {
                    clickListener.onInvoiceMarkAsPaid(invoice);
                    return true;
                } else if (id == R.id.action_send) {
                    clickListener.onInvoiceSendToCustomer(invoice);
                    return true;
                } else if (id == R.id.action_add_payment) {
                    clickListener.onInvoiceAddPayment(invoice);
                    return true;
                }
                return false;
            });
            
            popup.show();
        }

        private String getInvoiceStatusText(String status) {
            if (status == null) return "Nháp";
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
                case "partial":
                    return "Thanh toán một phần";
                default:
                    return status;
            }
        }

        private int getStatusBackground(String status) {
            if (status == null) return R.drawable.bg_status_draft;
            switch (status.toLowerCase()) {
                case "draft":
                    return R.drawable.bg_status_draft;
                case "sent":
                    return R.drawable.bg_status_active;
                case "paid":
                    return R.drawable.bg_status_success;
                case "overdue":
                case "cancelled":
                    return R.drawable.bg_status_error;
                case "partial":
                    return R.drawable.bg_status_pending;
                default:
                    return R.drawable.bg_status_draft;
            }
        }
    }
}
