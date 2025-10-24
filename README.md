# Phúc Đạt Financial Management Mobile App

Ứng dụng Android quản lý tài chính Phúc Đạt được phát triển bằng Java và Android Studio.

## 🏗️ Kiến trúc dự án

### 📁 Cấu trúc thư mục

```
app/
├── src/main/java/com/example/financialmanagement/
│   ├── activities/           # Các Activity chính
│   ├── fragments/           # Các Fragment
│   ├── adapters/            # RecyclerView Adapters
│   ├── models/              # Data Models
│   ├── services/            # API Services
│   ├── utils/               # Utility classes
│   ├── network/             # Network configuration
│   ├── database/            # Local database (Room)
│   ├── auth/                # Authentication
│   └── ui/                  # UI components
```

## 🚀 Tính năng chính

### 1. Quản lý Dự án
- Danh sách dự án
- Chi tiết dự án
- Tạo/sửa/xóa dự án
- Theo dõi tiến độ

### 2. Quản lý Chi phí
- Chi phí dự án
- Phân loại chi phí theo đối tượng
- Báo cáo chi phí
- Quản lý hóa đơn

### 3. Quản lý Khách hàng
- Danh sách khách hàng
- Thông tin chi tiết khách hàng
- Lịch sử giao dịch

### 4. Báo cáo Tài chính
- Báo cáo doanh thu
- Báo cáo chi phí
- Báo cáo lợi nhuận
- Biểu đồ thống kê

### 5. Quản lý Nhân viên
- Danh sách nhân viên
- Phân quyền theo vai trò
- Quản lý phòng ban

## 🔧 Công nghệ sử dụng

- **Language**: Java
- **IDE**: Android Studio
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Database (Local)
- **Network**: Retrofit + OkHttp
- **Authentication**: OAuth2 với Supabase
- **UI**: Material Design Components
- **Image Loading**: Glide
- **Charts**: MPAndroidChart

## 📱 API Integration

Ứng dụng tích hợp với API backend Phúc Đạt thông qua các endpoint:

### Base URL
```
http://localhost:8000/api/
```

### Các API chính:
- **Projects**: `/api/projects`
- **Expenses**: `/api/project-expenses`
- **Customers**: `/api/customers`
- **Employees**: `/api/employees`
- **Reports**: `/api/reports`

## 🛠️ Cài đặt và Chạy

### Yêu cầu hệ thống
- Android Studio Arctic Fox trở lên
- Android SDK API 21+ (Android 5.0)
- Java 8+

### Các bước cài đặt

1. **Clone repository**
```bash
git clone <repository-url>
cd FinancialmanagementPhucDatMobile
```

2. **Mở project trong Android Studio**
- Mở Android Studio
- Chọn "Open an existing project"
- Chọn thư mục `FinancialmanagementPhucDatMobile`

3. **Cấu hình API**
- Cập nhật `API_BASE_URL` trong `NetworkConfig.java`
- Cấu hình Supabase credentials trong `AuthConfig.java`

4. **Build và Run**
- Sync project với Gradle
- Chạy ứng dụng trên emulator hoặc thiết bị thật

## 📋 Cấu hình

### API Configuration
```java
public class NetworkConfig {
    public static final String BASE_URL = "http://your-api-url.com/api/";
    public static final int TIMEOUT_SECONDS = 30;
}
```

### Authentication
```java
public class AuthConfig {
    public static final String SUPABASE_URL = "your-supabase-url";
    public static final String SUPABASE_ANON_KEY = "your-supabase-anon-key";
}
```

## 🎨 UI/UX Design

### Material Design
- Sử dụng Material Design Components
- Theme tùy chỉnh cho thương hiệu Phúc Đạt
- Responsive design cho các kích thước màn hình khác nhau

### Navigation
- Bottom Navigation cho các màn hình chính
- Drawer Navigation cho menu
- Fragment-based navigation

## 📊 Database Schema

### Local Database (Room)
- **Projects**: Lưu trữ dự án offline
- **Expenses**: Lưu trữ chi phí offline
- **Customers**: Lưu trữ thông tin khách hàng
- **SyncStatus**: Trạng thái đồng bộ dữ liệu

## 🔐 Security

### Authentication
- OAuth2 với Supabase
- JWT token management
- Secure storage cho credentials

### Data Protection
- Encryption cho dữ liệu nhạy cảm
- Secure API communication (HTTPS)
- Input validation và sanitization

## 🧪 Testing

### Unit Tests
- JUnit tests cho business logic
- Mockito cho mocking dependencies

### UI Tests
- Espresso tests cho UI automation
- Integration tests cho API calls

## 📦 Build & Deployment

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Signing
- Cấu hình keystore cho release
- ProGuard/R8 cho code obfuscation

## 🚀 Deployment

### Google Play Store
1. Tạo signed APK/AAB
2. Upload lên Google Play Console
3. Cấu hình store listing
4. Phát hành ứng dụng

## 📞 Support

### Development Team
- **Lead Developer**: [Tên]
- **Backend Integration**: [Tên]
- **UI/UX Designer**: [Tên]

### Contact
- Email: support@phucdat.com
- Phone: +84 xxx xxx xxx

## 📄 License

Copyright © 2024 Phúc Đạt Company. All rights reserved.
