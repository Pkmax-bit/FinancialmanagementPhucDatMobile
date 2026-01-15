# APK Build Information

## ✅ Build Thành Công!

**Ngày build:** $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

## File APK

### Release APK (Đã ký - Sẵn sàng cài đặt)
- **File:** `financial-management-release-v1.0.apk`
- **Vị trí:** `C:\Projects\FinancialmanagementPhucDatMobile\financial-management-release-v1.0.apk`
- **Hoặc:** `app\build\outputs\apk\release\app-release.apk`

### Thông tin APK
- **Application ID:** `com.example.financialmanagement`
- **Version Code:** 1
- **Version Name:** 1.0
- **Min SDK:** 21 (Android 5.0 Lollipop)
- **Target SDK:** 34 (Android 14)
- **Signing:** ✅ Đã ký với keystore

## Cách Cài Đặt APK

### Cách 1: Copy vào điện thoại
1. Copy file `financial-management-release-v1.0.apk` vào điện thoại Android
2. Mở File Manager trên điện thoại
3. Tìm và tap vào file APK
4. Cho phép "Install from unknown sources" nếu được hỏi
5. Tap "Install"

### Cách 2: Dùng ADB (Android Debug Bridge)
```bash
adb install financial-management-release-v1.0.apk
```

### Cách 3: Dùng USB
1. Kết nối điện thoại với máy tính qua USB
2. Copy file APK vào thư mục Download trên điện thoại
3. Mở File Manager trên điện thoại và cài đặt

## Lưu Ý

1. **Cho phép cài đặt từ nguồn không xác định:**
   - Vào Settings → Security → Enable "Unknown Sources" hoặc "Install unknown apps"
   - Hoặc khi cài đặt, Android sẽ hỏi và cho phép tạm thời

2. **Yêu cầu hệ thống:**
   - Android 5.0 (API 21) trở lên
   - Kết nối internet để sử dụng các tính năng

3. **Permissions cần thiết:**
   - Internet (để kết nối API)
   - Camera (để chụp ảnh, quét QR)
   - Storage (để lưu file)
   - Location (nếu có tính năng liên quan)

## Build Lại APK

Nếu cần build lại:

```bash
cd C:\Projects\FinancialmanagementPhucDatMobile
.\gradlew.bat assembleRelease
```

File APK sẽ được tạo tại:
```
app\build\outputs\apk\release\app-release.apk
```

## Troubleshooting

### Lỗi: "App not installed"
- Kiểm tra xem đã cho phép "Install from unknown sources" chưa
- Đảm bảo điện thoại có đủ dung lượng
- Thử gỡ cài đặt phiên bản cũ trước (nếu có)

### Lỗi: "Package appears to be corrupt"
- Build lại APK
- Kiểm tra keystore file có đúng không

### APK không mở được
- Kiểm tra Android version (cần >= 5.0)
- Thử cài đặt trên thiết bị khác

## Thông Tin Build

- **Build Type:** Release
- **Signing:** ✅ Signed với keystore
- **Minify:** Disabled (để dễ debug)
- **ProGuard:** Disabled

## Next Steps

1. Test APK trên nhiều thiết bị Android khác nhau
2. Kiểm tra tất cả tính năng hoạt động đúng
3. Nếu cần, có thể build AAB (Android App Bundle) để upload lên Play Store:
   ```bash
   .\gradlew.bat bundleRelease
   ```



