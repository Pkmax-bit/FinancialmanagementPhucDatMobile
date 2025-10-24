# Settings Guide - Hướng dẫn trang Cài đặt

## 🔧 **Tính năng Settings**

### 1. **Hiển thị thông tin người dùng**
- ✅ **User Name**: Tên người dùng từ AuthManager
- ✅ **User Email**: Email người dùng từ AuthManager  
- ✅ **User ID**: ID người dùng từ AuthManager
- ✅ **Login Status**: Kiểm tra trạng thái đăng nhập

### 2. **Chức năng người dùng**
- ✅ **Logout**: Đăng xuất và chuyển về LoginActivity
- ✅ **Sync Data**: Đồng bộ dữ liệu với server
- ✅ **Clear Cache**: Xóa cache local

## 📱 **User Information Display**

### **Thông tin hiển thị:**
```java
// Lấy từ AuthManager
String userId = authManager.getUserId();
String userEmail = authManager.getUserEmail();
String userName = authManager.getUserName();
boolean isLoggedIn = authManager.isLoggedIn();
```

### **Fallback values:**
- Nếu không có tên: "Chưa có tên"
- Nếu không có email: "Chưa có email"  
- Nếu không có ID: "ID: Chưa có"
- Nếu chưa đăng nhập: Toast "Bạn chưa đăng nhập"

## 🐛 **Debug Logging**

### **Auth Debug Logs:**
```
D/AUTH_DEBUG: === AUTH DEBUG ===
D/AUTH_DEBUG: Authenticated: true
D/AUTH_DEBUG: Token: eyJhbGciOiJIUzI1NiIs...
D/AUTH_DEBUG: ================

D/AUTH_DEBUG: Loading user info
D/AUTH_DEBUG: User ID: 12345
D/AUTH_DEBUG: User Email: user@phucdat.com
D/AUTH_DEBUG: User Name: Nguyễn Văn A
```

## 🔄 **User Actions**

### **1. Logout Process:**
```java
// 1. Debug logging
ApiDebugger.logAuth("Performing logout", false);

// 2. Clear auth data
authManager.logout();

// 3. Show confirmation
Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();

// 4. Navigate to login
Intent intent = new Intent(getActivity(), LoginActivity.class);
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
startActivity(intent);
getActivity().finish();
```

### **2. Sync Data Process:**
```java
// 1. Debug logging
ApiDebugger.logAuth("Syncing data", authManager.isLoggedIn());

// 2. Show loading
Toast.makeText(getContext(), "Đang đồng bộ dữ liệu...", Toast.LENGTH_SHORT).show();

// 3. TODO: Implement actual sync
// - Sync projects from server
// - Sync expenses from server  
// - Update local database

// 4. Show completion
Toast.makeText(getContext(), "Đồng bộ hoàn tất", Toast.LENGTH_SHORT).show();
```

### **3. Clear Cache Process:**
```java
// 1. Debug logging
ApiDebugger.logAuth("Clearing cache", authManager.isLoggedIn());

// 2. Show confirmation
Toast.makeText(getContext(), "Đang xóa cache...", Toast.LENGTH_SHORT).show();

// 3. TODO: Implement actual cache clearing
// - Clear local database
// - Clear cached files
// - Clear SharedPreferences (except auth data)

// 4. Show completion
Toast.makeText(getContext(), "Đã xóa cache", Toast.LENGTH_SHORT).show();
```

## 🎨 **UI Layout**

### **User Profile Section:**
```xml
<!-- User Avatar -->
<de.hdodenhof.circleimageview.CircleImageView
    android:layout_width="60dp"
    android:layout_height="60dp"
    android:src="@drawable/ic_person"
    app:civ_border_width="2dp"
    app:civ_border_color="?attr/colorPrimary" />

<!-- User Info -->
<TextView android:id="@+id/tv_user_name" />
<TextView android:id="@+id/tv_user_email" />
<TextView android:id="@+id/tv_user_id" />
```

### **Action Buttons:**
```xml
<Button android:id="@+id/btn_logout" />
<Button android:id="@+id/btn_sync_data" />
<Button android:id="@+id/btn_clear_cache" />
```

## 🔍 **Troubleshooting**

### **1. Không hiển thị thông tin user:**
- Kiểm tra AuthManager có lưu thông tin không
- Kiểm tra SharedPreferences
- Xem debug logs trong logcat

### **2. Logout không hoạt động:**
- Kiểm tra AuthManager.logout()
- Kiểm tra navigation logic
- Xem error logs

### **3. User info không cập nhật:**
- Kiểm tra login process
- Kiểm tra token storage
- Xem auth debug logs

## 📊 **Data Flow**

### **Load User Info:**
```
SettingsFragment.onCreateView()
    ↓
initializeViews()
    ↓
loadUserInfo()
    ↓
authManager.getUserId()
authManager.getUserEmail()
authManager.getUserName()
    ↓
Display in UI
```

### **Logout Flow:**
```
User clicks Logout
    ↓
performLogout()
    ↓
authManager.logout()
    ↓
Clear auth data
    ↓
Navigate to LoginActivity
```

## 🚀 **Next Steps**

1. **Test user login** để có dữ liệu hiển thị
2. **Implement actual sync** cho sync data
3. **Implement cache clearing** cho clear cache
4. **Add user profile editing** nếu cần
5. **Add app version info** trong settings

## 📝 **Notes**

- Tất cả thông tin user được lấy từ AuthManager
- Debug logging giúp troubleshoot issues
- UI sẽ hiển thị fallback values nếu không có data
- Logout sẽ clear tất cả auth data và navigate về login
