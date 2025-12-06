package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Task;
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
        Call<Task> call = taskApi.getTask(id);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
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

    public interface TaskApi {
        @GET("tasks") // Assuming endpoint is /api/tasks
        Call<List<Task>> getTasks();

        @GET("tasks/{id}")
        Call<Task> getTask(@Path("id") String id);
    }

    public interface TaskCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
