# Database Integration Guide - Hướng dẫn tích hợp Database

## 🎯 **Mục đích**
Tích hợp thông tin đăng nhập từ bảng **employees** và **users** vào tab cài đặt, và đảm bảo trang tổng quan hiển thị được dữ liệu các dự án.

## 📊 **Database Schema**

### **1. Users Table**
```sql
CREATE TABLE users (
    id VARCHAR PRIMARY KEY,
    username VARCHAR UNIQUE NOT NULL,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    first_name VARCHAR,
    last_name VARCHAR,
    phone VARCHAR,
    role VARCHAR DEFAULT 'user',
    status VARCHAR DEFAULT 'active',
    employee_id VARCHAR REFERENCES employees(id),
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### **2. Employees Table**
```sql
CREATE TABLE employees (
    id VARCHAR PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    email VARCHAR UNIQUE NOT NULL,
    phone VARCHAR,
    department_id VARCHAR,
    position_id VARCHAR,
    hire_date DATE,
    salary DECIMAL,
    manager_id VARCHAR REFERENCES employees(id),
    employee_code VARCHAR UNIQUE,
    status VARCHAR DEFAULT 'active',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### **3. Projects Table**
```sql
CREATE TABLE projects (
    id VARCHAR PRIMARY KEY,
    name VARCHAR NOT NULL,
    project_code VARCHAR UNIQUE,
    description TEXT,
    status VARCHAR DEFAULT 'pending',
    priority VARCHAR DEFAULT 'medium',
    budget DECIMAL,
    start_date DATE,
    end_date DATE,
    customer_id VARCHAR REFERENCES customers(id),
    manager_id VARCHAR REFERENCES employees(id),
    hourly_rate DECIMAL,
    progress INTEGER DEFAULT 0,
    actual_cost DECIMAL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

## 🔧 **API Endpoints**

### **User Management:**
```java
// Get current user info
GET /api/users/me
Response: User object with employee details

// Get user by ID with employee
GET /api/users/{id}?include_employee=true
Response: User object with full employee information

// Update user profile
PUT /api/users/{id}
Body: User object
Response: Updated User object
```

### **Project Management:**
```java
// Get all projects
GET /api/projects?limit=1000
Response: List<Project>

// Get projects by status
GET /api/projects?status=active&limit=1000
Response: List<Project>

// Get recent projects
GET /api/projects?limit=5&sort=created_at&order=desc
Response: List<Project>
```

## 📱 **Settings Tab Integration**

### **User Information Display:**
```java
// Load user from database
userService.getCurrentUser(new UserService.UserCallback() {
    @Override
    public void onSuccess(User user) {
        // Display user info
        tvUserName.setText(user.getFullName());
        tvUserEmail.setText(user.getEmail());
        tvUserId.setText("ID: " + user.getId());
        tvUserRole.setText("Vai trò: " + user.getRole());
        
        // Display employee info if available
        if (user.getEmployee() != null) {
            Employee employee = user.getEmployee();
            String employeeInfo = "Nhân viên: " + employee.getFirstName() + " " + employee.getLastName();
            if (employee.getEmployeeCode() != null) {
                employeeInfo += " (" + employee.getEmployeeCode() + ")";
            }
            tvEmployeeInfo.setText(employeeInfo);
        }
    }
    
    @Override
    public void onError(String error) {
        // Fallback to AuthManager data
        showDefaultUserInfo();
    }
});
```

### **UI Components:**
```xml
<!-- User Profile Section -->
<TextView android:id="@+id/tv_user_name" />
<TextView android:id="@+id/tv_user_email" />
<TextView android:id="@+id/tv_user_id" />
<TextView android:id="@+id/tv_user_role" />
<TextView android:id="@+id/tv_employee_info" />
```

## 📊 **Dashboard Data Loading**

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

### **Success Feedback:**
```java
// Show success message when data loads
if (projectCount > 0) {
    Toast.makeText(getContext(), "Đã tải " + projectCount + " dự án", Toast.LENGTH_SHORT).show();
}
```

## 🐛 **Debug Logging**

### **User Information:**
```
D/AUTH_DEBUG: Loading user info from database
D/AUTH_DEBUG: User loaded successfully: Nguyễn Văn A
D/AUTH_DEBUG: User ID: 12345
D/AUTH_DEBUG: User Email: user@phucdat.com
D/AUTH_DEBUG: User Role: admin
```

### **Project Data:**
```
D/API_DEBUG: === API REQUEST ===
D/API_DEBUG: Method: GET
D/API_DEBUG: URL: http://192.168.1.17:3000/api/projects
D/API_DEBUG: ==================

D/QUERY_DEBUG: === QUERY PARAMETERS ===
D/QUERY_DEBUG: limit = 1000
D/QUERY_DEBUG: ========================

D/RESPONSE_DEBUG: === API RESPONSE ===
D/RESPONSE_DEBUG: Code: 200
D/RESPONSE_DEBUG: Message: OK
D/RESPONSE_DEBUG: Body: Total projects: 10
D/RESPONSE_DEBUG: ===================
```

## 🔍 **Error Handling**

### **Database Connection Issues:**
```java
// Fallback to AuthManager if database fails
private void showDefaultUserInfo() {
    String userId = authManager.getUserId();
    String userEmail = authManager.getUserEmail();
    String userName = authManager.getUserName();
    
    tvUserName.setText(userName != null ? userName : "Chưa có tên");
    tvUserEmail.setText(userEmail != null ? userEmail : "Chưa có email");
    tvUserId.setText(userId != null ? "ID: " + userId : "ID: Chưa có");
    tvUserRole.setText("Vai trò: Chưa xác định");
    tvEmployeeInfo.setText("Không có thông tin nhân viên");
}
```

### **API Error Handling:**
```java
// Show error messages to user
Toast.makeText(getContext(), "Lỗi tải thông tin user: " + error, Toast.LENGTH_SHORT).show();
Toast.makeText(getContext(), "Lỗi tải dự án: " + error, Toast.LENGTH_SHORT).show();
```

## 🚀 **Implementation Steps**

### **1. Database Setup:**
- ✅ Create users table
- ✅ Create employees table  
- ✅ Create projects table
- ✅ Set up foreign key relationships

### **2. API Development:**
- ✅ Implement user endpoints
- ✅ Implement project endpoints
- ✅ Add authentication middleware
- ✅ Add error handling

### **3. Android Integration:**
- ✅ Create User model
- ✅ Create UserService
- ✅ Update SettingsFragment
- ✅ Update DashboardFragment
- ✅ Add debug logging

### **4. Testing:**
- ✅ Test user login flow
- ✅ Test project data loading
- ✅ Test error scenarios
- ✅ Test UI updates

## 📝 **Data Flow**

### **Settings Tab:**
```
User opens Settings
    ↓
loadUserInfo()
    ↓
userService.getCurrentUser()
    ↓
API: GET /api/users/me
    ↓
Display user + employee info
```

### **Dashboard Tab:**
```
User opens Dashboard
    ↓
loadDashboardStats()
    ↓
loadTotalProjects()
    ↓
API: GET /api/projects?limit=1000
    ↓
Display project statistics
```

## 🎯 **Expected Results**

### **Settings Tab:**
- ✅ **User Name**: Full name from database
- ✅ **User Email**: Email from database
- ✅ **User ID**: Database ID
- ✅ **User Role**: Role from database
- ✅ **Employee Info**: Employee details if linked

### **Dashboard Tab:**
- ✅ **Total Projects**: Count from database
- ✅ **Active Projects**: Count with status=active
- ✅ **Completed Projects**: Count with status=completed
- ✅ **Recent Projects**: 5 most recent projects
- ✅ **Success Messages**: Toast notifications

## 🔧 **Troubleshooting**

### **Common Issues:**
1. **Database connection fails**: Check API endpoints
2. **User not found**: Check authentication
3. **Projects not loading**: Check API parameters
4. **UI not updating**: Check UI thread safety

### **Debug Steps:**
1. Check logcat for API debug logs
2. Verify database connectivity
3. Check API response format
4. Verify data parsing logic
