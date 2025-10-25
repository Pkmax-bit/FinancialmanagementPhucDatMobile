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
import retrofit2.http.*;

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
        @GET(NetworkConfig.Endpoints.QUOTES)
        Call<List<Quote>> getAllQuotes(@QueryMap Map<String, Object> params);
        
        @GET(NetworkConfig.Endpoints.QUOTES + "/{id}")
        Call<Quote> getQuoteById(@Path("id") String id);
        
        @POST(NetworkConfig.Endpoints.QUOTES)
        Call<Quote> createQuote(@Body Quote quote);
        
        @PUT(NetworkConfig.Endpoints.QUOTES + "/{id}")
        Call<Quote> updateQuote(@Path("id") String id, @Body Quote quote);
        
        @DELETE(NetworkConfig.Endpoints.QUOTES + "/{id}")
        Call<Void> deleteQuote(@Path("id") String id);
        
        @POST(NetworkConfig.Endpoints.QUOTES + "/{id}/approve")
        Call<Quote> approveQuote(@Path("id") String id);
        
        @POST(NetworkConfig.Endpoints.QUOTES + "/{id}/convert")
        Call<Quote> convertToInvoice(@Path("id") String id);
        
        @POST(NetworkConfig.Endpoints.QUOTES + "/{id}/send")
        Call<Quote> sendToCustomer(@Path("id") String id);
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
