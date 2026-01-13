# Thông Tin Lưu Trữ File - Task Attachments

## 📦 Bucket (Supabase Storage)

**Bucket Name:** `minhchung_chiphi`

Đây là bucket chính trong Supabase Storage để lưu trữ tất cả các file attachments của tasks.

---

## 📁 Cấu Trúc Thư Mục

### Khi Task có Group ID:
```
Groups/{group_id}/Tasks/{task_id}/{filename}
```

### Khi Task không có Group ID:
```
Tasks/{task_id}/{filename}
```

---

## 📍 Ví Dụ Cụ Thể (Từ Log)

### File 1: `35.jpg`
- **Bucket:** `minhchung_chiphi`
- **Group ID:** `50491a9a-2b7b-4446-85a4-f73f34b6a3aa`
- **Task ID:** `444fd233-c8c2-4961-9ed8-b2c086ab07ec`
- **Thư mục:** `Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/`
- **File name:** `35(5).jpg` (tự động thêm số nếu trùng tên)
- **Full path:** `Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/35(5).jpg`

### File 2: `34.jpg`
- **Bucket:** `minhchung_chiphi`
- **Group ID:** `50491a9a-2b7b-4446-85a4-f73f34b6a3aa`
- **Task ID:** `444fd233-c8c2-4961-9ed8-b2c086ab07ec`
- **Thư mục:** `Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/`
- **File name:** `34(5).jpg`
- **Full path:** `Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/34(5).jpg`

---

## 🔗 URL Format

**Public URL Format:**
```
https://{supabase_project}.supabase.co/storage/v1/object/public/{bucket}/{path}
```

**Ví dụ:**
```
https://mfmijckzlhevduwfigkl.supabase.co/storage/v1/object/public/minhchung_chiphi/Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/35(5).jpg
```

---

## 📂 Cấu Trúc Thư Mục Chi Tiết

```
minhchung_chiphi/                    ← Bucket
├── Groups/                          ← Thư mục chứa các nhóm dự án
│   └── {group_id}/                  ← ID của nhóm dự án
│       └── Tasks/                   ← Thư mục chứa tasks
│           └── {task_id}/            ← ID của task
│               ├── file1.jpg        ← File attachment 1
│               ├── file2.pdf        ← File attachment 2
│               └── file3.docx       ← File attachment 3
└── Tasks/                           ← Thư mục cho tasks không có group
    └── {task_id}/                   ← ID của task
        └── file1.jpg                ← File attachment
```

---

## 🔍 Cách Xem File Trong Supabase Dashboard

1. Đăng nhập vào Supabase Dashboard
2. Chọn project của bạn
3. Vào **Storage** → **Buckets**
4. Click vào bucket `minhchung_chiphi`
5. Navigate theo cấu trúc:
   - `Groups/` → `{group_id}/` → `Tasks/` → `{task_id}/` → `{filename}`

---

## 📝 Lưu Ý

1. **Tên file tự động sanitize:**
   - Chuyển tiếng Việt có dấu → không dấu
   - Thay khoảng trắng → underscore
   - Loại bỏ ký tự đặc biệt

2. **Xử lý trùng tên:**
   - Nếu file trùng tên, tự động thêm `(2)`, `(3)`, `(4)`, ...
   - Ví dụ: `35.jpg` → `35(2).jpg` → `35(3).jpg`

3. **File size limit:**
   - Mặc định: 10MB (có thể cấu hình)

4. **File types được phép:**
   - Images: jpg, jpeg, png, gif, webp, svg
   - Documents: pdf, doc, docx, xls, xlsx, txt, rtf, ppt, pptx, csv, zip, rar

---

## 🗂️ Database Record

File cũng được lưu trong bảng `task_attachments` với các thông tin:
- `id`: UUID của attachment
- `task_id`: ID của task
- `file_name`: Tên file trên storage (có thể có suffix nếu trùng)
- `original_file_name`: Tên file gốc từ user
- `file_url`: URL công khai để truy cập file
- `file_type`: MIME type
- `file_size`: Kích thước file (bytes)
- `uploaded_by`: ID người upload
- `created_at`: Thời gian upload


