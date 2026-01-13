package com.example.financialmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.TaskComment;
import com.example.financialmanagement.utils.CommentParser;
import com.example.financialmanagement.utils.FileIconHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<TaskComment> messages;
    private String currentUserId;
    private OnMessageReplyListener replyListener;
    private OnMessageActionListener actionListener;
    private RecyclerView recyclerView;
    
    public interface OnMessageReplyListener {
        void onReply(TaskComment message);
    }
    
    public interface OnMessageActionListener {
        void onCopy(TaskComment message);
        void onEdit(TaskComment message);
        void onDelete(TaskComment message);
        void onPin(TaskComment message, boolean pin);
        void onForward(TaskComment message);
        void onResend(TaskComment message);
    }
    
    public void setOnMessageReplyListener(OnMessageReplyListener listener) {
        this.replyListener = listener;
    }
    
    public void setOnMessageActionListener(OnMessageActionListener listener) {
        this.actionListener = listener;
    }

    public ChatMessageAdapter(Context context, List<TaskComment> messages, String currentUserId, OnMessageReplyListener replyListener, OnMessageActionListener actionListener) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.replyListener = replyListener;
        this.actionListener = actionListener;
    }
    
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
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
        TextView textContent, textTime, textFileName, textAvatar, textReplySender, textReplyContent, textStatus;
        ImageView imageFileIcon, iconPinned, iconError;
        ImageButton btnMessageMenu;
        ShapeableImageView imagePreview;
        LinearLayout layoutFile, layoutReplyPreview, layoutMessageContainer;
        RecyclerView recyclerImagesGrid;
        com.google.android.material.card.MaterialCardView cardMessage;
        android.widget.ProgressBar progressSending;

        SentMessageHolder(View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.text_message_content);
            textTime = itemView.findViewById(R.id.text_message_time);
            textFileName = itemView.findViewById(R.id.text_file_name);
            imageFileIcon = itemView.findViewById(R.id.image_file_icon);
            imagePreview = itemView.findViewById(R.id.image_message_preview);
            layoutFile = itemView.findViewById(R.id.layout_file_attachment);
            textAvatar = itemView.findViewById(R.id.text_chat_avatar_sent);
            recyclerImagesGrid = itemView.findViewById(R.id.recycler_images_grid);
            cardMessage = itemView.findViewById(R.id.card_message);
            iconPinned = itemView.findViewById(R.id.icon_pinned);
            layoutReplyPreview = itemView.findViewById(R.id.layout_reply_preview);
            textReplySender = itemView.findViewById(R.id.text_reply_sender);
            textReplyContent = itemView.findViewById(R.id.text_reply_content);
            btnMessageMenu = itemView.findViewById(R.id.btn_message_menu);
            progressSending = itemView.findViewById(R.id.progress_sending);
            textStatus = itemView.findViewById(R.id.text_status);
            iconError = itemView.findViewById(R.id.icon_error);
            
            // Find and set dynamic max width
            View parent = itemView;
            while (parent.getParent() != null && !(parent.getParent() instanceof RecyclerView)) {
                parent = (View) parent.getParent();
            }
            layoutMessageContainer = (LinearLayout) itemView.findViewById(R.id.layout_message_container);
            if (layoutMessageContainer != null) {
                setMessageMaxWidth(layoutMessageContainer);
            }
        }

        void bind(TaskComment message) {
            // Check for file/image in type and file_url
            String messageType = message.getType();
            String messageFileUrl = message.getFileUrl();
            boolean isFile = false;
            boolean isImage = false;
            String fileUrl = null;
            
            // First check type and file_url - support "file", "image", and "video" types
            if (messageFileUrl != null && !messageFileUrl.isEmpty()) {
                if ("image".equals(messageType)) {
                    // Type is image, use file_url directly
                    fileUrl = messageFileUrl;
                    isFile = true;
                    isImage = true;
                } else if ("video".equals(messageType)) {
                    // Type is video
                    fileUrl = messageFileUrl;
                    isFile = true;
                    isImage = false;
                } else if ("file".equals(messageType)) {
                    // Type is file, check if it's actually an image or video
                    fileUrl = messageFileUrl;
                    isFile = true;
                    isImage = isImageFile(fileUrl);
                }
            }
            
            // Check for multiple images in [FILE_URLS: ...] format
            List<String> allImageUrls = new ArrayList<>();
            String comment = message.getComment();
            if (comment != null && comment.contains("[FILE_URLS:")) {
                List<String> fileUrls = extractFileUrlsFromText(comment);
                // Filter only images
                for (String url : fileUrls) {
                    if (isImageFile(url)) {
                        allImageUrls.add(url);
                    }
                }
            }
            
            // If no file_url from type, check comment text for URLs
            if (!isFile && allImageUrls.isEmpty()) {
                if (comment != null && comment.contains("[FILE_URLS:")) {
                    List<String> fileUrls = extractFileUrlsFromText(comment);
                    if (!fileUrls.isEmpty()) {
                        fileUrl = fileUrls.get(0); // Take first file
                        isFile = true;
                        isImage = isImageFile(fileUrl);
                    }
                } else if (comment != null) {
                    // Try to extract URLs directly from comment text (http/https URLs)
                    List<String> extractedUrls = extractUrlsFromText(comment);
                    if (!extractedUrls.isEmpty()) {
                        // Check if any URL is an image or file
                        for (String url : extractedUrls) {
                            if (isImageFile(url) || isFileUrl(url)) {
                                fileUrl = url;
                                isFile = true;
                                isImage = isImageFile(url);
                                break;
                            }
                        }
                    }
                }
            }
            
            // Handle multiple images (grid layout)
            if (allImageUrls.size() > 1) {
                imagePreview.setVisibility(View.GONE);
                layoutFile.setVisibility(View.GONE);
                recyclerImagesGrid.setVisibility(View.VISIBLE);
                
                // Remove card background for images only
                if (cardMessage != null) {
                    cardMessage.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);
                    cardMessage.setCardElevation(0);
                }
                
                // Setup grid layout with Messenger-style layout
                int spanCount = getSpanCountForImages(allImageUrls.size());
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, spanCount);
                
                // Custom span size for Messenger-style layout
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return getSpanSizeForPosition(allImageUrls.size(), position, spanCount);
                    }
                });
                
                recyclerImagesGrid.setLayoutManager(gridLayoutManager);
                
                // Add spacing between items (like Messenger)
                int spacing = (int) (2 * context.getResources().getDisplayMetrics().density);
                recyclerImagesGrid.addItemDecoration(new androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        outRect.set(spacing, spacing, spacing, spacing);
                    }
                });
                
                // Create adapter for images
                ChatImageGridAdapter gridAdapter = new ChatImageGridAdapter(context, allImageUrls, (imageUrl, imageName) -> {
                    if (context instanceof com.example.financialmanagement.activities.TaskChatActivity) {
                        ((com.example.financialmanagement.activities.TaskChatActivity) context)
                            .showImageFullscreen(imageUrl, imageName);
                    } else if (context instanceof com.example.financialmanagement.activities.TaskDetailActivity) {
                        ((com.example.financialmanagement.activities.TaskDetailActivity) context)
                            .showImageFullscreen(imageUrl, imageName);
                    }
                });
                recyclerImagesGrid.setAdapter(gridAdapter);
                
                // Show text content below images
                String commentText = message.getComment();
                if (commentText != null && commentText.contains("[FILE_URLS:")) {
                    commentText = commentText.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                }
                if (commentText != null && !commentText.isEmpty()) {
                    textContent.setVisibility(View.VISIBLE);
                    CommentParser.parseAndSet(textContent, commentText, true);
                    // Restore card background if there's text
                    if (cardMessage != null) {
                        cardMessage.setCardBackgroundColor(context.getResources().getColor(R.color.chat_bubble_sent));
                        cardMessage.setCardElevation(1);
                    }
                } else {
                    textContent.setVisibility(View.GONE);
                }
            } else if (isImage && fileUrl != null) {
                // Single image
                imagePreview.setVisibility(View.VISIBLE);
                recyclerImagesGrid.setVisibility(View.GONE);
                layoutFile.setVisibility(View.GONE);
                
                // Remove card background for images only
                if (cardMessage != null) {
                    cardMessage.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);
                    cardMessage.setCardElevation(0);
                }
                
                String cleanUrl = fileUrl.endsWith("?") ? fileUrl.substring(0, fileUrl.length() - 1) : fileUrl;
                Glide.with(context)
                    .load(cleanUrl)
                    .placeholder(R.drawable.ic_attachment)
                    .error(R.drawable.ic_attachment)
                    .fitCenter()
                    .into(imagePreview);
                
                // Add click listener to open fullscreen
                final String finalImageUrl = cleanUrl;
                final String finalFileName = FileIconHelper.getFileName(cleanUrl);
                imagePreview.setOnClickListener(v -> {
                    if (context instanceof com.example.financialmanagement.activities.TaskChatActivity) {
                        ((com.example.financialmanagement.activities.TaskChatActivity) context)
                            .showImageFullscreen(finalImageUrl, finalFileName);
                    } else if (context instanceof com.example.financialmanagement.activities.TaskDetailActivity) {
                        ((com.example.financialmanagement.activities.TaskDetailActivity) context)
                            .showImageFullscreen(finalImageUrl, finalFileName);
                    } else {
                        openFile(finalImageUrl);
                    }
                });
                
                // Show text content below image
                String commentText = message.getComment();
                if (commentText != null && commentText.contains("[FILE_URLS:")) {
                    commentText = commentText.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                }
                if (commentText != null && !commentText.isEmpty()) {
                    textContent.setVisibility(View.VISIBLE);
                    CommentParser.parseAndSet(textContent, commentText, true);
                    // Restore card background if there's text
                    if (cardMessage != null) {
                        cardMessage.setCardBackgroundColor(context.getResources().getColor(R.color.chat_bubble_sent));
                        cardMessage.setCardElevation(1);
                    }
                } else {
                    textContent.setVisibility(View.GONE);
                }
            } else if (isFile && fileUrl != null) {
                imagePreview.setVisibility(View.GONE);
                recyclerImagesGrid.setVisibility(View.GONE);
                layoutFile.setVisibility(View.VISIBLE);
                
                // Restore card background for files
                if (cardMessage != null) {
                    cardMessage.setCardBackgroundColor(context.getResources().getColor(R.color.task_accent));
                    cardMessage.setCardElevation(1);
                }
                
                String cleanUrl = fileUrl.endsWith("?") ? fileUrl.substring(0, fileUrl.length() - 1) : fileUrl;
                imageFileIcon.setImageResource(FileIconHelper.getFileIconResource(cleanUrl));
                textFileName.setText(FileIconHelper.getFileName(cleanUrl));
                
                // Add click listener to open file
                final String finalFileUrl = cleanUrl;
                layoutFile.setOnClickListener(v -> openFile(finalFileUrl));
                
                String commentText = message.getComment();
                if (commentText != null && commentText.contains("[FILE_URLS:")) {
                    commentText = commentText.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                }
                if (commentText != null && !commentText.isEmpty()) {
                    textContent.setVisibility(View.VISIBLE);
                    CommentParser.parseAndSet(textContent, commentText, true);
                } else {
                    textContent.setVisibility(View.GONE);
                }
            } else {
                imagePreview.setVisibility(View.GONE);
                recyclerImagesGrid.setVisibility(View.GONE);
                layoutFile.setVisibility(View.GONE);
                textContent.setVisibility(View.VISIBLE);
                
                // Restore card background for text messages
                if (cardMessage != null) {
                    cardMessage.setCardBackgroundColor(context.getResources().getColor(R.color.task_accent));
                    cardMessage.setCardElevation(1);
                }
                
                // Clean comment text (remove FILE_URLS tags)
                String commentText = message.getComment();
                if (commentText != null && commentText.contains("[FILE_URLS:")) {
                    commentText = commentText.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                }
                CommentParser.parseAndSet(textContent, commentText, true);
            }
            textTime.setText(message.getCreatedAt());
            
            // Show send status (for sent messages)
            String sendStatus = message.getSendStatus();
            if (progressSending != null && textStatus != null && iconError != null) {
                if ("sending".equals(sendStatus)) {
                    // Đang gửi
                    progressSending.setVisibility(View.VISIBLE);
                    textStatus.setVisibility(View.GONE);
                    iconError.setVisibility(View.GONE);
                } else if ("failed".equals(sendStatus)) {
                    // Gửi thất bại
                    progressSending.setVisibility(View.GONE);
                    textStatus.setVisibility(View.GONE);
                    iconError.setVisibility(View.VISIBLE);
                    // Add click listener to retry
                    iconError.setOnClickListener(v -> {
                        if (actionListener != null) {
                            new androidx.appcompat.app.AlertDialog.Builder(context)
                                .setTitle("Gửi lại tin nhắn?")
                                .setMessage("Tin nhắn chưa được gửi. Bạn có muốn thử lại không?")
                                .setPositiveButton("Gửi lại", (dialog, which) -> {
                                    // TODO: Implement retry logic
                                    android.widget.Toast.makeText(context, "Đang thử gửi lại...", android.widget.Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                        }
                    });
                } else {
                    // Đã gửi thành công - show read status
                    progressSending.setVisibility(View.GONE);
                    iconError.setVisibility(View.GONE);
                    textStatus.setVisibility(View.VISIBLE);
                    
                    // Show single or double checkmark based on read status
                    if (message.getReadCount() > 0) {
                        // Message has been read by at least one person
                        textStatus.setText("✓✓"); // Double checkmark
                        textStatus.setTextColor(context.getResources().getColor(R.color.status_read)); // Blue for read
                    } else {
                        // Message sent but not read yet
                        textStatus.setText("✓"); // Single checkmark
                        textStatus.setTextColor(context.getResources().getColor(R.color.status_sent)); // Blue gray for sent
                    }
                }
            }
            
            // Show reply preview if this message is a reply
            setupReplyPreview(message, layoutReplyPreview, textReplySender, textReplyContent);
            
            // Show pinned icon
            if (iconPinned != null) {
                iconPinned.setVisibility(message.getIsPinned() ? View.VISIBLE : View.GONE);
            }

            if (textAvatar != null) {
                String name = message.getDisplayName();
                textAvatar.setText(name.substring(0, 1).toUpperCase());
                textAvatar.getBackground().setTint(getAvatarColor(name));
            }

            if (!isFile) {
                itemView.setOnClickListener(null);
            }
            
            // Store message in view tag for swipe gesture
            itemView.setTag(message);
            
            // Setup three dots menu button
            if (btnMessageMenu != null) {
                btnMessageMenu.setVisibility(View.VISIBLE);
                btnMessageMenu.setOnClickListener(v -> {
                    showMessageContextMenu(v, message);
                });
            }
            
            // Add swipe to reply gesture
            itemView.setOnTouchListener(new SwipeToReplyListener(itemView, message, replyListener));
            
            // Add long press for context menu
            itemView.setOnLongClickListener(v -> {
                showMessageContextMenu(v, message);
                return true;
            });
        }
    }

    class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView textContent, textTime, textSender, textFileName, textAvatar, textReplySender, textReplyContent;
        ImageView imageFileIcon, iconPinned;
        ImageButton btnMessageMenu;
        ShapeableImageView imagePreview;
        LinearLayout layoutFile, layoutReplyPreview, layoutMessageContainer;
        RecyclerView recyclerImagesGrid;
        com.google.android.material.card.MaterialCardView cardMessage;

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
            recyclerImagesGrid = itemView.findViewById(R.id.recycler_images_grid);
            cardMessage = itemView.findViewById(R.id.card_message);
            iconPinned = itemView.findViewById(R.id.icon_pinned);
            layoutReplyPreview = itemView.findViewById(R.id.layout_reply_preview);
            textReplySender = itemView.findViewById(R.id.text_reply_sender);
            textReplyContent = itemView.findViewById(R.id.text_reply_content);
            btnMessageMenu = itemView.findViewById(R.id.btn_message_menu);
            
            // Find and set dynamic max width
            layoutMessageContainer = (LinearLayout) itemView.findViewById(R.id.layout_message_container);
            if (layoutMessageContainer != null) {
                setMessageMaxWidth(layoutMessageContainer);
            }
        }

        void bind(TaskComment message) {
            textSender.setText(message.getDisplayName());
            
            // Check for file/image in type and file_url
            String messageType = message.getType();
            String messageFileUrl = message.getFileUrl();
            boolean isFile = false;
            boolean isImage = false;
            String fileUrl = null;
            
            // First check type and file_url - support "file", "image", and "video" types
            if (messageFileUrl != null && !messageFileUrl.isEmpty()) {
                if ("image".equals(messageType)) {
                    // Type is image, use file_url directly
                    fileUrl = messageFileUrl;
                    isFile = true;
                    isImage = true;
                } else if ("video".equals(messageType)) {
                    // Type is video
                    fileUrl = messageFileUrl;
                    isFile = true;
                    isImage = false;
                } else if ("file".equals(messageType)) {
                    // Type is file, check if it's actually an image or video
                    fileUrl = messageFileUrl;
                    isFile = true;
                    isImage = isImageFile(fileUrl);
                }
            }
            
            // Check for multiple images in [FILE_URLS: ...] format
            List<String> allImageUrls = new ArrayList<>();
            String comment = message.getComment();
            if (comment != null && comment.contains("[FILE_URLS:")) {
                List<String> fileUrls = extractFileUrlsFromText(comment);
                // Filter only images
                for (String url : fileUrls) {
                    if (isImageFile(url)) {
                        allImageUrls.add(url);
                    }
                }
            }
            
            // If no file_url from type, check comment text for URLs
            if (!isFile && allImageUrls.isEmpty()) {
                if (comment != null && comment.contains("[FILE_URLS:")) {
                    List<String> fileUrls = extractFileUrlsFromText(comment);
                    if (!fileUrls.isEmpty()) {
                        fileUrl = fileUrls.get(0); // Take first file
                        isFile = true;
                        isImage = isImageFile(fileUrl);
                    }
                } else if (comment != null) {
                    // Try to extract URLs directly from comment text (http/https URLs)
                    List<String> extractedUrls = extractUrlsFromText(comment);
                    if (!extractedUrls.isEmpty()) {
                        // Check if any URL is an image or file
                        for (String url : extractedUrls) {
                            if (isImageFile(url) || isFileUrl(url)) {
                                fileUrl = url;
                                isFile = true;
                                isImage = isImageFile(url);
                                break;
                            }
                        }
                    }
                }
            }
            
            // Handle multiple images (grid layout)
            if (allImageUrls.size() > 1) {
                imagePreview.setVisibility(View.GONE);
                layoutFile.setVisibility(View.GONE);
                recyclerImagesGrid.setVisibility(View.VISIBLE);
                
                // Remove card background for images only
                if (cardMessage != null) {
                    cardMessage.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);
                    cardMessage.setCardElevation(0);
                }
                
                // Setup grid layout with Messenger-style layout
                int spanCount = getSpanCountForImages(allImageUrls.size());
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, spanCount);
                
                // Custom span size for Messenger-style layout
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return getSpanSizeForPosition(allImageUrls.size(), position, spanCount);
                    }
                });
                
                recyclerImagesGrid.setLayoutManager(gridLayoutManager);
                
                // Add spacing between items (like Messenger)
                int spacing = (int) (2 * context.getResources().getDisplayMetrics().density);
                recyclerImagesGrid.addItemDecoration(new androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        outRect.set(spacing, spacing, spacing, spacing);
                    }
                });
                
                // Create adapter for images
                ChatImageGridAdapter gridAdapter = new ChatImageGridAdapter(context, allImageUrls, (imageUrl, imageName) -> {
                    if (context instanceof com.example.financialmanagement.activities.TaskChatActivity) {
                        ((com.example.financialmanagement.activities.TaskChatActivity) context)
                            .showImageFullscreen(imageUrl, imageName);
                    } else if (context instanceof com.example.financialmanagement.activities.TaskDetailActivity) {
                        ((com.example.financialmanagement.activities.TaskDetailActivity) context)
                            .showImageFullscreen(imageUrl, imageName);
                    }
                });
                recyclerImagesGrid.setAdapter(gridAdapter);
                
                // Show text content below images
                String commentText = message.getComment();
                if (commentText != null && commentText.contains("[FILE_URLS:")) {
                    commentText = commentText.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                }
                if (commentText != null && !commentText.isEmpty()) {
                    textContent.setVisibility(View.VISIBLE);
                    CommentParser.parseAndSet(textContent, commentText, false);
                    // Restore card background if there's text
                    if (cardMessage != null) {
                        cardMessage.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                        cardMessage.setCardElevation(1);
                    }
                } else {
                    textContent.setVisibility(View.GONE);
                }
            } else if (isImage && fileUrl != null) {
                // Single image
                imagePreview.setVisibility(View.VISIBLE);
                recyclerImagesGrid.setVisibility(View.GONE);
                layoutFile.setVisibility(View.GONE);
                
                // Remove card background for images only
                if (cardMessage != null) {
                    cardMessage.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);
                    cardMessage.setCardElevation(0);
                }
                
                String cleanUrl = fileUrl.endsWith("?") ? fileUrl.substring(0, fileUrl.length() - 1) : fileUrl;
                Glide.with(context)
                    .load(cleanUrl)
                    .placeholder(R.drawable.ic_attachment)
                    .error(R.drawable.ic_attachment)
                    .fitCenter()
                    .into(imagePreview);
                
                // Add click listener to open fullscreen
                final String finalImageUrl = cleanUrl;
                final String finalFileName = FileIconHelper.getFileName(cleanUrl);
                imagePreview.setOnClickListener(v -> {
                    if (context instanceof com.example.financialmanagement.activities.TaskChatActivity) {
                        ((com.example.financialmanagement.activities.TaskChatActivity) context)
                            .showImageFullscreen(finalImageUrl, finalFileName);
                    } else if (context instanceof com.example.financialmanagement.activities.TaskDetailActivity) {
                        ((com.example.financialmanagement.activities.TaskDetailActivity) context)
                            .showImageFullscreen(finalImageUrl, finalFileName);
                    } else {
                        openFile(finalImageUrl);
                    }
                });
                
                // Show text content below image
                String commentText = message.getComment();
                if (commentText != null && commentText.contains("[FILE_URLS:")) {
                    commentText = commentText.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                }
                if (commentText != null && !commentText.isEmpty()) {
                    textContent.setVisibility(View.VISIBLE);
                    CommentParser.parseAndSet(textContent, commentText, false);
                    // Restore card background if there's text
                    if (cardMessage != null) {
                        cardMessage.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                        cardMessage.setCardElevation(1);
                    }
                } else {
                    textContent.setVisibility(View.GONE);
                }
            } else if (isFile && fileUrl != null) {
                imagePreview.setVisibility(View.GONE);
                recyclerImagesGrid.setVisibility(View.GONE);
                layoutFile.setVisibility(View.VISIBLE);
                String cleanUrl = fileUrl.endsWith("?") ? fileUrl.substring(0, fileUrl.length() - 1) : fileUrl;
                imageFileIcon.setImageResource(FileIconHelper.getFileIconResource(cleanUrl));
                textFileName.setText(FileIconHelper.getFileName(cleanUrl));
                
                // Add click listener to open file
                final String finalFileUrl = cleanUrl;
                layoutFile.setOnClickListener(v -> openFile(finalFileUrl));
                
                String commentText = message.getComment();
                if (commentText != null && commentText.contains("[FILE_URLS:")) {
                    commentText = commentText.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                }
                if (commentText != null && !commentText.isEmpty()) {
                    textContent.setVisibility(View.VISIBLE);
                    CommentParser.parseAndSet(textContent, commentText, false);
                } else {
                    textContent.setVisibility(View.GONE);
                }
            } else {
                imagePreview.setVisibility(View.GONE);
                recyclerImagesGrid.setVisibility(View.GONE);
                layoutFile.setVisibility(View.GONE);
                textContent.setVisibility(View.VISIBLE);
                
                // Clean comment text (remove FILE_URLS tags)
                String commentText = message.getComment();
                if (commentText != null && commentText.contains("[FILE_URLS:")) {
                    commentText = commentText.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                }
                CommentParser.parseAndSet(textContent, commentText, false);
            }
            textTime.setText(message.getCreatedAt());
            
            // Show reply preview if this message is a reply
            setupReplyPreview(message, layoutReplyPreview, textReplySender, textReplyContent);
            
            // Show pinned icon
            if (iconPinned != null) {
                iconPinned.setVisibility(message.getIsPinned() ? View.VISIBLE : View.GONE);
            }

            if (textAvatar != null) {
                String name = message.getDisplayName();
                textAvatar.setText(name.substring(0, 1).toUpperCase());
                textAvatar.getBackground().setTint(getAvatarColor(name));
            }

            if (!isFile) {
                itemView.setOnClickListener(null);
            }
            
            // Store message in view tag for swipe gesture
            itemView.setTag(message);
            
            // Setup three dots menu button
            if (btnMessageMenu != null) {
                btnMessageMenu.setVisibility(View.VISIBLE);
                btnMessageMenu.setOnClickListener(v -> {
                    showMessageContextMenu(v, message);
                });
            }
            
            // Add swipe to reply gesture
            itemView.setOnTouchListener(new SwipeToReplyListener(itemView, message, replyListener));
            
            // Add long press for context menu
            itemView.setOnLongClickListener(v -> {
                showMessageContextMenu(v, message);
                return true;
            });
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
    
    /**
     * Check if a URL is an image file
     */
    private boolean isImageFile(String url) {
        if (url == null || url.isEmpty()) return false;
        String lowerUrl = url.toLowerCase();

        // Remove query parameters and fragments for extension check
        String urlWithoutParams = lowerUrl;
        int queryIndex = urlWithoutParams.indexOf('?');
        if (queryIndex > 0) {
            urlWithoutParams = urlWithoutParams.substring(0, queryIndex);
        }
        int fragmentIndex = urlWithoutParams.indexOf('#');
        if (fragmentIndex > 0) {
            urlWithoutParams = urlWithoutParams.substring(0, fragmentIndex);
        }

        // Check extension
        boolean hasImageExtension = urlWithoutParams.endsWith(".jpg") || urlWithoutParams.endsWith(".jpeg") ||
                                    urlWithoutParams.endsWith(".png") || urlWithoutParams.endsWith(".gif") ||
                                    urlWithoutParams.endsWith(".webp") || urlWithoutParams.endsWith(".bmp") ||
                                    urlWithoutParams.endsWith(".svg");

        // Check MIME type in URL
        boolean hasImageMimeType = lowerUrl.contains("image/jpeg") || lowerUrl.contains("image/png") ||
                                   lowerUrl.contains("image/gif") || lowerUrl.contains("image/webp") ||
                                   lowerUrl.contains("image/bmp") || lowerUrl.contains("image/svg");

        return hasImageExtension || hasImageMimeType;
    }
    
    /**
     * Extract file URLs from text containing [FILE_URLS: ...] tags
     */
    private List<String> extractFileUrlsFromText(String text) {
        List<String> urls = new ArrayList<>();
        if (text == null) return urls;

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[FILE_URLS: (.*?)\\]");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String urlContent = matcher.group(1);
            if (urlContent != null) {
                // Split by comma or space
                String[] parts = urlContent.split("[,\\s]+");
                for (String part : parts) {
                    String cleanUrl = part.trim();
                    if (!cleanUrl.isEmpty()) {
                        urls.add(cleanUrl);
                    }
                }
            }
        }
        return urls;
    }
    
    /**
     * Extract URLs directly from text (http/https URLs)
     */
    private List<String> extractUrlsFromText(String text) {
        List<String> urls = new ArrayList<>();
        if (text == null || text.isEmpty()) return urls;
        
        // Pattern to match http/https URLs
        java.util.regex.Pattern urlPattern = java.util.regex.Pattern.compile(
            "(https?://[^\\s<>\"{}|\\\\^`\\[\\]]+)",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = urlPattern.matcher(text);
        
        while (matcher.find()) {
            String url = matcher.group(1);
            if (url != null && !url.isEmpty()) {
                urls.add(url);
            }
        }
        
        return urls;
    }
    
    /**
     * Check if URL is a file (not an image)
     */
    private boolean isFileUrl(String url) {
        if (url == null || url.isEmpty()) return false;
        if (isImageFile(url)) return false; // Images are handled separately
        
        String lowerUrl = url.toLowerCase();
        // Check for common file extensions
        return lowerUrl.contains(".pdf") || lowerUrl.contains(".doc") || 
               lowerUrl.contains(".docx") || lowerUrl.contains(".xls") || 
               lowerUrl.contains(".xlsx") || lowerUrl.contains(".txt") ||
               lowerUrl.contains(".zip") || lowerUrl.contains(".rar");
    }
    
    /**
     * Get span count for grid layout based on number of images (Messenger style)
     */
    private int getSpanCountForImages(int imageCount) {
        if (imageCount == 2) {
            return 2; // 2 columns for 2 images
        } else if (imageCount == 3) {
            return 3; // 3 columns: 1 large (span 2) + 2 small (span 1 each)
        } else if (imageCount == 4) {
            return 2; // 2x2 grid
        } else {
            return 3; // 3 columns for 5+ images
        }
    }
    
    /**
     * Get span size for each position (Messenger style)
     */
    private int getSpanSizeForPosition(int totalImages, int position, int spanCount) {
        if (totalImages == 2) {
            return 1; // Each image takes 1 span in 2-column grid
        } else if (totalImages == 3) {
            // First image takes 2 spans, others take 1 span each
            return position == 0 ? 2 : 1;
        } else if (totalImages == 4) {
            return 1; // Each image takes 1 span in 2x2 grid
        } else {
            // For 5+ images, all take 1 span in 3-column grid
            return 1;
        }
    }
    
    /**
     * Swipe to Reply gesture listener
     */
    private static class SwipeToReplyListener implements View.OnTouchListener {
        private View view;
        private TaskComment message;
        private OnMessageReplyListener listener;
        private float startX;
        private float startY;
        private boolean isSwiping = false;
        private static final float SWIPE_THRESHOLD = 100f;
        
        public SwipeToReplyListener(View view, TaskComment message, OnMessageReplyListener listener) {
            this.view = view;
            this.message = message;
            this.listener = listener;
        }
        
        @Override
        public boolean onTouch(View v, android.view.MotionEvent event) {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    isSwiping = false;
                    return false; // Let other touch events handle
                    
                case android.view.MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - startX;
                    float deltaY = event.getY() - startY;
                    
                    // Check if horizontal swipe (left for reply)
                    if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 20) {
                        isSwiping = true;
                        // Visual feedback: slight translation when swiping left
                        if (deltaX < 0 && Math.abs(deltaX) < SWIPE_THRESHOLD) {
                            view.setTranslationX(deltaX * 0.3f); // Move slightly to show swipe
                        }
                    }
                    return false;
                    
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    // Reset translation
                    view.setTranslationX(0);
                    
                    if (isSwiping) {
                        float finalDeltaX = event.getX() - startX;
                        // Swipe left (negative deltaX) to reply
                        if (finalDeltaX < -SWIPE_THRESHOLD) {
                            if (message != null && listener != null) {
                                listener.onReply(message);
                                // Haptic feedback
                                view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                            }
                        }
                    }
                    isSwiping = false;
                    return false;
            }
            return false;
        }
    }
    
    private void showMessageContextMenu(View view, TaskComment message) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.chat_message_context_menu, popupMenu.getMenu());
        
        // Show/hide options based on ownership
        boolean isOwner = currentUserId != null && currentUserId.equals(message.getUserId());
        popupMenu.getMenu().findItem(R.id.menu_copy).setVisible(true);
        popupMenu.getMenu().findItem(R.id.menu_reply).setVisible(true);
        popupMenu.getMenu().findItem(R.id.menu_forward).setVisible(true);
        popupMenu.getMenu().findItem(R.id.menu_resend).setVisible(isOwner);
        popupMenu.getMenu().findItem(R.id.menu_edit).setVisible(isOwner);
        popupMenu.getMenu().findItem(R.id.menu_pin).setVisible(true);
        popupMenu.getMenu().findItem(R.id.menu_delete).setVisible(isOwner);
        
        // Update pin menu text
        if (message.getIsPinned()) {
            popupMenu.getMenu().findItem(R.id.menu_pin).setTitle("Bỏ ghim");
        } else {
            popupMenu.getMenu().findItem(R.id.menu_pin).setTitle("Ghim");
        }
        
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_copy) {
                if (actionListener != null) {
                    actionListener.onCopy(message);
                }
                return true;
            } else if (itemId == R.id.menu_reply) {
                if (replyListener != null) {
                    replyListener.onReply(message);
                }
                return true;
            } else if (itemId == R.id.menu_forward) {
                if (actionListener != null) {
                    actionListener.onForward(message);
                }
                return true;
            } else if (itemId == R.id.menu_resend) {
                if (actionListener != null) {
                    actionListener.onResend(message);
                }
                return true;
            } else if (itemId == R.id.menu_edit) {
                if (actionListener != null) {
                    actionListener.onEdit(message);
                }
                return true;
            } else if (itemId == R.id.menu_delete) {
                if (actionListener != null) {
                    actionListener.onDelete(message);
                }
                return true;
            } else if (itemId == R.id.menu_pin) {
                if (actionListener != null) {
                    actionListener.onPin(message, !message.getIsPinned());
                }
                return true;
            }
            return false;
        });
        
        popupMenu.show();
    }
    
    private TaskComment findParentMessage(String parentId) {
        if (messages == null || parentId == null) {
            return null;
        }
        for (TaskComment msg : messages) {
            if (parentId.equals(msg.getId())) {
                return msg;
            }
        }
        return null;
    }
    
    /**
     * Setup reply preview UI
     */
    private void setupReplyPreview(TaskComment message, LinearLayout layoutReplyPreview, TextView textReplySender, TextView textReplyContent) {
        if (layoutReplyPreview == null || textReplySender == null || textReplyContent == null) {
            return;
        }
        
        // Always hide first, then show if needed
        layoutReplyPreview.setVisibility(View.GONE);
        
        String parentId = message.getParentId();
        if (parentId == null || parentId.isEmpty() || parentId.equals("null")) {
            return;
        }
        
        // Find parent message
        TaskComment parentMessage = findParentMessage(parentId);
        if (parentMessage == null) {
            // Parent message not found in current list - might not be loaded yet
            // Still show preview to indicate this is a reply
            layoutReplyPreview.setVisibility(View.VISIBLE);
            textReplySender.setText("Tin nhắn");
            textReplyContent.setText("Đang tải nội dung...");
            return;
        }
        
        // Found parent message - show preview
        layoutReplyPreview.setVisibility(View.VISIBLE);
        String senderName = parentMessage.getDisplayName();
        if (senderName != null && !senderName.isEmpty()) {
            textReplySender.setText(senderName);
        } else {
            textReplySender.setText("Người dùng");
        }
        
        // Get parent message content
        String parentContent = parentMessage.getComment();
        if (parentContent != null && parentContent.contains("[FILE_URLS:")) {
            parentContent = parentContent.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
        }
        
        // If parent has image/file, show indicator
        String parentType = parentMessage.getType();
        String parentFileUrl = parentMessage.getFileUrl();
        if (parentContent == null || parentContent.isEmpty()) {
            if ("image".equals(parentType) || (parentFileUrl != null && isImageFile(parentFileUrl))) {
                parentContent = "Hình ảnh";
            } else if ("video".equals(parentType)) {
                parentContent = "Video";
            } else if ("file".equals(parentType) || parentFileUrl != null) {
                parentContent = "File đính kèm";
            } else {
                parentContent = "Tin nhắn";
            }
        }
        
        // Display full content without any limits
        if (parentContent != null && !parentContent.isEmpty()) {
            textReplyContent.setText(parentContent);
            // Ensure TextView can display all content without truncation
            textReplyContent.setSingleLine(false);
            textReplyContent.setMaxLines(Integer.MAX_VALUE); // No limit on lines
            textReplyContent.setEllipsize(null); // No ellipsize
            textReplyContent.setHorizontallyScrolling(false);
        } else {
            textReplyContent.setText("Tin nhắn");
        }
        
        // Add click listener to jump to parent message
        final String finalParentId = parentId;
        final TaskComment finalParentMessage = parentMessage;
        layoutReplyPreview.setOnClickListener(v -> {
            scrollToMessage(finalParentId, finalParentMessage);
        });
    }
    
    /**
     * Set dynamic max width for message based on screen size
     */
    private void setMessageMaxWidth(LinearLayout layout) {
        if (layout == null) return;
        
        // Get screen width
        android.util.DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        
        // Calculate max width as 75% of screen width
        int maxWidth = (int) (screenWidth * 0.75f);
        
        // Get min width from resources
        int minWidth = context.getResources().getDimensionPixelSize(R.dimen.chat_message_min_width);
        
        // Create custom LayoutParams with constraints
        // Note: LinearLayout doesn't have setMaxWidth, so we override onMeasure behavior
        // by setting a MeasureSpec listener
        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                     int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int measuredWidth = v.getMeasuredWidth();
                if (measuredWidth > maxWidth) {
                    ViewGroup.LayoutParams params = v.getLayoutParams();
                    if (params != null) {
                        params.width = maxWidth;
                        v.setLayoutParams(params);
                    }
                }
                // Remove listener after first layout to avoid infinite loop
                v.removeOnLayoutChangeListener(this);
            }
        });
        
        // Set minimum width
        layout.setMinimumWidth(minWidth);
    }
    
    /**
     * Scroll to a specific message when reply preview is clicked
     */
    private void scrollToMessage(String messageId, TaskComment parentMessage) {
        if (recyclerView == null || messageId == null) return;
        
        // Find position of the parent message in the list
        int targetPosition = -1;
        for (int i = 0; i < messages.size(); i++) {
            TaskComment msg = messages.get(i);
            if (msg.getId() != null && msg.getId().equals(messageId)) {
                targetPosition = i;
                break;
            }
        }
        
        if (targetPosition != -1) {
            // Scroll to the message position
            recyclerView.smoothScrollToPosition(targetPosition);
            
            // Highlight the message briefly
            final int finalPosition = targetPosition;
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(finalPosition);
                    if (holder != null && holder.itemView != null) {
                        highlightMessage(holder.itemView);
                    }
                }
            }, 300); // Wait for scroll to complete
        } else {
            // Message not found - show toast
            android.widget.Toast.makeText(context, "Không tìm thấy tin nhắn được trả lời", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Highlight a message view briefly
     */
    private void highlightMessage(View messageView) {
        // Save original background
        final android.graphics.drawable.Drawable originalBackground = messageView.getBackground();
        
        // Set highlight color (light blue)
        messageView.setBackgroundColor(0x4042A5F5); // Semi-transparent blue
        
        // Restore original background after 1 second
        messageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                messageView.setBackground(originalBackground);
            }
        }, 1000);
    }
    
    /**
     * Show voice player UI for voice messages
     * This is called from bind() when message type is "voice"
     */
    private void showVoicePlayer(TaskComment message, String audioUrl) {
        // For now, show a simple text message indicating voice message
        // A full voice player UI would be integrated into the message layout
        // This is a simplified implementation - full UI would require custom ViewHolder
        
        // Extract duration from comment if available
        String comment = message.getComment();
        String duration = "0:00";
        if (comment != null && comment.contains("[VOICE:")) {
            int start = comment.indexOf("[VOICE:") + 7;
            int end = comment.indexOf("]", start);
            if (end > start) {
                duration = comment.substring(start, end);
            }
        }
        
        // Show as file attachment with play icon
        // This is temporary - proper UI would show waveform and progress
        android.widget.Toast.makeText(context, 
            "Voice message (" + duration + ") - Tap to play (coming soon)", 
            android.widget.Toast.LENGTH_SHORT).show();
    }
}
