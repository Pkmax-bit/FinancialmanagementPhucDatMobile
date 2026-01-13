package com.example.financialmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.financialmanagement.R;
import com.example.financialmanagement.utils.FileIconHelper;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class ChatImageGridAdapter extends RecyclerView.Adapter<ChatImageGridAdapter.ImageViewHolder> {
    
    private Context context;
    private List<String> imageUrls;
    private OnImageClickListener clickListener;
    
    public interface OnImageClickListener {
        void onImageClick(String imageUrl, String imageName);
    }
    
    public ChatImageGridAdapter(Context context, List<String> imageUrls, OnImageClickListener clickListener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.clickListener = clickListener;
    }
    
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_image_grid, parent, false);
        return new ImageViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        String cleanUrl = imageUrl.endsWith("?") ? imageUrl.substring(0, imageUrl.length() - 1) : imageUrl;
        
        // Calculate item size based on grid span (Messenger style)
        int totalImages = imageUrls.size();
        int spanCount = getSpanCountForImages(totalImages);
        int spanSize = getSpanSizeForPosition(totalImages, position, spanCount);
        
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (16 * context.getResources().getDisplayMetrics().density); // Card padding
        int itemSpacing = (int) (2 * context.getResources().getDisplayMetrics().density); // Spacing between items (2dp like Messenger)
        int maxWidth = (int) (280 * context.getResources().getDisplayMetrics().density); // Max message width
        int availableWidth = Math.min(screenWidth - padding * 2, maxWidth) - padding * 2;
        
        // Calculate base item size (for 1 span)
        int baseItemSize = (availableWidth - (itemSpacing * (spanCount - 1))) / spanCount;
        
        // Calculate width based on span size
        int itemWidth = baseItemSize * spanSize + (itemSpacing * (spanSize - 1));
        
        // Calculate height (Messenger uses square for most, but larger for first image in 3-image layout)
        int itemHeight;
        if (totalImages == 3 && position == 0) {
            // First image in 3-image layout: width is 2 spans, height should be 2x base size
            itemHeight = baseItemSize * 2 + itemSpacing; // 2 rows tall
        } else if (totalImages == 3 && position > 0) {
            // Small images in 3-image layout: 1 span each, square
            itemHeight = baseItemSize;
        } else {
            // Other cases: square
            itemHeight = itemWidth;
        }
        
        ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
        params.width = itemWidth;
        params.height = itemHeight;
        holder.imageView.setLayoutParams(params);
        
        // Load image
        Glide.with(context)
            .load(cleanUrl)
            .placeholder(R.drawable.ic_attachment)
            .error(R.drawable.ic_attachment)
            .centerCrop()
            .into(holder.imageView);
        
        // Click listener
        holder.imageView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onImageClick(cleanUrl, FileIconHelper.getFileName(cleanUrl));
            }
        });
    }
    
    private int getSpanCountForImages(int imageCount) {
        if (imageCount == 2) {
            return 2;
        } else if (imageCount == 3) {
            return 3;
        } else if (imageCount == 4) {
            return 2;
        } else {
            return 3;
        }
    }
    
    private int getSpanSizeForPosition(int totalImages, int position, int spanCount) {
        if (totalImages == 2) {
            return 1;
        } else if (totalImages == 3) {
            return position == 0 ? 2 : 1;
        } else if (totalImages == 4) {
            return 1;
        } else {
            return 1;
        }
    }
    
    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
    
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageView;
        
        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_grid_item);
        }
    }
}

