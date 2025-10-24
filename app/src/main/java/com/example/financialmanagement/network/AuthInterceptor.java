package com.example.financialmanagement.network;

import android.content.Context;
import com.example.financialmanagement.auth.AuthManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

/**
 * Auth Interceptor - Interceptor xử lý authentication
 * Tự động thêm Authorization header vào các API requests
 */
public class AuthInterceptor implements Interceptor {
    
    private Context context;
    
    public AuthInterceptor(Context context) {
        this.context = context;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Lấy token từ AuthManager
        AuthManager authManager = new AuthManager(context);
        String token = authManager.getAccessToken();
        
        if (token != null && !token.isEmpty()) {
            // Thêm Authorization header
            Request newRequest = originalRequest.newBuilder()
                    .addHeader(NetworkConfig.Headers.AUTHORIZATION, "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }
        
        return chain.proceed(originalRequest);
    }
}
