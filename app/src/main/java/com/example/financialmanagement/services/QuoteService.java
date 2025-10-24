package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Quote Service - API service cho quản lý báo giá
 * Tương ứng với API endpoint /api/quotes
 */
public class QuoteService {
    
    private QuoteApi quoteApi;
    
    public interface QuoteCallback {
        void onSuccess(List<Quote> quotes);
        void onSuccess(Quote quote);
        void onSuccess();
        void onError(String error);
    }
    
    public interface QuoteApi {
        Call<List<Quote>> getAllQuotes(Map<String, Object> params);
        Call<Quote> getQuoteById(String id);
        Call<Quote> createQuote(Quote quote);
        Call<Quote> updateQuote(String id, Quote quote);
        Call<Void> deleteQuote(String id);
        Call<Quote> approveQuote(String id);
        Call<Quote> convertToInvoice(String id);
        Call<Quote> sendToCustomer(String id);
    }
    
    public QuoteService(Context context) {
        quoteApi = ApiClient.getRetrofit(context).create(QuoteApi.class);
    }
    
    public void getAllQuotes(Map<String, Object> params, QuoteCallback callback) {
        Call<List<Quote>> call = quoteApi.getAllQuotes(params);
        call.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải báo giá: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void getQuoteById(String id, QuoteCallback callback) {
        Call<Quote> call = quoteApi.getQuoteById(id);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải báo giá: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void createQuote(Quote quote, QuoteCallback callback) {
        Call<Quote> call = quoteApi.createQuote(quote);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tạo báo giá: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void updateQuote(String id, Quote quote, QuoteCallback callback) {
        Call<Quote> call = quoteApi.updateQuote(id, quote);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi cập nhật báo giá: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void deleteQuote(String id, QuoteCallback callback) {
        Call<Void> call = quoteApi.deleteQuote(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Lỗi xóa báo giá: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void approveQuote(String id, QuoteCallback callback) {
        Call<Quote> call = quoteApi.approveQuote(id);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi duyệt báo giá: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void convertToInvoice(String id, QuoteCallback callback) {
        Call<Quote> call = quoteApi.convertToInvoice(id);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi chuyển đổi báo giá: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void sendToCustomer(String id, QuoteCallback callback) {
        Call<Quote> call = quoteApi.sendToCustomer(id);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi gửi báo giá: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
