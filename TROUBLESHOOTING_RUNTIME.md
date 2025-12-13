# Hướng dẫn Khắc phục: Build thành công nhưng App không chạy

## Các nguyên nhân phổ biến và cách khắc phục

### 1. Kiểm tra Logcat để xem lỗi cụ thể

**Cách xem logcat:**
- Mở Android Studio
- Mở tab "Logcat" ở dưới cùng
- Chạy app và xem log để tìm lỗi (thường có từ khóa: `FATAL EXCEPTION`, `crash`, `error`)

**Hoặc dùng ADB:**
```bash
adb logcat | findstr /i "financialmanagement crash exception error fatal"
```

### 2. Kiểm tra Permissions (Quyền truy cập)

App cần các quyền sau. Kiểm tra trong **Settings > Apps > Financial Management > Permissions**:
- ✅ Internet
- ✅ Network State
- ✅ Storage (nếu cần)
- ✅ Camera (nếu cần)

**Lưu ý:** Android 6.0+ (API 23+) yêu cầu xin quyền runtime. Kiểm tra xem app có xin quyền đúng cách không.

### 3. Kiểm tra Network Security Configuration

App có `android:usesCleartextTraffic="true"` trong AndroidManifest, nhưng từ Android 9+ cần file network security config.

**Tạo file:** `app/src/main/res/xml/network_security_config.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

**Thêm vào AndroidManifest.xml trong thẻ `<application>`:**
```xml
android:networkSecurityConfig="@xml/network_security_config"
```

### 4. Kiểm tra Backend Connection

App có thể đang cố kết nối đến backend nhưng không thành công.

**Kiểm tra:**
- Backend có đang chạy không?
- URL API có đúng không?
- Có thể truy cập từ thiết bị/emulator không? (localhost không hoạt động trên thiết bị thật)

**File cấu hình API:** Tìm file chứa BASE_URL hoặc API endpoint

### 5. Kiểm tra Resources (Tài nguyên)

Một số resource có thể bị thiếu hoặc lỗi.

**Kiểm tra các file sau có tồn tại:**
- ✅ `item_intro_slide.xml` - Layout cho intro slides
- ✅ `ic_dashboard.xml`, `ic_projects.xml`, `ic_customers.xml`, `ic_reports.xml` - Icons
- ✅ `dot_selected.xml`, `dot_unselected.xml` - Dot indicators
- ✅ Các gradient backgrounds: `bg_gradient_purple.xml`, `bg_gradient_blue.xml`, etc.

### 6. Kiểm tra Device/Emulator Compatibility

**Kiểm tra:**
- Min SDK: 21 (Android 5.0)
- Target SDK: 34 (Android 14)
- Device/Emulator phải có Android 5.0 trở lên

### 7. Kiểm tra Installation

**Cách cài đặt lại:**
```bash
# Gỡ app cũ
adb uninstall com.example.financialmanagement

# Cài đặt lại
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 8. Kiểm tra ProGuard/R8

Hiện tại ProGuard đã tắt (`isMinifyEnabled = false`), nên không phải vấn đề này.

### 9. Kiểm tra Application Class

App không có custom Application class, nên không phải vấn đề này.

### 10. Kiểm tra Dependencies

Tất cả dependencies đã được khai báo đúng trong `build.gradle.kts`.

**Lưu ý:** Có một số dependencies trùng lặp:
- `glide:4.15.1` và `glide:4.16.0` - Nên chỉ dùng một version
- `MPAndroidChart` được khai báo 2 lần

## Các bước Debug nhanh

1. **Xem logcat ngay khi mở app:**
   - Mở Android Studio
   - Chạy app
   - Xem Logcat để tìm lỗi cụ thể

2. **Kiểm tra xem app có cài đặt được không:**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Kiểm tra xem app có khởi động được không:**
   ```bash
   adb shell am start -n com.example.financialmanagement/.activities.IntroActivity
   ```

4. **Xem log khi app crash:**
   ```bash
   adb logcat -c  # Clear log
   adb logcat | findstr /i "financialmanagement"
   # Sau đó mở app và xem log
   ```

## Các lỗi thường gặp

### Lỗi: "App keeps stopping"
- **Nguyên nhân:** Crash ngay khi khởi động
- **Giải pháp:** Xem logcat để tìm exception cụ thể

### Lỗi: "Unable to connect to backend"
- **Nguyên nhân:** Backend không chạy hoặc URL sai
- **Giải pháp:** Kiểm tra backend và URL API

### Lỗi: "Resource not found"
- **Nguyên nhân:** Thiếu resource hoặc tên sai
- **Giải pháp:** Kiểm tra tất cả resource được reference

### Lỗi: "Permission denied"
- **Nguyên nhân:** Thiếu quyền runtime
- **Giải pháp:** Thêm code xin quyền runtime cho Android 6.0+

## Liên hệ hỗ trợ

Nếu vẫn không giải quyết được, vui lòng cung cấp:
1. Logcat output khi app crash
2. Android version của thiết bị/emulator
3. Mô tả chi tiết hành vi (app không mở? crash ngay? hiển thị màn hình đen?)

