package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.utils.ApiDebugger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.Map;

/**
 * QR Login Service - Xử lý đăng nhập bằng QR code
 */
public class QRLoginService {
    
    private QRLoginApi qrLoginApi;
    private Context context;
    
    public QRLoginService(Context context) {
        this.context = context;
        this.qrLoginApi = ApiClient.getRetrofit(context).create(QRLoginApi.class);
    }
    
    /**
     * Verify QR code session (with optional secret token)
     */
    public void verifyQRCode(String sessionId, String secretToken, QRVerifyCallback callback) {
        JsonObject request = new JsonObject();
        request.addProperty("session_id", sessionId);
        if (secretToken != null) {
            request.addProperty("secret_token", secretToken);
        }
        
        ApiDebugger.logRequest("POST", "QR Verify", null, request.toString());
        
        Call<QRVerifyResponse> call = qrLoginApi.verifyQRCode(request);
        call.enqueue(new Callback<QRVerifyResponse>() {
            @Override
            public void onResponse(Call<QRVerifyResponse> call, Response<QRVerifyResponse> response) {
                ApiDebugger.logResponse(response.code(), response.message(), 
                    response.body() != null ? response.body().toString() : "null");
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = "Lỗi xác thực QR code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JsonObject errorJson = new Gson().fromJson(errorBody, JsonObject.class);
                            if (errorJson.has("detail")) {
                                error = errorJson.get("detail").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<QRVerifyResponse> call, Throwable t) {
                ApiDebugger.logError("verifyQRCode", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Verify QR code session (without secret token - for Android login)
     */
    public void verifyQRCode(String sessionId, QRVerifyCallback callback) {
        verifyQRCode(sessionId, null, callback);
    }
    
    /**
     * Generate QR code for web login (mobile to web)
     */
    public void generateQRCode(QRGenerateCallback callback) {
        ApiDebugger.logRequest("POST", "QR Generate (Mobile)", null, "{}");
        
        Call<QRGenerateResponse> call = qrLoginApi.generateQRCode();
        call.enqueue(new Callback<QRGenerateResponse>() {
            @Override
            public void onResponse(Call<QRGenerateResponse> call, Response<QRGenerateResponse> response) {
                ApiDebugger.logResponse(response.code(), response.message(), 
                    response.body() != null ? response.body().toString() : "null");
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = "Lỗi tạo QR code: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JsonObject errorJson = new Gson().fromJson(errorBody, JsonObject.class);
                            if (errorJson.has("detail")) {
                                error = errorJson.get("detail").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<QRGenerateResponse> call, Throwable t) {
                ApiDebugger.logError("generateQRCode", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Check QR status (for mobile polling)
     */
    public void checkQRStatus(String sessionId, QRStatusCallback callback) {
        ApiDebugger.logRequest("GET", "QR Status", null, "session_id: " + sessionId);
        
        Call<QRStatusResponse> call = qrLoginApi.checkQRStatus(sessionId);
        call.enqueue(new Callback<QRStatusResponse>() {
            @Override
            public void onResponse(Call<QRStatusResponse> call, Response<QRStatusResponse> response) {
                ApiDebugger.logResponse(response.code(), response.message(), 
                    response.body() != null ? response.body().toString() : "null");
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = "Lỗi kiểm tra trạng thái QR: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JsonObject errorJson = new Gson().fromJson(errorBody, JsonObject.class);
                            if (errorJson.has("detail")) {
                                error = errorJson.get("detail").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<QRStatusResponse> call, Throwable t) {
                ApiDebugger.logError("checkQRStatus", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Complete QR login and get access token
     */
    public void completeQRLogin(String sessionId, String secretToken, QRCompleteCallback callback) {
        JsonObject request = new JsonObject();
        request.addProperty("session_id", sessionId);
        request.addProperty("secret_token", secretToken);
        
        ApiDebugger.logRequest("POST", "QR Complete", null, request.toString());
        
        Call<QRVerifyResponse> call = qrLoginApi.completeQRLogin(request);
        call.enqueue(new Callback<QRVerifyResponse>() {
            @Override
            public void onResponse(Call<QRVerifyResponse> call, Response<QRVerifyResponse> response) {
                ApiDebugger.logResponse(response.code(), response.message(), 
                    response.body() != null ? response.body().toString() : "null");
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = "Lỗi hoàn tất đăng nhập: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JsonObject errorJson = new Gson().fromJson(errorBody, JsonObject.class);
                            if (errorJson.has("detail")) {
                                error = errorJson.get("detail").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<QRVerifyResponse> call, Throwable t) {
                ApiDebugger.logError("completeQRLogin", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    // API Interface
    interface QRLoginApi {
        @POST("auth/qr/verify")
        Call<QRVerifyResponse> verifyQRCode(@Body JsonObject request);
        
        @POST("auth/qr/complete")
        Call<QRVerifyResponse> completeQRLogin(@Body JsonObject request);
        
        @POST("auth/qr/mobile/generate")
        Call<QRGenerateResponse> generateQRCode();
        
        @GET("auth/qr/mobile/status/{session_id}")
        Call<QRStatusResponse> checkQRStatus(@Path("session_id") String sessionId);
    }
    
    // Response Models
    public static class QRVerifyResponse {
        private boolean success;
        private String access_token;
        private String token_type;
        private Integer expires_in;
        private String message;
        
        public boolean isSuccess() { return success; }
        public String getAccessToken() { return access_token; }
        public String getTokenType() { return token_type; }
        public Integer getExpiresIn() { return expires_in; }
        public String getMessage() { return message; }
    }
    
    public static class QRGenerateResponse {
        private String session_id;
        private String qr_code;
        private String expires_at;
        
        public String getSessionId() { return session_id; }
        public String getQrCode() { return qr_code; }
        public String getExpiresAt() { return expires_at; }
    }
    
    public static class QRStatusResponse {
        private String status;
        private String verified_at;
        private String user_email;
        
        public String getStatus() { return status; }
        public String getVerifiedAt() { return verified_at; }
        public String getUserEmail() { return user_email; }
    }
    
    // Callbacks
    public interface QRVerifyCallback {
        void onSuccess(QRVerifyResponse response);
        void onError(String error);
    }
    
    public interface QRCompleteCallback {
        void onSuccess(QRVerifyResponse response);
        void onError(String error);
    }
    
    public interface QRGenerateCallback {
        void onSuccess(QRGenerateResponse response);
        void onError(String error);
    }
    
    public interface QRStatusCallback {
        void onSuccess(QRStatusResponse response);
        void onError(String error);
    }
}

