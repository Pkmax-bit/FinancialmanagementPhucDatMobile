# Hướng dẫn sử dụng file JSON để cấu hình Remote Config

## 📁 Các file JSON đã tạo

### 1. `firebase_remote_config_template.json`
- File JSON đầy đủ theo format của Firebase API
- Có thể dùng để import qua Firebase CLI hoặc REST API
- Bao gồm metadata và descriptions

### 2. `firebase_remote_config_simple.json`
- File JSON đơn giản, dễ đọc
- Chỉ chứa key-value pairs
- Dùng để tham khảo hoặc copy-paste thủ công

---

## 🔧 Cách sử dụng

### Cách 1: Copy-paste thủ công vào Firebase Console (Khuyến nghị)

#### Bước 1: Mở Remote Config
1. Vào Firebase Console
2. Click **"Build"** → **"Remote Config"**
3. Hoặc truy cập: `https://console.firebase.google.com/project/YOUR_PROJECT_ID/config`

#### Bước 2: Thêm từng parameter

Mở file `firebase_remote_config_simple.json` và thêm từng parameter:

**Parameter 1: welcome_message**
1. Click **"Add parameter"** hoặc **"Create parameter"**
2. **Parameter key**: `welcome_message`
3. **Default value**: `Xin chào, `
4. **Description**: `Welcome message hiển thị trên Dashboard`
5. Click **"Save"**

**Parameter 2: api_base_url**
1. Click **"Add parameter"**
2. **Parameter key**: `api_base_url`
3. **Default value**: `https://financial-management-backend-3m78.onrender.com`
4. **Description**: `API base URL cho backend`
5. Click **"Save"**

**Parameter 3: maintenance_mode**
1. Click **"Add parameter"**
2. **Parameter key**: `maintenance_mode`
3. **Data type**: Chọn **Boolean**
4. **Default value**: `false`
5. **Description**: `Bật/tắt chế độ bảo trì`
6. Click **"Save"**

**Parameter 4: enable_feature_x**
1. Click **"Add parameter"**
2. **Parameter key**: `enable_feature_x`
3. **Data type**: Chọn **Boolean**
4. **Default value**: `false`
5. **Description**: `Feature flag để bật/tắt tính năng X`
6. Click **"Save"**

**Parameter 5: app_version_min**
1. Click **"Add parameter"**
2. **Parameter key**: `app_version_min`
3. **Default value**: `1.0`
4. **Description**: `Version tối thiểu yêu cầu`
5. Click **"Save"**

**Parameter 6: color_primary**
1. Click **"Add parameter"**
2. **Parameter key**: `color_primary`
3. **Default value**: `#2563EB`
4. **Description**: `Màu primary của app`
5. Click **"Save"**

**Parameter 7: max_upload_size_mb**
1. Click **"Add parameter"**
2. **Parameter key**: `max_upload_size_mb`
3. **Data type**: Chọn **Number**
4. **Default value**: `10`
5. **Description**: `Kích thước upload tối đa (MB)`
6. Click **"Save"**

**Parameter 8: show_promo**
1. Click **"Add parameter"**
2. **Parameter key**: `show_promo`
3. **Data type**: Chọn **Boolean**
4. **Default value**: `false`
5. **Description**: `Hiển thị promo banner hay không`
6. Click **"Save"**

**Parameter 9: promo_message**
1. Click **"Add parameter"**
2. **Parameter key**: `promo_message`
3. **Default value**: (để trống)
4. **Description**: `Nội dung promo message`
5. Click **"Save"**

#### Bước 3: Publish
1. Sau khi thêm tất cả parameters
2. Click nút **"Publish changes"** ở góc trên bên phải
3. Xác nhận publish

---

### Cách 2: Import qua Firebase CLI (Nâng cao)

#### Bước 1: Cài đặt Firebase CLI
```bash
npm install -g firebase-tools
```

#### Bước 2: Login
```bash
firebase login
```

#### Bước 3: Chọn project
```bash
firebase use YOUR_PROJECT_ID
```

#### Bước 4: Import config
```bash
firebase remoteconfig:get -o firebase_remote_config_template.json
```

**Lưu ý**: Firebase CLI không hỗ trợ import trực tiếp từ file JSON. Bạn vẫn cần thêm thủ công qua Console.

---

### Cách 3: Sử dụng REST API (Nâng cao)

Có thể dùng Firebase REST API để set config programmatically. Xem tài liệu:
https://firebase.google.com/docs/remote-config/use-config-rest

---

## 📋 Danh sách Parameters

| Parameter Key | Data Type | Default Value | Mô tả |
|--------------|-----------|---------------|-------|
| `welcome_message` | String | `"Xin chào, "` | Welcome message trên Dashboard |
| `api_base_url` | String | `"https://financial-management-backend-3m78.onrender.com"` | API base URL |
| `maintenance_mode` | Boolean | `false` | Chế độ bảo trì |
| `enable_feature_x` | Boolean | `false` | Feature flag |
| `app_version_min` | String | `"1.0"` | Version tối thiểu |
| `color_primary` | String | `"#2563EB"` | Màu primary |
| `max_upload_size_mb` | Number | `10` | Kích thước upload tối đa (MB) |
| `show_promo` | Boolean | `false` | Hiển thị promo |
| `promo_message` | String | `""` | Nội dung promo |

---

## ✅ Checklist

Sau khi thêm tất cả parameters:

- [ ] Đã thêm `welcome_message`
- [ ] Đã thêm `api_base_url`
- [ ] Đã thêm `maintenance_mode`
- [ ] Đã thêm `enable_feature_x`
- [ ] Đã thêm `app_version_min`
- [ ] Đã thêm `color_primary`
- [ ] Đã thêm `max_upload_size_mb`
- [ ] Đã thêm `show_promo`
- [ ] Đã thêm `promo_message`
- [ ] Đã **Publish changes**

---

## 💡 Tips

1. **Thêm từng parameter một**: Dễ kiểm tra và tránh lỗi
2. **Kiểm tra data type**: Đảm bảo chọn đúng String/Boolean/Number
3. **Publish ngay**: Sau khi thêm xong, nhớ publish để app nhận được
4. **Test sau khi publish**: Chạy app và kiểm tra log để verify

---

## 🔄 Cập nhật sau này

Khi cần thay đổi giá trị:

1. Vào Firebase Console → Remote Config
2. Tìm parameter cần sửa
3. Click vào để edit
4. Thay đổi **Default value** hoặc thêm **Condition values**
5. Click **"Save"**
6. Click **"Publish changes"**

App sẽ nhận config mới trong lần fetch tiếp theo (tối đa 1 giờ) hoặc khi force fetch.



