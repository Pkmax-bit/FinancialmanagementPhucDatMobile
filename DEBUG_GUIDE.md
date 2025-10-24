# Debug Guide - Hướng dẫn Debug API

## 🔧 **Cấu hình Debug**

### 1. **Bật/Tắt Debug Logging**
Trong file `app/src/main/java/com/example/financialmanagement/config/DebugConfig.java`:

```java
// Bật/tắt từng loại debug riêng biệt
public static final boolean API_DEBUG_ENABLED = true;        // API requests/responses
public static final boolean NETWORK_DEBUG_ENABLED = true;    // Network configuration
public static final boolean AUTH_DEBUG_ENABLED = true;       // Authentication
public static final boolean QUERY_DEBUG_ENABLED = true;      // Query parameters
public static final boolean RESPONSE_DEBUG_ENABLED = true;   // Response bodies
```

### 2. **Log Tags trong Logcat**
- `API_DEBUG`: API requests và responses
- `QUERY_DEBUG`: Query parameters
- `RESPONSE_DEBUG`: Response bodies
- `NETWORK_DEBUG`: Network configuration
- `AUTH_DEBUG`: Authentication details

## 📱 **Dashboard Data Loading**

### **Các API calls được thực hiện:**

#### 1. **Total Projects Count**
```java
// API: GET /api/projects?limit=1000
// Response: List<Project>
// Display: Số lượng dự án
```

#### 2. **Total Expenses**
```java
// API: GET /api/project-expenses
// Response: List<ProjectExpense>
// Display: Tổng chi phí (sum of all expenses)
```

#### 3. **Total Revenue**
```java
// API: GET /api/projects?limit=1000
// Response: List<Project>
// Display: Tổng doanh thu (sum of project budgets)
```

#### 4. **Recent Projects**
```java
// API: GET /api/projects?limit=5&sort=created_at&order=desc
// Response: List<Project>
// Display: 5 dự án gần đây nhất
```

## 🐛 **Debug Logging Examples**

### **Request Logging:**
```
D/API_DEBUG: === API REQUEST ===
D/API_DEBUG: Method: GET
D/API_DEBUG: URL: http://192.168.1.17:3000/api/projects
D/API_DEBUG: ==================
```

### **Query Parameters:**
```
D/QUERY_DEBUG: === QUERY PARAMETERS ===
D/QUERY_DEBUG: limit = 5
D/QUERY_DEBUG: sort = created_at
D/QUERY_DEBUG: order = desc
D/QUERY_DEBUG: ========================
```

### **Response Logging:**
```
D/RESPONSE_DEBUG: === API RESPONSE ===
D/RESPONSE_DEBUG: Code: 200
D/RESPONSE_DEBUG: Message: OK
D/RESPONSE_DEBUG: Body: Total projects: 10
D/RESPONSE_DEBUG: ===================
```

### **Error Logging:**
```
E/API_DEBUGGER: === API ERROR ===
E/API_DEBUGGER: Operation: loadTotalProjects
E/API_DEBUGGER: Error: Connection timeout
E/API_DEBUGGER: ================
```

## 🔍 **Troubleshooting**

### **1. Không có dữ liệu hiển thị:**
- Kiểm tra API endpoint có hoạt động không
- Kiểm tra network connection
- Xem logcat để debug API calls

### **2. API calls thất bại:**
- Kiểm tra base URL trong `NetworkConfig.java`
- Kiểm tra server có chạy không
- Xem error logs trong logcat

### **3. Dữ liệu không chính xác:**
- Kiểm tra API response format
- Kiểm tra data parsing logic
- Xem response logs để debug

## 📊 **Dashboard Features**

### **Real-time Data Loading:**
- ✅ **Total Projects**: Đếm từ API `/projects`
- ✅ **Total Expenses**: Tính tổng từ API `/project-expenses`
- ✅ **Total Revenue**: Tính tổng budget từ API `/projects`
- ✅ **Recent Projects**: Lấy 5 dự án mới nhất

### **Error Handling:**
- ✅ Hiển thị "0" khi API lỗi
- ✅ Toast message cho user
- ✅ Debug logging cho developer

### **Performance:**
- ✅ Async API calls
- ✅ UI thread safety
- ✅ Error recovery

## 🚀 **Next Steps**

1. **Test API endpoints** với Postman/curl
2. **Check server logs** để đảm bảo API hoạt động
3. **Monitor logcat** để debug API calls
4. **Verify data format** từ server response
5. **Test error scenarios** (network issues, server down)

## 📝 **Notes**

- Debug logging chỉ hoạt động trong debug build
- Production build sẽ tự động tắt debug logging
- Có thể bật/tắt từng loại debug riêng biệt
- Tất cả API calls đều có error handling
