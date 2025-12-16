package com.example.financialmanagement.services;

import android.content.Context;
import android.util.Log;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import com.example.financialmanagement.utils.ErrorHandler;
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
    
    private static final String TAG = "QuoteService";
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
                    List<Quote> quotes = response.body();
                    Log.d(TAG, "getAllQuotes success - Total quotes: " + (quotes != null ? quotes.size() : 0));
                    if (quotes != null && !quotes.isEmpty()) {
                        // Log first few quotes for debugging
                        for (int i = 0; i < Math.min(3, quotes.size()); i++) {
                            Quote q = quotes.get(i);
                            String customerName = q.getCustomer() != null ? q.getCustomer().getName() : "N/A";
                            String projectName = q.getProject() != null ? q.getProject().getName() : "N/A";
                            Log.d(TAG, String.format("Quote %d: ID=%s, Customer=%s, Project=%s, Items=%d", 
                                i+1, q.getId(), customerName, projectName, 
                                q.getItems() != null ? q.getItems().size() : 0));
                        }
                    }
                    callback.onSuccess(quotes);
                } else {
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "getAllQuotes", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "getAllQuotes", t);
                callback.onError(error);
            }
        });
    }
    
    public void getQuoteById(String id, QuoteCallback callback) {
        Call<Quote> call = quoteApi.getQuoteById(id);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    Quote quote = response.body();
                    if (quote != null) {
                        callback.onSuccess(quote);
                    } else {
                        // Try to parse error from response body
                        String errorMsg = "Không thể parse dữ liệu báo giá";
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e(TAG, "Error body: " + errorBody);
                                if (errorBody.contains("validation error")) {
                                    errorMsg = "Lỗi validation: Dữ liệu báo giá không hợp lệ. Vui lòng kiểm tra lại backend.";
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        callback.onError(errorMsg);
                    }
                } else {
                    // Try to get detailed error from response body
                    String errorMsg = "Lỗi tải báo giá";
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            Log.e(TAG, "Error response body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    
                    // Check if it's a validation error (status issue)
                    if (errorBody != null && errorBody.contains("validation error") && errorBody.contains("status")) {
                        // This is a backend validation issue - try fallback: get from list
                        Log.w(TAG, "Validation error detected (status='approved' not accepted), trying fallback: get from quotes list");
                        tryGetQuoteFromList(id, callback);
                        return;
                    }
                    
                    // Parse error message
                    if (errorBody != null && !errorBody.isEmpty()) {
                        // Try to extract error message from JSON
                        if (errorBody.contains("\"detail\"")) {
                            try {
                                int detailStart = errorBody.indexOf("\"detail\"");
                                int detailValueStart = errorBody.indexOf("\"", detailStart + 8) + 1;
                                int detailValueEnd = errorBody.indexOf("\"", detailValueStart);
                                if (detailValueEnd > detailValueStart) {
                                    errorMsg = errorBody.substring(detailValueStart, detailValueEnd);
                                    // Clean up the message
                                    errorMsg = errorMsg.replace("\\n", "\n");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing detail from error body", e);
                                errorMsg = "Lỗi validation từ server";
                            }
                        } else if (errorBody.length() < 500) {
                            // If error body is short, use it directly
                            errorMsg = errorBody;
                        } else {
                            errorMsg = ErrorHandler.parseError(response);
                        }
                    } else {
                        errorMsg = ErrorHandler.parseError(response);
                    }
                    
                    ErrorHandler.logError(TAG, "getQuoteById", response);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "getQuoteById", t);
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                callback.onError(error);
            }
        });
    }
    
    /**
     * Fallback method: Try to get quote from list if direct get fails
     * This is used when backend validation rejects status "approved" in getById
     * but accepts it in getAllQuotes
     * After getting quote from list, also try to load items from getById response body
     */
    private void tryGetQuoteFromList(String quoteId, QuoteCallback callback) {
        // Get all quotes without filter (backend may not support id filter)
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        
        Call<List<Quote>> call = quoteApi.getAllQuotes(params);
        call.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Quote> quotes = response.body();
                    // Find the quote with matching ID
                    for (Quote quote : quotes) {
                        if (quote != null && quote.getId() != null && quote.getId().equals(quoteId)) {
                            Log.d(TAG, "Successfully retrieved quote from list (fallback) - ID: " + quoteId);
                            
                            // Check if quote has items, if not, try to load them from getById
                            if (quote.getItems() == null || quote.getItems().isEmpty()) {
                                Log.d(TAG, "Quote from list has no items, trying to load items from getById endpoint");
                                // Try to get items from getById endpoint (even if it fails validation)
                                tryLoadItemsFromGetById(quoteId, quote, callback);
                            } else {
                                Log.d(TAG, "Quote from list has " + quote.getItems().size() + " items");
                                callback.onSuccess(quote);
                            }
                            return;
                        }
                    }
                    Log.w(TAG, "Quote not found in list - ID: " + quoteId);
                    callback.onError("Không tìm thấy báo giá trong danh sách");
                } else {
                    String error = ErrorHandler.parseError(response);
                    Log.e(TAG, "Failed to get quotes list for fallback: " + error);
                    callback.onError("Không thể tải báo giá từ danh sách: " + error);
                }
            }
            
            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                Log.e(TAG, "Network error in fallback: " + error);
                callback.onError("Lỗi kết nối khi tải báo giá: " + error);
            }
        });
    }
    
    /**
     * Try to load items from getById endpoint even if it returns validation error
     * Backend may return items in response body even when validation fails
     */
    private void tryLoadItemsFromGetById(String quoteId, Quote quote, QuoteCallback callback) {
        Call<Quote> call = quoteApi.getQuoteById(quoteId);
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                // Even if response is not successful, try to parse items from error body
                // Backend might return quote data in error response
                if (response.isSuccessful() && response.body() != null) {
                    Quote quoteWithItems = response.body();
                    if (quoteWithItems.getItems() != null && !quoteWithItems.getItems().isEmpty()) {
                        Log.d(TAG, "Successfully loaded " + quoteWithItems.getItems().size() + " items from getById");
                        quote.setItems(quoteWithItems.getItems());
                        callback.onSuccess(quote);
                        return;
                    }
                }
                
                // If not successful, try to parse JSON from response body
                try {
                    if (response.errorBody() != null) {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "Trying to parse items from error response body");
                        
                        // Try to parse as JSON and extract items if present
                        // Note: This is a workaround - normally error body won't have quote data
                        // But we try anyway in case backend returns partial data
                        if (errorBody.contains("\"items\"") || errorBody.contains("\"quote_items\"")) {
                            // Use Gson to parse - might work if backend returns partial JSON
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                Quote partialQuote = gson.fromJson(errorBody, Quote.class);
                                if (partialQuote != null && partialQuote.getItems() != null && !partialQuote.getItems().isEmpty()) {
                                    Log.d(TAG, "Successfully parsed " + partialQuote.getItems().size() + " items from error body");
                                    quote.setItems(partialQuote.getItems());
                                    callback.onSuccess(quote);
                                    return;
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "Could not parse items from error body: " + e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Error reading error body for items: " + e.getMessage());
                }
                
                // If we can't get items, return quote without items
                Log.w(TAG, "Could not load items from getById, returning quote without items");
                callback.onSuccess(quote);
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                // If getById fails completely, just return quote without items
                Log.w(TAG, "getById failed, returning quote without items: " + t.getMessage());
                callback.onSuccess(quote);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "createQuote", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "createQuote", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "updateQuote", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "updateQuote", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "deleteQuote", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "deleteQuote", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "approveQuote", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "approveQuote", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "convertToInvoice", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "convertToInvoice", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "sendToCustomer", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "sendToCustomer", t);
                callback.onError(error);
            }
        });
    }
}
