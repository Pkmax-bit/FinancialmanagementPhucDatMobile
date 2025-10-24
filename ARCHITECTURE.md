# Kiến trúc Ứng dụng Android - Phúc Đạt Financial Management

## 📋 Tổng quan

Ứng dụng Android quản lý tài chính Phúc Đạt được xây dựng theo kiến trúc MVVM (Model-View-ViewModel) với các thành phần chính:

## 🏗️ Kiến trúc tổng quan

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
├─────────────────────────────────────────────────────────────┤
│  Activities          │  Fragments         │  Adapters       │
│  - MainActivity      │  - DashboardFrag   │  - ProjectsAdap │
│  - LoginActivity     │  - ProjectsFrag    │  - ExpensesAdap │
│  - ProjectDetailAct  │  - ExpensesFrag    │  - RecentProjAd │
│  - ExpenseDetailAct  │  - ReportsFrag     │                │
│  - CustomerDetailAct │  - SettingsFrag     │                │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    BUSINESS LAYER                          │
├─────────────────────────────────────────────────────────────┤
│  Services            │  AuthManager        │  Utils          │
│  - ProjectService    │  - Login/Logout     │  - CurrencyFmt  │
│  - ExpenseService    │  - Token Management │  - DateUtils    │
│  - CustomerService   │  - Session Storage  │  - Validation   │
│  - ReportService     │  - AuthCallback     │  - Constants    │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    DATA LAYER                               │
├─────────────────────────────────────────────────────────────┤
│  Models              │  Network            │  Database       │
│  - Project           │  - ApiClient        │  - Room DB      │
│  - ProjectExpense    │  - AuthInterceptor  │  - Entities     │
│  - Customer          │  - NetworkConfig    │  - DAOs         │
│  - Employee          │  - Retrofit         │  - Migrations  │
│  - ExpenseObject     │  - OkHttp           │                │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    EXTERNAL LAYER                           │
├─────────────────────────────────────────────────────────────┤
│  Backend API         │  Supabase Auth      │  Local Storage │
│  - REST Endpoints    │  - OAuth2           │  - SharedPrefs  │
│  - JSON Responses   │  - JWT Tokens       │  - Room Cache   │
│  - Error Handling   │  - Session Mgmt     │  - File Cache   │
└─────────────────────────────────────────────────────────────┘
```

## 📁 Cấu trúc thư mục

```
app/src/main/java/com/example/financialmanagement/
├── activities/           # Các Activity chính
│   ├── MainActivity.java
│   ├── LoginActivity.java
│   ├── ProjectDetailActivity.java
│   ├── ExpenseDetailActivity.java
│   └── CustomerDetailActivity.java
├── fragments/           # Các Fragment
│   ├── DashboardFragment.java
│   ├── ProjectsFragment.java
│   ├── ExpensesFragment.java
│   ├── ReportsFragment.java
│   └── SettingsFragment.java
├── adapters/            # RecyclerView Adapters
│   ├── ProjectsAdapter.java
│   ├── ExpensesAdapter.java
│   └── RecentProjectsAdapter.java
├── models/              # Data Models
│   ├── Project.java
│   ├── ProjectExpense.java
│   ├── Customer.java
│   ├── Employee.java
│   └── ExpenseObject.java
├── services/            # API Services
│   ├── ProjectService.java
│   ├── ExpenseService.java
│   └── CustomerService.java
├── network/             # Network Configuration
│   ├── ApiClient.java
│   ├── AuthInterceptor.java
│   └── NetworkConfig.java
├── auth/                # Authentication
│   ├── AuthManager.java
│   └── AuthCallback.java
├── utils/               # Utility Classes
│   └── CurrencyFormatter.java
├── database/            # Local Database (Room)
│   ├── AppDatabase.java
│   ├── entities/
│   └── daos/
└── ui/                  # UI Components
    ├── components/
    └── widgets/
```

## 🔄 Luồng dữ liệu

### 1. Authentication Flow
```
User Input → LoginActivity → AuthManager → Supabase API → Token Storage → MainActivity
```

### 2. Data Loading Flow
```
Fragment → Service → ApiClient → Retrofit → Backend API → Response → Model → Adapter → RecyclerView
```

### 3. Offline Support Flow
```
API Call → Success → Room Database → UI Update
API Call → Failure → Room Database → Cached Data → UI Update
```

## 🛠️ Công nghệ sử dụng

### Core Android
- **Language**: Java 8+
- **Min SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture**: MVVM

### UI/UX
- **Material Design 3**: Modern UI components
- **ViewBinding**: Type-safe view binding
- **RecyclerView**: Efficient list rendering
- **Navigation**: Fragment-based navigation

### Network
- **Retrofit 2**: HTTP client
- **OkHttp**: Network interceptor
- **Gson**: JSON serialization
- **OAuth2**: Authentication

### Database
- **Room**: Local database
- **SharedPreferences**: Settings storage
- **Cache**: Offline data support

### Charts & Visualization
- **MPAndroidChart**: Financial charts
- **CircleImageView**: Profile images
- **Glide**: Image loading

## 🔐 Security

### Authentication
- OAuth2 với Supabase
- JWT token management
- Secure token storage
- Auto token refresh

### Data Protection
- HTTPS communication
- Input validation
- SQL injection prevention
- Secure API endpoints

## 📊 Performance

### Optimization
- RecyclerView với ViewHolder pattern
- Image loading với Glide
- Network caching
- Database indexing
- Background processing

### Memory Management
- WeakReference cho callbacks
- Proper lifecycle management
- Image memory optimization
- Garbage collection friendly

## 🧪 Testing

### Unit Tests
- JUnit 4 cho business logic
- Mockito cho mocking
- Room database testing
- Network layer testing

### UI Tests
- Espresso cho UI automation
- Fragment testing
- Activity testing
- Integration testing

## 🚀 Deployment

### Build Variants
- **Debug**: Development với logging
- **Release**: Production với obfuscation

### Signing
- Keystore configuration
- ProGuard/R8 obfuscation
- APK/AAB optimization

## 📱 Features

### Core Features
1. **Dashboard**: Tổng quan tài chính
2. **Projects**: Quản lý dự án
3. **Expenses**: Quản lý chi phí
4. **Reports**: Báo cáo tài chính
5. **Settings**: Cài đặt ứng dụng

### Advanced Features
1. **Offline Support**: Hoạt động không cần mạng
2. **Real-time Sync**: Đồng bộ dữ liệu
3. **Role-based Access**: Phân quyền theo vai trò
4. **Data Export**: Xuất báo cáo
5. **Push Notifications**: Thông báo

## 🔧 Configuration

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

## 📈 Monitoring

### Analytics
- User behavior tracking
- Performance metrics
- Error reporting
- Usage statistics

### Logging
- Network requests
- Database operations
- User actions
- Error logs

## 🔄 Maintenance

### Updates
- OTA updates
- Database migrations
- API versioning
- Backward compatibility

### Support
- Error handling
- User feedback
- Bug fixes
- Feature requests

---

**Tài liệu này được cập nhật thường xuyên để phản ánh kiến trúc hiện tại của ứng dụng.**
