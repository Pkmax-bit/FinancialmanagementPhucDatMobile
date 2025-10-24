# Authentication Fix Guide - Hướng dẫn sửa lỗi xác thực

## 🚨 **Vấn đề hiện tại:**
```
403 Forbidden - {"detail":"Not authenticated"}
```

## 🔍 **Nguyên nhân:**
1. **Token không được gửi**: Authorization header không được thêm vào request
2. **Token hết hạn**: Access token đã hết hạn
3. **Token không hợp lệ**: Token không đúng format hoặc bị corrupt
4. **User chưa đăng nhập**: AuthManager không có thông tin đăng nhập

## ✅ **Giải pháp đã triển khai:**

### **1. Enhanced AuthInterceptor:**
```java
@Override
public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();
    
    // Lấy token từ AuthManager
    AuthManager authManager = new AuthManager(context);
    String token = authManager.getAccessToken();
    
    // Debug logging
    ApiDebugger.logAuth("AuthInterceptor - Token available: " + (token != null && !token.isEmpty()), 
        authManager.isLoggedIn());
    ApiDebugger.logAuth("AuthInterceptor - Request URL: " + originalRequest.url(), 
        authManager.isLoggedIn());
    
    if (token != null && !token.isEmpty()) {
        // Thêm Authorization header
        Request newRequest = originalRequest.newBuilder()
                .addHeader(NetworkConfig.Headers.AUTHORIZATION, "Bearer " + token)
                .build();
        
        ApiDebugger.logAuth("AuthInterceptor - Added Bearer token", true);
        Response response = chain.proceed(newRequest);
        
        // Check for authentication errors
        if (response.code() == 401 || response.code() == 403) {
            ApiDebugger.logAuth("AuthInterceptor - Authentication failed: " + response.code(), false);
            // Token might be expired, clear it
            authManager.logout();
        }
        
        return response;
    } else {
        ApiDebugger.logAuth("AuthInterceptor - No token available, proceeding without auth", false);
    }
    
    return chain.proceed(originalRequest);
}
```

### **2. Authentication Test trong ExpensesFragment:**
```java
private void testAuthentication() {
    // Test authentication by checking if user is logged in and has valid token
    String token = authManager.getAccessToken();
    String userId = authManager.getUserId();
    String userRole = authManager.getUserRole();
    
    ApiDebugger.logAuth("=== AUTHENTICATION TEST ===", authManager.isLoggedIn());
    ApiDebugger.logAuth("Is Logged In: " + authManager.isLoggedIn(), authManager.isLoggedIn());
    ApiDebugger.logAuth("Token Available: " + (token != null && !token.isEmpty()), authManager.isLoggedIn());
    ApiDebugger.logAuth("User ID: " + userId, authManager.isLoggedIn());
    ApiDebugger.logAuth("User Role: " + userRole, authManager.isLoggedIn());
    ApiDebugger.logAuth("=========================", authManager.isLoggedIn());
    
    if (!authManager.isLoggedIn() || token == null || token.isEmpty()) {
        ApiDebugger.logAuth("Authentication test failed - user not properly logged in", false);
        Toast.makeText(getContext(), "Lỗi xác thực: Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
    }
}
```

### **3. Error Handling cho Authentication:**
```java
@Override
public void onError(String error) {
    if (getActivity() != null) {
        getActivity().runOnUiThread(() -> {
            // Check if it's an authentication error
            if (error.contains("403") || error.contains("401") || error.contains("Not authenticated")) {
                ApiDebugger.logAuth("Authentication failed, redirecting to login", false);
                Toast.makeText(getContext(), "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                
                // Clear auth data and redirect to login
                authManager.logout();
                
                // TODO: Navigate to login activity
                // Intent intent = new Intent(getContext(), LoginActivity.class);
                // startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Lỗi tải chi phí: " + error, Toast.LENGTH_SHORT).show();
            }
            
            ApiDebugger.logError("loadExpenses", new Exception(error));
        });
    }
}
```

### **4. Pre-authentication Check:**
```java
private void loadExpenses() {
    // Check authentication first
    if (!authManager.isLoggedIn()) {
        ApiDebugger.logAuth("User not logged in, cannot load expenses", false);
        Toast.makeText(getContext(), "Vui lòng đăng nhập để xem chi phí", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // ... rest of the method
}
```

## 🔍 **Debug Logging:**

### **Authentication Test Logs:**
```
D/AUTH_DEBUG: === AUTHENTICATION TEST ===
D/AUTH_DEBUG: Is Logged In: true/false
D/AUTH_DEBUG: Token Available: true/false
D/AUTH_DEBUG: User ID: user123
D/AUTH_DEBUG: User Role: admin/manager/employee
D/AUTH_DEBUG: =========================
```

### **AuthInterceptor Logs:**
```
D/AUTH_DEBUG: AuthInterceptor - Token available: true/false
D/AUTH_DEBUG: AuthInterceptor - Request URL: http://192.168.1.17:3000/api/project-expenses
D/AUTH_DEBUG: AuthInterceptor - Added Bearer token
D/AUTH_DEBUG: AuthInterceptor - Authentication failed: 403
```

### **Error Handling Logs:**
```
D/AUTH_DEBUG: Authentication failed, redirecting to login
E/API_DEBUGGER: === API ERROR ===
E/API_DEBUGGER: Operation: loadExpenses
E/API_DEBUGGER: Error: Lỗi tải danh sách chi phí: 403
```

## 🛠️ **Troubleshooting Steps:**

### **Step 1: Check Authentication Status**
```java
// In ExpensesFragment or any other fragment
AuthManager authManager = new AuthManager(getContext());
boolean isLoggedIn = authManager.isLoggedIn();
String token = authManager.getAccessToken();
String userId = authManager.getUserId();
String userRole = authManager.getUserRole();

Log.d("AUTH_DEBUG", "Is Logged In: " + isLoggedIn);
Log.d("AUTH_DEBUG", "Token: " + token);
Log.d("AUTH_DEBUG", "User ID: " + userId);
Log.d("AUTH_DEBUG", "User Role: " + userRole);
```

### **Step 2: Check Token Format**
```java
// Token should be in format: "Bearer <actual_token>"
String token = authManager.getAccessToken();
if (token != null && !token.isEmpty()) {
    Log.d("AUTH_DEBUG", "Token length: " + token.length());
    Log.d("AUTH_DEBUG", "Token starts with: " + token.substring(0, Math.min(10, token.length())));
}
```

### **Step 3: Test API Call with Manual Headers**
```java
// Test if API works with manual authentication
Request.Builder requestBuilder = new Request.Builder()
    .url("http://192.168.1.17:3000/api/project-expenses")
    .addHeader("Authorization", "Bearer " + token)
    .addHeader("Content-Type", "application/json");

Request request = requestBuilder.build();
// Make the request and check response
```

## 🔧 **Common Issues & Solutions:**

### **Issue 1: Token not being sent**
**Solution**: Check AuthInterceptor is properly added to OkHttpClient
```java
// In ApiClient.java
OkHttpClient.Builder builder = new OkHttpClient.Builder();
builder.addInterceptor(new AuthInterceptor(context));
```

### **Issue 2: Token expired**
**Solution**: Implement token refresh or redirect to login
```java
if (response.code() == 401 || response.code() == 403) {
    // Token expired, clear auth data
    authManager.logout();
    // Redirect to login
}
```

### **Issue 3: User not logged in**
**Solution**: Check authentication before API calls
```java
if (!authManager.isLoggedIn()) {
    Toast.makeText(context, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
    return;
}
```

### **Issue 4: Wrong API endpoint**
**Solution**: Verify API endpoint and authentication requirements
```java
// Check if endpoint requires authentication
// Some endpoints might be public, others require auth
```

## 📱 **User Experience:**

### **Authentication Flow:**
1. **Login Required**: User must be logged in to access expenses
2. **Token Validation**: App checks if token is valid before API calls
3. **Error Handling**: Clear error messages for authentication failures
4. **Auto Logout**: Automatic logout when token expires
5. **Redirect to Login**: Seamless redirect to login screen

### **Error Messages:**
- ✅ **"Vui lòng đăng nhập để xem chi phí"** - When user not logged in
- ✅ **"Lỗi xác thực: Vui lòng đăng nhập lại"** - When authentication test fails
- ✅ **"Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."** - When token expired

## 🚀 **Expected Results:**

### **Successful Authentication:**
```
D/AUTH_DEBUG: === AUTHENTICATION TEST ===
D/AUTH_DEBUG: Is Logged In: true
D/AUTH_DEBUG: Token Available: true
D/AUTH_DEBUG: User ID: user123
D/AUTH_DEBUG: User Role: admin
D/AUTH_DEBUG: =========================
D/AUTH_DEBUG: AuthInterceptor - Token available: true
D/AUTH_DEBUG: AuthInterceptor - Added Bearer token
D/API_DEBUG: --> GET http://192.168.1.17:3000/api/project-expenses
D/API_DEBUG: <-- 200 OK http://192.168.1.17:3000/api/project-expenses
```

### **Failed Authentication:**
```
D/AUTH_DEBUG: === AUTHENTICATION TEST ===
D/AUTH_DEBUG: Is Logged In: false
D/AUTH_DEBUG: Token Available: false
D/AUTH_DEBUG: User ID: null
D/AUTH_DEBUG: User Role: null
D/AUTH_DEBUG: =========================
D/AUTH_DEBUG: Authentication test failed - user not properly logged in
```

## 📋 **Next Steps:**

1. **Test the app** with the enhanced authentication handling
2. **Check debug logs** to see authentication status
3. **Verify token format** and API endpoint requirements
4. **Implement login flow** if user is not authenticated
5. **Add token refresh** mechanism if needed

Authentication issues should now be properly handled with comprehensive logging and error handling! 🎉
