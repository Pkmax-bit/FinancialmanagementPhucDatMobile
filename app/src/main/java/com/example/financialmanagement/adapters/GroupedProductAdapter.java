package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Product;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.util.ArrayList;
import java.util.List;

public class GroupedProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<DisplayItem> displayItems = new ArrayList<>();
    private OnProductClickListener listener;
    private OnHeaderClickListener headerListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(String categoryName);
    }

    public GroupedProductAdapter(List<DisplayItem> displayItems, OnProductClickListener listener, OnHeaderClickListener headerListener) {
        this.displayItems = displayItems;
        this.listener = listener;
        this.headerListener = headerListener;
    }

    public void updateItems(List<DisplayItem> newItems) {
        this.displayItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind((DisplayItem.Header) displayItems.get(position));
        } else {
            ((ProductViewHolder) holder).bind((DisplayItem.Item) displayItems.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return displayItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return displayItems.get(position).getType();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCount;
        ImageView ivExpand;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_category_name);
            tvCount = itemView.findViewById(R.id.tv_product_count);
            ivExpand = itemView.findViewById(R.id.iv_expand_collapse);
            
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    DisplayItem item = displayItems.get(pos);
                    if (item instanceof DisplayItem.Header) {
                        headerListener.onHeaderClick(((DisplayItem.Header) item).categoryName);
                    }
                }
            });
        }

        void bind(DisplayItem.Header header) {
            tvName.setText(header.categoryName);
            tvCount.setText("(" + header.count + ")");
            ivExpand.setRotation(header.isExpanded ? 180 : 0);
        }
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSku, tvPrice, tvQuantity;
        TextView tvStatus;

        ProductViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvSku = itemView.findViewById(R.id.tv_product_sku);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_product_quantity);
            tvStatus = itemView.findViewById(R.id.tv_product_status_chip);
            
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    DisplayItem item = displayItems.get(pos);
                    if (item instanceof DisplayItem.Item) {
                        listener.onProductClick(((DisplayItem.Item) item).product);
                    }
                }
            });
        }

        void bind(DisplayItem.Item item) {
            Product product = item.product;
            tvName.setText(product.getName());
            tvSku.setText("SKU: " + (product.getSku() != null ? product.getSku() : "N/A"));
            
            if (product.getUnitPrice() != null) {
                tvPrice.setText(CurrencyFormatter.format(product.getUnitPrice()));
            } else {
                tvPrice.setText("0 đ");
            }

            if ("inventory".equals(product.getType()) && product.getQuantity() != null) {
                tvQuantity.setVisibility(View.VISIBLE);
                tvQuantity.setText("QTY: " + product.getQuantity());
            } else {
                tvQuantity.setVisibility(View.GONE);
            }
            
            if (tvStatus != null) {
                // Determine status logic here if needed, for now hiding or showing basic
                // Example: tvStatus.setVisibility(View.VISIBLE);
            }
        }
    }

    // Helper classes for display items
    public static abstract class DisplayItem {
        abstract int getType();

        public static class Header extends DisplayItem {
            public String categoryName;
            public int count;
            public boolean isExpanded;

            public Header(String categoryName, int count, boolean isExpanded) {
                this.categoryName = categoryName;
                this.count = count;
                this.isExpanded = isExpanded;
            }

            @Override
            int getType() { return TYPE_HEADER; }
        }

        public static class Item extends DisplayItem {
            public Product product;

            public Item(Product product) {
                this.product = product;
            }

            @Override
            int getType() { return TYPE_ITEM; }
        }
    }
}
