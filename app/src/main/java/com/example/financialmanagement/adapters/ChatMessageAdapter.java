package com.example.financialmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.TaskComment;
import com.example.financialmanagement.utils.CommentParser;
import com.example.financialmanagement.utils.FileIconHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<TaskComment> messages;
    private String currentUserId;

    public ChatMessageAdapter(Context context, List<TaskComment> messages, String currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        TaskComment message = messages.get(position);
        if (currentUserId != null && currentUserId.equals(message.getUserId())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskComment message = messages.get(position);
        if (holder instanceof SentMessageHolder) {
            ((SentMessageHolder) holder).bind(message);
        } else {
            ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView textContent, textTime, textFileName, textAvatar;
        ImageView imageFileIcon;
        ShapeableImageView imagePreview;
        LinearLayout layoutFile;

        SentMessageHolder(View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.text_message_content);
            textTime = itemView.findViewById(R.id.text_message_time);
            textFileName = itemView.findViewById(R.id.text_file_name);
            imageFileIcon = itemView.findViewById(R.id.image_file_icon);
            imagePreview = itemView.findViewById(R.id.image_message_preview);
            layoutFile = itemView.findViewById(R.id.layout_file_attachment);
            textAvatar = itemView.findViewById(R.id.text_chat_avatar_sent);
        }

        void bind(TaskComment message) {
            boolean isFile = "file".equals(message.getType()) && message.getFileUrl() != null;
            boolean isImage = isFile && message.getFileUrl().matches(".*\\.(jpg|jpeg|png|gif|webp|bmp|svg)(\\?.*|$)");

            if (isImage) {
                imagePreview.setVisibility(View.VISIBLE);
                layoutFile.setVisibility(View.GONE);
                Glide.with(context).load(message.getFileUrl()).into(imagePreview);
                textContent.setVisibility(message.getComment() != null && !message.getComment().isEmpty() ? View.VISIBLE : View.GONE);
            } else if (isFile) {
                imagePreview.setVisibility(View.GONE);
                layoutFile.setVisibility(View.VISIBLE);
                imageFileIcon.setImageResource(FileIconHelper.getFileIconResource(message.getFileUrl()));
                textFileName.setText(FileIconHelper.getFileName(message.getFileUrl()));
                textContent.setVisibility(message.getComment() != null && !message.getComment().isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                imagePreview.setVisibility(View.GONE);
                layoutFile.setVisibility(View.GONE);
                textContent.setVisibility(View.VISIBLE);
            }

            CommentParser.parseAndSet(textContent, message.getComment(), true);
            textTime.setText(message.getCreatedAt());

            if (textAvatar != null) {
                String name = message.getDisplayName();
                textAvatar.setText(name.substring(0, 1).toUpperCase());
                textAvatar.getBackground().setTint(getAvatarColor(name));
            }

            if (isFile) {
                itemView.setOnClickListener(v -> openFile(message.getFileUrl()));
            } else {
                itemView.setOnClickListener(null);
            }
        }
    }

    class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView textContent, textTime, textSender, textFileName, textAvatar;
        ImageView imageFileIcon;
        ShapeableImageView imagePreview;
        LinearLayout layoutFile;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.text_message_content);
            textTime = itemView.findViewById(R.id.text_message_time);
            textSender = itemView.findViewById(R.id.text_sender_name);
            textFileName = itemView.findViewById(R.id.text_file_name);
            imageFileIcon = itemView.findViewById(R.id.image_file_icon);
            imagePreview = itemView.findViewById(R.id.image_message_preview);
            layoutFile = itemView.findViewById(R.id.layout_file_attachment);
            textAvatar = itemView.findViewById(R.id.text_chat_avatar);
        }

        void bind(TaskComment message) {
            textSender.setText(message.getDisplayName());
            
            boolean isFile = "file".equals(message.getType()) && message.getFileUrl() != null;
            boolean isImage = isFile && message.getFileUrl().matches(".*\\.(jpg|jpeg|png|gif|webp|bmp|svg)(\\?.*|$)");

            if (isImage) {
                imagePreview.setVisibility(View.VISIBLE);
                layoutFile.setVisibility(View.GONE);
                Glide.with(context).load(message.getFileUrl()).into(imagePreview);
                textContent.setVisibility(message.getComment() != null && !message.getComment().isEmpty() ? View.VISIBLE : View.GONE);
            } else if (isFile) {
                imagePreview.setVisibility(View.GONE);
                layoutFile.setVisibility(View.VISIBLE);
                imageFileIcon.setImageResource(FileIconHelper.getFileIconResource(message.getFileUrl()));
                textFileName.setText(FileIconHelper.getFileName(message.getFileUrl()));
                textContent.setVisibility(message.getComment() != null && !message.getComment().isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                imagePreview.setVisibility(View.GONE);
                layoutFile.setVisibility(View.GONE);
                textContent.setVisibility(View.VISIBLE);
            }

            CommentParser.parseAndSet(textContent, message.getComment(), false);
            textTime.setText(message.getCreatedAt());

            if (textAvatar != null) {
                String name = message.getDisplayName();
                textAvatar.setText(name.substring(0, 1).toUpperCase());
                textAvatar.getBackground().setTint(getAvatarColor(name));
            }

            if (isFile) {
                itemView.setOnClickListener(v -> openFile(message.getFileUrl()));
            } else {
                itemView.setOnClickListener(null);
            }
        }
    }

    private void openFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(fileUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getAvatarColor(String name) {
        if (name == null || name.isEmpty()) return Color.GRAY;
        int hash = name.hashCode();
        int[] colors = {
            Color.parseColor("#0075FF"), Color.parseColor("#34C759"), 
            Color.parseColor("#FF9500"), Color.parseColor("#FF3B30"),
            Color.parseColor("#AF52DE"), Color.parseColor("#5856D6")
        };
        return colors[Math.abs(hash) % colors.length];
    }
}
