# Hướng Dẫn Test Upload Hình và File

## Cách Test Upload trên Android

### 1. Test trên App Android (Cách chính)

#### Bước 1: Mở Task Detail
- Mở ứng dụng Android
- Chọn một Task bất kỳ để xem chi tiết
- Scroll xuống phần "Files" hoặc "Attachments"

#### Bước 2: Upload Hình Ảnh
1. Click nút "Thêm file" hoặc "Add Attachment"
2. Chọn "Chọn hình ảnh" (Add Image)
3. Chọn một hoặc nhiều hình ảnh từ gallery
4. Quan sát:
   - ✅ File hiển thị trong danh sách với trạng thái "Đang tải..."
   - ✅ Sau khi upload thành công, trạng thái chuyển sang "Thành công"
   - ✅ File URL được hiển thị
   - ✅ Có thể click vào file để mở/xem

#### Bước 3: Upload File (PDF, DOC, etc.)
1. Click nút "Thêm file"
2. Chọn "Chọn tệp" (Add File)
3. Chọn file từ file manager
4. Quan sát tương tự như upload hình

#### Bước 4: Kiểm tra Logcat
Mở Android Studio Logcat và filter theo tag:
- `UPLOAD_DEBUG` - Xem log chi tiết upload
- `API_DEBUG` - Xem request/response
- `API_REQUEST` - Xem request details
- `API_RESPONSE` - Xem response details

**Logs cần kiểm tra:**
```
UPLOAD_DEBUG: File: image.jpg, MIME: image/jpeg, Size: 123456
API_REQUEST: URL: http://.../api/tasks/{taskId}/attachments
API_REQUEST: Method: POST
API_RESPONSE: Code: 200
UPLOAD_DEBUG: Response code: 200, message: OK
```

### 2. Test bằng Script Python

#### Cài đặt dependencies:
```bash
pip install requests
```

#### Chạy script:
```bash
cd FinancialmanagementPhucDatMobile
python test_upload_file.py
```

#### Input cần thiết:
- Email và password để đăng nhập (hoặc set AUTH_TOKEN trong code)
- Task ID để test upload

#### Script sẽ:
1. Đăng nhập và lấy token
2. Tạo test files (hình PNG và file text)
3. Upload từng file
4. Hiển thị kết quả chi tiết

### 3. Test bằng cURL (Command Line)

#### Test upload hình:
```bash
curl -X POST "http://localhost:8000/api/tasks/{TASK_ID}/attachments" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

#### Test upload file:
```bash
curl -X POST "http://localhost:8000/api/tasks/{TASK_ID}/attachments" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/document.pdf"
```

### 4. Kiểm tra Backend

#### Kiểm tra database:
```sql
SELECT * FROM task_attachments 
WHERE task_id = '{TASK_ID}' 
ORDER BY created_at DESC;
```

#### Kiểm tra Storage:
- Vào Supabase Storage
- Kiểm tra bucket `task-attachments` hoặc tương tự
- Xem file đã được upload chưa

## Các Lỗi Thường Gặp và Cách Xử Lý

### 1. Lỗi "Không thể mở file"
**Nguyên nhân:** 
- File URI không hợp lệ
- Quyền truy cập file bị từ chối

**Giải pháp:**
- Kiểm tra quyền READ_EXTERNAL_STORAGE trong AndroidManifest.xml
- Kiểm tra file URI có hợp lệ không

### 2. Lỗi "Upload failed: 401 Unauthorized"
**Nguyên nhân:**
- Token hết hạn hoặc không hợp lệ

**Giải pháp:**
- Đăng nhập lại để lấy token mới
- Kiểm tra AuthInterceptor có thêm token đúng không

### 3. Lỗi "Upload failed: 413 Payload Too Large"
**Nguyên nhân:**
- File quá lớn

**Giải pháp:**
- Kiểm tra giới hạn file size (hiện tại là 10MB)
- Nén file trước khi upload

### 4. Lỗi "File read error"
**Nguyên nhân:**
- Không đọc được file từ URI
- File bị corrupt

**Giải pháp:**
- Kiểm tra file có tồn tại không
- Thử chọn file khác

### 5. Lỗi "Network error"
**Nguyên nhân:**
- Mất kết nối mạng
- Server không phản hồi

**Giải pháp:**
- Kiểm tra kết nối internet
- Kiểm tra BASE_URL trong NetworkConfig
- Kiểm tra server có đang chạy không

## Checklist Test

- [ ] Upload hình ảnh đơn (1 file)
- [ ] Upload nhiều hình ảnh cùng lúc
- [ ] Upload file PDF
- [ ] Upload file Word (.doc, .docx)
- [ ] Upload file Excel (.xls, .xlsx)
- [ ] Upload file text (.txt)
- [ ] Upload file lớn (>5MB)
- [ ] Upload file có tên tiếng Việt
- [ ] Kiểm tra file hiển thị sau khi upload
- [ ] Kiểm tra có thể mở/xem file sau khi upload
- [ ] Test với mạng chậm
- [ ] Test với mất kết nối giữa chừng
- [ ] Test với file không hợp lệ

## So Sánh Web vs Android

| Tính năng | Web | Android |
|-----------|-----|---------|
| Upload đơn | ✅ | ✅ |
| Upload nhiều | ✅ | ✅ |
| Progress indicator | ✅ | ✅ |
| Error handling | ✅ | ✅ |
| File preview | ✅ | ✅ |
| Retry on error | ❌ | ❌ (có thể thêm) |

## Kết Quả Mong Đợi

Sau khi test thành công, bạn sẽ thấy:
1. ✅ File được upload lên server
2. ✅ File URL được trả về
3. ✅ File hiển thị trong danh sách attachments
4. ✅ Có thể click để mở/xem file
5. ✅ File được lưu trong database và storage


<<<<<<< HEAD



=======
>>>>>>> parent of 8b1e2712 (chỉnh sửa , tối ưu và báo giá)
