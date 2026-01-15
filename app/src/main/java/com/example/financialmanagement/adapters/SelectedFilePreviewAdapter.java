package com.example.financialmanagement.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.financialmanagement.R;
import com.example.financialmanagement.utils.FileIconHelper;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class SelectedFilePreviewAdapter extends RecyclerView.Adapter<SelectedFilePreviewAdapter.FilePreviewViewHolder> {
    
    private Context context;
    private List<Uri> fileUris;
    private OnRemoveClickListener removeListener;
    
    public interface OnRemoveClickListener {
        void onRemove(Uri uri);
    }
    
    public SelectedFilePreviewAdapter(Context context, List<Uri> fileUris, OnRemoveClickListener removeListener) {
        this.context = context;
        this.fileUris = fileUris;
        this.removeListener = removeListener;
    }
    
    @NonNull
    @Override
    public FilePreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_file_preview, parent, false);
        return new FilePreviewViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FilePreviewViewHolder holder, int position) {
        Uri fileUri = fileUris.get(position);
        
        // Check if it's an image
        String fileName = getFileNameFromUri(fileUri);
        boolean isImage = isImageFile(fileName);
        
        if (isImage) {
            // Show image preview
            holder.imagePreview.setVisibility(View.VISIBLE);
            holder.imageFileIcon.setVisibility(View.GONE);
            Glide.with(context)
                .load(fileUri)
                .placeholder(R.drawable.ic_attachment)
                .error(R.drawable.ic_attachment)
                .centerCrop()
                .into(holder.imagePreview);
        } else {
            // Show file icon
            holder.imagePreview.setVisibility(View.GONE);
            holder.imageFileIcon.setVisibility(View.VISIBLE);
            holder.imageFileIcon.setImageResource(FileIconHelper.getFileIconResource(fileName));
        }
        
        // Remove button
        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemove(fileUri);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return fileUris.size();
    }
    
    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        if (fileName == null) {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }
        return fileName;
    }
    
    private boolean isImageFile(String fileName) {
        if (fileName == null) return false;
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
               lowerName.endsWith(".png") || lowerName.endsWith(".gif") ||
               lowerName.endsWith(".webp") || lowerName.endsWith(".bmp");
    }
    
    static class FilePreviewViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imagePreview;
        ImageView imageFileIcon;
        ImageButton btnRemove;
        
        FilePreviewViewHolder(View itemView) {
            super(itemView);
            imagePreview = itemView.findViewById(R.id.image_preview);
            imageFileIcon = itemView.findViewById(R.id.image_file_icon);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}



