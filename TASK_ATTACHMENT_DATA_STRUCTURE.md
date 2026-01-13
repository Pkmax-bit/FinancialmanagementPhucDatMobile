# Cấu Trúc Dữ Liệu Task Attachments

## 📊 Bảng Database: `task_attachments`

### Schema (Cấu trúc bảng)

```sql
CREATE TABLE task_attachments (
    id UUID PRIMARY KEY,                    -- ID duy nhất của attachment
    task_id UUID NOT NULL,                  -- ID của task (foreign key)
    file_name VARCHAR(255) NOT NULL,         -- Tên file trên storage (có thể có suffix nếu trùng)
    original_file_name VARCHAR(255),        -- Tên file gốc từ user (hiển thị)
    file_url TEXT NOT NULL,                 -- URL công khai để truy cập file
    file_size BIGINT,                       -- Kích thước file (bytes)
    file_type VARCHAR(100),                 -- MIME type (ví dụ: image/jpeg, application/pdf)
    uploaded_by UUID,                       -- ID người upload (foreign key đến users)
    created_at TIMESTAMP WITH TIME ZONE      -- Thời gian upload
);
```

---

## 📝 Dữ Liệu Được Lưu

### Ví dụ Record Thực Tế (Từ Log)

```json
{
    "id": "03e2e33e-3b1c-46f5-8fd1-f5c2c5d69146",
    "task_id": "444fd233-c8c2-4961-9ed8-b2c086ab07ec",
    "file_name": "35(5).jpg",
    "original_file_name": "35.jpg",
    "file_url": "https://mfmijckzlhevduwfigkl.supabase.co/storage/v1/object/public/minhchung_chiphi/Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/35(5).jpg?",
    "file_type": "image/jpeg",
    "file_size": 30621,
    "uploaded_by": "ed57da6d-f1b2-4ead-bc98-d4a4a14b5d54",
    "uploaded_by_name": "Nguyễn Văn Test Updated",
    "created_at": "2026-01-12T02:24:40.867653Z"
}
```

---

## 🔗 URL Format

### Cấu Trúc URL

```
https://{supabase_project}.supabase.co/storage/v1/object/public/{bucket}/{folder_path}/{filename}
```

### Ví Dụ URL Thực Tế

**File 1:**
```
https://mfmijckzlhevduwfigkl.supabase.co/storage/v1/object/public/minhchung_chiphi/Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/35(5).jpg?
```

**File 2:**
```
https://mfmijckzlhevduwfigkl.supabase.co/storage/v1/object/public/minhchung_chiphi/Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/34(5).jpg?
```

### Phân Tích URL

| Phần | Giá Trị | Mô Tả |
|------|---------|-------|
| Protocol | `https://` | HTTPS protocol |
| Domain | `mfmijckzlhevduwfigkl.supabase.co` | Supabase project domain |
| API Path | `/storage/v1/object/public/` | Supabase Storage public API path |
| Bucket | `minhchung_chiphi` | Tên bucket lưu trữ |
| Folder Path | `Groups/{group_id}/Tasks/{task_id}/` | Đường dẫn thư mục |
| Filename | `35(5).jpg` | Tên file trên storage |
| Query String | `?` | Dấu ? ở cuối (có thể loại bỏ) |

---

## 📂 Cấu Trúc Thư Mục

### Khi Task có Group ID:
```
minhchung_chiphi/
└── Groups/
    └── {group_id}/
        └── Tasks/
            └── {task_id}/
                └── {filename}
```

**Ví dụ:**
```
minhchung_chiphi/Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/35(5).jpg
```

### Khi Task không có Group ID:
```
minhchung_chiphi/
└── Tasks/
    └── {task_id}/
        └── {filename}
```

---

## 📋 Chi Tiết Các Trường

### 1. `id` (UUID)
- **Type:** UUID (Primary Key)
- **Mô tả:** ID duy nhất của attachment
- **Ví dụ:** `03e2e33e-3b1c-46f5-8fd1-f5c2c5d69146`

### 2. `task_id` (UUID)
- **Type:** UUID (Foreign Key → tasks.id)
- **Mô tả:** ID của task mà file này thuộc về
- **Ví dụ:** `444fd233-c8c2-4961-9ed8-b2c086ab07ec`

### 3. `file_name` (VARCHAR)
- **Type:** VARCHAR(255)
- **Mô tả:** Tên file thực tế trên storage (có thể có suffix nếu trùng tên)
- **Ví dụ:** `35(5).jpg`, `document(2).pdf`
- **Lưu ý:** File name được sanitize (loại bỏ dấu tiếng Việt, ký tự đặc biệt)

### 4. `original_file_name` (VARCHAR)
- **Type:** VARCHAR(255), nullable
- **Mô tả:** Tên file gốc từ user (để hiển thị)
- **Ví dụ:** `35.jpg`, `Tài liệu.docx`
- **Lưu ý:** Giữ nguyên tên gốc, có thể có dấu tiếng Việt

### 5. `file_url` (TEXT)
- **Type:** TEXT
- **Mô tả:** URL công khai để truy cập file
- **Format:** `https://{project}.supabase.co/storage/v1/object/public/{bucket}/{path}`
- **Ví dụ:** `https://mfmijckzlhevduwfigkl.supabase.co/storage/v1/object/public/minhchung_chiphi/Groups/.../35(5).jpg?`
- **Lưu ý:** Có thể có dấu `?` ở cuối (cần loại bỏ khi sử dụng)

### 6. `file_size` (BIGINT)
- **Type:** BIGINT, nullable
- **Mô tả:** Kích thước file tính bằng bytes
- **Ví dụ:** `30621` (bytes) = ~30 KB

### 7. `file_type` (VARCHAR)
- **Type:** VARCHAR(100), nullable
- **Mô tả:** MIME type của file
- **Ví dụ:** 
  - `image/jpeg`
  - `image/png`
  - `application/pdf`
  - `application/vnd.openxmlformats-officedocument.wordprocessingml.document`

### 8. `uploaded_by` (UUID)
- **Type:** UUID (Foreign Key → users.id), nullable
- **Mô tả:** ID của user đã upload file
- **Ví dụ:** `ed57da6d-f1b2-4ead-bc98-d4a4a14b5d54`

### 9. `created_at` (TIMESTAMP)
- **Type:** TIMESTAMP WITH TIME ZONE
- **Mô tả:** Thời gian upload file
- **Format:** ISO 8601
- **Ví dụ:** `2026-01-12T02:24:40.867653Z`

---

## 🔍 Query Database

### Xem tất cả attachments của một task:
```sql
SELECT * FROM task_attachments 
WHERE task_id = '444fd233-c8c2-4961-9ed8-b2c086ab07ec'
ORDER BY created_at DESC;
```

### Xem attachments với thông tin user:
```sql
SELECT 
    ta.*,
    u.full_name as uploaded_by_name
FROM task_attachments ta
LEFT JOIN users u ON ta.uploaded_by = u.id
WHERE ta.task_id = '444fd233-c8c2-4961-9ed8-b2c086ab07ec'
ORDER BY ta.created_at DESC;
```

### Đếm số lượng attachments:
```sql
SELECT COUNT(*) as total_attachments
FROM task_attachments
WHERE task_id = '444fd233-c8c2-4961-9ed8-b2c086ab07ec';
```

---

## 📤 API Response Format

### Khi upload thành công, API trả về:

```json
{
    "id": "03e2e33e-3b1c-46f5-8fd1-f5c2c5d69146",
    "file_name": "35(5).jpg",
    "original_file_name": "35.jpg",
    "file_url": "https://mfmijckzlhevduwfigkl.supabase.co/storage/v1/object/public/minhchung_chiphi/Groups/50491a9a-2b7b-4446-85a4-f73f34b6a3aa/Tasks/444fd233-c8c2-4961-9ed8-b2c086ab07ec/35(5).jpg?",
    "file_type": "image/jpeg",
    "file_size": 30621,
    "uploaded_by_name": "Nguyễn Văn Test Updated",
    "created_at": "2026-01-12T02:24:40.867653"
}
```

---

## ⚠️ Lưu Ý Quan Trọng

1. **URL có dấu `?` ở cuối:**
   - URL từ API có thể có dấu `?` ở cuối
   - Cần loại bỏ trước khi sử dụng: `url.replace("?", "")` hoặc `url.endsWith("?") ? url.substring(0, url.length()-1) : url`

2. **File name sanitization:**
   - Tên file được chuyển từ tiếng Việt có dấu → không dấu
   - Khoảng trắng → underscore
   - Ký tự đặc biệt bị loại bỏ

3. **Xử lý trùng tên:**
   - Nếu file trùng tên, tự động thêm `(2)`, `(3)`, `(4)`, ...
   - Ví dụ: `35.jpg` → `35(2).jpg` → `35(3).jpg`

4. **File size limit:**
   - Mặc định: 10MB
   - Có thể cấu hình trong settings

5. **File types được phép:**
   - Images: jpg, jpeg, png, gif, webp, svg
   - Documents: pdf, doc, docx, xls, xlsx, txt, rtf, ppt, pptx, csv, zip, rar

---

## 🔗 Liên Kết

- **Bucket:** `minhchung_chiphi`
- **Storage Path:** `Groups/{group_id}/Tasks/{task_id}/`
- **Public URL Format:** `https://{project}.supabase.co/storage/v1/object/public/{bucket}/{path}`


