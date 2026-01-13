package com.example.financialmanagement.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.ChatMessageAdapter;
import com.example.financialmanagement.adapters.SelectedFilePreviewAdapter;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.models.TaskComment;
import com.example.financialmanagement.services.TaskService;
import com.example.financialmanagement.utils.FileIconHelper;
import com.example.financialmanagement.utils.InputValidator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TaskChatActivity extends AppCompatActivity {

    private String taskId;
    private RecyclerView recyclerView;
    private ChatMessageAdapter adapter;
    private List<TaskComment> messageList = new ArrayList<>();
    private TaskService taskService;
    private AuthManager authManager;
    private EditText editMessage;
    
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_FILE_PICK = 1002;
    private static final int REQUEST_CAMERA = 1003;
    private static final int REQUEST_VIDEO_PICK = 1004;
    private static final int REQUEST_MEDIA_PROJECTION = 1005;
    private List<Uri> selectedFileUris = new ArrayList<>();
    private Uri cameraImageUri = null;
    private TaskComment replyingToMessage = null; // Message being replied to
    private RecyclerView recyclerSelectedFiles;
    private SelectedFilePreviewAdapter selectedFileAdapter;
    private AlertDialog uploadProgressDialog;
    
    // Real-time polling - Optimized
    private android.os.Handler realtimeHandler;
    private Runnable realtimeRunnable;
    private static final int POLLING_INTERVAL_ACTIVE = 3000; // 3 seconds when active
    private static final int POLLING_INTERVAL_INACTIVE = 10000; // 10 seconds when inactive
    private static final int POLLING_INTERVAL_BACKGROUND = 30000; // 30 seconds in background
    private int currentPollingInterval = POLLING_INTERVAL_ACTIVE;
    private boolean isPolling = false;
    private String lastMessageId = null;
    private long lastActivityTime = System.currentTimeMillis();
    private int consecutiveEmptyPolls = 0;
    private int pollingErrorCount = 0;
    
    // Typing indicator
    private TextView textTypingIndicator;
    private android.os.Handler typingHandler;
    private Runnable typingRunnable;
    private Runnable stopTypingRunnable;
    private boolean isUserTyping = false;
    private static final int TYPING_TIMEOUT = 3000; // Stop typing after 3 seconds of inactivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_chat);

        taskId = getIntent().getStringExtra("task_id");
        if (taskId == null) {
            taskId = getIntent().getStringExtra("taskId");
        }
        String taskTitle = getIntent().getStringExtra("task_title");

        setupToolbar(taskTitle);
        initializeViews();

        taskService = new TaskService(this);
        authManager = new AuthManager(this);
        
        loadMessages();
        setupRealtimePolling();
        setupReadReceiptsTracking();
    }

    private void setupToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title != null ? title : "Trao đổi");
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        editMessage = findViewById(R.id.edit_chat_message);
        ImageButton btnSend = findViewById(R.id.btn_send_message);
        ImageButton btnAttach = findViewById(R.id.btn_attach_file);
        ImageButton btnEmoji = findViewById(R.id.btn_emoji);
        ImageButton btnVoice = findViewById(R.id.btn_voice);
        textTypingIndicator = findViewById(R.id.text_typing_indicator);
        
        // Setup typing indicator
        setupTypingIndicator();
        
        // Setup emoji picker
        btnEmoji.setOnClickListener(v -> showEmojiPicker());
        
        // Setup voice recording
        btnVoice.setOnClickListener(v -> startVoiceRecording());
        
        // Setup selected files preview
        recyclerSelectedFiles = findViewById(R.id.recycler_selected_files);
        recyclerSelectedFiles.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        selectedFileAdapter = new SelectedFilePreviewAdapter(this, selectedFileUris, uri -> {
            selectedFileUris.remove(uri);
            updateSelectedFilesPreview();
        });
        recyclerSelectedFiles.setAdapter(selectedFileAdapter);
        
        // Setup reply preview
        LinearLayout layoutReplyPreview = findViewById(R.id.layout_reply_preview_input);
        TextView textReplySenderInput = findViewById(R.id.text_reply_sender_input);
        TextView textReplyContentInput = findViewById(R.id.text_reply_content_input);
        ImageButton btnCancelReply = findViewById(R.id.btn_cancel_reply);
        
        btnCancelReply.setOnClickListener(v -> {
            replyingToMessage = null;
            hideReplyPreview();
        });
        
        btnSend.setOnClickListener(v -> sendMessage());
        btnAttach.setOnClickListener(v -> showAttachmentOptions());
        
        // Track user activity for smart polling
        editMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                lastActivityTime = System.currentTimeMillis();
            }
        });
    }
    
    private void showReplyPreview(TaskComment message) {
        LinearLayout layoutReplyPreview = findViewById(R.id.layout_reply_preview_input);
        TextView textReplySenderInput = findViewById(R.id.text_reply_sender_input);
        TextView textReplyContentInput = findViewById(R.id.text_reply_content_input);
        
        if (layoutReplyPreview != null && textReplySenderInput != null && textReplyContentInput != null) {
            layoutReplyPreview.setVisibility(View.VISIBLE);
            textReplySenderInput.setText(message.getDisplayName());
            
            String replyContent = message.getComment();
            if (replyContent != null) {
                // Remove FILE_URLS tags
                if (replyContent.contains("[FILE_URLS:")) {
                    replyContent = replyContent.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                }
                if (replyContent.isEmpty()) {
                    replyContent = message.getType() != null && message.getType().equals("image") ? "Hình ảnh" : 
                                  (message.getType() != null && message.getType().equals("file") ? "File đính kèm" : "Tin nhắn");
                }
                if (replyContent.length() > 50) {
                    replyContent = replyContent.substring(0, 50) + "...";
                }
                textReplyContentInput.setText(replyContent);
            } else {
                textReplyContentInput.setText("Tin nhắn");
            }
        }
    }
    
    private void hideReplyPreview() {
        LinearLayout layoutReplyPreview = findViewById(R.id.layout_reply_preview_input);
        if (layoutReplyPreview != null) {
            layoutReplyPreview.setVisibility(View.GONE);
        }
    }
    
    private void updateSelectedFilesPreview() {
        if (selectedFileAdapter != null) {
            selectedFileAdapter.notifyDataSetChanged();
        }
        if (recyclerSelectedFiles != null) {
            recyclerSelectedFiles.setVisibility(selectedFileUris.isEmpty() ? View.GONE : View.VISIBLE);
        }
        if (selectedFileUris.isEmpty()) {
            editMessage.setHint("Nhập tin nhắn...");
        } else {
            editMessage.setHint("Đã chọn " + selectedFileUris.size() + " file (có thể thêm text)");
        }
    }

    private void loadMessages() {
        loadMessages(false);
    }
    
    private void loadMessages(boolean isRealtimeUpdate) {
        loadMessagesOptimized(isRealtimeUpdate);
    }
    
    /**
     * Optimized message loading with smart caching
     */
    private void loadMessagesOptimized(boolean isRealtimeUpdate) {
        // Reset error count on new request
        if (!isRealtimeUpdate) {
            pollingErrorCount = 0;
        }
        
        taskService.getTaskComments(taskId, new TaskService.TaskCallback<List<TaskComment>>() {
            @Override
            public void onSuccess(List<TaskComment> result) {
                // Reset error counter on success
                pollingErrorCount = 0;
                // Flatten nested structure (comments with replies) into flat list
                List<TaskComment> flatList = flattenComments(result);
                
                // Check if there are any changes
                boolean hasChanges = false;
                
                if (isRealtimeUpdate) {
                    // Real-time update: merge new messages
                    hasChanges = mergeNewMessagesOptimized(flatList);
                    
                    // Track empty polls for smart interval adjustment
                    if (!hasChanges) {
                        consecutiveEmptyPolls++;
                    } else {
                        consecutiveEmptyPolls = 0; // Reset on new messages
                    }
                } else {
                    // Initial load: replace all
                    messageList.clear();
                    messageList.addAll(flatList);
                    sortMessages(); // Sort to show pinned messages first
                    hasChanges = true;
                }
                
                // Track last message ID for real-time updates
                if (!messageList.isEmpty()) {
                    lastMessageId = messageList.get(messageList.size() - 1).getId();
                }
                
                // Debug: Log comments to see what data we have
                for (TaskComment comment : result) {
                    android.util.Log.d("TaskChat", "Comment: id=" + comment.getId() + 
                        ", type=" + comment.getType() + 
                        ", fileUrl=" + comment.getFileUrl() + 
                        ", comment=" + (comment.getComment() != null ? comment.getComment().substring(0, Math.min(100, comment.getComment().length())) : "null"));
                }
                
                if (adapter == null) {
                    adapter = new ChatMessageAdapter(TaskChatActivity.this, messageList, authManager.getUserId(), 
                        new ChatMessageAdapter.OnMessageReplyListener() {
                            @Override
                            public void onReply(TaskComment message) {
                                replyingToMessage = message;
                                showReplyPreview(message);
                            }
                        },
                        new ChatMessageAdapter.OnMessageActionListener() {
                            @Override
                            public void onCopy(TaskComment message) {
                                copyMessage(message);
                            }

                            @Override
                            public void onEdit(TaskComment message) {
                                showEditMessageDialog(message);
                            }

                            @Override
                            public void onDelete(TaskComment message) {
                                showDeleteMessageConfirmation(message);
                            }

                            @Override
                            public void onPin(TaskComment message, boolean pin) {
                                pinMessage(message, pin);
                            }

                            @Override
                            public void onForward(TaskComment message) {
                                forwardMessage(message);
                            }

                            @Override
                            public void onResend(TaskComment message) {
                                resendMessage(message);
                            }
                        });
                    adapter.setOnMessageReplyListener(message -> {
                        replyingToMessage = message;
                        String replyText = message.getComment() != null ? message.getComment() : "Tin nhắn";
                        if (replyText.length() > 50) {
                            replyText = replyText.substring(0, 50) + "...";
                        }
                        editMessage.setHint("Trả lời: " + replyText);
                        editMessage.requestFocus();
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                
                if (!messageList.isEmpty()) {
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onError(String error) {
                // Increment error count for exponential backoff
                pollingErrorCount++;
                
                // Only show toast for initial load, not for polling errors
                if (!isRealtimeUpdate) {
                    Toast.makeText(TaskChatActivity.this, "Lỗi tải tin nhắn: " + error, Toast.LENGTH_SHORT).show();
                } else {
                    // Silent fail for background polling - will retry with backoff
                    android.util.Log.e("TaskChat", "Polling error (retry " + pollingErrorCount + "): " + error);
                }
            }
        });
    }

    private void sendMessage() {
        String content = editMessage.getText().toString().trim();
        if (content.isEmpty() && selectedFileUris.isEmpty()) return;
        
        // Update activity time when user sends message
        lastActivityTime = System.currentTimeMillis();
        consecutiveEmptyPolls = 0; // Reset counter

        // If there are files to upload, upload them first
        if (!selectedFileUris.isEmpty()) {
            uploadAndSendFiles(content);
        } else {
            // Send text message only
            sendTextMessage(content);
        }
    }
    
    private void sendTextMessage(String content) {
        // Validate message content
        InputValidator.ValidationResult validation = InputValidator.validateMessage(content);
        if (!validation.isValid()) {
            Toast.makeText(this, validation.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Use sanitized content
        String sanitizedContent = validation.getSanitizedData();
        String parentId = replyingToMessage != null ? replyingToMessage.getId() : null;
        
        // Create temporary message with "sending" status
        TaskComment tempMessage = new TaskComment();
        String tempId = "temp_" + System.currentTimeMillis();
        tempMessage.setTempId(tempId);
        tempMessage.setComment(sanitizedContent);
        tempMessage.setSendStatus("sending");
        tempMessage.setCreatedAt(new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(new java.util.Date()));
        tempMessage.setUserId(authManager.getUserId());
        tempMessage.setParentId(parentId);
        
        // Add to list immediately to show "sending" state
        messageList.add(tempMessage);
        final int tempMessageIndex = messageList.size() - 1;
        adapter.notifyItemInserted(tempMessageIndex);
        recyclerView.scrollToPosition(tempMessageIndex);
        
        // Clear input
        editMessage.setText("");
        editMessage.setHint("Nhập tin nhắn...");
        TaskComment replyingMessage = replyingToMessage;
        replyingToMessage = null;
        hideReplyPreview();
        
        taskService.sendTaskComment(taskId, sanitizedContent, parentId, new TaskService.TaskCallback<TaskComment>() {
            @Override
            public void onSuccess(TaskComment result) {
                // Replace temp message with real message
                result.setSendStatus("sent");
                messageList.set(tempMessageIndex, result);
                adapter.notifyItemChanged(tempMessageIndex);
            }

            @Override
            public void onError(String error) {
                // Update message status to failed
                tempMessage.setSendStatus("failed");
                adapter.notifyItemChanged(tempMessageIndex);
                Toast.makeText(TaskChatActivity.this, "Không thể gửi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void uploadAndSendFiles(String textContent) {
        if (selectedFileUris.isEmpty()) return;
        
        final String finalTextContent = textContent;
        final int totalFiles = selectedFileUris.size();
        final int[] uploadedCount = {0};
        final List<String> uploadedUrls = new ArrayList<>();
        final List<Boolean> isImageList = new ArrayList<>();
        
        // Show upload progress dialog
        showUploadProgressDialog(0, totalFiles);
        
        // Check if all are images - if yes, send as one message with multiple images
        boolean allImages = true;
        for (Uri uri : selectedFileUris) {
            String fileName = getFileNameFromUri(uri);
            if (fileName != null && !isImageFile(fileName)) {
                allImages = false;
                break;
            }
        }
        final boolean finalAllImages = allImages; // Make it final for use in inner class
        
        // Validate and upload all files
        for (int i = 0; i < selectedFileUris.size(); i++) {
            final Uri fileUri = selectedFileUris.get(i);
            final int fileIndex = i;
            
            String fileName = getFileNameFromUri(fileUri);
            if (fileName == null || fileName.isEmpty()) {
                fileName = "file_" + (fileIndex + 1);
            }
            
            final String finalFileName = fileName;
            
            // Validate file before upload
            InputValidator.ValidationResult fileValidation = InputValidator.validateFile(this, fileUri, finalFileName);
            if (!fileValidation.isValid()) {
                Toast.makeText(this, "File \"" + finalFileName + "\": " + fileValidation.getMessage(), Toast.LENGTH_LONG).show();
                // Skip this file, continue with others
                uploadedCount[0]++;
                if (uploadedCount[0] >= totalFiles && !uploadedUrls.isEmpty()) {
                    // Process uploaded files
                    if (finalAllImages && uploadedUrls.size() > 1) {
                        sendMultipleImagesAsOneMessage(finalTextContent, uploadedUrls);
                    } else {
                        sendFilesAsSeparateMessages(finalTextContent, uploadedUrls, isImageList);
                    }
                } else if (uploadedCount[0] >= totalFiles && uploadedUrls.isEmpty()) {
                    Toast.makeText(this, "Không có file nào hợp lệ để gửi", Toast.LENGTH_SHORT).show();
                }
                continue;
            }
            
            // Upload file
            taskService.uploadTaskAttachment(this, taskId, fileUri, finalFileName, new TaskService.TaskCallback<String>() {
                @Override
                public void onSuccess(String fileUrl) {
                    uploadedUrls.add(fileUrl);
                    isImageList.add(isImageFile(fileUrl));
                    uploadedCount[0]++;
                    
                    // Update progress dialog
                    updateUploadProgressDialog(uploadedCount[0], totalFiles);
                    
                    // When all files are uploaded, send as one message if all are images
                    if (uploadedCount[0] >= totalFiles) {
                        dismissUploadProgressDialog();
                        if (finalAllImages && uploadedUrls.size() > 1) {
                            // Send all images in one message with grid layout
                            sendMultipleImagesAsOneMessage(finalTextContent, uploadedUrls);
                        } else {
                            // Send each file as separate message (original behavior)
                            sendFilesAsSeparateMessages(finalTextContent, uploadedUrls, isImageList);
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    uploadedCount[0]++;
                    updateUploadProgressDialog(uploadedCount[0], totalFiles);
                    
                    if (uploadedCount[0] >= totalFiles) {
                        dismissUploadProgressDialog();
                        Toast.makeText(TaskChatActivity.this, "Lỗi tải lên file: " + error, Toast.LENGTH_SHORT).show();
                        editMessage.setText("");
                        editMessage.setHint("Nhập tin nhắn...");
                        selectedFileUris.clear();
                        updateSelectedFilesPreview();
                    }
                }
            });
        }
    }
    
    private void showUploadProgressDialog(int current, int total) {
        if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
            return;
        }
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_upload_progress, null);
        TextView textTitle = dialogView.findViewById(R.id.text_upload_title);
        TextView textStatus = dialogView.findViewById(R.id.text_upload_status);
        TextView textFilename = dialogView.findViewById(R.id.text_upload_filename);
        TextView textPercent = dialogView.findViewById(R.id.text_progress_percent);
        android.widget.ProgressBar progressBar = dialogView.findViewById(R.id.progress_upload);
        
        textTitle.setText("Đang tải lên file...");
        textStatus.setText(current + " / " + total + " file");
        textFilename.setVisibility(View.GONE);
        textPercent.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        
        uploadProgressDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        uploadProgressDialog.show();
    }
    
    private void updateUploadProgressDialog(int current, int total) {
        if (uploadProgressDialog == null || !uploadProgressDialog.isShowing()) {
            return;
        }
        
        TextView textStatus = uploadProgressDialog.findViewById(R.id.text_upload_status);
        if (textStatus != null) {
            textStatus.setText(current + " / " + total + " file");
        }
    }
    
    private void dismissUploadProgressDialog() {
        if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
            uploadProgressDialog.dismiss();
            uploadProgressDialog = null;
        }
    }
    
    private void sendMultipleImagesAsOneMessage(String textContent, List<String> imageUrls) {
        // Combine all URLs into [FILE_URLS: ...] format
        StringBuilder fileUrlsBuilder = new StringBuilder("[FILE_URLS: ");
        for (int i = 0; i < imageUrls.size(); i++) {
            if (i > 0) fileUrlsBuilder.append(", ");
            fileUrlsBuilder.append(imageUrls.get(i));
        }
        fileUrlsBuilder.append("]");
        
        String commentText = textContent;
        if (!textContent.isEmpty()) {
            commentText = textContent + " " + fileUrlsBuilder.toString();
        } else {
            commentText = fileUrlsBuilder.toString();
        }
        
        String parentId = replyingToMessage != null ? replyingToMessage.getId() : null;
        taskService.sendTaskComment(taskId, commentText, parentId, new TaskService.TaskCallback<TaskComment>() {
            @Override
            public void onSuccess(TaskComment result) {
                editMessage.setText("");
                editMessage.setHint("Nhập tin nhắn...");
                selectedFileUris.clear();
                updateSelectedFilesPreview();
                replyingToMessage = null;
                messageList.add(result);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                Toast.makeText(TaskChatActivity.this, "Đã gửi " + imageUrls.size() + " hình", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskChatActivity.this, "Lỗi gửi tin nhắn: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void sendFilesAsSeparateMessages(String textContent, List<String> fileUrls, List<Boolean> isImageList) {
        final int[] successCount = {0};
        for (int i = 0; i < fileUrls.size(); i++) {
            final String fileUrl = fileUrls.get(i);
            final boolean isImage = isImageList.get(i);
            final int fileIndex = i;
            
            String type = isImage ? "image" : (isVideoFile(fileUrl) ? "video" : "file");
            String commentText = (fileIndex == 0 && !textContent.isEmpty()) ? textContent : FileIconHelper.getFileName(fileUrl);
            
            String parentId = (fileIndex == 0 && replyingToMessage != null) ? replyingToMessage.getId() : null;
            taskService.sendTaskCommentWithFile(taskId, commentText, type, fileUrl, parentId, new TaskService.TaskCallback<TaskComment>() {
                @Override
                public void onSuccess(TaskComment result) {
                    successCount[0]++;
                    messageList.add(result);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    
                    if (fileIndex == fileUrls.size() - 1) {
                        editMessage.setText("");
                        editMessage.setHint("Nhập tin nhắn...");
                        selectedFileUris.clear();
                        updateSelectedFilesPreview();
                        replyingToMessage = null;
                        hideReplyPreview();
                        Toast.makeText(TaskChatActivity.this, "Đã gửi " + fileUrls.size() + " file", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    if (fileIndex == fileUrls.size() - 1) {
                        Toast.makeText(TaskChatActivity.this, "Lỗi gửi file: " + error, Toast.LENGTH_SHORT).show();
                        editMessage.setText("");
                        editMessage.setHint("Nhập tin nhắn...");
                        selectedFileUris.clear();
                        updateSelectedFilesPreview();
                    }
                }
            });
        }
    }
    
    private void showAttachmentOptions() {
        String[] options = {"Chụp hình", "Chọn hình ảnh", "Chọn file", "Chọn video", "Chụp màn hình"};
        new AlertDialog.Builder(this)
            .setTitle("Đính kèm")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // Chụp hình
                        openCamera();
                        break;
                    case 1: // Chọn hình ảnh
                        openImagePicker();
                        break;
                    case 2: // Chọn file
                        openFilePicker();
                        break;
                    case 3: // Chọn video
                        openVideoPicker();
                        break;
                    case 4: // Chụp màn hình
                        startScreenCapture();
                        break;
                }
            })
            .show();
    }
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple selection
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), REQUEST_IMAGE_PICK);
    }
    
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple selection
        String[] mimeTypes = {"application/pdf", "application/msword", 
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "application/vnd.ms-excel", 
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            "text/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Chọn file"), REQUEST_FILE_PICK);
    }
    
    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Chọn video"), REQUEST_VIDEO_PICK);
    }
    
    private void openCamera() {
        try {
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create a file to save the image
                android.content.ContentValues values = new android.content.ContentValues();
                values.put(android.provider.MediaStore.Images.Media.TITLE, "Chat_Image_" + System.currentTimeMillis());
                values.put(android.provider.MediaStore.Images.Media.DESCRIPTION, "Image from chat");
                cameraImageUri = getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            } else {
                Toast.makeText(this, "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi mở camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void startScreenCapture() {
        // Note: Screen capture requires MediaProjection API which is complex
        // For now, show a message to use system screenshot
        Toast.makeText(this, "Vui lòng sử dụng chức năng chụp màn hình của hệ thống (Power + Volume Down)", Toast.LENGTH_LONG).show();
        // TODO: Implement MediaProjection API for automatic screen capture
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && cameraImageUri != null) {
                // Camera image captured, add to selected files and upload immediately
                selectedFileUris.clear();
                selectedFileUris.add(cameraImageUri);
                updateSelectedFilesPreview();
                // Auto-upload the captured image
                uploadAndSendFiles("");
                cameraImageUri = null;
            } else if (requestCode == REQUEST_IMAGE_PICK || requestCode == REQUEST_FILE_PICK || requestCode == REQUEST_VIDEO_PICK) {
                if (data != null) {
                    selectedFileUris.clear();
                    
                    // Check if multiple files were selected
                    if (data.getClipData() != null) {
                        // Multiple files selected
                        android.content.ClipData clipData = data.getClipData();
                        int count = clipData.getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri uri = clipData.getItemAt(i).getUri();
                            if (uri != null) {
                                selectedFileUris.add(uri);
                            }
                        }
                    } else if (data.getData() != null) {
                        // Single file selected
                        selectedFileUris.add(data.getData());
                    }
                    
                    // Update preview
                    updateSelectedFilesPreview();
                }
            }
        }
    }
    
    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        try {
            android.content.ContentResolver contentResolver = getContentResolver();
            android.database.Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (fileName == null || fileName.isEmpty()) {
            String path = uri.getPath();
            if (path != null) {
                int lastSlash = path.lastIndexOf('/');
                if (lastSlash != -1 && lastSlash < path.length() - 1) {
                    fileName = path.substring(lastSlash + 1);
                }
            }
        }
        
        return fileName;
    }
    
    private boolean isImageFile(String url) {
        if (url == null || url.isEmpty()) return false;
        String lowerUrl = url.toLowerCase();
        
        // Remove query parameters
        int queryIndex = lowerUrl.indexOf('?');
        if (queryIndex > 0) {
            lowerUrl = lowerUrl.substring(0, queryIndex);
        }
        
        return lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") ||
               lowerUrl.endsWith(".png") || lowerUrl.endsWith(".gif") ||
               lowerUrl.endsWith(".webp") || lowerUrl.endsWith(".bmp") ||
               lowerUrl.endsWith(".svg");
    }
    
    private boolean isVideoFile(String url) {
        if (url == null || url.isEmpty()) return false;
        String lowerUrl = url.toLowerCase();
        
        // Remove query parameters
        int queryIndex = lowerUrl.indexOf('?');
        if (queryIndex > 0) {
            lowerUrl = lowerUrl.substring(0, queryIndex);
        }
        
        return lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".avi") ||
               lowerUrl.endsWith(".mov") || lowerUrl.endsWith(".mkv") ||
               lowerUrl.endsWith(".3gp") || lowerUrl.endsWith(".webm");
    }
    
    public void showImageFullscreen(String imageUrl, String imageName) {
        // Tạo dialog fullscreen
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        android.view.View dialogView = android.view.LayoutInflater.from(this).inflate(R.layout.dialog_image_viewer, null);
        dialog.setContentView(dialogView);

        android.widget.ImageView imageFullscreen = dialogView.findViewById(R.id.image_fullscreen);
        TextView textImageName = dialogView.findViewById(R.id.text_image_name);
        ImageButton buttonClose = dialogView.findViewById(R.id.button_close);
        ImageButton buttonDownload = dialogView.findViewById(R.id.button_download);

        // Load image với Glide
        com.bumptech.glide.Glide.with(this)
            .load(imageUrl)
            .into(imageFullscreen);
        
        // Set file name
        if (imageName != null && !imageName.isEmpty()) {
            textImageName.setText(imageName);
        } else {
            textImageName.setText(com.example.financialmanagement.utils.FileIconHelper.getFileName(imageUrl));
        }
        
        // Nút đóng
        buttonClose.setOnClickListener(v -> dialog.dismiss());
        
        // Nút tải về
        buttonDownload.setOnClickListener(v -> {
            downloadImage(imageUrl, imageName != null ? imageName : com.example.financialmanagement.utils.FileIconHelper.getFileName(imageUrl));
            Toast.makeText(this, "Đang tải về...", Toast.LENGTH_SHORT).show();
        });
        
        dialog.show();
    }
    
    private void downloadImage(String imageUrl, String fileName) {
        try {
            android.app.DownloadManager downloadManager = (android.app.DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            android.net.Uri uri = android.net.Uri.parse(imageUrl);
            
            android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_PICTURES, fileName);
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            
            downloadManager.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tải về: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showEditMessageDialog(TaskComment message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sửa tin nhắn");
        
        final EditText input = new EditText(this);
        input.setText(message.getComment());
        input.setHint("Nhập tin nhắn...");
        input.setMinLines(3);
        input.setMaxLines(10);
        builder.setView(input);
        
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newContent = input.getText().toString().trim();
            if (!newContent.isEmpty()) {
                updateMessage(message.getId(), newContent);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        
        builder.show();
    }
    
    private void showDeleteMessageConfirmation(TaskComment message) {
        new AlertDialog.Builder(this)
            .setTitle("Xóa tin nhắn")
            .setMessage("Bạn có chắc chắn muốn xóa tin nhắn này?")
            .setPositiveButton("Xóa", (dialog, which) -> deleteMessage(message.getId()))
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void updateMessage(String commentId, String newContent) {
        taskService.updateTaskComment(commentId, newContent, null, new TaskService.TaskCallback<TaskComment>() {
            @Override
            public void onSuccess(TaskComment result) {
                // Update message in list
                for (int i = 0; i < messageList.size(); i++) {
                    if (messageList.get(i).getId().equals(commentId)) {
                        messageList.set(i, result);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
                Toast.makeText(TaskChatActivity.this, "Đã cập nhật tin nhắn", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskChatActivity.this, "Lỗi cập nhật: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void deleteMessage(String commentId) {
        taskService.deleteTaskComment(commentId, new TaskService.TaskCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Remove message from list
                for (int i = 0; i < messageList.size(); i++) {
                    if (messageList.get(i).getId().equals(commentId)) {
                        messageList.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
                Toast.makeText(TaskChatActivity.this, "Đã xóa tin nhắn", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskChatActivity.this, "Lỗi xóa: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void pinMessage(TaskComment message, boolean pin) {
        taskService.updateTaskComment(message.getId(), null, pin, new TaskService.TaskCallback<TaskComment>() {
            @Override
            public void onSuccess(TaskComment result) {
                // Update message in list and reorder
                for (int i = 0; i < messageList.size(); i++) {
                    if (messageList.get(i).getId().equals(message.getId())) {
                        messageList.set(i, result);
                        break;
                    }
                }
                // Reorder: pinned messages first
                sortMessages();
                adapter.notifyDataSetChanged();
                Toast.makeText(TaskChatActivity.this, pin ? "Đã ghim tin nhắn" : "Đã bỏ ghim tin nhắn", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskChatActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Flatten nested comments structure (comments with replies) into flat list
     * This ensures all comments including replies are in a single list with parent_id set correctly
     */
    private List<TaskComment> flattenComments(List<TaskComment> comments) {
        List<TaskComment> flatList = new ArrayList<>();
        if (comments == null) return flatList;
        
        for (TaskComment comment : comments) {
            // Add the parent comment first
            flatList.add(comment);
            
            // If this comment has replies, recursively flatten them
            List<TaskComment> replies = comment.getReplies();
            if (replies != null && !replies.isEmpty()) {
                for (TaskComment reply : replies) {
                    // Ensure parent_id is set correctly for replies
                    if (reply.getParentId() == null || reply.getParentId().isEmpty()) {
                        reply.setParentId(comment.getId());
                    }
                    
                    // Add the reply to flat list
                    flatList.add(reply);
                    
                    // Recursively process nested replies (if any)
                    List<TaskComment> nestedReplies = reply.getReplies();
                    if (nestedReplies != null && !nestedReplies.isEmpty()) {
                        List<TaskComment> flattenedNestedReplies = flattenComments(nestedReplies);
                        flatList.addAll(flattenedNestedReplies);
                    }
                }
            }
        }
        
        return flatList;
    }
    
    private void sortMessages() {
        // Sort: pinned messages first, then by created_at
        java.util.Collections.sort(messageList, (m1, m2) -> {
            boolean p1 = m1.getIsPinned();
            boolean p2 = m2.getIsPinned();
            if (p1 && !p2) return -1;
            if (!p1 && p2) return 1;
            // Both pinned or both not pinned, sort by date
            if (m1.getCreatedAt() != null && m2.getCreatedAt() != null) {
                return m1.getCreatedAt().compareTo(m2.getCreatedAt());
            }
            return 0;
        });
    }
    
    private void copyMessage(TaskComment message) {
        String content = message.getComment();
        if (content != null && !content.isEmpty()) {
            // Remove FILE_URLS tags
            if (content.contains("[FILE_URLS:")) {
                content = content.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
            }
            if (content.isEmpty()) {
                content = message.getType() != null && message.getType().equals("image") ? "Hình ảnh" : 
                         (message.getType() != null && message.getType().equals("file") ? "File đính kèm" : "Tin nhắn");
            }
        } else {
            content = message.getType() != null && message.getType().equals("image") ? "Hình ảnh" : 
                     (message.getType() != null && message.getType().equals("file") ? "File đính kèm" : "Tin nhắn");
        }
        
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Tin nhắn", content);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Đã sao chép", Toast.LENGTH_SHORT).show();
    }
    
    private void forwardMessage(TaskComment message) {
        // TODO: Implement forward to other tasks or chats
        Toast.makeText(this, "Tính năng chuyển tiếp đang được phát triển", Toast.LENGTH_SHORT).show();
    }
    
    private void resendMessage(TaskComment message) {
        // Resend the message (create a new message with same content)
        String content = message.getComment();
        if (content != null && content.contains("[FILE_URLS:")) {
            // If it has files, extract and resend
            List<String> fileUrls = extractFileUrlsFromText(content);
            if (!fileUrls.isEmpty()) {
                // Resend with files
                Toast.makeText(this, "Đang gửi lại tin nhắn...", Toast.LENGTH_SHORT).show();
                // For now, just send the text
                String textContent = content.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                if (!textContent.isEmpty() || !fileUrls.isEmpty()) {
                    taskService.sendTaskComment(taskId, textContent, null, new TaskService.TaskCallback<TaskComment>() {
                        @Override
                        public void onSuccess(TaskComment result) {
                            messageList.add(result);
                            adapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                            Toast.makeText(TaskChatActivity.this, "Đã gửi lại", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(TaskChatActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                // Just text
                taskService.sendTaskComment(taskId, content, null, new TaskService.TaskCallback<TaskComment>() {
                    @Override
                    public void onSuccess(TaskComment result) {
                        messageList.add(result);
                        adapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                        Toast.makeText(TaskChatActivity.this, "Đã gửi lại", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(TaskChatActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            // Just text
            taskService.sendTaskComment(taskId, content, null, new TaskService.TaskCallback<TaskComment>() {
                @Override
                public void onSuccess(TaskComment result) {
                    messageList.add(result);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    Toast.makeText(TaskChatActivity.this, "Đã gửi lại", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(TaskChatActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private List<String> extractFileUrlsFromText(String text) {
        List<String> urls = new ArrayList<>();
        if (text != null && text.contains("[FILE_URLS:")) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[FILE_URLS:(.*?)\\]");
            java.util.regex.Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String urlsString = matcher.group(1);
                if (urlsString != null) {
                    String[] urlArray = urlsString.split(",");
                    for (String url : urlArray) {
                        String cleanUrl = url.trim();
                        if (!cleanUrl.isEmpty()) {
                            urls.add(cleanUrl);
                        }
                    }
                }
            }
        }
        return urls;
    }
    
    // ==================== REAL-TIME CHAT METHODS ====================
    
    /**
     * Setup real-time polling to check for new messages
     * OPTIMIZED: Dynamic interval based on activity and errors
     */
    private void setupRealtimePolling() {
        realtimeHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        realtimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPolling) {
                    // Calculate smart polling interval
                    int interval = calculateSmartPollingInterval();
                    
                    // Poll for new messages
                    loadMessagesOptimized(true);
                    
                    // Schedule next poll with dynamic interval
                    realtimeHandler.postDelayed(this, interval);
                }
            }
        };
    }
    
    /**
     * Calculate smart polling interval based on activity and connection state
     */
    private int calculateSmartPollingInterval() {
        // If errors occurred, use exponential backoff
        if (pollingErrorCount > 0) {
            int backoffInterval = POLLING_INTERVAL_ACTIVE * (int) Math.pow(2, Math.min(pollingErrorCount, 4));
            return Math.min(backoffInterval, 60000); // Max 60 seconds
        }
        
        // Calculate inactive time
        long inactiveTime = System.currentTimeMillis() - lastActivityTime;
        
        // If no new messages for a while, slow down polling
        if (consecutiveEmptyPolls > 10) {
            return POLLING_INTERVAL_INACTIVE; // 10s after 10 empty polls
        }
        
        // If user inactive for > 5 minutes
        if (inactiveTime > 5 * 60 * 1000) {
            return POLLING_INTERVAL_BACKGROUND; // 30s
        }
        
        // If user inactive for > 1 minute
        if (inactiveTime > 60 * 1000) {
            return POLLING_INTERVAL_INACTIVE; // 10s
        }
        
        // Active polling
        return POLLING_INTERVAL_ACTIVE; // 3s
    }
    
    /**
     * Start polling for new messages
     */
    private void startRealtimePolling() {
        if (!isPolling) {
            isPolling = true;
            currentPollingInterval = POLLING_INTERVAL_ACTIVE; // Start with active interval
            realtimeHandler.postDelayed(realtimeRunnable, currentPollingInterval);
            android.util.Log.d("TaskChat", "Started real-time polling with interval: " + currentPollingInterval + "ms");
        }
    }
    
    /**
     * Stop polling for new messages
     */
    private void stopRealtimePolling() {
        if (isPolling) {
            isPolling = false;
            realtimeHandler.removeCallbacks(realtimeRunnable);
            android.util.Log.d("TaskChat", "Stopped real-time polling");
        }
    }
    
    /**
     * Optimized merge with early exit if no changes
     */
    private boolean mergeNewMessagesOptimized(List<TaskComment> serverMessages) {
        return mergeNewMessages(serverMessages);
    }
    
    /**
     * Merge new messages from server into local list
     * - Add new messages
     * - Update existing messages (edit, pin, etc.)
     * - Remove deleted messages
     * @return true if there were any changes
     */
    private boolean mergeNewMessages(List<TaskComment> serverMessages) {
        boolean hasChanges = false;
        int initialSize = messageList.size();
        
        // Build map of existing messages by ID for fast lookup
        java.util.Map<String, Integer> existingIds = new java.util.HashMap<>();
        for (int i = 0; i < messageList.size(); i++) {
            TaskComment msg = messageList.get(i);
            if (msg.getId() != null && !msg.getId().isEmpty()) {
                existingIds.put(msg.getId(), i);
            }
        }
        
        // Build map of server messages by ID
        java.util.Map<String, TaskComment> serverIds = new java.util.HashMap<>();
        for (TaskComment msg : serverMessages) {
            if (msg.getId() != null && !msg.getId().isEmpty()) {
                serverIds.put(msg.getId(), msg);
            }
        }
        
        // Check for new or updated messages
        for (TaskComment serverMsg : serverMessages) {
            String msgId = serverMsg.getId();
            if (msgId == null || msgId.isEmpty()) continue;
            
            if (existingIds.containsKey(msgId)) {
                // Message exists - check if it's updated
                int index = existingIds.get(msgId);
                TaskComment localMsg = messageList.get(index);
                
                // Check if message was updated (compare comment text, pinned status, etc.)
                boolean needsUpdate = false;
                if (!equals(localMsg.getComment(), serverMsg.getComment())) {
                    needsUpdate = true;
                }
                if (localMsg.getIsPinned() != serverMsg.getIsPinned()) {
                    needsUpdate = true;
                }
                
                if (needsUpdate) {
                    // Update message
                    messageList.set(index, serverMsg);
                    adapter.notifyItemChanged(index);
                    hasChanges = true;
                    android.util.Log.d("TaskChat", "Updated message: " + msgId);
                }
            } else {
                // New message - add it
                messageList.add(serverMsg);
                adapter.notifyItemInserted(messageList.size() - 1);
                hasChanges = true;
                android.util.Log.d("TaskChat", "New message: " + msgId);
                
                // Auto-scroll to new message if user is at bottom
                if (recyclerView != null) {
                    LinearLayoutManager layoutManager = 
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                        // If user is viewing last 2 messages, auto-scroll to new message
                        if (lastVisiblePosition >= messageList.size() - 3) {
                            recyclerView.smoothScrollToPosition(messageList.size() - 1);
                        }
                    }
                }
            }
        }
        
        // Check for deleted messages
        java.util.List<Integer> toRemove = new java.util.ArrayList<>();
        for (int i = 0; i < messageList.size(); i++) {
            TaskComment localMsg = messageList.get(i);
            String msgId = localMsg.getId();
            
            // Skip temporary messages (being sent)
            if (msgId == null || msgId.isEmpty() || msgId.startsWith("temp_")) {
                continue;
            }
            
            if (!serverIds.containsKey(msgId)) {
                // Message deleted on server
                toRemove.add(i);
            }
        }
        
        // Remove deleted messages (in reverse order to maintain indices)
        for (int i = toRemove.size() - 1; i >= 0; i--) {
            int index = toRemove.get(i);
            messageList.remove(index);
            adapter.notifyItemRemoved(index);
            hasChanges = true;
            android.util.Log.d("TaskChat", "Removed message at index: " + index);
        }
        
        // Re-sort if there were changes
        if (hasChanges) {
            sortMessages();
            adapter.notifyDataSetChanged();
        }
        
        // Show notification if new messages were added (and user sent them from another device)
        if (hasChanges && messageList.size() > initialSize) {
            int newCount = messageList.size() - initialSize;
            // Only show notification if user is not the sender
            boolean hasNewFromOthers = false;
            for (int i = initialSize; i < messageList.size(); i++) {
                TaskComment msg = messageList.get(i);
                if (msg.getUserId() != null && !msg.getUserId().equals(authManager.getUserId())) {
                    hasNewFromOthers = true;
                    break;
                }
            }
            
            if (hasNewFromOthers) {
                // Subtle notification (could be improved with sound/vibration)
                android.util.Log.d("TaskChat", newCount + " new message(s) from others");
            }
        }
        
        return hasChanges;
    }
    
    /**
     * Helper to compare two strings (null-safe)
     */
    private boolean equals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Start polling when activity is visible
        startRealtimePolling();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Stop polling when activity is not visible (save battery)
        stopRealtimePolling();
    }
    
    // ==================== VOICE MESSAGES ====================
    
    /**
     * Start voice recording
     */
    private void startVoiceRecording() {
        // Check microphone permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != 
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1001);
                return;
            }
        }
        
        showVoiceRecordingDialog();
    }
    
    /**
     * Show voice recording dialog
     */
    private void showVoiceRecordingDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        View recordView = getLayoutInflater().inflate(R.layout.dialog_voice_record, null);
        dialog.setContentView(recordView);
        dialog.setCancelable(false);
        
        TextView textDuration = recordView.findViewById(R.id.text_recording_duration);
        android.widget.Button btnCancel = recordView.findViewById(R.id.btn_cancel_recording);
        android.widget.Button btnSend = recordView.findViewById(R.id.btn_send_recording);
        
        // Start recording
        com.example.financialmanagement.utils.AudioRecorder recorder = 
            new com.example.financialmanagement.utils.AudioRecorder(this);
        
        recorder.startRecording(this, new com.example.financialmanagement.utils.AudioRecorder.OnRecordingListener() {
            @Override
            public void onDurationUpdate(long durationMs) {
                String formatted = com.example.financialmanagement.utils.AudioRecorder.formatDuration(durationMs);
                textDuration.setText(formatted);
            }

            @Override
            public void onRecordingComplete(String filePath, long durationMs) {
                dialog.dismiss();
                // Upload and send voice message
                uploadAndSendVoiceMessage(filePath, durationMs);
            }

            @Override
            public void onRecordingError(String error) {
                dialog.dismiss();
                Toast.makeText(TaskChatActivity.this, "Lỗi ghi âm: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Cancel button
        btnCancel.setOnClickListener(v -> {
            recorder.cancelRecording();
            dialog.dismiss();
        });
        
        // Send button
        btnSend.setOnClickListener(v -> {
            recorder.stopRecording();
            // Dialog will dismiss in onRecordingComplete
        });
        
        dialog.show();
    }
    
    /**
     * Upload and send voice message
     */
    private void uploadAndSendVoiceMessage(String filePath, long durationMs) {
        // Show uploading
        Toast.makeText(this, "Đang tải lên...", Toast.LENGTH_SHORT).show();
        
        // Upload file
        Uri fileUri = Uri.fromFile(new java.io.File(filePath));
        String fileName = "voice_" + System.currentTimeMillis() + ".m4a";
        
        taskService.uploadTaskAttachment(this, taskId, fileUri, fileName, new TaskService.TaskCallback<String>() {
            @Override
            public void onSuccess(String fileUrl) {
                // Send as voice message
                String durationText = com.example.financialmanagement.utils.AudioRecorder.formatDuration(durationMs);
                String comment = "[VOICE:" + durationText + "]"; // Format: [VOICE:MM:SS]
                
                String parentId = replyingToMessage != null ? replyingToMessage.getId() : null;
                taskService.sendTaskCommentWithFile(taskId, comment, fileUrl, "voice", parentId, 
                    new TaskService.TaskCallback<TaskComment>() {
                        @Override
                        public void onSuccess(TaskComment result) {
                            messageList.add(result);
                            adapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                            
                            // Clear reply
                            replyingToMessage = null;
                            hideReplyPreview();
                            
                            Toast.makeText(TaskChatActivity.this, "Đã gửi tin nhắn thoại", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(TaskChatActivity.this, "Lỗi gửi: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                
                // Delete temp file
                new java.io.File(filePath).delete();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskChatActivity.this, "Lỗi tải lên: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // ==================== EMOJI PICKER ====================
    
    /**
     * Show emoji picker bottom sheet
     */
    private void showEmojiPicker() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = 
            new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View emojiView = getLayoutInflater().inflate(R.layout.emoji_picker_layout, null);
        dialog.setContentView(emojiView);
        
        // Setup RecyclerView
        RecyclerView recyclerEmojis = emojiView.findViewById(R.id.recycler_emojis);
        recyclerEmojis.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 8));
        
        // Load initial emojis (Recent)
        final int[] currentCategory = {com.example.financialmanagement.utils.EmojiHelper.CATEGORY_RECENT};
        com.example.financialmanagement.adapters.EmojiAdapter adapter = 
            new com.example.financialmanagement.adapters.EmojiAdapter(
                com.example.financialmanagement.utils.EmojiHelper.getEmojis(currentCategory[0]),
                emoji -> {
                    // Insert emoji at cursor position
                    int cursorPosition = editMessage.getSelectionStart();
                    String currentText = editMessage.getText().toString();
                    String newText = currentText.substring(0, cursorPosition) + emoji + 
                                   currentText.substring(cursorPosition);
                    editMessage.setText(newText);
                    editMessage.setSelection(cursorPosition + emoji.length());
                    // Don't dismiss dialog - allow selecting multiple emojis
                }
            );
        recyclerEmojis.setAdapter(adapter);
        
        // Setup category tabs
        setupEmojiCategories(emojiView, adapter, currentCategory);
        
        dialog.show();
    }
    
    /**
     * Setup emoji category tabs
     */
    private void setupEmojiCategories(View emojiView, 
                                      com.example.financialmanagement.adapters.EmojiAdapter adapter,
                                      int[] currentCategory) {
        TextView tabRecent = emojiView.findViewById(R.id.tab_recent);
        TextView tabSmileys = emojiView.findViewById(R.id.tab_smileys);
        TextView tabPeople = emojiView.findViewById(R.id.tab_people);
        TextView tabNature = emojiView.findViewById(R.id.tab_nature);
        TextView tabFood = emojiView.findViewById(R.id.tab_food);
        TextView tabActivities = emojiView.findViewById(R.id.tab_activities);
        TextView tabTravel = emojiView.findViewById(R.id.tab_travel);
        TextView tabObjects = emojiView.findViewById(R.id.tab_objects);
        TextView tabSymbols = emojiView.findViewById(R.id.tab_symbols);
        TextView tabFlags = emojiView.findViewById(R.id.tab_flags);
        
        View.OnClickListener categoryClickListener = v -> {
            int category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_RECENT;
            if (v.getId() == R.id.tab_recent) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_RECENT;
            else if (v.getId() == R.id.tab_smileys) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_SMILEYS;
            else if (v.getId() == R.id.tab_people) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_PEOPLE;
            else if (v.getId() == R.id.tab_nature) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_NATURE;
            else if (v.getId() == R.id.tab_food) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_FOOD;
            else if (v.getId() == R.id.tab_activities) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_ACTIVITIES;
            else if (v.getId() == R.id.tab_travel) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_TRAVEL;
            else if (v.getId() == R.id.tab_objects) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_OBJECTS;
            else if (v.getId() == R.id.tab_symbols) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_SYMBOLS;
            else if (v.getId() == R.id.tab_flags) category = com.example.financialmanagement.utils.EmojiHelper.CATEGORY_FLAGS;
            
            currentCategory[0] = category;
            adapter.updateEmojis(com.example.financialmanagement.utils.EmojiHelper.getEmojis(category));
        };
        
        tabRecent.setOnClickListener(categoryClickListener);
        tabSmileys.setOnClickListener(categoryClickListener);
        tabPeople.setOnClickListener(categoryClickListener);
        tabNature.setOnClickListener(categoryClickListener);
        tabFood.setOnClickListener(categoryClickListener);
        tabActivities.setOnClickListener(categoryClickListener);
        tabTravel.setOnClickListener(categoryClickListener);
        tabObjects.setOnClickListener(categoryClickListener);
        tabSymbols.setOnClickListener(categoryClickListener);
        tabFlags.setOnClickListener(categoryClickListener);
    }
    
    // ==================== TYPING INDICATOR ====================
    
    /**
     * Setup typing indicator
     * Send typing status when user types and poll for others' typing status
     */
    private void setupTypingIndicator() {
        typingHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        
        // Listen to text changes
        editMessage.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onUserTyping();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        
        // Poll for others' typing status
        typingRunnable = new Runnable() {
            @Override
            public void run() {
                pollTypingUsers();
                typingHandler.postDelayed(this, 2000); // Poll every 2 seconds
            }
        };
        typingHandler.postDelayed(typingRunnable, 2000);
    }
    
    /**
     * Called when user is typing
     */
    private void onUserTyping() {
        if (!isUserTyping) {
            isUserTyping = true;
            // Send typing status to server
            taskService.updateTypingStatus(taskId, true, new TaskService.TaskCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    android.util.Log.d("TaskChat", "Typing status sent");
                }

                @Override
                public void onError(String error) {
                    android.util.Log.e("TaskChat", "Failed to send typing status: " + error);
                }
            });
        }
        
        // Reset stop typing timer
        if (stopTypingRunnable != null) {
            typingHandler.removeCallbacks(stopTypingRunnable);
        }
        stopTypingRunnable = new Runnable() {
            @Override
            public void run() {
                onUserStoppedTyping();
            }
        };
        typingHandler.postDelayed(stopTypingRunnable, TYPING_TIMEOUT);
    }
    
    /**
     * Called when user stopped typing
     */
    private void onUserStoppedTyping() {
        if (isUserTyping) {
            isUserTyping = false;
            // Send stop typing status to server
            taskService.updateTypingStatus(taskId, false, new TaskService.TaskCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    android.util.Log.d("TaskChat", "Stop typing status sent");
                }

                @Override
                public void onError(String error) {
                    android.util.Log.e("TaskChat", "Failed to send stop typing status: " + error);
                }
            });
        }
    }
    
    /**
     * Poll for users currently typing
     */
    private void pollTypingUsers() {
        taskService.getTypingUsers(taskId, new TaskService.TaskCallback<java.util.List<String>>() {
            @Override
            public void onSuccess(java.util.List<String> typingNames) {
                updateTypingIndicatorUI(typingNames);
            }

            @Override
            public void onError(String error) {
                // Silently fail - not critical
            }
        });
    }
    
    /**
     * Update typing indicator UI
     */
    private void updateTypingIndicatorUI(java.util.List<String> typingNames) {
        if (textTypingIndicator == null) return;
        
        if (typingNames == null || typingNames.isEmpty()) {
            textTypingIndicator.setVisibility(View.GONE);
        } else {
            textTypingIndicator.setVisibility(View.VISIBLE);
            
            String text;
            if (typingNames.size() == 1) {
                text = typingNames.get(0) + " đang soạn tin nhắn...";
            } else if (typingNames.size() == 2) {
                text = typingNames.get(0) + " và " + typingNames.get(1) + " đang soạn tin nhắn...";
            } else {
                text = typingNames.get(0) + " và " + (typingNames.size() - 1) + " người khác đang soạn tin nhắn...";
            }
            textTypingIndicator.setText(text);
        }
    }
    
    // ==================== READ RECEIPTS ====================
    
    /**
     * Setup read receipts tracking
     * Mark messages as read when they become visible
     */
    private void setupReadReceiptsTracking() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                markVisibleMessagesAsRead();
            }
        });
    }
    
    /**
     * Mark all visible messages as read
     */
    private void markVisibleMessagesAsRead() {
        if (recyclerView == null || recyclerView.getLayoutManager() == null) {
            return;
        }
        
        LinearLayoutManager layoutManager = 
            (LinearLayoutManager) recyclerView.getLayoutManager();
        
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        
        java.util.List<String> unreadMessageIds = new java.util.ArrayList<>();
        
        for (int i = firstVisiblePosition; i <= lastVisiblePosition && i < messageList.size(); i++) {
            TaskComment message = messageList.get(i);
            
            // Skip own messages
            if (message.getUserId() != null && message.getUserId().equals(authManager.getUserId())) {
                continue;
            }
            
            // Skip temporary messages
            if (message.getId() == null || message.getId().startsWith("temp_")) {
                continue;
            }
            
            // Check if already read by me
            java.util.List<String> readBy = message.getReadBy();
            if (readBy != null && readBy.contains(authManager.getUserId())) {
                continue;
            }
            
            // Add to list of messages to mark as read
            unreadMessageIds.add(message.getId());
        }
        
        // Mark messages as read (batch)
        if (!unreadMessageIds.isEmpty()) {
            taskService.markMessagesAsReadBatch(taskId, unreadMessageIds, new TaskService.TaskCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // Update local state
                    for (String msgId : unreadMessageIds) {
                        for (TaskComment msg : messageList) {
                            if (msg.getId() != null && msg.getId().equals(msgId)) {
                                if (msg.getReadBy() == null) {
                                    msg.setReadBy(new java.util.ArrayList<>());
                                }
                                if (!msg.getReadBy().contains(authManager.getUserId())) {
                                    msg.getReadBy().add(authManager.getUserId());
                                    msg.setReadCount(msg.getReadBy().size());
                                }
                            }
                        }
                    }
                    // Update UI
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    android.util.Log.d("TaskChat", "Marked " + unreadMessageIds.size() + " messages as read");
                }

                @Override
                public void onError(String error) {
                    android.util.Log.e("TaskChat", "Failed to mark messages as read: " + error);
                }
            });
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up
        stopRealtimePolling();
        
        // Stop typing indicator
        if (typingHandler != null) {
            typingHandler.removeCallbacks(typingRunnable);
            if (stopTypingRunnable != null) {
                typingHandler.removeCallbacks(stopTypingRunnable);
            }
        }
        // Send stop typing before leaving
        if (isUserTyping) {
            taskService.updateTypingStatus(taskId, false, new TaskService.TaskCallback<Void>() {
                @Override
                public void onSuccess(Void result) {}
                @Override
                public void onError(String error) {}
            });
        }
        
        if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
            uploadProgressDialog.dismiss();
        }
    }
}
