package com.example.financialmanagement.network;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * API Client - Quản lý Retrofit client
 * Cấu hình và khởi tạo Retrofit cho API calls
 */
public class ApiClient {
    
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;
    
    /**
     * Lấy Retrofit instance với URL mặc định
     */
    public static Retrofit getRetrofit(Context context) {
        return getRetrofit(context, NetworkConfig.BASE_URL);
    }
    
    /**
     * Lấy Retrofit instance với URL tùy chỉnh
     */
    public static Retrofit getRetrofit(Context context, String baseUrl) {
        if (retrofit == null || !retrofit.baseUrl().toString().equals(baseUrl)) {
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
    
    /**
     * Lấy OkHttpClient instance
     */
    public static OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            // Enhanced logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    // Log to Android Logcat with custom tag
                    android.util.Log.d("API_DEBUG", message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Custom debug interceptor for query parameters
            okhttp3.Interceptor debugInterceptor = new okhttp3.Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws java.io.IOException {
                    okhttp3.Request request = chain.request();
                    
                    // Log request details
                    android.util.Log.d("API_REQUEST", "URL: " + request.url());
                    android.util.Log.d("API_REQUEST", "Method: " + request.method());
                    android.util.Log.d("API_REQUEST", "Headers: " + request.headers());
                    
                    // Log query parameters
                    if (request.url().query() != null) {
                        android.util.Log.d("API_QUERY", "Query: " + request.url().query());
                    }
                    
                    // Log request body if present
                    if (request.body() != null) {
                        android.util.Log.d("API_BODY", "Request Body: " + request.body().toString());
                    }
                    
                    okhttp3.Response response = chain.proceed(request);
                    
                    // Log response details
                    android.util.Log.d("API_RESPONSE", "Code: " + response.code());
                    android.util.Log.d("API_RESPONSE", "Message: " + response.message());
                    android.util.Log.d("API_RESPONSE", "Headers: " + response.headers());
                    
                    return response;
                }
            };
            
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .addInterceptor(debugInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }
    
    /**
     * Reset client (dùng khi thay đổi cấu hình)
     */
    public static void resetClient() {
        retrofit = null;
        okHttpClient = null;
    }
}
