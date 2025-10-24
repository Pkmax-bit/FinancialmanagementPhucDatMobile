# Project Management Dashboard Guide

## 🎯 **Mục đích**
Ứng dụng Android chủ yếu dùng để **quản lý dự án**. Trang Dashboard hiển thị thống kê dự án thay vì tài chính.

## 📊 **Dashboard Statistics**

### **1. Tổng dự án (Total Projects)**
```java
// API: GET /api/projects?limit=1000
// Hiển thị: Số lượng tất cả dự án
// Label: "Tổng dự án"
```

### **2. Dự án đang hoạt động (Active Projects)**
```java
// API: GET /api/projects?limit=1000&status=active
// Hiển thị: Số lượng dự án đang hoạt động
// Label: "Dự án đang hoạt động"
```

### **3. Dự án hoàn thành (Completed Projects)**
```java
// API: GET /api/projects?limit=1000&status=completed
// Hiển thị: Số lượng dự án đã hoàn thành
// Label: "Dự án hoàn thành"
```

### **4. Dự án gần đây (Recent Projects)**
```java
// API: GET /api/projects?limit=5&sort=created_at&order=desc
// Hiển thị: 5 dự án mới nhất
// RecyclerView: RecentProjectsAdapter
```

## 🔧 **API Calls**

### **Project Statistics:**
```java
// 1. Total Projects
Map<String, Object> params = new HashMap<>();
params.put("limit", 1000);
projectService.getAllProjects(params, callback);

// 2. Active Projects  
Map<String, Object> params = new HashMap<>();
params.put("limit", 1000);
params.put("status", "active");
projectService.getAllProjects(params, callback);

// 3. Completed Projects
Map<String, Object> params = new HashMap<>();
params.put("limit", 1000);
params.put("status", "completed");
projectService.getAllProjects(params, callback);

// 4. Recent Projects
Map<String, Object> params = new HashMap<>();
params.put("limit", 5);
params.put("sort", "created_at");
params.put("order", "desc");
projectService.getAllProjects(params, callback);
```

## 🐛 **Debug Logging**

### **Request Logging:**
```
D/API_DEBUG: === API REQUEST ===
D/API_DEBUG: Method: GET
D/API_DEBUG: URL: http://192.168.1.17:3000/api/projects
D/API_DEBUG: ==================

D/QUERY_DEBUG: === QUERY PARAMETERS ===
D/QUERY_DEBUG: limit = 1000
D/QUERY_DEBUG: status = active
D/QUERY_DEBUG: ========================
```

### **Response Logging:**
```
D/RESPONSE_DEBUG: === API RESPONSE ===
D/RESPONSE_DEBUG: Code: 200
D/RESPONSE_DEBUG: Message: OK
D/RESPONSE_DEBUG: Body: Active projects: 5
D/RESPONSE_DEBUG: ===================
```

## 🎨 **UI Layout**

### **Welcome Section:**
```xml
<TextView android:text="Chào mừng trở lại!" />
<TextView android:text="Tổng quan quản lý dự án" />
```

### **Statistics Cards:**
```xml
<!-- Card 1: Total Projects -->
<TextView android:id="@+id/tv_total_projects" />
<TextView android:text="Tổng dự án" />

<!-- Card 2: Active Projects -->
<TextView android:id="@+id/tv_total_expenses" />
<TextView android:text="Dự án đang hoạt động" />

<!-- Card 3: Completed Projects -->
<TextView android:id="@+id/tv_total_revenue" />
<TextView android:text="Dự án hoàn thành" />
```

### **Recent Projects Section:**
```xml
<RecyclerView android:id="@+id/rv_recent_projects" />
```

## 🔍 **Error Handling**

### **API Error Scenarios:**
```java
// 1. Network Error
onError("Lỗi kết nối: " + t.getMessage());
// Display: "0" in UI

// 2. Server Error
onError("Lỗi tải danh sách dự án: " + response.code());
// Display: "0" in UI

// 3. Empty Response
if (projects == null || projects.isEmpty()) {
    // Display: "0" in UI
}
```

### **Fallback Values:**
- **Total Projects**: "0" nếu API lỗi
- **Active Projects**: "0" nếu API lỗi
- **Completed Projects**: "0" nếu API lỗi
- **Recent Projects**: Empty list nếu API lỗi

## 📱 **User Experience**

### **Loading States:**
- ✅ **Async Loading**: Tất cả data được load bất đồng bộ
- ✅ **UI Thread Safety**: UI updates trên main thread
- ✅ **Error Recovery**: Hiển thị "0" khi API lỗi
- ✅ **Debug Feedback**: Logcat logs cho debugging

### **Performance:**
- ✅ **Efficient API Calls**: Chỉ gọi API khi cần
- ✅ **Caching**: Có thể implement caching sau
- ✅ **Error Handling**: Graceful error handling
- ✅ **User Feedback**: Toast messages cho errors

## 🚀 **Next Steps**

### **1. Project Status Management:**
- Implement project status filtering
- Add project status indicators
- Create project status change workflow

### **2. Project Details:**
- Navigate to project detail screen
- Show project progress
- Display project timeline

### **3. Project Actions:**
- Create new project
- Edit existing project
- Delete project
- Archive project

### **4. Advanced Features:**
- Project search and filtering
- Project categories/tags
- Project team management
- Project deadline tracking

## 📝 **Notes**

- **Focus**: Ứng dụng tập trung vào quản lý dự án
- **API**: Sử dụng project endpoints thay vì expense endpoints
- **UI**: Labels và messages phù hợp với project management
- **Debug**: Comprehensive logging cho troubleshooting
- **Error Handling**: Graceful fallbacks cho tất cả scenarios

## 🔧 **Troubleshooting**

### **Common Issues:**
1. **"Query map was null"**: Đã sửa bằng cách sử dụng empty HashMap
2. **API connection errors**: Check network và server status
3. **Empty data**: Check API response format
4. **UI not updating**: Check UI thread safety

### **Debug Steps:**
1. Check logcat for API debug logs
2. Verify API endpoints are working
3. Check network connectivity
4. Verify data parsing logic
