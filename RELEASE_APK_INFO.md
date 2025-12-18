# Thông tin Release APK

## File APK đã được tạo

✅ **Release APK (Signed)**: `app-release.apk`
- **Vị trí**: `app\build\outputs\apk\release\app-release.apk`
- **Kích thước**: ~7.3 MB
- **Version Code**: 1
- **Version Name**: 1.0
- **Đã được signed**: ✅ Có

✅ **Copy cho OTA Update**: `backend/apk_releases/app-release-v1.0.apk`

✅ **Backup copy**: `app-release-v1.0-signed.apk` (ở root project)

## Keystore Information

**File**: `financial-management-release.jks`
- **Alias**: financial-management
- **Algorithm**: RSA 2048-bit
- **Validity**: 10,000 days (~27 years)
- **Password**: phucdat2024 (cả store và key)

⚠️ **QUAN TRỌNG**: 
- **PHẢI** backup file `financial-management-release.jks` và password
- **KHÔNG** commit keystore lên Git (đã có trong .gitignore)
- Mất keystore = không thể update app lên Play Store!

## Cách sử dụng APK

### 1. Cài đặt trực tiếp lên thiết bị Android

**Cách 1: Copy file vào điện thoại**
1. Copy file `app-release.apk` vào điện thoại
2. Mở File Manager trên điện thoại
3. Tap vào file APK để cài đặt
4. Cho phép "Install from unknown sources" nếu được hỏi

**Cách 2: Dùng ADB**
```bash
adb install app\build\outputs\apk\release\app-release.apk
```

**Cách 3: Dùng Android Studio**
1. Kết nối điện thoại qua USB
2. Enable USB Debugging
3. Run → Run 'app'

### 2. Phát hành qua OTA Update

APK đã được copy vào `backend/apk_releases/app-release-v1.0.apk`

Khi có bản cập nhật mới:
1. Build APK mới với version code cao hơn
2. Copy vào `backend/apk_releases/` với tên mới
3. Cập nhật `APP_VERSION_CODE` trong `backend/routers/app_updates.py`
4. Deploy backend
5. App sẽ tự động hiển thị dialog update

## Lần sau khi build Release APK

```bash
cd C:\Projects\FinancialmanagementPhucDatMobile

# 1. Tăng version trong app/build.gradle.kts
# versionCode = 2
# versionName = "1.1"

# 2. Build release APK
.\gradlew.bat assembleRelease

# 3. Copy APK cho OTA update
Copy-Item app\build\outputs\apk\release\app-release.apk ..\Financial-management-Phuc-Dat\backend\apk_releases\app-release-v1.1.apk

# 4. Backup APK
Copy-Item app\build\outputs\apk\release\app-release.apk app-release-v1.1-signed.apk
```

## Upload lên Google Play Store

Nếu muốn upload lên Play Store:
1. Build AAB (Android App Bundle) thay vì APK:
   ```bash
   .\gradlew.bat bundleRelease
   ```
2. File AAB: `app\build\outputs\bundle\release\app-release.aab`
3. Upload file AAB lên Google Play Console
4. **QUAN TRỌNG**: Phải dùng cùng keystore cho mọi lần upload!

## Bảo mật

- ✅ APK đã được signed với release keystore
- ✅ Keystore password: phucdat2024
- ⚠️ **KHÔNG** chia sẻ keystore file và password
- ⚠️ **PHẢI** backup keystore ở nhiều nơi an toàn

## Troubleshooting

### Lỗi: "App not installed"
- Kiểm tra xem đã gỡ app cũ chưa (nếu version code thấp hơn)
- Kiểm tra signature có khớp không
- Thử cài đặt lại

### Lỗi: "Package appears to be corrupt"
- Build lại APK
- Kiểm tra keystore có đúng không

### Muốn test trên nhiều thiết bị
- Copy APK vào nhiều thiết bị
- Hoặc host APK trên server và share link download

