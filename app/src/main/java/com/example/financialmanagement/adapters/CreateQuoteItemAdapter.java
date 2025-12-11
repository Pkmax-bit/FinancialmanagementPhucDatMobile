package com.example.financialmanagement.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.utils.CurrencyFormatter;

import java.util.List;

public class CreateQuoteItemAdapter extends RecyclerView.Adapter<CreateQuoteItemAdapter.ItemViewHolder> {

    private List<Quote.QuoteItem> items;
    private OnItemChangeListener listener;

    public interface OnItemChangeListener {
        void onItemRemove(int position);
        void onItemQuantityChanged(int position, double newQuantity);
        void onItemUpdated(int position); // New callback
    }

    public CreateQuoteItemAdapter(List<Quote.QuoteItem> items, OnItemChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_create_quote_product, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Prevent TextWatcher from triggering infinite loop on rebind
        holder.textWatcher.updatePosition(position);
        
        Quote.QuoteItem item = items.get(position);
        holder.bind(item, listener, position);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTotalPrice, tvUnit;
        EditText etQuantity, etLength, etWidth, etHeight, etArea, etVolume, etUnitPrice;
        ImageButton btnRemove;
        MyTextWatcher textWatcher;

        ItemViewHolder(View itemView) {
            super(itemView);
            this.textWatcher = new MyTextWatcher(this);
            tvName = itemView.findViewById(R.id.tv_product_name);
            etUnitPrice = itemView.findViewById(R.id.et_unit_price);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            etQuantity = itemView.findViewById(R.id.et_quantity);
            etLength = itemView.findViewById(R.id.et_length);
            etWidth = itemView.findViewById(R.id.et_width);
            etHeight = itemView.findViewById(R.id.et_height);
            etArea = itemView.findViewById(R.id.et_area);
            etVolume = itemView.findViewById(R.id.et_volume);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            
            etQuantity.addTextChangedListener(watcherFor(etQuantity));
            etUnitPrice.addTextChangedListener(watcherFor(etUnitPrice));
            etLength.addTextChangedListener(watcherFor(etLength));
            etWidth.addTextChangedListener(watcherFor(etWidth));
            etHeight.addTextChangedListener(watcherFor(etHeight));
            etArea.addTextChangedListener(watcherFor(etArea));
            etVolume.addTextChangedListener(watcherFor(etVolume));
        }

        private TextWatcher watcherFor(EditText editText) {
            return new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (editText.hasFocus()) {
                        textWatcher.onTextChanged(editText, s.toString());
                    }
                }
            };
        }

        void bind(Quote.QuoteItem item, OnItemChangeListener listener, int position) {
            try {
                tvName.setText(item.getNameProduct());
                
                if (!etUnitPrice.hasFocus()) {
                    etUnitPrice.setText(item.getUnitPrice() != null ? String.format("%.0f", item.getUnitPrice()) : "");
                }
                
                if (item.getTotalPrice() != null) {
                    tvTotalPrice.setText(CurrencyFormatter.format(item.getTotalPrice()));
                } else {
                    tvTotalPrice.setText("0");
                }
                
                if (item.getUnit() != null) {
                    tvUnit.setText("Unit: " + item.getUnit());
                } else {
                    tvUnit.setVisibility(View.GONE);
                }

                // Important: Remove listener before setting text to avoid loop, though here we use custom watcher logic
                // But since we are binding, we just set the value.
                // Check if focus is on this edit text to avoid rewriting while user types
                if (!etQuantity.hasFocus()) {
                    etQuantity.setText(String.valueOf(item.getQuantity()));
                }
                if (!etLength.hasFocus()) {
                    etLength.setText(item.getLength() != null ? String.valueOf(item.getLength()) : "");
                }
                if (!etWidth.hasFocus()) {
                    etWidth.setText(item.getDepth() != null ? String.valueOf(item.getDepth()) : "");
                }
                if (!etHeight.hasFocus()) {
                    etHeight.setText(item.getHeight() != null ? String.valueOf(item.getHeight()) : "");
                }
                if (!etArea.hasFocus()) {
                    etArea.setText(item.getArea() != null ? String.format("%.2f", item.getArea()) : "");
                }
                if (!etVolume.hasFocus()) {
                    etVolume.setText(item.getVolume() != null ? String.format("%.3f", item.getVolume()) : "");
                }
            } catch (Exception e) {
                // If binding fails, just hide the item or log it, don't crash app
                e.printStackTrace();
            }

            btnRemove.setOnClickListener(v -> {
                if (listener != null) listener.onItemRemove(getAdapterPosition());
            });

            btnRemove.setOnClickListener(v -> {
                if (listener != null) listener.onItemRemove(getAdapterPosition());
            });
        }
    }

    // Custom TextWatcher to handle position
    class MyTextWatcher implements TextWatcher {
        private int position;
        private ItemViewHolder holder; // Reference to holder

        public MyTextWatcher(ItemViewHolder holder) {
            this.holder = holder;
        }

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {}

        // Custom method called by wrapper
        public void onTextChanged(EditText view, String value) {
            if (position == RecyclerView.NO_POSITION || items == null || position >= items.size()) return;
            
            Quote.QuoteItem item = items.get(position);
            double val = 0;
            try {
                if (!value.isEmpty()) val = Double.parseDouble(value);
            } catch (NumberFormatException e) { val = 0; }

            int id = view.getId();
            if (id == R.id.et_quantity) {
                item.setQuantity(val);
                calculate(item); 
            } else if (id == R.id.et_unit_price) {
                item.setUnitPrice(val);
                updateTotalPrice(item);
            } else if (id == R.id.et_length) {
                item.setLength(val);
                calculate(item);
            } else if (id == R.id.et_width) {
                item.setDepth(val);
                calculate(item);
            } else if (id == R.id.et_height) {
                item.setHeight(val);
                calculate(item);
            } else if (id == R.id.et_area) {
                item.setArea(val);
                updateTotalPrice(item);
            } else if (id == R.id.et_volume) {
                item.setVolume(val);
            }
            
            if (listener != null) listener.onItemUpdated(position);
        }
        
        private void calculate(Quote.QuoteItem item) {
            double L = item.getLength() != null ? item.getLength() : 0;
            double W = item.getDepth() != null ? item.getDepth() : 0;
            double H = item.getHeight() != null ? item.getHeight() : 0;
            
            if (L > 0 && W > 0) {
                double area = (L * W / 1000000.0);
                item.setArea(area);
                // Update UI if not focused
                if (holder != null && !holder.etArea.hasFocus()) {
                   holder.etArea.setText(String.format("%.2f", area));
                }
            }

            if (L > 0 && W > 0 && H > 0) {
                double vol = (L * W * H / 1000000000.0);
                item.setVolume(vol);
                if (holder != null && !holder.etVolume.hasFocus()) {
                   holder.etVolume.setText(String.format("%.3f", vol));
                }
            }
            
            updateTotalPrice(item);
        }
        
        private void updateTotalPrice(Quote.QuoteItem item) {
            double price = item.getUnitPrice() != null ? item.getUnitPrice() : 0;
            double qty = item.getQuantity() != null ? item.getQuantity() : 0;
            double area = item.getArea() != null ? item.getArea() : 0;
            double total = 0;
            
            if (area > 0) {
                total = qty * area * price;
            } else {
                total = qty * price;
            }
            item.setTotalPrice(total);
            
            // Should we update tvTotalPrice?
             if (holder != null) {
                holder.tvTotalPrice.setText(CurrencyFormatter.format(total));
             }
        }
    }
}
