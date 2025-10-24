# Project Cost Management Guide - Hướng dẫn quản lý chi phí dự án

## 🎯 **Mục đích**
Tập trung app vào việc quản lý chi phí dự án với logic chi phí thực tế của dự án web Phúc Đạt, bao gồm:
- **Dashboard**: Hiển thị danh sách dự án với chi phí thực tế
- **Chi phí**: Tạo chi phí thực tế theo role (Admin, Manager, Employee)
- **Báo cáo**: Báo cáo chi phí dự án
- **Cài đặt**: Đăng xuất và hiển thị nhân viên

## 📊 **Dashboard - Tổng quan dự án**

### **Project Cost Summary:**
```java
private void loadProjectCostSummary() {
    // Load project cost summary for dashboard
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 1000);
    
    projectService.getAllProjects(params, new ProjectService.ProjectCallback() {
        @Override
        public void onSuccess(List<Project> projects) {
            if (projects != null) {
                double totalBudget = 0;
                double totalActualCost = 0;
                int activeProjects = 0;
                
                for (Project project : projects) {
                    if (project.getBudget() != null) {
                        totalBudget += project.getBudget();
                    }
                    if (project.getActualCost() != null) {
                        totalActualCost += project.getActualCost();
                    }
                    if ("active".equals(project.getStatus())) {
                        activeProjects++;
                    }
                }
                
                // Update UI with cost information
                updateCostSummary(totalBudget, totalActualCost, activeProjects);
            }
        }
    });
}
```

### **Dashboard Features:**
- ✅ **Total Projects**: Tổng số dự án
- ✅ **Active Projects**: Dự án đang hoạt động
- ✅ **Completed Projects**: Dự án hoàn thành
- ✅ **Cost Summary**: Tổng ngân sách và chi phí thực tế
- ✅ **Recent Projects**: Dự án gần đây với thông tin chi phí

## 💰 **Chi phí - Logic theo Role**

### **Role-based Expense Management:**

#### **Admin Role:**
```java
case "admin":
    // Admin can see all expenses
    ApiDebugger.logAuth("Loading all expenses for Admin", true);
    break;
```

#### **Manager Role:**
```java
case "manager":
    // Manager can see expenses for their projects
    params.put("manager_id", authManager.getUserId());
    ApiDebugger.logAuth("Loading manager expenses for user: " + authManager.getUserId(), true);
    break;
```

#### **Employee Role:**
```java
case "employee":
default:
    // Employee can only see their own expenses
    params.put("created_by", authManager.getUserId());
    ApiDebugger.logAuth("Loading employee expenses for user: " + authManager.getUserId(), true);
    break;
```

### **Expense Management Features:**
- ✅ **Role-based Access**: Quyền truy cập theo vai trò
- ✅ **Cost Creation**: Tạo chi phí thực tế
- ✅ **Cost Tracking**: Theo dõi chi phí dự án
- ✅ **Cost Approval**: Duyệt chi phí (cho Manager/Admin)

## 📈 **Báo cáo - Chi phí dự án**

### **Project Cost Reports:**
```java
/**
 * Reports Fragment - Màn hình báo cáo chi phí dự án
 * Tập trung vào báo cáo chi phí thực tế của dự án web Phúc Đạt
 * Hiển thị các biểu đồ và thống kê chi phí dự án
 */
```

### **Report Features:**
- ✅ **Cost Analysis**: Phân tích chi phí dự án
- ✅ **Budget vs Actual**: So sánh ngân sách và chi phí thực tế
- ✅ **Project Profitability**: Lợi nhuận dự án
- ✅ **Cost Trends**: Xu hướng chi phí theo thời gian

## ⚙️ **Cài đặt - Đăng xuất và nhân viên**

### **Settings Features:**
- ✅ **User Information**: Thông tin người dùng từ database
- ✅ **Employee Details**: Thông tin nhân viên
- ✅ **Logout Function**: Đăng xuất an toàn
- ✅ **Role Display**: Hiển thị vai trò người dùng

### **User Information Display:**
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

## 🔧 **API Integration**

### **Project Cost API:**
```java
// Get project cost summary
GET /api/projects?limit=1000

// Response format
{
  "success": true,
  "projects": [
    {
      "id": "project123",
      "name": "Dự án web Phúc Đạt",
      "budget": 50000000,
      "actual_cost": 35000000,
      "status": "active",
      "created_at": "2025-10-21T01:48:22.695953+00:00"
    }
  ],
  "total": 1
}
```

### **Expense API with Role Filtering:**
```java
// Admin: Get all expenses
GET /api/project-expenses

// Manager: Get expenses for managed projects
GET /api/project-expenses?manager_id=user123

// Employee: Get own expenses
GET /api/project-expenses?created_by=user123
```

## 🎯 **User Experience**

### **Dashboard Experience:**
- ✅ **Project Overview**: Tổng quan dự án với chi phí
- ✅ **Cost Summary**: Tóm tắt chi phí dự án
- ✅ **Recent Activity**: Hoạt động gần đây
- ✅ **Quick Actions**: Thao tác nhanh

### **Expense Management Experience:**
- ✅ **Role-based Interface**: Giao diện theo vai trò
- ✅ **Cost Creation**: Tạo chi phí dễ dàng
- ✅ **Cost Tracking**: Theo dõi chi phí trực quan
- ✅ **Approval Workflow**: Quy trình duyệt chi phí

### **Reporting Experience:**
- ✅ **Visual Charts**: Biểu đồ trực quan
- ✅ **Cost Analysis**: Phân tích chi phí chi tiết
- ✅ **Export Options**: Tùy chọn xuất báo cáo
- ✅ **Real-time Data**: Dữ liệu thời gian thực

## 🔍 **Debug Logging**

### **Project Cost Logging:**
```
D/API_DEBUG: === API REQUEST ===
D/API_DEBUG: Method: GET
D/API_DEBUG: URL: http://192.168.1.17:3000/api/projects
D/API_DEBUG: ==================

D/RESPONSE_DEBUG: === API RESPONSE ===
D/RESPONSE_DEBUG: Code: 200
D/RESPONSE_DEBUG: Message: OK
D/RESPONSE_DEBUG: Body: Total Budget: 50000000, Actual Cost: 35000000
D/RESPONSE_DEBUG: ===================
```

### **Role-based Logging:**
```
D/AUTH_DEBUG: Loading all expenses for Admin
D/AUTH_DEBUG: Loading manager expenses for user: user123
D/AUTH_DEBUG: Loading employee expenses for user: user456
```

## 📱 **UI Components**

### **Dashboard Layout:**
```xml
<!-- Project Statistics -->
<TextView android:id="@+id/tv_total_projects" />
<TextView android:id="@+id/tv_total_expenses" />
<TextView android:id="@+id/tv_total_revenue" />

<!-- Recent Projects -->
<RecyclerView android:id="@+id/rv_recent_projects" />
```

### **Expenses Layout:**
```xml
<!-- Expense List -->
<RecyclerView android:id="@+id/rv_expenses" />

<!-- Role-based Actions -->
<Button android:id="@+id/btn_add_expense" />
<Button android:id="@+id/btn_approve_expenses" />
```

### **Settings Layout:**
```xml
<!-- User Information -->
<TextView android:id="@+id/tv_user_name" />
<TextView android:id="@+id/tv_user_email" />
<TextView android:id="@+id/tv_user_role" />
<TextView android:id="@+id/tv_employee_info" />

<!-- Actions -->
<Button android:id="@+id/btn_logout" />
```

## 🚀 **Implementation Status**

### **✅ Completed:**
- ✅ **Dashboard**: Project cost summary
- ✅ **Expenses**: Role-based expense management
- ✅ **Settings**: User and employee information
- ✅ **API Integration**: Project and expense APIs
- ✅ **Debug Logging**: Comprehensive logging

### **🔄 In Progress:**
- 🔄 **Reports**: Enhanced cost reporting
- 🔄 **Cost Approval**: Manager approval workflow
- 🔄 **Cost Analytics**: Advanced cost analysis

### **📋 Pending:**
- 📋 **Cost Categories**: Expense categorization
- 📋 **Budget Alerts**: Budget threshold notifications
- 📋 **Cost Forecasting**: Predictive cost analysis

## 🎯 **Expected Results**

### **Project Cost Management:**
- ✅ **Real-time Cost Tracking**: Theo dõi chi phí thời gian thực
- ✅ **Role-based Access**: Truy cập theo vai trò
- ✅ **Cost Analytics**: Phân tích chi phí chi tiết
- ✅ **Budget Management**: Quản lý ngân sách hiệu quả

### **User Experience:**
- ✅ **Intuitive Interface**: Giao diện trực quan
- ✅ **Role-based Features**: Tính năng theo vai trò
- ✅ **Real-time Updates**: Cập nhật thời gian thực
- ✅ **Comprehensive Reporting**: Báo cáo toàn diện

Dự án Android đã được tập trung vào quản lý chi phí dự án với logic chi phí thực tế! 🎉
