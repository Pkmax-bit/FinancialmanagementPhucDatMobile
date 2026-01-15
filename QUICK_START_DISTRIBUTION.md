# ⚡ Hướng Dẫn Nhanh - Phân Phối App

## 🚀 3 Bước Đơn Giản

### BƯỚC 1: Build APK Release

**Cách nhanh nhất:**
```bash
# Chạy script build
build-release.bat
```

**Hoặc thủ công:**
```bash
.\gradlew assembleRelease
```

**APK sẽ ở:**
```
app\build\outputs\apk\release\app-release.apk
```

---

### BƯỚC 2: Upload APK

**Chọn 1 trong các cách:**

#### ✅ Cách 1: Google Drive (Dễ nhất)
1. Upload `app-release.apk` lên Google Drive
2. Click chuột phải → **Get link** → **Anyone with the link**
3. Copy link

#### ✅ Cách 2: GitHub Releases (Chuyên nghiệp)
1. Vào GitHub repo → **Releases** → **Create a new release**
2. Tag: `v1.0`
3. Upload APK file
4. Publish
5. Copy download link

#### ✅ Cách 3: Server riêng
1. Upload APK lên server
2. Copy link: `https://yourdomain.com/app-release.apk`

---

### BƯỚC 3: Chia Sẻ Link

**Gửi link cho user qua:**
- 📧 Email
- 💬 Zalo / Telegram
- 📱 QR Code (tạo từ link)

**User sẽ:**
1. Click link trên điện thoại
2. Download APK
3. Mở file → Cài đặt
4. Xong! ✅

---

## 📱 Tạo Trang Download Đẹp

1. Copy file `download.html` lên server
2. Đổi tên file APK trong HTML: `app-release.apk`
3. Upload cả 2 file (HTML + APK) lên cùng thư mục
4. Share link HTML cho user

**Ví dụ:**
```
https://yourdomain.com/download.html
```

---

## 🎯 Khuyến Nghị

### Cho Test Nhanh:
- ✅ Email trực tiếp file APK
- ✅ Hoặc chia sẻ qua Zalo/Telegram

### Cho Phân Phối Nội Bộ:
- ✅ Google Drive + Share link
- ✅ Hoặc GitHub Releases

### Cho Phân Phối Công Khai:
- ✅ Google Play Store (tốt nhất)
- ✅ Hoặc GitHub Releases

---

## ⚠️ Lưu Ý

1. **Luôn tăng versionCode** trước khi build APK mới
2. **Kiểm tra APK** trên thiết bị thật trước khi chia sẻ
3. **Hướng dẫn user** bật "Unknown sources" nếu cần
4. **Giữ keystore an toàn** - mất keystore = không update được app

---

## 📞 Cần Giúp?

Xem file `APP_DISTRIBUTION_GUIDE.md` để biết chi tiết hơn.

---

**Chúc bạn thành công! 🎉**



