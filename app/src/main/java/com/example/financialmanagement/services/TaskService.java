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
        TaskComment comment = new TaskComment();
        comment.setComment(content);
        comment.setType("text");
        
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

    public interface TaskCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
