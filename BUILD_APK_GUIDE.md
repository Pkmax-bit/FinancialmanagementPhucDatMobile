# Hướng dẫn Build APK cho Android App

## 1. Build Debug APK (Đơn giản - Dùng để test)

### Cách 1: Dùng Gradle Command Line
```bash
cd C:\Projects\FinancialmanagementPhucDatMobile
.\gradlew.bat assembleDebug
```

### Cách 2: Dùng Android Studio
1. Mở project trong Android Studio
2. Menu: **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
3. Chờ build xong, click **locate** để mở thư mục chứa APK

**Vị trí file:**
```
app\build\outputs\apk\debug\app-debug.apk
```

---

## 2. Build Release APK (Cần signing - Dùng để publish)

### Bước 1: Tạo Keystore (Chỉ cần làm 1 lần)

```bash
keytool -genkey -v -keystore financial-management-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias financial-management
```

**Thông tin cần nhập:**
- Password: (nhập password mạnh, nhớ kỹ!)
- Tên, tổ chức, địa chỉ: (nhập thông tin của bạn)
- Confirm password: (nhập lại password)

**Lưu ý:** 
- File `financial-management-release.jks` sẽ được tạo
- **QUAN TRỌNG:** Lưu file này và password ở nơi an toàn! Mất file này = không thể update app lên Play Store!

### Bước 2: Tạo file `keystore.properties`

Tạo file `keystore.properties` ở thư mục gốc project:

```properties
storePassword=YOUR_STORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=financial-management
storeFile=financial-management-release.jks
```

**Lưu ý:** Thêm `keystore.properties` vào `.gitignore` để không commit lên Git!

### Bước 3: Cập nhật `app/build.gradle.kts`

Thêm vào cuối file `app/build.gradle.kts`:

```kotlin
// Load keystore properties
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = java.util.Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
}

android {
    // ... existing code ...
    
    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### Bước 4: Build Release APK

```bash
cd C:\Projects\FinancialmanagementPhucDatMobile
.\gradlew.bat assembleRelease
```

**Vị trí file:**
```
app\build\outputs\apk\release\app-release.apk
```

---

## 3. Build APK Bundle (AAB) - Cho Google Play Store

```bash
.\gradlew.bat bundleRelease
```

**Vị trí file:**
```
app\build\outputs\bundle\release\app-release.aab
```

---

## 4. Cài đặt APK lên thiết bị Android

### Cách 1: Copy file APK vào điện thoại
1. Copy file `app-debug.apk` vào điện thoại
2. Mở file manager trên điện thoại
3. Tap vào file APK để cài đặt
4. Cho phép "Install from unknown sources" nếu được hỏi

### Cách 2: Dùng ADB
```bash
adb install app\build\outputs\apk\debug\app-debug.apk
```

### Cách 3: Dùng Android Studio
1. Kết nối điện thoại qua USB
2. Enable USB Debugging trên điện thoại
3. Trong Android Studio: **Run** → **Run 'app'**

---

## 5. Kiểm tra kích thước APK

```bash
dir app\build\outputs\apk\debug\app-debug.apk
```

---

## Lưu ý quan trọng:

1. **Debug APK:**
   - Không cần signing
   - Có thể cài đặt trực tiếp
   - Không thể upload lên Play Store

2. **Release APK:**
   - Cần signing key
   - An toàn hơn, đã được optimize
   - Có thể upload lên Play Store

3. **Keystore:**
   - **PHẢI** backup keystore file và password
   - Mất keystore = không thể update app lên Play Store
   - Nên lưu ở nhiều nơi (cloud, USB, etc.)

4. **Version Code:**
   - Mỗi lần upload lên Play Store phải tăng `versionCode` trong `build.gradle.kts`
   - `versionName` có thể là bất kỳ (ví dụ: "1.0", "1.1", "2.0")

---

## Troubleshooting

### Lỗi: "Execution failed for task ':app:signReleaseBundle'"
→ Kiểm tra lại keystore.properties và đường dẫn file keystore

### Lỗi: "Keystore was tampered with, or password was incorrect"
→ Kiểm tra lại password trong keystore.properties

### APK quá lớn (>100MB)
→ Cân nhắc dùng Android App Bundle (.aab) thay vì APK

