package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.util.List;

/**
 * Quotes Adapter - Adapter cho danh sách báo giá
 * Hiển thị thông tin báo giá với các thao tác CRUD và approval workflow
 */
public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {

    private List<Quote> quotes;
    private QuoteClickListener clickListener;

    public interface QuoteClickListener {
        void onQuoteClick(Quote quote);
        void onQuoteEdit(Quote quote);
        void onQuoteDelete(Quote quote);
        void onQuoteApprove(Quote quote);
        void onQuoteConvertToInvoice(Quote quote);
        void onQuoteSendToCustomer(Quote quote);
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
        holder.bind(quote, clickListener);
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

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuoteNumber = itemView.findViewById(R.id.tv_quote_number);
            // tvQuoteTitle removed - no longer needed
            tvQuoteStatus = itemView.findViewById(R.id.tv_quote_status);
            tvQuoteTotal = itemView.findViewById(R.id.tv_quote_total);
            tvQuoteCustomer = itemView.findViewById(R.id.tv_quote_customer);
            tvQuoteProject = itemView.findViewById(R.id.tv_quote_project);
            tvQuoteValidUntil = itemView.findViewById(R.id.tv_quote_valid_until);
        }

        public void bind(Quote quote, QuoteClickListener clickListener) {
            // Basic information
            tvQuoteNumber.setText(quote.getQuoteNumber());
            // tvQuoteTitle removed - no longer needed
            tvQuoteStatus.setText(getQuoteStatusText(quote.getStatus()));
            
            // Financial information
            if (quote.getTotal() != null) {
                tvQuoteTotal.setText(CurrencyFormatter.format(quote.getTotal()));
            } else {
                tvQuoteTotal.setText("Chưa có tổng tiền");
            }
            
            // Related information
            if (quote.getCustomer() != null) {
                tvQuoteCustomer.setText(quote.getCustomer().getName());
            } else {
                tvQuoteCustomer.setText("Chưa có khách hàng");
            }
            
            if (quote.getProject() != null) {
                tvQuoteProject.setText(quote.getProject().getName());
            } else {
                tvQuoteProject.setText("Chưa có dự án");
            }
            
            // Valid until
            if (quote.getValidUntil() != null) {
                tvQuoteValidUntil.setText("Có hiệu lực đến: " + quote.getValidUntil().toString());
            } else {
                tvQuoteValidUntil.setText("Chưa thiết lập thời hạn");
            }
            
            // Click listeners
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onQuoteClick(quote);
                }
            });
        }

        private String getQuoteStatusText(String status) {
            if (status == null) return "Draft";
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
    }
}

