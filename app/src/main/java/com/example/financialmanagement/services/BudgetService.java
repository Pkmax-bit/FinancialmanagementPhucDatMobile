package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Budget;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Budget Service - API service cho quản lý ngân sách
 * Tương ứng với API endpoint /api/budgets
 */
public class BudgetService {
    
    private BudgetApi budgetApi;
    
    public interface BudgetCallback {
        void onSuccess(List<Budget> budgets);
        void onSuccess(Budget budget);
        void onSuccess();
        void onError(String error);
    }
    
    public interface BudgetApi {
        Call<List<Budget>> getAllBudgets(Map<String, Object> params);
        Call<Budget> getBudgetById(String id);
        Call<Budget> createBudget(Budget budget);
        Call<Budget> updateBudget(String id, Budget budget);
        Call<Void> deleteBudget(String id);
        Call<Budget> approveBudget(String id);
        Call<Budget> allocateBudget(String id, Map<String, Object> allocation);
        Call<Budget> trackBudget(String id);
    }
    
    public BudgetService(Context context) {
        budgetApi = ApiClient.getRetrofit(context).create(BudgetApi.class);
    }
    
    public void getAllBudgets(Map<String, Object> params, BudgetCallback callback) {
        Call<List<Budget>> call = budgetApi.getAllBudgets(params);
        call.enqueue(new Callback<List<Budget>>() {
            @Override
            public void onResponse(Call<List<Budget>> call, Response<List<Budget>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải ngân sách: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<Budget>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void getBudgetById(String id, BudgetCallback callback) {
        Call<Budget> call = budgetApi.getBudgetById(id);
        call.enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải ngân sách: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void createBudget(Budget budget, BudgetCallback callback) {
        Call<Budget> call = budgetApi.createBudget(budget);
        call.enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tạo ngân sách: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void updateBudget(String id, Budget budget, BudgetCallback callback) {
        Call<Budget> call = budgetApi.updateBudget(id, budget);
        call.enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi cập nhật ngân sách: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void deleteBudget(String id, BudgetCallback callback) {
        Call<Void> call = budgetApi.deleteBudget(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Lỗi xóa ngân sách: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void approveBudget(String id, BudgetCallback callback) {
        Call<Budget> call = budgetApi.approveBudget(id);
        call.enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi duyệt ngân sách: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void allocateBudget(String id, Map<String, Object> allocation, BudgetCallback callback) {
        Call<Budget> call = budgetApi.allocateBudget(id, allocation);
        call.enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi phân bổ ngân sách: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void trackBudget(String id, BudgetCallback callback) {
        Call<Budget> call = budgetApi.trackBudget(id);
        call.enqueue(new Callback<Budget>() {
            @Override
            public void onResponse(Call<Budget> call, Response<Budget> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi theo dõi ngân sách: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Budget> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
