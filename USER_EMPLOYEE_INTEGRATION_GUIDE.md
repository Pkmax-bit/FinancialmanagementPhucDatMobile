# User & Employee Integration Guide - Hướng dẫn tích hợp thông tin User và Employee

## 🎯 **Mục đích**
Truyền thông tin người dùng dựa vào dữ liệu đăng nhập từ bảng **users** và **employees** vào phần cài đặt, hiển thị tên, email, ID, vai trò và thông tin nhân viên.

## 📊 **Database Schema**

### **1. Users Table:**
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

### **2. Employees Table:**
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

## 🔧 **API Integration**

### **User Service với Employee Details:**
```java
// Lấy thông tin user với employee details
public void getCurrentUser(UserCallback callback) {
    Map<String, Object> params = new HashMap<>();
    params.put("include_employee", true);
    
    Call<User> call = userApi.getCurrentUser(params);
    call.enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful() && response.body() != null) {
                callback.onSuccess(response.body());
            } else {
                callback.onError("Lỗi tải thông tin user: " + response.code());
            }
        }
        
        @Override
        public void onFailure(Call<User> call, Throwable t) {
            callback.onError("Lỗi kết nối: " + t.getMessage());
        }
    });
}
```

### **API Endpoints:**
```java
// Get current user with employee details
GET /api/users/me?include_employee=true

// Response format
{
  "id": "user123",
  "username": "john_doe",
  "email": "john@phucdat.com",
  "first_name": "John",
  "last_name": "Doe",
  "role": "admin",
  "employee_id": "emp123",
  "employee": {
    "id": "emp123",
    "first_name": "John",
    "last_name": "Doe",
    "employee_code": "EMP001",
    "department_id": "dept001",
    "position_id": "pos001",
    "status": "active"
  }
}
```

## 📱 **Settings Fragment Integration**

### **Display User Information:**
```java
private void displayUserInfo(User user) {
    // Display basic user information from users table
    tvUserName.setText(user.getFullName());
    tvUserEmail.setText(user.getEmail());
    tvUserId.setText("ID: " + user.getId());
    tvUserRole.setText("Vai trò: " + (user.getRole() != null ? user.getRole() : "Chưa xác định"));
    
    // Display employee information from employees table
    if (user.getEmployee() != null) {
        Employee employee = user.getEmployee();
        String employeeInfo = "Nhân viên: " + employee.getFirstName() + " " + employee.getLastName();
        if (employee.getEmployeeCode() != null) {
            employeeInfo += " (" + employee.getEmployeeCode() + ")";
        }
        if (employee.getDepartment() != null) {
            employeeInfo += "\nPhòng ban: " + employee.getDepartmentId();
        }
        if (employee.getPosition() != null) {
            employeeInfo += "\nChức vụ: " + employee.getPositionId();
        }
        tvEmployeeInfo.setText(employeeInfo);
    }
}
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

## 🔍 **Debug Logging**

### **User Information Logging:**
```
D/AUTH_DEBUG: Loading user info from database
D/AUTH_DEBUG: User loaded successfully: John Doe
D/AUTH_DEBUG: User Name: John Doe
D/AUTH_DEBUG: User Email: john@phucdat.com
D/AUTH_DEBUG: User ID: user123
D/AUTH_DEBUG: User Role: admin
```

### **Employee Information Logging:**
```
D/AUTH_DEBUG: Employee loaded: John Doe
D/AUTH_DEBUG: Employee Code: EMP001
D/AUTH_DEBUG: Department: dept001
D/AUTH_DEBUG: Position: pos001
```

### **API Request Logging:**
```
D/API_DEBUG: === API REQUEST ===
D/API_DEBUG: Method: GET
D/API_DEBUG: URL: http://192.168.1.17:3000/api/users/me
D/API_DEBUG: ==================

D/QUERY_DEBUG: === QUERY PARAMETERS ===
D/QUERY_DEBUG: include_employee = true
D/QUERY_DEBUG: ========================
```

## 🎯 **Expected Results**

### **Settings Tab Display:**
- ✅ **User Name**: Tên đầy đủ từ bảng users
- ✅ **User Email**: Email từ bảng users
- ✅ **User ID**: ID từ bảng users
- ✅ **User Role**: Vai trò từ bảng users
- ✅ **Employee Info**: Thông tin nhân viên từ bảng employees

### **Employee Information:**
- ✅ **Employee Name**: Tên nhân viên
- ✅ **Employee Code**: Mã nhân viên
- ✅ **Department ID**: ID phòng ban
- ✅ **Position ID**: ID chức vụ

## 🔧 **Error Handling**

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
@Override
public void onError(String error) {
    if (getActivity() != null) {
        getActivity().runOnUiThread(() -> {
            showDefaultUserInfo();
            ApiDebugger.logError("loadUserInfo", new Exception(error));
            Toast.makeText(getContext(), "Lỗi tải thông tin user: " + error, Toast.LENGTH_SHORT).show();
        });
    }
}
```

## 📊 **Data Flow**

### **Settings Tab Data Loading:**
```
User opens Settings
    ↓
loadUserInfo()
    ↓
userService.getCurrentUser()
    ↓
API: GET /api/users/me?include_employee=true
    ↓
Display user + employee info
```

### **Database Query:**
```sql
-- Get user with employee details
SELECT 
    u.id, u.username, u.email, u.first_name, u.last_name, u.role,
    e.id as employee_id, e.first_name as emp_first_name, 
    e.last_name as emp_last_name, e.employee_code,
    e.department_id, e.position_id
FROM users u
LEFT JOIN employees e ON u.employee_id = e.id
WHERE u.id = ?
```

## 🚀 **Implementation Steps**

### **1. Database Setup:**
- ✅ Create users table
- ✅ Create employees table
- ✅ Set up foreign key relationship (users.employee_id → employees.id)

### **2. API Development:**
- ✅ Implement user endpoints
- ✅ Add include_employee parameter
- ✅ Add authentication middleware
- ✅ Add error handling

### **3. Android Integration:**
- ✅ Update UserService
- ✅ Update SettingsFragment
- ✅ Add debug logging
- ✅ Add error handling

### **4. Testing:**
- ✅ Test user login flow
- ✅ Test employee data loading
- ✅ Test error scenarios
- ✅ Test UI updates

## 📝 **Files Modified**

### **1. Updated Files:**
- ✅ **UserService.java** - Added include_employee parameter
- ✅ **SettingsFragment.java** - Enhanced user info display
- ✅ **fragment_settings.xml** - Updated layout for employee info

### **2. API Endpoints:**
- ✅ `GET /api/users/me?include_employee=true` - Get user with employee
- ✅ `GET /api/users/{id}?include_employee=true` - Get user by ID with employee

### **3. Database Tables:**
- ✅ **users** - User authentication and profile
- ✅ **employees** - Employee information and organization

## 🎯 **User Experience**

### **Settings Tab Features:**
- ✅ **Real-time Data**: Thông tin từ database
- ✅ **Employee Details**: Thông tin nhân viên đầy đủ
- ✅ **Error Handling**: Fallback khi lỗi
- ✅ **Debug Logging**: Chi tiết debug information

### **Information Displayed:**
- ✅ **User Name**: Tên đầy đủ từ users table
- ✅ **User Email**: Email từ users table
- ✅ **User ID**: ID từ users table
- ✅ **User Role**: Vai trò từ users table
- ✅ **Employee Name**: Tên nhân viên từ employees table
- ✅ **Employee Code**: Mã nhân viên từ employees table
- ✅ **Department**: Phòng ban từ employees table
- ✅ **Position**: Chức vụ từ employees table

## 🔧 **Troubleshooting**

### **Common Issues:**
1. **User not found**: Check authentication
2. **Employee not linked**: Check employee_id in users table
3. **API connection fails**: Check network configuration
4. **Data not displaying**: Check UI thread safety

### **Debug Steps:**
1. Check logcat for API debug logs
2. Verify database connectivity
3. Check API response format
4. Verify data parsing logic

Dự án Android đã được tích hợp hoàn chỉnh với thông tin user và employee từ database! 🎉
