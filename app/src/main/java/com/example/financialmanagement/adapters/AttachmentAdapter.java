package com.example.financialmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.AttachmentItem;

import java.util.ArrayList;
import java.util.List;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder> {

    private Context context;
    private List<AttachmentItem> attachments;
    private OnAttachmentRemovedListener listener;

    public interface OnAttachmentRemovedListener {
        void onAttachmentRemoved(AttachmentItem attachment);
    }

    public AttachmentAdapter(Context context, List<AttachmentItem> attachments, OnAttachmentRemovedListener listener) {
        this.context = context;
        this.attachments = attachments != null ? attachments : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attachment_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttachmentItem attachment = attachments.get(position);

        // Determine if this is an image (from URI or URL)
        boolean isImage = attachment.isImage();
        if (!isImage && attachment.getUploadedUrl() != null) {
            // Check if uploaded URL is an image
            String url = attachment.getUploadedUrl().toLowerCase();
            isImage = url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || 
                     url.endsWith(".gif") || url.endsWith(".webp") || url.endsWith(".bmp");
        }

        if (isImage) {
            // Show image preview
            holder.imagePreview.setVisibility(View.VISIBLE);
            holder.layoutFileIcon.setVisibility(View.GONE);

            // Load from URI (local) or URL (uploaded)
            if (attachment.getUploadedUrl() != null && !attachment.getUploadedUrl().isEmpty()) {
                // Load from URL (already uploaded)
                String imageUrl = attachment.getUploadedUrl();
                if (imageUrl.endsWith("?")) {
                    imageUrl = imageUrl.substring(0, imageUrl.length() - 1);
                }
                Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_attachment)
                    .error(R.drawable.ic_attachment)
                    .into(holder.imagePreview);
                
                // Add click listener to open fullscreen
                final String finalImageUrl = imageUrl;
                final String finalFileName = attachment.getFileName();
                holder.imagePreview.setOnClickListener(v -> {
                    if (context instanceof com.example.financialmanagement.activities.TaskDetailActivity) {
                        ((com.example.financialmanagement.activities.TaskDetailActivity) context)
                            .showImageFullscreen(finalImageUrl, finalFileName);
                    } else {
                        // Fallback: open with Intent
                        try {
                            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                            intent.setData(android.net.Uri.parse(finalImageUrl));
                            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } catch (Exception ex) {
                            android.widget.Toast.makeText(context, "Không thể mở hình ảnh", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (attachment.getUri() != null) {
                // Load from local URI
                Glide.with(context)
                    .load(attachment.getUri())
                    .centerCrop()
                    .placeholder(R.drawable.ic_attachment)
                    .error(R.drawable.ic_attachment)
                    .into(holder.imagePreview);
                
                // Add click listener to open fullscreen (if possible)
                holder.imagePreview.setOnClickListener(v -> {
                    // For local images, try to open with URI
                    if (attachment.getUri() != null) {
                        try {
                            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(attachment.getUri(), "image/*");
                            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            android.widget.Toast.makeText(context, "Không thể mở hình ảnh", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            // Show file icon with file name
            holder.imagePreview.setVisibility(View.GONE);
            holder.layoutFileIcon.setVisibility(View.VISIBLE);

            // Show file name if available
            if (holder.textFileName != null && attachment.getFileName() != null) {
                holder.textFileName.setText(attachment.getFileName());
                holder.textFileName.setVisibility(View.VISIBLE);
            }
            
            holder.textFileType.setText(attachment.getFileExtension());

            // Set appropriate file icon based on type
            int iconRes = getFileIconResource(attachment.getMimeType());
            holder.imageFileIcon.setImageResource(iconRes);
            
            // Add click listener to open file
            holder.layoutFileIcon.setOnClickListener(v -> {
                if (attachment.getUploadedUrl() != null && !attachment.getUploadedUrl().isEmpty()) {
                    // Open uploaded file URL
                    String fileUrl = attachment.getUploadedUrl();
                    if (fileUrl.endsWith("?")) {
                        fileUrl = fileUrl.substring(0, fileUrl.length() - 1);
                    }
                    try {
                        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                        intent.setData(android.net.Uri.parse(fileUrl));
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        android.widget.Toast.makeText(context, "Không thể mở file", android.widget.Toast.LENGTH_SHORT).show();
                    }
                } else if (attachment.getUri() != null) {
                    // Open local file
                    try {
                        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                        intent.setDataAndType(attachment.getUri(), attachment.getMimeType());
                        intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        android.widget.Toast.makeText(context, "Không thể mở file", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Handle upload status overlay
        updateUploadStatusDisplay(holder, attachment);

        // Handle remove
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAttachmentRemoved(attachment);
            }
        });
    }

    private void updateUploadStatusDisplay(ViewHolder holder, AttachmentItem attachment) {
        switch (attachment.getUploadStatus()) {
            case PENDING:
                holder.layoutUploadOverlay.setVisibility(View.GONE);
                break;

            case UPLOADING:
                holder.layoutUploadOverlay.setVisibility(View.VISIBLE);
                holder.progressUpload.setVisibility(View.VISIBLE);
                holder.imageUploadStatus.setVisibility(View.GONE);
                holder.imageUploadError.setVisibility(View.GONE);
                holder.textUploadProgress.setVisibility(View.VISIBLE);
                holder.textUploadProgress.setText(attachment.getUploadProgress() + "%");
                break;

            case SUCCESS:
                holder.layoutUploadOverlay.setVisibility(View.VISIBLE);
                holder.progressUpload.setVisibility(View.GONE);
                holder.imageUploadStatus.setVisibility(View.VISIBLE);
                holder.imageUploadError.setVisibility(View.GONE);
                holder.textUploadProgress.setVisibility(View.GONE);
                break;

            case ERROR:
                holder.layoutUploadOverlay.setVisibility(View.VISIBLE);
                holder.progressUpload.setVisibility(View.GONE);
                holder.imageUploadStatus.setVisibility(View.GONE);
                holder.imageUploadError.setVisibility(View.VISIBLE);
                holder.textUploadProgress.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    public void addAttachment(AttachmentItem attachment) {
        attachments.add(attachment);
        notifyItemInserted(attachments.size() - 1);
    }

    public void removeAttachment(AttachmentItem attachment) {
        int position = attachments.indexOf(attachment);
        if (position >= 0) {
            attachments.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<AttachmentItem> getAttachments() {
        return attachments;
    }

    private int getFileIconResource(String mimeType) {
        if (mimeType == null) return R.drawable.ic_attachment;

        if (mimeType.startsWith("application/pdf")) {
            return R.drawable.ic_pdf;
        } else if (mimeType.startsWith("application/msword") ||
                   mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return R.drawable.ic_word;
        } else if (mimeType.startsWith("application/vnd.ms-excel") ||
                   mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return R.drawable.ic_excel;
        } else if (mimeType.startsWith("application/vnd.ms-powerpoint") ||
                   mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
            return R.drawable.ic_powerpoint;
        } else {
            return R.drawable.ic_attachment;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePreview;
        LinearLayout layoutFileIcon;
        ImageView imageFileIcon;
        TextView textFileName;
        TextView textFileType;
        ImageButton btnRemove;

        // Upload status views
        LinearLayout layoutUploadOverlay;
        ProgressBar progressUpload;
        ImageView imageUploadStatus;
        ImageView imageUploadError;
        TextView textUploadProgress;

        ViewHolder(View itemView) {
            super(itemView);
            imagePreview = itemView.findViewById(R.id.image_preview);
            layoutFileIcon = itemView.findViewById(R.id.layout_file_icon);
            imageFileIcon = itemView.findViewById(R.id.image_file_icon);
            textFileName = itemView.findViewById(R.id.text_file_name);
            textFileType = itemView.findViewById(R.id.text_file_type);
            btnRemove = itemView.findViewById(R.id.btn_remove_attachment);

            // Upload status views
            layoutUploadOverlay = itemView.findViewById(R.id.layout_upload_overlay);
            progressUpload = itemView.findViewById(R.id.progress_upload);
            imageUploadStatus = itemView.findViewById(R.id.image_upload_status);
            imageUploadError = itemView.findViewById(R.id.image_upload_error);
            textUploadProgress = itemView.findViewById(R.id.text_upload_progress);
        }
    }
}
