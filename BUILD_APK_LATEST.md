# 📦 Hướng dẫn Build APK Mới Nhất

## 📊 Trạng thái hiện tại

### File APK hiện có:
- **File**: `backend/apk_releases/app-release-v1.0.apk`
- **Size**: 7.2 MB
- **Last Modified**: 12/18/2025 9:00:44 AM

### Database:
- **Version**: 1.0 (Code: 1)
- **File Size**: 29,707,823 bytes (~28.33 MB)
- **Download URL**: Google Drive link

**⚠️ Lưu ý**: File APK trong database (28.33 MB) lớn hơn file local (7.2 MB), có thể là file đã được build lại hoặc có thay đổi.

---

## 🔨 Cách Build APK Mới

### **Cách 1: Sử dụng Script tự động (Khuyến nghị)**

```bash
cd C:\Projects\FinancialmanagementPhucDatMobile
.\build-release.bat
```

Script sẽ:
1. Build release APK
2. Copy file vào `backend/apk_releases/`
3. Tự động đặt tên theo version

### **Cách 2: Build thủ công qua Android Studio**

1. Mở project trong Android Studio
2. **Build** → **Generate Signed Bundle / APK**
3. Chọn **APK**
4. Chọn keystore và nhập password
5. Chọn **release** build variant
6. Click **Finish**
7. APK sẽ được tạo tại: `app/build/outputs/apk/release/app-release.apk`

### **Cách 3: Build qua Gradle Command Line**

```bash
cd C:\Projects\FinancialmanagementPhucDatMobile

# Build release APK
.\gradlew.bat assembleRelease

# Hoặc nếu dùng wrapper
gradlew assembleRelease
```

APK sẽ được tạo tại: `app/build/outputs/apk/release/app-release.apk`

---

## 📝 Cập nhật Version trước khi Build

### 1. Cập nhật version trong `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2        // Tăng lên (2, 3, 4...)
    versionName = "1.1"   // Version name mới
}
```

### 2. Build APK:

```bash
.\build-release.bat
```

### 3. Upload và cập nhật database:

```bash
# Option A: Upload lên Supabase Storage (nếu file < giới hạn)
python backend/scripts/upload_new_apk_and_update_version.py \
  --code 2 \
  --name "1.1" \
  --notes "Version 1.1 - New features"

# Option B: Dùng Google Drive (nếu file quá lớn)
# 1. Upload APK lên Google Drive
# 2. Lấy direct download link
# 3. Cập nhật database:
python backend/scripts/update_version_with_google_drive.py \
  --code 2 \
  --url "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID" \
  --notes "Version 1.1 - New features"
```

---

## 🔍 Kiểm tra APK đã build

### Kiểm tra file:

```bash
# Windows PowerShell
Get-ChildItem -Path "app\build\outputs\apk\release" -Filter "*.apk"

# Hoặc
dir app\build\outputs\apk\release\*.apk
```

### Kiểm tra thông tin APK:

```bash
# Sử dụng aapt (Android Asset Packaging Tool)
# Cần có Android SDK installed
aapt dump badging app-release.apk | findstr "versionCode versionName"
```

---

## 📋 Checklist trước khi Build

- [ ] Đã cập nhật `versionCode` trong `build.gradle.kts`
- [ ] Đã cập nhật `versionName` trong `build.gradle.kts`
- [ ] Đã test app trên thiết bị/emulator
- [ ] Đã kiểm tra keystore file tồn tại
- [ ] Đã có `keystore.properties` với thông tin đúng

---

## 🚀 Quy trình hoàn chỉnh

### Bước 1: Cập nhật Version
```kotlin
// app/build.gradle.kts
versionCode = 2
versionName = "1.1"
```

### Bước 2: Build APK
```bash
.\build-release.bat
```

### Bước 3: Copy APK
```bash
# APK sẽ được copy tự động vào backend/apk_releases/
# Hoặc copy thủ công:
copy app\build\outputs\apk\release\app-release.apk backend\apk_releases\app-release-v1.1.apk
```

### Bước 4: Upload và cập nhật Database
```bash
python backend/scripts/update_version_with_google_drive.py \
  --code 2 \
  --url "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID" \
  --notes "Version 1.1 - New features"
```

---

## ⚠️ Lưu ý

1. **Version Code phải tăng dần**: 1 → 2 → 3 → 4...
2. **Keystore phải giống nhau**: Dùng cùng keystore cho mọi release
3. **Test trước khi release**: Đảm bảo app hoạt động đúng
4. **File size**: Nếu > 28 MB, dùng Google Drive thay vì Supabase Storage

---

## ✅ Kết quả mong đợi

Sau khi build và upload:
- ✅ APK file mới trong `backend/apk_releases/`
- ✅ Database có version mới với `is_active = true`
- ✅ Frontend tự động hiển thị version mới
- ✅ User có thể download version mới từ Settings page



