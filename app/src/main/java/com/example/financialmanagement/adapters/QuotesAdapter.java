package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Quotes Adapter - Adapter cho danh sách báo giá
 * Hiển thị thông tin báo giá với các thao tác CRUD và approval workflow
 */
public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {

    private List<Quote> quotes;
    private QuoteClickListener clickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface QuoteClickListener {
        void onQuoteClick(Quote quote);
        void onQuoteEdit(Quote quote);
        void onQuoteDelete(Quote quote);
        void onQuoteApprove(Quote quote);
        void onQuoteConvertToInvoice(Quote quote);
        void onQuoteSendToCustomer(Quote quote);
        void onQuoteViewDetails(Quote quote);
        void onQuoteReview(Quote quote);
        void onQuoteExportPDF(Quote quote);
    }

    public QuotesAdapter(List<Quote> quotes, QuoteClickListener clickListener) {
        this.quotes = quotes;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quotes.get(position);
        holder.bind(quote, clickListener, dateFormat);
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public void updateQuotes(List<Quote> newQuotes) {
        this.quotes = newQuotes;
        notifyDataSetChanged();
    }

    static class QuoteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQuoteNumber;
        private TextView tvQuoteTitle;
        private TextView tvQuoteStatus;
        private TextView tvQuoteTotal;
        private TextView tvQuoteCustomer;
        private TextView tvQuoteProject;
        private TextView tvQuoteValidUntil;
        private ImageButton btnQuoteMenu;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuoteNumber = itemView.findViewById(R.id.tv_quote_number);
            // tvQuoteTitle removed - no longer needed
            tvQuoteStatus = itemView.findViewById(R.id.tv_quote_status);
            tvQuoteTotal = itemView.findViewById(R.id.tv_quote_total);
            tvQuoteCustomer = itemView.findViewById(R.id.tv_quote_customer);
            tvQuoteProject = itemView.findViewById(R.id.tv_quote_project);
            tvQuoteValidUntil = itemView.findViewById(R.id.tv_quote_valid_until);
            btnQuoteMenu = itemView.findViewById(R.id.btn_quote_menu);
        }

        public void bind(Quote quote, QuoteClickListener clickListener, SimpleDateFormat dateFormat) {
            // Basic information
            tvQuoteNumber.setText(quote.getQuoteNumber() != null ? quote.getQuoteNumber() : "N/A");
            
            // Status with background
            String status = quote.getStatus();
            tvQuoteStatus.setText(getQuoteStatusText(status));
            tvQuoteStatus.setBackgroundResource(getStatusBackground(status));
            
            // Financial information
            if (quote.getTotalAmount() != null) {
                tvQuoteTotal.setText(CurrencyFormatter.format(quote.getTotalAmount()));
            } else {
                tvQuoteTotal.setText("0 ₫");
            }
            
            // Customer name
            if (quote.getCustomer() != null && quote.getCustomer().getName() != null) {
                tvQuoteCustomer.setText(quote.getCustomer().getName());
            } else {
                tvQuoteCustomer.setText("Chưa có khách hàng");
            }
            
            // Project name - display prominently
            if (tvQuoteProject != null) {
                if (quote.getProject() != null && quote.getProject().getName() != null) {
                    tvQuoteProject.setText(quote.getProject().getName());
                } else {
                    tvQuoteProject.setText("Chưa có dự án");
                }
            }
            
            // Valid until - format date properly
            if (tvQuoteValidUntil != null) {
                java.util.Date validUntil = quote.getValidUntil();
                if (validUntil == null) {
                    validUntil = quote.getExpiryDate();
                }
                if (validUntil != null && dateFormat != null) {
                    tvQuoteValidUntil.setText(dateFormat.format(validUntil));
                } else {
                    tvQuoteValidUntil.setText("N/A");
                }
            }
            
            // Click listeners
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onQuoteClick(quote);
                }
            });

            // Setup menu button
            if (btnQuoteMenu != null) {
                btnQuoteMenu.setOnClickListener(v -> {
                    showPopupMenu(v, quote, clickListener);
                });
            }
        }

        private void showPopupMenu(View view, Quote quote, QuoteClickListener clickListener) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_quote_item, popupMenu.getMenu());

            // Set menu item click listener
            popupMenu.setOnMenuItemClickListener(item -> {
                if (clickListener == null) return false;

                int itemId = item.getItemId();
                if (itemId == R.id.action_view_details) {
                    clickListener.onQuoteViewDetails(quote);
                    return true;
                } else if (itemId == R.id.action_edit) {
                    clickListener.onQuoteEdit(quote);
                    return true;
                } else if (itemId == R.id.action_review) {
                    clickListener.onQuoteReview(quote);
                    return true;
                } else if (itemId == R.id.action_export_pdf) {
                    clickListener.onQuoteExportPDF(quote);
                    return true;
                } else if (itemId == R.id.action_approve_to_invoice) {
                    clickListener.onQuoteConvertToInvoice(quote);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    clickListener.onQuoteDelete(quote);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        }

        private String getQuoteStatusText(String status) {
            if (status == null) return "Nháp";
            switch (status.toLowerCase()) {
                case "draft":
                    return "Nháp";
                case "sent":
                    return "Đã gửi";
                case "approved":
                    return "Đã duyệt";
                case "rejected":
                    return "Từ chối";
                case "converted":
                    return "Đã chuyển đổi";
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
                case "approved":
                    return R.drawable.bg_status_success;
                case "rejected":
                    return R.drawable.bg_status_error;
                case "converted":
                    return R.drawable.bg_status_active;
                default:
                    return R.drawable.bg_status_draft;
            }
        }
    }
}

