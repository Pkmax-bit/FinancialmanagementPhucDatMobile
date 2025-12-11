package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Product;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private ProductClickListener clickListener;
    private boolean isMultiSelectMode = false;
    private Set<String> selectedProductIds = new HashSet<>();

    public interface ProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> products, ProductClickListener clickListener) {
        this.products = products;
        this.clickListener = clickListener;
    }

    public void setMultiSelectMode(boolean multiSelectMode) {
        isMultiSelectMode = multiSelectMode;
        notifyDataSetChanged();
    }

    public List<Product> getSelectedProducts() {
        List<Product> selected = new ArrayList<>();
        for (Product p : products) {
            if (selectedProductIds.contains(p.getId())) {
                selected.add(p);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, clickListener, isMultiSelectMode, selectedProductIds);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvSku, tvCategory, tvPrice, tvQuantity;
        private CheckBox cbSelect;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvSku = itemView.findViewById(R.id.tv_product_sku);
            tvCategory = itemView.findViewById(R.id.tv_product_category);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_product_quantity);
            cbSelect = itemView.findViewById(R.id.cb_select);
        }

        public void bind(Product product, ProductClickListener listener, boolean isMultiSelect, Set<String> selectedIds) {
            tvName.setText(product.getName());
            
            if (product.getSku() != null && !product.getSku().isEmpty()) {
                tvSku.setText("SKU: " + product.getSku());
                tvSku.setVisibility(View.VISIBLE);
            } else {
                tvSku.setVisibility(View.GONE);
            }
            
            if (product.getCategory() != null && !product.getCategory().isEmpty()) {
                tvCategory.setText(product.getCategory());
                tvCategory.setVisibility(View.VISIBLE);
            } else {
                tvCategory.setVisibility(View.GONE);
            }
            
            Double price = product.getUnitPrice();
            if (price != null) {
                tvPrice.setText(CurrencyFormatter.format(price));
            } else {
                tvPrice.setText("0 ₫");
            }
            
            Double qty = product.getQuantity();
            String unit = product.getUnit() != null ? product.getUnit() : "";
            if (qty != null) {
                 tvQuantity.setText("Tồn: " + qty + " " + unit);
            } else {
                 tvQuantity.setText("Tồn: 0 " + unit);
            }

            // Handle Multi Selection UI
            if (isMultiSelect) {
                cbSelect.setVisibility(View.VISIBLE);
                cbSelect.setChecked(selectedIds.contains(product.getId()));
                
                // Allow clicking whole row to toggle check
                itemView.setOnClickListener(v -> {
                    if (selectedIds.contains(product.getId())) {
                        selectedIds.remove(product.getId());
                        cbSelect.setChecked(false);
                    } else {
                        selectedIds.add(product.getId());
                        cbSelect.setChecked(true);
                    }
                });
                
                // Also sync with checkbox click
                cbSelect.setOnClickListener(v -> {
                    if (cbSelect.isChecked()) {
                        selectedIds.add(product.getId());
                    } else {
                        selectedIds.remove(product.getId());
                    }
                });
            } else {
                cbSelect.setVisibility(View.GONE);
                itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onProductClick(product);
                });
            }
        }
    }
}
