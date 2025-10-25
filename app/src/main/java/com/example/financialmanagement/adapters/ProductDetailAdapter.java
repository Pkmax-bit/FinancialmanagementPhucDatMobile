package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Product;

import java.util.List;

public class ProductDetailAdapter extends RecyclerView.Adapter<ProductDetailAdapter.ProductViewHolder> {
    
    private List<Product> products;
    
    public ProductDetailAdapter(List<Product> products) {
        this.products = products;
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_detail, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }
    
    @Override
    public int getItemCount() {
        return products.size();
    }
    
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvProductName, tvProductDescription, tvProductQuantity;
        private TextView tvProductPrice, tvProductTotal, tvProductUnit;
        
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductDescription = itemView.findViewById(R.id.tv_product_description);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductTotal = itemView.findViewById(R.id.tv_product_total);
            tvProductUnit = itemView.findViewById(R.id.tv_product_unit);
        }
        
        public void bind(Product product) {
            // Product name - use nameProduct if available, otherwise name
            String productName = product.getNameProduct() != null ? product.getNameProduct() : product.getName();
            tvProductName.setText(productName != null ? productName : "Sản phẩm");
            
            // Description
            tvProductDescription.setText(product.getDescription() != null ? product.getDescription() : "Không có mô tả");
            
            // Quantity and unit
            if (product.getQuantity() != null) {
                tvProductQuantity.setText(String.format("%.2f", product.getQuantity()));
            } else {
                tvProductQuantity.setText("0");
            }
            
            // Unit
            tvProductUnit.setText(product.getUnit() != null ? product.getUnit() : "cái");
            
            // Price
            if (product.getUnitPrice() != null) {
                tvProductPrice.setText(String.format("%,.0f VNĐ", product.getUnitPrice()));
            } else {
                tvProductPrice.setText("0 VNĐ");
            }
            
            // Total
            if (product.getTotalPrice() != null) {
                tvProductTotal.setText(String.format("%,.0f VNĐ", product.getTotalPrice()));
            } else {
                tvProductTotal.setText("0 VNĐ");
            }
        }
    }
}
