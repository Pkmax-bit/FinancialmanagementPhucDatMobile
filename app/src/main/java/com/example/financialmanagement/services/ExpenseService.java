package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.ProjectExpense;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

/**
 * Expense Service - Service quản lý chi phí
 * Xử lý các API calls liên quan đến chi phí dự án
 */
public class ExpenseService {
    
    private Context context;
    private ExpenseApi expenseApi;
    
    public ExpenseService(Context context) {
        this.context = context;
        this.expenseApi = ApiClient.getRetrofit(context).create(ExpenseApi.class);
    }
    
    /**
     * Lấy tất cả chi phí
     */
    public void getAllExpenses(ExpenseCallback callback) {
        getAllExpenses(null, callback);
    }
    
    /**
     * Lấy chi phí với parameters
     */
    public void getAllExpenses(Map<String, Object> params, ExpenseCallback callback) {
        Call<List<ProjectExpense>> call = expenseApi.getExpenses(params);
        call.enqueue(new Callback<List<ProjectExpense>>() {
            @Override
            public void onResponse(Call<List<ProjectExpense>> call, Response<List<ProjectExpense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải danh sách chi phí: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<ProjectExpense>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Lấy chi phí theo ID
     */
    public void getExpense(String expenseId, ExpenseCallback callback) {
        Call<ProjectExpense> call = expenseApi.getExpense(expenseId);
        call.enqueue(new Callback<ProjectExpense>() {
            @Override
            public void onResponse(Call<ProjectExpense> call, Response<ProjectExpense> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải chi phí: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ProjectExpense> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Tạo chi phí mới
     */
    public void createExpense(ProjectExpense expense, ExpenseCallback callback) {
        Call<ProjectExpense> call = expenseApi.createExpense(expense);
        call.enqueue(new Callback<ProjectExpense>() {
            @Override
            public void onResponse(Call<ProjectExpense> call, Response<ProjectExpense> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tạo chi phí: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ProjectExpense> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Cập nhật chi phí
     */
    public void updateExpense(String expenseId, ProjectExpense expense, ExpenseCallback callback) {
        Call<ProjectExpense> call = expenseApi.updateExpense(expenseId, expense);
        call.enqueue(new Callback<ProjectExpense>() {
            @Override
            public void onResponse(Call<ProjectExpense> call, Response<ProjectExpense> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi cập nhật chi phí: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ProjectExpense> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Xóa chi phí
     */
    public void deleteExpense(String expenseId, ExpenseCallback callback) {
        Call<Void> call = expenseApi.deleteExpense(expenseId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Lỗi xóa chi phí: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Lấy chi phí theo dự án
     */
    public void getExpensesByProject(String projectId, ExpenseCallback callback) {
        Call<List<ProjectExpense>> call = expenseApi.getExpensesByProject(projectId);
        call.enqueue(new Callback<List<ProjectExpense>>() {
            @Override
            public void onResponse(Call<List<ProjectExpense>> call, Response<List<ProjectExpense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải chi phí dự án: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<ProjectExpense>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Expense API Interface
     */
    public interface ExpenseApi {
        @GET(NetworkConfig.Endpoints.EXPENSES)
        Call<List<ProjectExpense>> getExpenses(@QueryMap Map<String, Object> params);
        
        @GET(NetworkConfig.Endpoints.EXPENSE_DETAIL)
        Call<ProjectExpense> getExpense(@Path("id") String expenseId);
        
        @POST(NetworkConfig.Endpoints.EXPENSES)
        Call<ProjectExpense> createExpense(@Body ProjectExpense expense);
        
        @PUT(NetworkConfig.Endpoints.EXPENSE_DETAIL)
        Call<ProjectExpense> updateExpense(@Path("id") String expenseId, @Body ProjectExpense expense);
        
        @DELETE(NetworkConfig.Endpoints.EXPENSE_DETAIL)
        Call<Void> deleteExpense(@Path("id") String expenseId);
        
        @GET(NetworkConfig.Endpoints.EXPENSES)
        Call<List<ProjectExpense>> getExpensesByProject(@Query("project_id") String projectId);
    }
    
    /**
     * Expense Callback Interface
     */
    public interface ExpenseCallback {
        void onSuccess(List<ProjectExpense> expenses);
        void onSuccess(ProjectExpense expense);
        void onError(String error);
        
        // Default implementations to avoid ambiguous calls
        default void onSuccess() {
            onSuccess((List<ProjectExpense>) null);
        }
    }
}
