package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Task;
import com.example.financialmanagement.models.TaskDetailResponse;
import com.example.financialmanagement.models.TaskGroup;
import com.example.financialmanagement.models.TaskComment;
import com.example.financialmanagement.models.TaskParticipant;
import com.example.financialmanagement.models.AssigneeWithRole;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;
import java.util.List;

public class TaskService {
    private TaskApi taskApi;

    public TaskService(Context context) {
        this.taskApi = ApiClient.getRetrofit(context).create(TaskApi.class);
    }

    public void getTasks(final TaskCallback<List<Task>> callback) {
        Call<List<Task>> call = taskApi.getTasks();
        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void getTask(String id, final TaskCallback<Task> callback) {
        taskApi.getTaskDetail(id).enqueue(new Callback<TaskDetailResponse>() {
            @Override
            public void onResponse(Call<TaskDetailResponse> call, Response<TaskDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getTask());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TaskDetailResponse> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void getTaskDetail(String id, final TaskCallback<TaskDetailResponse> callback) {
        taskApi.getTaskDetail(id).enqueue(new Callback<TaskDetailResponse>() {
            @Override
            public void onResponse(Call<TaskDetailResponse> call, Response<TaskDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TaskDetailResponse> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void getTaskGroups(final TaskCallback<List<TaskGroup>> callback) {
        taskApi.getTaskGroups().enqueue(new Callback<List<TaskGroup>>() {
            @Override
            public void onResponse(Call<List<TaskGroup>> call, Response<List<TaskGroup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<TaskGroup>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void getTasksByGroup(String groupId, final TaskCallback<List<Task>> callback) {
        taskApi.getTasksByGroup(groupId).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void getTaskComments(String taskId, final TaskCallback<List<TaskComment>> callback) {
        taskApi.getTaskComments(taskId).enqueue(new Callback<List<TaskComment>>() {
            @Override
            public void onResponse(Call<List<TaskComment>> call, Response<List<TaskComment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<TaskComment>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void sendTaskComment(String taskId, String content, final TaskCallback<TaskComment> callback) {
        sendTaskComment(taskId, content, null, callback);
    }
    
    public void sendTaskComment(String taskId, String content, String parentId, final TaskCallback<TaskComment> callback) {
        TaskComment comment = new TaskComment();
        comment.setComment(content);
        comment.setType("text");
        if (parentId != null && !parentId.isEmpty()) {
            comment.setParentId(parentId);
        }
        
        taskApi.sendTaskComment(taskId, comment).enqueue(new Callback<TaskComment>() {
            @Override
            public void onResponse(Call<TaskComment> call, Response<TaskComment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TaskComment> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }
    
    public void sendTaskCommentWithFile(String taskId, String content, String type, String fileUrl, final TaskCallback<TaskComment> callback) {
        sendTaskCommentWithFile(taskId, content, type, fileUrl, null, callback);
    }
    
    public void sendTaskCommentWithFile(String taskId, String content, String type, String fileUrl, String parentId, final TaskCallback<TaskComment> callback) {
        TaskComment comment = new TaskComment();
        comment.setComment(content);
        comment.setType(type);
        comment.setFileUrl(fileUrl);
        if (parentId != null && !parentId.isEmpty()) {
            comment.setParentId(parentId);
        }
        
        taskApi.sendTaskComment(taskId, comment).enqueue(new Callback<TaskComment>() {
            @Override
            public void onResponse(Call<TaskComment> call, Response<TaskComment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TaskComment> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }
    
    public void updateTaskComment(String commentId, String content, Boolean isPinned, final TaskCallback<TaskComment> callback) {
        TaskComment comment = new TaskComment();
        if (content != null) {
            comment.setComment(content);
        }
        if (isPinned != null) {
            comment.setIsPinned(isPinned);
        }
        
        taskApi.updateTaskComment(commentId, comment).enqueue(new Callback<TaskComment>() {
            @Override
            public void onResponse(Call<TaskComment> call, Response<TaskComment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TaskComment> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }
    
    public void deleteTaskComment(String commentId, final TaskCallback<Void> callback) {
        taskApi.deleteTaskComment(commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public interface TaskApi {
        @GET("tasks")
        Call<List<Task>> getTasks();

        @GET("tasks")
        Call<List<Task>> getTasksByGroup(@Query("group_id") String groupId);

        @GET("tasks/{id}")
        Call<TaskDetailResponse> getTaskDetail(@Path("id") String id);

        @GET("tasks/groups")
        Call<List<TaskGroup>> getTaskGroups();

        @GET("tasks/{id}/comments")
        Call<List<TaskComment>> getTaskComments(@Path("id") String taskId);

        @POST("tasks/{id}/comments")
        Call<TaskComment> sendTaskComment(@Path("id") String taskId, @Body TaskComment comment);
        
        @PUT("tasks/comments/{comment_id}")
        Call<TaskComment> updateTaskComment(@Path("comment_id") String commentId, @Body TaskComment comment);
        
        @DELETE("tasks/comments/{comment_id}")
        Call<Void> deleteTaskComment(@Path("comment_id") String commentId);
        
        @POST("tasks/{task_id}/comments/{comment_id}/read")
        Call<Void> markMessageAsRead(@Path("task_id") String taskId, @Path("comment_id") String commentId);
        
        @POST("tasks/{task_id}/comments/batch-read")
        Call<Void> markMessagesAsReadBatch(@Path("task_id") String taskId, @Body java.util.Map<String, Object> body);
        
        @POST("tasks/{task_id}/typing")
        Call<Void> updateTypingStatus(@Path("task_id") String taskId, @Body java.util.Map<String, Boolean> body);
        
        @GET("tasks/{task_id}/typing")
        Call<java.util.List<java.util.Map<String, Object>>> getTypingUsers(@Path("task_id") String taskId);

        @GET("tasks/{id}/participants")
        Call<List<TaskParticipant>> getTaskParticipants(@Path("id") String taskId);

        // Checklist endpoints
        @GET("tasks/{taskId}/checklists")
        Call<java.util.List<com.example.financialmanagement.models.TaskChecklist>> getTaskChecklists(@Path("taskId") String taskId);

        @POST("tasks/{taskId}/checklists")
        Call<com.example.financialmanagement.models.TaskChecklist> createChecklist(
            @Path("taskId") String taskId,
            @Body ChecklistCreateRequest request
        );

        @POST("tasks/checklists/{checklistId}/items")
        Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> createChecklistItem(
            @Path("checklistId") String checklistId,
            @Body ChecklistItemCreateRequest request
        );

        @PUT("tasks/checklist-items/{itemId}")
        Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> updateChecklistItem(
            @Path("itemId") String itemId,
            @Body ChecklistItemUpdateRequest request
        );
        
        @PUT("tasks/checklists/{checklistId}")
        Call<com.example.financialmanagement.models.TaskChecklist> updateChecklist(
            @Path("checklistId") String checklistId,
            @Body ChecklistCreateRequest request
        );
        
        @DELETE("tasks/checklists/{checklistId}")
        Call<okhttp3.ResponseBody> deleteChecklist(
            @Path("checklistId") String checklistId
        );
        
        @DELETE("tasks/checklist-items/{itemId}")
        Call<okhttp3.ResponseBody> deleteChecklistItem(
            @Path("itemId") String itemId
        );

        @Multipart
        @POST("tasks/{taskId}/attachments")
        Call<TaskAttachmentResponse> uploadTaskAttachment(
            @Path("taskId") String taskId,
            @Part okhttp3.MultipartBody.Part file,
            @Query("checklist_item_id") String checklistItemId
        );
        
        @POST("tasks/{taskId}/comments/{commentId}/react")
        Call<com.example.financialmanagement.models.MessageReaction> reactToMessage(
            @Path("taskId") String taskId,
            @Path("commentId") String commentId,
            @Body java.util.Map<String, String> reactionData
        );
        
        @GET("tasks/{taskId}/comments/{commentId}/reactions")
        Call<java.util.List<com.example.financialmanagement.models.MessageReaction>> getMessageReactions(
            @Path("taskId") String taskId,
            @Path("commentId") String commentId
        );
        
        @PUT("tasks/{id}")
        Call<Task> updateTaskStatus(@Path("id") String id, @Body java.util.Map<String, String> body);
    }

    public static class ChecklistItemCreateRequest {
        public String content;
        public String assignee_id;
        public Integer sort_order;
        public java.util.List<ChecklistItemAssignment> assignments;

        public ChecklistItemCreateRequest(String content, String assigneeId, Integer sortOrder) {
            this.content = content;
            this.assignee_id = assigneeId;
            this.sort_order = sortOrder;
            this.assignments = new java.util.ArrayList<>();
        }

        public ChecklistItemCreateRequest(String content, String assigneeId, Integer sortOrder, java.util.List<ChecklistItemAssignment> assignments) {
            this.content = content;
            this.assignee_id = assigneeId;
            this.sort_order = sortOrder;
            this.assignments = assignments != null ? assignments : new java.util.ArrayList<>();
        }
    }

    public static class ChecklistItemAssignment {
        public String employee_id;
        public String responsibility_type;

        public ChecklistItemAssignment(String employeeId, String responsibilityType) {
            this.employee_id = employeeId;
            this.responsibility_type = responsibilityType;
        }
    }

    public static class ChecklistCreateRequest {
        public String title;

        public ChecklistCreateRequest(String title) {
            this.title = title;
        }
    }

    public static class ChecklistItemUpdateRequest {
        public Boolean is_completed;
        public String content;

        public ChecklistItemUpdateRequest(Boolean isCompleted) {
            this.is_completed = isCompleted;
            this.content = null;
        }
        
        public ChecklistItemUpdateRequest(Boolean isCompleted, String content) {
            this.is_completed = isCompleted;
            this.content = content;
        }
        
        public ChecklistItemUpdateRequest(String content) {
            this.is_completed = null;
            this.content = content;
        }
    }

    public void getTaskParticipants(String taskId, final TaskCallback<List<TaskParticipant>> callback) {
        taskApi.getTaskParticipants(taskId).enqueue(new Callback<List<TaskParticipant>>() {
            @Override
            public void onResponse(Call<List<TaskParticipant>> call, Response<List<TaskParticipant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<TaskParticipant>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void getTaskChecklists(String taskId, final TaskCallback<java.util.List<com.example.financialmanagement.models.TaskChecklist>> callback) {
        taskApi.getTaskChecklists(taskId).enqueue(new Callback<java.util.List<com.example.financialmanagement.models.TaskChecklist>>() {
            @Override
            public void onResponse(Call<java.util.List<com.example.financialmanagement.models.TaskChecklist>> call, Response<java.util.List<com.example.financialmanagement.models.TaskChecklist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<java.util.List<com.example.financialmanagement.models.TaskChecklist>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void createChecklistItem(String checklistId, String content, String description, String assigneeId, final TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> callback) {
        ChecklistItemCreateRequest request = new ChecklistItemCreateRequest(content, assigneeId, 0);
        taskApi.createChecklistItem(checklistId, request).enqueue(new Callback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
            @Override
            public void onResponse(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Response<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    // Model for upload attachment response
    public static class TaskAttachmentResponse {
        public String id;
        public String file_name;
        public String original_file_name;
        public String file_url;
        public String file_type;
        public long file_size;
        public String uploaded_by_name;
        public String created_at;
    }

    public void uploadTaskAttachment(android.content.Context context, String taskId, android.net.Uri fileUri, String fileName, final TaskCallback<String> callback) {
        uploadTaskAttachment(context, taskId, fileUri, fileName, null, callback);
    }
    
    public void uploadTaskAttachment(android.content.Context context, String taskId, android.net.Uri fileUri, String fileName, String checklistItemId, final TaskCallback<String> callback) {
        // Create multipart request body
        try {
            // Đọc file đúng cách - sử dụng ByteArrayOutputStream để đọc toàn bộ file
            java.io.InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                callback.onError("Không thể mở file");
                return;
            }
            
            java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] fileBytes = byteArrayOutputStream.toByteArray();
            inputStream.close();
            byteArrayOutputStream.close();

            // Detect MIME type from file extension
            String mimeType = getMimeTypeFromFileName(fileName);
            if (mimeType == null) {
                // Try to get MIME type from ContentResolver
                mimeType = context.getContentResolver().getType(fileUri);
                if (mimeType == null) {
                    mimeType = "application/octet-stream"; // fallback
                }
            }

            // Log for debugging
            android.util.Log.d("UPLOAD_DEBUG", "File: " + fileName + ", MIME: " + mimeType + ", Size: " + fileBytes.length);

            // Tạo RequestBody với MediaType đúng
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse(mimeType);
            if (mediaType == null) {
                mediaType = okhttp3.MediaType.parse("application/octet-stream");
            }
            okhttp3.RequestBody fileBody = okhttp3.RequestBody.create(mediaType, fileBytes);
            
            // Tạo MultipartBody.Part với filename đúng
            // Lưu ý: Retrofit sẽ tự động thêm Content-Disposition header
            okhttp3.MultipartBody.Part filePart = okhttp3.MultipartBody.Part.createFormData("file", fileName, fileBody);

            taskApi.uploadTaskAttachment(taskId, filePart, checklistItemId).enqueue(new Callback<TaskAttachmentResponse>() {
                @Override
                public void onResponse(Call<TaskAttachmentResponse> call, Response<TaskAttachmentResponse> response) {
                    android.util.Log.d("UPLOAD_DEBUG", "Response code: " + response.code() + ", message: " + response.message());
                    if (response.isSuccessful() && response.body() != null) {
                        // Return the file_url from response, loại bỏ dấu ? ở cuối nếu có
                        String fileUrl = response.body().file_url;
                        if (fileUrl != null && fileUrl.endsWith("?")) {
                            fileUrl = fileUrl.substring(0, fileUrl.length() - 1);
                        }
                        callback.onSuccess(fileUrl);
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            android.util.Log.e("UPLOAD_DEBUG", "Error body: " + errorBody);
                            callback.onError("Upload failed: " + response.message() + " - " + errorBody);
                        } catch (Exception e) {
                            callback.onError("Upload failed: " + response.message());
                        }
                    }
                }

                @Override
                public void onFailure(Call<TaskAttachmentResponse> call, Throwable t) {
                    android.util.Log.e("UPLOAD_DEBUG", "Network error: " + t.getMessage());
                    callback.onError("Upload error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            android.util.Log.e("UPLOAD_DEBUG", "File read error: " + e.getMessage());
            callback.onError("File read error: " + e.getMessage());
        }
    }

    private String getMimeTypeFromFileName(String fileName) {
        if (fileName == null) return null;

        String extension = fileName.toLowerCase();
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (extension.endsWith(".png")) {
            return "image/png";
        } else if (extension.endsWith(".gif")) {
            return "image/gif";
        } else if (extension.endsWith(".webp")) {
            return "image/webp";
        } else if (extension.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (extension.endsWith(".pdf")) {
            return "application/pdf";
        } else if (extension.endsWith(".doc")) {
            return "application/msword";
        } else if (extension.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (extension.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (extension.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (extension.endsWith(".csv")) {
            return "text/csv";
        } else if (extension.endsWith(".txt")) {
            return "text/plain";
        } else if (extension.endsWith(".rtf")) {
            return "application/rtf";
        } else if (extension.endsWith(".ppt")) {
            return "application/vnd.ms-powerpoint";
        } else if (extension.endsWith(".pptx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else if (extension.endsWith(".zip")) {
            return "application/zip";
        } else if (extension.endsWith(".rar")) {
            return "application/vnd.rar";
        }

        return null;
    }

    public void createChecklistItemWithAssignments(String checklistId, String content, java.util.List<AssigneeWithRole> assignees, final TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> callback) {
        // Convert AssigneeWithRole to ChecklistItemAssignment
        java.util.List<ChecklistItemAssignment> assignments = new java.util.ArrayList<>();
        String primaryAssigneeId = null;

        for (AssigneeWithRole assignee : assignees) {
            assignments.add(new ChecklistItemAssignment(assignee.getEmployeeId(), assignee.getResponsibilityType()));

            // Set primary assignee (first 'responsible' or 'accountable')
            if (primaryAssigneeId == null && ("responsible".equals(assignee.getResponsibilityType()) || "accountable".equals(assignee.getResponsibilityType()))) {
                primaryAssigneeId = assignee.getEmployeeId();
            }
        }

        ChecklistItemCreateRequest request = new ChecklistItemCreateRequest(content, primaryAssigneeId, 0, assignments);
        taskApi.createChecklistItem(checklistId, request).enqueue(new Callback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
            @Override
            public void onResponse(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Response<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void createChecklist(String taskId, String title, final TaskCallback<com.example.financialmanagement.models.TaskChecklist> callback) {
        ChecklistCreateRequest request = new ChecklistCreateRequest(title);
        taskApi.createChecklist(taskId, request).enqueue(new Callback<com.example.financialmanagement.models.TaskChecklist>() {
            @Override
            public void onResponse(Call<com.example.financialmanagement.models.TaskChecklist> call, Response<com.example.financialmanagement.models.TaskChecklist> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.financialmanagement.models.TaskChecklist> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void updateChecklistItemCompletion(String itemId, boolean isCompleted, final TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> callback) {
        ChecklistItemUpdateRequest request = new ChecklistItemUpdateRequest(isCompleted);
        taskApi.updateChecklistItem(itemId, request).enqueue(new Callback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
            @Override
            public void onResponse(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Response<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }
    
    public void updateChecklistItemContent(String itemId, String content, final TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> callback) {
        ChecklistItemUpdateRequest request = new ChecklistItemUpdateRequest(content);
        taskApi.updateChecklistItem(itemId, request).enqueue(new Callback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
            @Override
            public void onResponse(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Response<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void updateChecklist(String checklistId, String title, final TaskCallback<com.example.financialmanagement.models.TaskChecklist> callback) {
        ChecklistCreateRequest request = new ChecklistCreateRequest(title);
        taskApi.updateChecklist(checklistId, request).enqueue(new Callback<com.example.financialmanagement.models.TaskChecklist>() {
            @Override
            public void onResponse(Call<com.example.financialmanagement.models.TaskChecklist> call, Response<com.example.financialmanagement.models.TaskChecklist> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.financialmanagement.models.TaskChecklist> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }
    
    public void deleteChecklist(String checklistId, final TaskCallback<String> callback) {
        taskApi.deleteChecklist(checklistId).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Checklist deleted successfully");
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }
    
    public void deleteChecklistItem(String itemId, final TaskCallback<String> callback) {
        taskApi.deleteChecklistItem(itemId).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Checklist item deleted successfully");
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }
    
    public void updateChecklistItemFull(String itemId, String content, Boolean isCompleted, final TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> callback) {
        ChecklistItemUpdateRequest request = new ChecklistItemUpdateRequest(isCompleted, content);
        taskApi.updateChecklistItem(itemId, request).enqueue(new Callback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
            @Override
            public void onResponse(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Response<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    /**
     * Mark message as read
     */
    public void markMessageAsRead(String taskId, String commentId, final TaskCallback<Void> callback) {
        Call<Void> call = taskApi.markMessageAsRead(taskId, commentId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    /**
     * Mark multiple messages as read (batch)
     */
    public void markMessagesAsReadBatch(String taskId, List<String> messageIds, final TaskCallback<Void> callback) {
        // Create request body
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("message_ids", messageIds);
        
        Call<Void> call = taskApi.markMessagesAsReadBatch(taskId, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    /**
     * Update typing status (user is typing or stopped typing)
     */
    public void updateTypingStatus(String taskId, boolean isTyping, final TaskCallback<Void> callback) {
        java.util.Map<String, Boolean> body = new java.util.HashMap<>();
        body.put("is_typing", isTyping);
        
        Call<Void> call = taskApi.updateTypingStatus(taskId, body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    /**
     * Get list of users currently typing
     */
    public void getTypingUsers(String taskId, final TaskCallback<java.util.List<String>> callback) {
        Call<java.util.List<java.util.Map<String, Object>>> call = taskApi.getTypingUsers(taskId);
        call.enqueue(new Callback<java.util.List<java.util.Map<String, Object>>>() {
            @Override
            public void onResponse(Call<java.util.List<java.util.Map<String, Object>>> call, Response<java.util.List<java.util.Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<String> typingNames = new java.util.ArrayList<>();
                    for (java.util.Map<String, Object> user : response.body()) {
                        String name = (String) user.get("user_name");
                        if (name == null || name.isEmpty()) {
                            name = (String) user.get("employee_name");
                        }
                        if (name != null && !name.isEmpty()) {
                            typingNames.add(name);
                        }
                    }
                    callback.onSuccess(typingNames);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<java.util.List<java.util.Map<String, Object>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    /**
     * React to a message (toggle reaction)
     */
    public void reactToMessage(String taskId, String commentId, String emoji, TaskCallback<com.example.financialmanagement.models.MessageReaction> callback) {
        java.util.Map<String, String> reactionData = new java.util.HashMap<>();
        reactionData.put("emoji", emoji);
        
        Call<com.example.financialmanagement.models.MessageReaction> call = taskApi.reactToMessage(taskId, commentId, reactionData);
        
        call.enqueue(new Callback<com.example.financialmanagement.models.MessageReaction>() {
            @Override
            public void onResponse(Call<com.example.financialmanagement.models.MessageReaction> call, Response<com.example.financialmanagement.models.MessageReaction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to react: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.financialmanagement.models.MessageReaction> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    /**
     * Get reactions for a message
     */
    public void getMessageReactions(String taskId, String commentId, TaskCallback<java.util.List<com.example.financialmanagement.models.MessageReaction>> callback) {
        Call<java.util.List<com.example.financialmanagement.models.MessageReaction>> call = taskApi.getMessageReactions(taskId, commentId);
        
        call.enqueue(new Callback<java.util.List<com.example.financialmanagement.models.MessageReaction>>() {
            @Override
            public void onResponse(Call<java.util.List<com.example.financialmanagement.models.MessageReaction>> call, Response<java.util.List<com.example.financialmanagement.models.MessageReaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get reactions: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<java.util.List<com.example.financialmanagement.models.MessageReaction>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    /**
     * Hoàn thành nhiệm vụ (cập nhật status thành "completed")
     */
    public void completeTask(String taskId, final TaskCallback<Void> callback) {
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("status", "completed");
        
        taskApi.updateTaskStatus(taskId, body).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }
    
    public interface TaskCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
