# Firebase Remote Config - Setup với Kotlin DSL

## ✅ Chọn: Kotlin DSL (build.gradle.kts)

**Lý do:**
- Project của bạn đã đang dùng Kotlin DSL
- Type-safe, IDE support tốt hơn
- Modern và được Google khuyến nghị
- Dễ maintain hơn Groovy

## 1. Cập nhật build.gradle.kts

### File: `build.gradle.kts` (root level)

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false // ✅ Thêm dòng này
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
```

### File: `app/build.gradle.kts`

Thêm vào phần `plugins`:
```kotlin
plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // ✅ Thêm dòng này
}
```

Thêm vào phần `dependencies`:
```kotlin
dependencies {
    // ... existing dependencies ...
    
    // Firebase BOM (Bill of Materials) - quản lý version tự động
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    
    // Firebase Remote Config
    implementation("com.google.firebase:firebase-config")
    
    // Firebase Analytics (optional nhưng recommended)
    implementation("com.google.firebase:firebase-analytics")
    
    // ... other dependencies ...
}
```

## 2. Tải file google-services.json

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Tạo project mới hoặc chọn project có sẵn
3. Thêm Android app với package name: `com.example.financialmanagement`
4. Tải file `google-services.json`
5. Đặt file vào thư mục `app/` (cùng cấp với `build.gradle.kts`)

## 3. Sync Project

Sau khi thêm dependencies, sync project:
- Click **Sync Now** trong Android Studio
- Hoặc chạy: `./gradlew build`

## 4. Verify Setup

Kiểm tra xem Firebase đã được thêm đúng chưa:
- File `google-services.json` có trong `app/`
- Plugin `com.google.gms.google-services` đã được apply
- Dependencies đã được download

## So sánh: Kotlin DSL vs Groovy

| Tính năng | Kotlin DSL ✅ | Groovy |
|-----------|---------------|--------|
| Type Safety | ✅ Có | ❌ Không |
| IDE Support | ✅ Tốt hơn | ⚠️ Hạn chế |
| Modern | ✅ Mới hơn | ⚠️ Legacy |
| Project của bạn | ✅ Đang dùng | ❌ Không |
| Khuyến nghị | ✅ Nên dùng | ⚠️ Legacy |

## Next Steps

Sau khi setup xong, xem file `FIREBASE_REMOTE_CONFIG_GUIDE.md` để:
- Tạo RemoteConfigManager
- Sử dụng trong app
- Cấu hình trên Firebase Console



