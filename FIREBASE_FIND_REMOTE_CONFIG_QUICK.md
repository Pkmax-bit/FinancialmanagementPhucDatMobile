# Tìm Remote Config - Hướng dẫn nhanh

## 🎯 Bạn đang ở đâu?

Bạn đang ở trang **"Project Overview"** của Firebase Console.

## ✅ Cách tìm Remote Config (3 bước)

### Bước 1: Click vào "Build"
Trong trang Project Overview, bạn thấy phần:
```
Build
Run
Analytics
AI
```

→ **Click vào "Build"** (có thể là một card hoặc menu item)

### Bước 2: Tìm Remote Config
Sau khi click "Build", bạn sẽ thấy danh sách các services:
- Authentication
- Firestore Database
- Realtime Database
- Storage
- Functions
- **Remote Config** ← Tìm cái này!

### Bước 3: Click vào Remote Config
Click vào **"Remote Config"** để mở trang cấu hình.

---

## 🔄 Nếu không thấy trong "Build"

### Thử cách khác:

**Cách 1: Menu bên trái**
1. Nhìn vào **menu bên trái** (sidebar) của Firebase Console
2. Scroll xuống tìm phần **"Build"** hoặc **"Engage"**
3. Trong đó có **"Remote Config"**

**Cách 2: Thanh tìm kiếm**
1. Ở góc trên bên phải, có thanh tìm kiếm 🔍
2. Gõ: **"Remote Config"**
3. Click vào kết quả

**Cách 3: URL trực tiếp**
1. Xem **Project ID** của bạn (ở góc trên bên trái)
2. Mở URL này (thay `YOUR_PROJECT_ID`):
   ```
   https://console.firebase.google.com/project/YOUR_PROJECT_ID/config
   ```

---

## 📸 Mô tả giao diện

```
Firebase Console - Project Overview
│
├── [Firebase logo] Project Overview
│
├── Project shortcuts
│   ├── App Check
│   ├── App Hosting
│   └── Authentication
│
├── Product categories
│   ├── Build ← CLICK VÀO ĐÂY!
│   │   ├── Authentication
│   │   ├── Firestore Database
│   │   ├── Realtime Database
│   │   ├── Storage
│   │   ├── Functions
│   │   └── Remote Config ← Ở ĐÂY!
│   │
│   ├── Run
│   ├── Analytics
│   └── AI
│
└── ...
```

---

## ⚡ Quick Action

**Nếu bạn muốn nhanh nhất:**

1. Copy URL này và thay `YOUR_PROJECT_ID`:
   ```
   https://console.firebase.google.com/project/YOUR_PROJECT_ID/config
   ```

2. Hoặc tìm Project ID:
   - Nhìn vào URL hiện tại: `console.firebase.google.com/project/XXXXX/...`
   - `XXXXX` chính là Project ID

3. Paste vào trình duyệt và Enter

---

## 💡 Lưu ý

- Remote Config có thể chưa được enable lần đầu
- Khi truy cập lần đầu, Firebase sẽ tự động enable
- Nếu không thấy, thử refresh trang (F5)



