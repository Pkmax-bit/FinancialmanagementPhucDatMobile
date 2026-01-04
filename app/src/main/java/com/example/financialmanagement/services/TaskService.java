package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Task;
import com.example.financialmanagement.models.TaskDetailResponse;
import com.example.financialmanagement.models.TaskGroup;
import com.example.financialmanagement.models.TaskComment;
import com.example.financialmanagement.models.TaskParticipant;
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

    public interface TaskCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
