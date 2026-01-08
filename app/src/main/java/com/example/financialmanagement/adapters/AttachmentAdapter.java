package com.example.financialmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        if (attachment.isImage()) {
            // Show image preview
            holder.imagePreview.setVisibility(View.VISIBLE);
            holder.layoutFileIcon.setVisibility(View.GONE);

            Glide.with(context)
                .load(attachment.getUri())
                .centerCrop()
                .into(holder.imagePreview);
        } else {
            // Show file icon
            holder.imagePreview.setVisibility(View.GONE);
            holder.layoutFileIcon.setVisibility(View.VISIBLE);

            holder.textFileType.setText(attachment.getFileExtension());

            // Set appropriate file icon based on type
            int iconRes = getFileIconResource(attachment.getMimeType());
            holder.imageFileIcon.setImageResource(iconRes);
        }

        // Handle remove
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAttachmentRemoved(attachment);
            }
        });
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
        TextView textFileType;
        ImageButton btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            imagePreview = itemView.findViewById(R.id.image_preview);
            layoutFileIcon = itemView.findViewById(R.id.layout_file_icon);
            imageFileIcon = itemView.findViewById(R.id.image_file_icon);
            textFileType = itemView.findViewById(R.id.text_file_type);
            btnRemove = itemView.findViewById(R.id.btn_remove_attachment);
        }
    }
}
