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

import java.text.DecimalFormat;
import java.util.List;

/**
 * Adapter for displaying quote items in QuoteDetailActivity
 */
public class QuoteItemDetailAdapter extends RecyclerView.Adapter<QuoteItemDetailAdapter.QuoteItemViewHolder> {

    private List<Quote.QuoteItem> items;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    public QuoteItemDetailAdapter(List<Quote.QuoteItem> items) {
        this.items = items != null ? items : new java.util.ArrayList<>();
    }

    @NonNull
    @Override
    public QuoteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote_detail_product, parent, false);
        return new QuoteItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteItemViewHolder holder, int position) {
        Quote.QuoteItem item = items.get(position);
        holder.bind(item, position + 1);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(List<Quote.QuoteItem> newItems) {
        this.items = newItems != null ? newItems : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    static class QuoteItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemNumber;
        private TextView tvProductName;
        private TextView tvDescription;
        private TextView tvQuantity;
        private TextView tvUnitPrice;
        private TextView tvTotalPrice;
        private TextView tvUnit;
        private View llDimensions;
        private TextView tvDimensions;
        private TextView tvArea;
        private TextView tvVolume;

        public QuoteItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemNumber = itemView.findViewById(R.id.tv_item_number);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            llDimensions = itemView.findViewById(R.id.ll_dimensions);
            tvDimensions = itemView.findViewById(R.id.tv_dimensions);
            tvArea = itemView.findViewById(R.id.tv_area);
            tvVolume = itemView.findViewById(R.id.tv_volume);
        }

        public void bind(Quote.QuoteItem item, int position) {
            if (item == null) return;

            // Item number
            if (tvItemNumber != null) {
                tvItemNumber.setText(String.valueOf(position));
            }

            // Product name
            String productName = item.getNameProduct();
            if (tvProductName != null) {
                tvProductName.setText(productName != null && !productName.isEmpty() ? productName : "Sản phẩm");
            }

            // Description
            if (tvDescription != null) {
                String description = item.getProductDescription();
                if (description == null || description.isEmpty()) {
                    description = item.getDescription();
                }
                if (description != null && !description.isEmpty() && 
                    !description.equals(productName)) {
                    tvDescription.setText(description);
                    tvDescription.setVisibility(View.VISIBLE);
                } else {
                    tvDescription.setVisibility(View.GONE);
                }
            }
            
            // Category info (optional)
            if (item.getCategoryName() != null && !item.getCategoryName().isEmpty()) {
                String currentDesc = tvDescription.getText().toString();
                if (tvDescription.getVisibility() == View.VISIBLE) {
                    tvDescription.setText(currentDesc + " (" + item.getCategoryName() + ")");
                } else {
                    tvDescription.setText("Loại: " + item.getCategoryName());
                    tvDescription.setVisibility(View.VISIBLE);
                }
            }

            // Quantity
            if (tvQuantity != null) {
                Double quantity = item.getQuantity();
                if (quantity != null) {
                    tvQuantity.setText(formatNumber(quantity));
                } else {
                    tvQuantity.setText("0");
                }
            }

            // Unit
            if (tvUnit != null) {
                String unit = item.getProductUnit();
                if (unit != null && !unit.isEmpty()) {
                    tvUnit.setText(unit);
                    tvUnit.setVisibility(View.VISIBLE);
                } else {
                    tvUnit.setVisibility(View.GONE);
                }
            }

            // Unit price
            if (tvUnitPrice != null) {
                Double unitPrice = item.getUnitPrice();
                if (unitPrice != null) {
                    tvUnitPrice.setText(CurrencyFormatter.format(unitPrice));
                } else {
                    tvUnitPrice.setText("0 ₫");
                }
            }

            // Total price
            if (tvTotalPrice != null) {
                Double totalPrice = item.getTotalPrice();
                if (totalPrice == null) {
                    Double qty = item.getQuantity();
                    Double price = item.getUnitPrice();
                    if (qty != null && price != null) {
                        totalPrice = qty * price;
                    }
                }
                if (totalPrice != null) {
                    tvTotalPrice.setText(CurrencyFormatter.format(totalPrice));
                } else {
                    tvTotalPrice.setText("0 ₫");
                }
            }

            // Dimensions (height x length x depth) logic
            boolean hasDimensionsInfo = false;
            
            // Dimensions
            if (tvDimensions != null) {
                Double height = item.getHeight();
                Double length = item.getLength(); // Treat as Width/Ngang
                Double depth = item.getDepth();
                
                if (height != null || length != null || depth != null) {
                    StringBuilder dims = new StringBuilder();
                    
                    // Convert to mm (assuming stored as meters) -> NOW assuming stored as mm based on user feedback
                    // Length = Ngang (Width)
                    if (length != null && length > 0) {
                        dims.append("Ngang: ").append(formatNumber(length)).append(" mm");
                    }
                    
                    // Height = Cao
                    if (height != null && height > 0) {
                        if (dims.length() > 0) dims.append(" x ");
                        dims.append("Cao: ").append(formatNumber(height)).append(" mm");
                    }
                    
                    // Depth = Sâu/Dày
                    if (depth != null && depth > 0) {
                        if (dims.length() > 0) dims.append(" x ");
                        dims.append("Sâu: ").append(formatNumber(depth)).append(" mm");
                    }
                    
                    if (dims.length() > 0) {
                        tvDimensions.setText(dims.toString());
                        tvDimensions.setVisibility(View.VISIBLE);
                        hasDimensionsInfo = true;
                    } else {
                        tvDimensions.setVisibility(View.GONE);
                    }
                } else {
                    tvDimensions.setVisibility(View.GONE);
                }
            }

            // Area
            if (tvArea != null) {
                Double area = item.getArea();
                if (area != null && area > 0) {
                    tvArea.setText("Diện tích: " + formatNumber(area) + " m²");
                    tvArea.setVisibility(View.VISIBLE);
                    hasDimensionsInfo = true;
                } else {
                    tvArea.setVisibility(View.GONE);
                }
            }

            // Volume
            if (tvVolume != null) {
                Double volume = item.getVolume();
                if (volume != null && volume > 0) {
                    tvVolume.setText("Thể tích: " + formatNumber(volume) + " m³");
                    tvVolume.setVisibility(View.VISIBLE);
                    hasDimensionsInfo = true;
                } else {
                    tvVolume.setVisibility(View.GONE);
                }
            }
            
            // Show/Hide container based on content
            if (llDimensions != null) {
                llDimensions.setVisibility(hasDimensionsInfo ? View.VISIBLE : View.GONE);
            }
        }

        private String formatNumber(Double value) {
            if (value == null) return "0";
            DecimalFormat df = new DecimalFormat("#,###.##");
            return df.format(value);
        }
    }
}


