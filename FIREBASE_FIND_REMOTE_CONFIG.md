# Cách tìm Remote Config trong Firebase Console

## 🔍 Vấn đề

Bạn đang thấy các menu này trong Firebase Console:
- Build
- App Check
- App Hosting
- Authentication
- Data Connect
- Extensions
- Firestore Database
- Functions
- Hosting
- Machine Learning
- Realtime Database
- Storage

**Nhưng KHÔNG thấy "Remote Config"!**

## ✅ Giải pháp

### Cách 1: Tìm trong menu bên trái (Sidebar)

1. **Nhìn vào menu bên trái** của Firebase Console (không phải menu trên cùng)
2. **Scroll xuống** để tìm các phần:
   - **"Build"** section
   - **"Engage"** section
3. Trong các phần đó, tìm **"Remote Config"**
4. Click vào **"Remote Config"**

### Cách 2: Dùng thanh tìm kiếm

1. Ở **góc trên bên phải** Firebase Console
2. Có một **thanh tìm kiếm** (search box) với icon 🔍
3. Gõ: **"Remote Config"** hoặc **"remote config"**
4. Click vào kết quả tìm được

### Cách 3: Truy cập trực tiếp qua URL

1. Xem **Project ID** của bạn:
   - Ở góc trên bên trái Firebase Console
   - Hoặc trong URL: `console.firebase.google.com/project/YOUR_PROJECT_ID/...`

2. Copy và paste URL này vào trình duyệt (thay `YOUR_PROJECT_ID`):
   ```
   https://console.firebase.google.com/project/YOUR_PROJECT_ID/config
   ```

3. Hoặc thử:
   ```
   https://console.firebase.google.com/project/YOUR_PROJECT_ID/remoteconfig
   ```

### Cách 4: Tìm trong menu "More" hoặc "See all"

1. Nếu menu bên trái có nút **"More"** hoặc **"See all"**
2. Click vào để xem thêm options
3. Tìm **"Remote Config"** trong danh sách mở rộng

## 📍 Vị trí thường thấy

Remote Config thường nằm ở:

1. **Menu bên trái** → **"Engage"** section → **"Remote Config"**
2. Hoặc **"Build"** section → **"Remote Config"**
3. Hoặc ở cuối danh sách menu

## 🖼️ Hình ảnh mô tả

```
Firebase Console
├── Project Overview
├── Build
│   ├── Authentication
│   ├── Firestore Database
│   ├── Realtime Database
│   ├── Storage
│   ├── Functions
│   └── Remote Config  ← Ở ĐÂY!
├── Engage
│   ├── Cloud Messaging
│   └── Remote Config  ← Hoặc ở ĐÂY!
└── ...
```

## ⚠️ Nếu vẫn không thấy

### Kiểm tra:

1. **Bạn có đang ở đúng project không?**
   - Kiểm tra project name ở góc trên bên trái
   - Đảm bảo đây là project bạn vừa tạo

2. **Remote Config có thể chưa được enable**
   - Thử truy cập trực tiếp qua URL (Cách 3)
   - Firebase sẽ tự động enable khi bạn truy cập lần đầu

3. **Refresh trang**
   - Nhấn `F5` hoặc `Ctrl + R` để refresh
   - Đôi khi menu cần thời gian để load

4. **Kiểm tra quyền truy cập**
   - Đảm bảo bạn có quyền **Owner** hoặc **Editor** của project
   - Nếu chỉ có quyền Viewer, bạn không thể thấy Remote Config

## 🚀 Sau khi tìm thấy

1. Click vào **"Remote Config"**
2. Nếu lần đầu, click **"Get started"** hoặc **"Create configuration"**
3. Bắt đầu thêm parameters (xem Bước 5 trong file hướng dẫn chính)

## 💡 Tip

Nếu bạn đang dùng Firebase Console trên mobile hoặc màn hình nhỏ:
- Menu có thể bị ẩn
- Click vào icon **☰** (hamburger menu) để mở sidebar
- Scroll để tìm Remote Config



