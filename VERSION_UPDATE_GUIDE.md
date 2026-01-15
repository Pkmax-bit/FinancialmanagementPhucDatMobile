# 📱 Hướng Dẫn Cập Nhật Version App

## 📋 Version Hiện Tại

- **versionCode**: `1`
- **versionName**: `"1.0"`

---

## 🚀 CÁCH 1: Cập Nhật Version Thủ Công

### Bước 1: Mở file `app/build.gradle.kts`

### Bước 2: Tìm và cập nhật version

```kotlin
defaultConfig {
    applicationId = "com.example.financialmanagement"
    minSdk = 23
    targetSdk = 34
    versionCode = 2        // ✅ Tăng lên (1 → 2 → 3 → ...)
    versionName = "1.1"    // ✅ Cập nhật (1.0 → 1.1 → 1.2 → ...)
}
```

### Bước 3: Sync Project
- File → Sync Project with Gradle Files
- Hoặc click "Sync Now" khi Android Studio nhắc

### Bước 4: Build APK mới
- Build → Build Bundle(s) / APK(s) → Build APK(s)
- Hoặc: `./gradlew assembleRelease`

---

## 🎯 QUY TẮC VERSION

### versionCode (Bắt buộc tăng)
- **Luôn tăng** mỗi khi publish lên Play Store
- **Số nguyên**: 1, 2, 3, 4, ...
- **Không được giảm** hoặc giữ nguyên
- Play Store dùng để so sánh version mới/cũ

### versionName (Hiển thị cho user)
- **Format**: `"MAJOR.MINOR.PATCH"` (ví dụ: "1.0.0", "1.1.0", "2.0.0")
- **Có thể tùy ý**: "1.0", "1.1-beta", "2.0.1"
- **Hiển thị** trong Settings → About app

### Ví dụ Versioning:

| Lần Publish | versionCode | versionName | Mô tả |
|------------|-------------|-------------|-------|
| Lần 1      | 1           | "1.0"       | Version đầu tiên |
| Lần 2      | 2           | "1.1"       | Bug fixes |
| Lần 3      | 3           | "1.2"       | Tính năng mới |
| Lần 4      | 4           | "2.0"       | Major update |
| Lần 5      | 5           | "2.0.1"     | Hotfix |

---

## 🔄 CÁCH 2: Tự Động Tăng Version (Advanced)

### Tạo file `version.properties`

**File**: `version.properties` (trong thư mục root)

```properties
VERSION_CODE=1
VERSION_NAME=1.0
```

### Cập nhật `app/build.gradle.kts`

```kotlin
// Load version from properties file
val versionPropertiesFile = rootProject.file("version.properties")
val versionProperties = Properties()
if (versionPropertiesFile.exists()) {
    versionProperties.load(FileInputStream(versionPropertiesFile))
}

android {
    defaultConfig {
        // Auto-increment versionCode
        val currentVersionCode = (versionProperties.getProperty("VERSION_CODE", "1").toIntOrNull() ?: 1)
        versionCode = currentVersionCode + 1
        
        // Get versionName from properties
        versionName = versionProperties.getProperty("VERSION_NAME", "1.0")
    }
    
    // Task để tự động tăng version sau khi build
    tasks.register("incrementVersion") {
        doLast {
            val newVersionCode = (versionProperties.getProperty("VERSION_CODE", "1").toIntOrNull() ?: 1) + 1
            versionProperties.setProperty("VERSION_CODE", newVersionCode.toString())
            versionProperties.store(FileOutputStream(versionPropertiesFile), null)
            println("Version code incremented to: $newVersionCode")
        }
    }
}
```

**Cách dùng:**
```bash
./gradlew incrementVersion
```

---

## 🔐 CÁCH 3: Tích Hợp với Remote Config (Kiểm Soát Minimum Version)

### Bước 1: Cập nhật Remote Config trong Firebase Console

1. Vào **Firebase Console** → **Remote Config**
2. Thêm/Update parameter:
   - **Key**: `app_version_min`
   - **Type**: String
   - **Value**: `"1.1"` (version tối thiểu yêu cầu)
   - **Description**: "Minimum app version required"

### Bước 2: Tạo Version Checker

**File**: `app/src/main/java/com/example/financialmanagement/utils/VersionChecker.java`

```java
package com.example.financialmanagement.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.example.financialmanagement.R;

public class VersionChecker {
    private static final String TAG = "VersionChecker";
    
    /**
     * Kiểm tra version hiện tại có đáp ứng minimum version từ Remote Config không
     */
    public static void checkMinimumVersion(Activity activity, RemoteConfigManager configManager) {
        try {
            // Lấy version hiện tại của app
            String currentVersion = activity.getPackageManager()
                .getPackageInfo(activity.getPackageName(), 0).versionName;
            
            // Lấy minimum version từ Remote Config
            String minVersion = configManager.getMinAppVersion();
            
            if (minVersion == null || minVersion.isEmpty()) {
                Log.d(TAG, "No minimum version set in Remote Config");
                return;
            }
            
            // So sánh version
            if (isVersionLower(currentVersion, minVersion)) {
                // Version quá cũ, yêu cầu update
                showUpdateRequiredDialog(activity);
            } else {
                Log.d(TAG, "App version is up to date: " + currentVersion);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error checking version", e);
        }
    }
    
    /**
     * So sánh 2 version strings
     * @return true nếu currentVersion < minVersion
     */
    private static boolean isVersionLower(String currentVersion, String minVersion) {
        try {
            String[] current = currentVersion.split("\\.");
            String[] minimum = minVersion.split("\\.");
            
            int maxLength = Math.max(current.length, minimum.length);
            
            for (int i = 0; i < maxLength; i++) {
                int currentPart = i < current.length ? Integer.parseInt(current[i]) : 0;
                int minPart = i < minimum.length ? Integer.parseInt(minimum[i]) : 0;
                
                if (currentPart < minPart) {
                    return true; // Current version is lower
                } else if (currentPart > minPart) {
                    return false; // Current version is higher
                }
            }
            
            return false; // Versions are equal
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing version", e);
            return false;
        }
    }
    
    /**
     * Hiển thị dialog yêu cầu update
     */
    private static void showUpdateRequiredDialog(Activity activity) {
        new AlertDialog.Builder(activity)
            .setTitle("Cập nhật bắt buộc")
            .setMessage("Phiên bản ứng dụng của bạn quá cũ. Vui lòng cập nhật lên phiên bản mới nhất để tiếp tục sử dụng.")
            .setCancelable(false)
            .setPositiveButton("Cập nhật", (dialog, which) -> {
                // Mở Play Store hoặc download link
                openUpdatePage(activity);
            })
            .setNegativeButton("Thoát", (dialog, which) -> {
                activity.finish();
            })
            .show();
    }
    
    /**
     * Mở trang update (Play Store hoặc download link)
     */
    private static void openUpdatePage(Activity activity) {
        try {
            // Mở Play Store
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
            activity.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // Nếu không có Play Store, mở browser
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName()));
            activity.startActivity(intent);
        }
    }
}
```

### Bước 3: Gọi Version Checker trong MainActivity

**File**: `app/src/main/java/com/example/financialmanagement/MainActivity.java`

```java
import com.example.financialmanagement.utils.VersionChecker;
import com.example.financialmanagement.utils.RemoteConfigManager;

@Override
protected void onResume() {
    super.onResume();
    
    // Kiểm tra version sau khi Remote Config đã fetch
    RemoteConfigManager configManager = RemoteConfigManager.getInstance(this);
    configManager.fetchConfig(new RemoteConfigManager.ConfigFetchCallback() {
        @Override
        public void onSuccess() {
            // Kiểm tra minimum version
            VersionChecker.checkMinimumVersion(MainActivity.this, configManager);
        }
        
        @Override
        public void onError(String error) {
            Log.e(TAG, "Failed to fetch config for version check: " + error);
        }
    });
}
```

---

## 📦 BUILD APK VỚI VERSION MỚI

### Option 1: Build Debug APK
```bash
./gradlew assembleDebug
```
**Output**: `app/build/outputs/apk/debug/app-debug.apk`

### Option 2: Build Release APK (Signed)
```bash
./gradlew assembleRelease
```
**Output**: `app/build/outputs/apk/release/app-release.apk`

**Lưu ý**: Cần có `keystore.properties` và keystore file để sign APK

### Option 3: Build qua Android Studio
1. **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Chọn **release** hoặc **debug**
3. Đợi build xong
4. Click **locate** để mở thư mục chứa APK

---

## 🎯 WORKFLOW CẬP NHẬT VERSION

### 1. Development Phase
```kotlin
versionCode = 1
versionName = "1.0"
```

### 2. Testing Phase
```kotlin
versionCode = 2
versionName = "1.0-beta"
```

### 3. Release Phase
```kotlin
versionCode = 3
versionName = "1.0"
```

### 4. Hotfix Phase
```kotlin
versionCode = 4
versionName = "1.0.1"
```

### 5. Feature Update
```kotlin
versionCode = 5
versionName = "1.1"
```

---

## ✅ CHECKLIST CẬP NHẬT VERSION

Trước khi publish:

- [ ] Đã tăng `versionCode` (bắt buộc)
- [ ] Đã cập nhật `versionName` (khuyến nghị)
- [ ] Đã sync project với Gradle
- [ ] Đã build và test APK mới
- [ ] Đã kiểm tra version hiển thị đúng trong Settings
- [ ] Đã cập nhật `app_version_min` trong Remote Config (nếu cần)
- [ ] Đã test version checker (nếu có)

---

## 🔍 KIỂM TRA VERSION TRONG APP

### Cách 1: Trong Code
```java
try {
    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
    String version = pInfo.versionName;
    int versionCode = pInfo.versionCode;
    Log.d(TAG, "Version: " + version + " (" + versionCode + ")");
} catch (PackageManager.NameNotFoundException e) {
    e.printStackTrace();
}
```

### Cách 2: Trong Settings Activity
```java
TextView tvVersion = findViewById(R.id.tv_version);
try {
    String versionName = getPackageManager()
        .getPackageInfo(getPackageName(), 0).versionName;
    tvVersion.setText("Version " + versionName);
} catch (PackageManager.NameNotFoundException e) {
    tvVersion.setText("Unknown");
}
```

---

## 📝 VÍ DỤ: CẬP NHẬT TỪ 1.0 → 1.1

### Bước 1: Cập nhật `app/build.gradle.kts`
```kotlin
defaultConfig {
    // ...
    versionCode = 2        // Tăng từ 1 → 2
    versionName = "1.1"     // Cập nhật từ "1.0" → "1.1"
}
```

### Bước 2: Sync Project
- File → Sync Project with Gradle Files

### Bước 3: Build APK
```bash
./gradlew assembleRelease
```

### Bước 4: Test APK mới
- Cài đặt APK trên thiết bị
- Kiểm tra Settings → About → Version = "1.1"

### Bước 5: (Tùy chọn) Cập nhật Remote Config
- Firebase Console → Remote Config
- Update `app_version_min` = `"1.1"` (nếu muốn yêu cầu user update)

---

## 🆘 TROUBLESHOOTING

### Lỗi: "Version code already used"
**Nguyên nhân**: Đã publish version code này lên Play Store
**Giải pháp**: Tăng `versionCode` lên số cao hơn

### Lỗi: "Version name không hiển thị"
**Nguyên nhân**: Chưa sync project
**Giải pháp**: File → Sync Project with Gradle Files

### Lỗi: "APK không cài được"
**Nguyên nhân**: Version code thấp hơn version đã cài
**Giải pháp**: Gỡ app cũ hoặc tăng version code

---

## 📚 Tài Liệu Tham Khảo

- [Android Versioning Guide](https://developer.android.com/studio/publish/versioning)
- [Play Store Version Requirements](https://support.google.com/googleplay/android-developer/answer/9888179)

---

**Chúc bạn cập nhật version thành công! 🎉**



