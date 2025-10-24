# 🎯 HƯỚNG DẪN QUY TRÌNH KINH DOANH HOÀN CHỈNH TRONG MOBILE APP

## 📋 **Tổng quan Quy trình**

Mobile app được thiết kế theo quy trình kinh doanh hoàn chỉnh từ **khách hàng** → **dự án** → **báo giá** → **ngân sách** → **hóa đơn** → **chi phí thực tế** → **báo cáo**. Mỗi bước được tích hợp chặt chẽ với role-based access control.

---

## 🚀 **BƯỚC 1: QUẢN LÝ KHÁCH HÀNG (CustomersFragment)**

### 📝 **Mục đích**
Thiết lập thông tin khách hàng làm nền tảng cho toàn bộ quy trình quản lý dự án và tài chính.

### 🎯 **Chức năng chính:**
- ✅ **Tạo khách hàng** với thông tin cơ bản
- ✅ **Phân loại khách hàng** (Cá nhân/Công ty/Chính phủ)
- ✅ **Thiết lập credit limit** và payment terms
- ✅ **Quản lý thông tin liên lạc** (email, phone, address)
- ✅ **Theo dõi dự án** của khách hàng

### 🔑 **Role-based Access:**
```java
switch (userRole.toLowerCase()) {
    case "admin":
        // Admin can see all customers
        break;
    case "manager":
        // Manager can see customers for their projects
        params.put("manager_id", authManager.getUserId());
        break;
    case "sales":
        // Sales can see customers they created
        params.put("created_by", authManager.getUserId());
        break;
}
```

### 📊 **API Endpoints:**
- `GET /api/customers` - Lấy danh sách khách hàng
- `POST /api/customers` - Tạo khách hàng mới
- `PUT /api/customers/{id}` - Cập nhật khách hàng
- `DELETE /api/customers/{id}` - Xóa khách hàng

---

## 🏗️ **BƯỚC 2: QUẢN LÝ DỰ ÁN (ProjectsFragment)**

### 📝 **Mục đích**
Tạo dự án và liên kết với khách hàng để quản lý toàn bộ quy trình từ báo giá đến báo cáo.

### 🎯 **Chức năng chính:**
- ✅ **Tạo dự án** với thông tin chi tiết
- ✅ **Liên kết với khách hàng** đã tạo
- ✅ **Phân công Project Manager**
- ✅ **Thiết lập ngân sách** và timeline
- ✅ **Theo dõi tiến độ** dự án

### 🔑 **Role-based Access:**
```java
switch (userRole.toLowerCase()) {
    case "admin":
        // Admin can see all projects
        break;
    case "manager":
        // Manager can see projects they manage
        params.put("manager_id", authManager.getUserId());
        break;
    case "employee":
        // Employee can see projects they're assigned to
        params.put("assigned_to", authManager.getUserId());
        break;
}
```

### 📊 **API Endpoints:**
- `GET /api/projects` - Lấy danh sách dự án
- `POST /api/projects` - Tạo dự án mới
- `PUT /api/projects/{id}` - Cập nhật dự án
- `DELETE /api/projects/{id}` - Xóa dự án

---

## 💰 **BƯỚC 3: QUẢN LÝ BÁO GIÁ (QuotesFragment)**

### 📝 **Mục đích**
Tạo báo giá chi tiết cho khách hàng với approval workflow và chuyển đổi thành hóa đơn.

### 🎯 **Chức năng chính:**
- ✅ **Tạo báo giá** với chi tiết sản phẩm/dịch vụ
- ✅ **Tính toán giá cả** và thuế
- ✅ **Gửi báo giá** cho khách hàng
- ✅ **Approval workflow** cho báo giá
- ✅ **Chuyển đổi** báo giá thành hóa đơn

### 🔑 **Role-based Access:**
```java
switch (userRole.toLowerCase()) {
    case "admin":
        // Admin can see all quotes
        break;
    case "manager":
        // Manager can see quotes for their projects
        params.put("manager_id", authManager.getUserId());
        break;
    case "sales":
        // Sales can see quotes they created
        params.put("created_by", authManager.getUserId());
        break;
}
```

### 📊 **API Endpoints:**
- `GET /api/quotes` - Lấy danh sách báo giá
- `POST /api/quotes` - Tạo báo giá mới
- `PUT /api/quotes/{id}` - Cập nhật báo giá
- `POST /api/quotes/{id}/approve` - Duyệt báo giá
- `POST /api/quotes/{id}/convert` - Chuyển đổi thành hóa đơn

---

## 📊 **BƯỚC 4: QUẢN LÝ NGÂN SÁCH (BudgetFragment)**

### 📝 **Mục đích**
Lập ngân sách chi tiết cho dự án với phân bổ chi phí theo danh mục.

### 🎯 **Chức năng chính:**
- ✅ **Tạo ngân sách** dự án
- ✅ **Phân bổ chi phí** theo danh mục
- ✅ **Thiết lập mục tiêu** chi phí
- ✅ **Theo dõi ngân sách** vs chi phí thực tế
- ✅ **Báo cáo variance** analysis

### 🔑 **Role-based Access:**
```java
switch (userRole.toLowerCase()) {
    case "admin":
        // Admin can see all budgets
        break;
    case "manager":
        // Manager can see budgets for their projects
        params.put("manager_id", authManager.getUserId());
        break;
    case "accountant":
        // Accountant can see all budgets for financial management
        break;
}
```

### 📊 **API Endpoints:**
- `GET /api/budgets` - Lấy danh sách ngân sách
- `POST /api/budgets` - Tạo ngân sách mới
- `PUT /api/budgets/{id}` - Cập nhật ngân sách
- `GET /api/budgets/{id}/variance` - Báo cáo variance

---

## 🧾 **BƯỚC 5: QUẢN LÝ HÓA ĐƠN (InvoicesFragment)**

### 📝 **Mục đích**
Tạo hóa đơn từ báo giá đã duyệt và theo dõi thanh toán.

### 🎯 **Chức năng chính:**
- ✅ **Tạo hóa đơn** từ báo giá đã duyệt
- ✅ **Theo dõi thanh toán** (paid/unpaid)
- ✅ **Gửi hóa đơn** cho khách hàng
- ✅ **Quản lý payment** terms
- ✅ **Báo cáo tài chính** từ hóa đơn

### 🔑 **Role-based Access:**
```java
switch (userRole.toLowerCase()) {
    case "admin":
        // Admin can see all invoices
        break;
    case "manager":
        // Manager can see invoices for their projects
        params.put("manager_id", authManager.getUserId());
        break;
    case "accountant":
        // Accountant can see all invoices for financial management
        break;
}
```

### 📊 **API Endpoints:**
- `GET /api/invoices` - Lấy danh sách hóa đơn
- `POST /api/invoices` - Tạo hóa đơn mới
- `PUT /api/invoices/{id}` - Cập nhật hóa đơn
- `POST /api/invoices/{id}/mark-paid` - Đánh dấu đã thanh toán
- `POST /api/invoices/{id}/send` - Gửi hóa đơn cho khách hàng

---

## 💸 **BƯỚC 6: QUẢN LÝ CHI PHÍ (ExpensesFragment)**

### 📝 **Mục đích**
Theo dõi chi phí thực tế của dự án và so sánh với ngân sách kế hoạch.

### 🎯 **Chức năng chính:**
- ✅ **Ghi nhận chi phí** thực tế
- ✅ **Phân loại chi phí** theo danh mục
- ✅ **So sánh** planned vs actual costs
- ✅ **Theo dõi variance** analysis
- ✅ **Báo cáo chi phí** chi tiết

### 🔑 **Role-based Access:**
```java
switch (userRole.toLowerCase()) {
    case "admin":
        // Admin can see all expenses
        break;
    case "manager":
        // Manager can see expenses for their projects
        params.put("manager_id", authManager.getUserId());
        break;
    case "employee":
        // Employee can only see their own expenses
        params.put("created_by", authManager.getUserId());
        break;
}
```

### 📊 **API Endpoints:**
- `GET /api/project-expenses` - Lấy danh sách chi phí
- `POST /api/project-expenses` - Tạo chi phí mới
- `PUT /api/project-expenses/{id}` - Cập nhật chi phí
- `GET /api/project-expenses/variance` - Báo cáo variance

---

## 📈 **BƯỚC 7: BÁO CÁO TÀI CHÍNH (ReportsFragment)**

### 📝 **Mục đích**
Tạo báo cáo tài chính chi tiết với phân tích variance và profitability.

### 🎯 **Chức năng chính:**
- ✅ **Báo cáo dự án** (Project Reports)
- ✅ **Báo cáo chi phí** (Cost Reports)
- ✅ **Variance analysis** (Planned vs Actual)
- ✅ **Profitability analysis** (P&L)
- ✅ **Balance Sheet** và Cash Flow

### 🔑 **Role-based Access:**
```java
switch (userRole.toLowerCase()) {
    case "admin":
        // Admin can see all reports
        break;
    case "manager":
        // Manager can see reports for their projects
        params.put("manager_id", authManager.getUserId());
        break;
    case "accountant":
        // Accountant can see all financial reports
        break;
}
```

### 📊 **API Endpoints:**
- `GET /api/reports/project-performance` - Báo cáo hiệu suất dự án
- `GET /api/reports/cost-variance` - Báo cáo variance chi phí
- `GET /api/reports/profit-loss` - Báo cáo P&L
- `GET /api/reports/balance-sheet` - Báo cáo Balance Sheet

---

## 👥 **BƯỚC 8: KHÁCH HÀNG XEM (CustomerViewFragment)**

### 📝 **Mục đích**
Cho phép khách hàng theo dõi tiến độ dự án và xem timeline với hình ảnh.

### 🎯 **Chức năng chính:**
- ✅ **Timeline dự án** với hình ảnh
- ✅ **Progress tracking** real-time
- ✅ **Communication** với team
- ✅ **Document sharing** và updates
- ✅ **Payment status** tracking

### 🔑 **Role-based Access:**
```java
switch (userRole.toLowerCase()) {
    case "customer":
        // Customer can see their own projects
        params.put("customer_id", authManager.getUserId());
        break;
    case "admin":
    case "manager":
        // Admin/Manager can see all customer views
        break;
}
```

### 📊 **API Endpoints:**
- `GET /api/customer-view/projects` - Dự án của khách hàng
- `GET /api/customer-view/timeline/{project_id}` - Timeline dự án
- `POST /api/customer-view/upload-image` - Upload hình ảnh
- `GET /api/customer-view/communications` - Communications

---

## 🔄 **WORKFLOW INTEGRATION**

### **Complete Business Flow:**
```
1. Customer Creation → 2. Project Creation → 3. Quote Creation → 4. Budget Planning
                                    ↓
8. Customer View ← 7. Financial Reports ← 6. Expense Tracking ← 5. Invoice Management
```

### **Role-based Workflow:**
- **Admin**: Full access to all functions
- **Manager**: Project management and team oversight
- **Sales**: Customer and quote management
- **Accountant**: Financial management and reporting
- **Employee**: Limited access to assigned projects
- **Customer**: View-only access to their projects

---

## 📱 **MOBILE APP FEATURES**

### **Navigation Structure:**
```
MainActivity
├── DashboardFragment (Tổng quan)
├── CustomersFragment (Khách hàng)
├── ProjectsFragment (Dự án)
├── QuotesFragment (Báo giá)
├── BudgetFragment (Ngân sách)
├── InvoicesFragment (Hóa đơn)
├── ExpensesFragment (Chi phí)
├── ReportsFragment (Báo cáo)
└── SettingsFragment (Cài đặt)
```

### **Authentication & Authorization:**
- ✅ **Login/Logout** functionality
- ✅ **Role-based access** control
- ✅ **Token management** với refresh
- ✅ **Session management** và timeout
- ✅ **User profile** management

### **Data Management:**
- ✅ **Real-time sync** với backend
- ✅ **Offline support** cho critical data
- ✅ **Data validation** và error handling
- ✅ **Cache management** cho performance
- ✅ **Background sync** cho updates

### **User Experience:**
- ✅ **Intuitive navigation** với bottom tabs
- ✅ **Role-based UI** adaptation
- ✅ **Real-time notifications** cho updates
- ✅ **Offline indicators** và sync status
- ✅ **Comprehensive error handling** với user feedback

---

## 🎯 **EXPECTED RESULTS**

### **Business Process Efficiency:**
- ✅ **Streamlined workflow** từ khách hàng đến báo cáo
- ✅ **Role-based access** đảm bảo security
- ✅ **Real-time tracking** của tất cả processes
- ✅ **Comprehensive reporting** cho decision making
- ✅ **Customer transparency** với project visibility

### **Mobile App Benefits:**
- ✅ **Anytime, anywhere** access
- ✅ **Role-based functionality** phù hợp với từng user
- ✅ **Real-time updates** và notifications
- ✅ **Offline capability** cho critical operations
- ✅ **Seamless integration** với existing systems

Mobile app đã được thiết kế hoàn chỉnh theo quy trình kinh doanh từ khách hàng đến báo cáo! 🎉
