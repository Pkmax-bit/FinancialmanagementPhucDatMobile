# Báo Cáo Phân Quyền Chức Năng Nhiệm Vụ - Android

## Tổng Quan

**Trạng thái:** ⚠️ **Có một phần, chưa đầy đủ**

Hiện tại chỉ có phân quyền cho **Checklist/Subtasks**, các chức năng khác chưa có phân quyền.

---

## ✅ Đã Có Phân Quyền

### 1. Checklist/Subtasks ✅

**Các method kiểm tra quyền:**
- `isAdmin()` - Kiểm tra user có phải admin không
- `isResponsibleInTask()` - Kiểm tra user có role "responsible" trong task không
- `canManageChecklistItems()` - Admin hoặc Responsible có thể quản lý checklist
- `canCompleteChecklistItem()` - Admin, Responsible, hoặc người được giao có thể đánh dấu hoàn thành

**Áp dụng phân quyền:**
- ✅ Ẩn nút "Thêm Checklist" nếu không có quyền
- ✅ Ẩn nút Edit/Delete checklist nếu không có quyền
- ✅ Ẩn nút Edit/Delete checklist item nếu không có quyền
- ✅ Enable/Disable checkbox hoàn thành dựa trên quyền
- ✅ Chỉ người được giao, admin, hoặc responsible mới đánh dấu hoàn thành được

**Code location:**
- `TaskDetailActivity.java` - Lines 1014-1019, 1157-1160, 1181-1184, 1215-1221, 1294-1300, 2240-2294

---

## ❌ Chưa Có Phân Quyền

### 1. Tạo/Sửa/Xóa Nhiệm Vụ ❌
- Chưa có chức năng này nên chưa có phân quyền
- **Cần làm:** Khi implement, cần check quyền trước khi cho phép tạo/sửa/xóa

### 2. Chat/Trao Đổi ❌
- Không có kiểm tra quyền trước khi gửi tin nhắn
- Không có kiểm tra quyền trước khi edit/delete tin nhắn
- **Cần làm:** 
  - Chỉ cho phép gửi tin nhắn nếu là participant trong task
  - Chỉ cho phép edit/delete tin nhắn của chính mình hoặc nếu là admin/responsible

### 3. File Đính Kèm ❌
- Không có kiểm tra quyền trước khi upload file
- Không có kiểm tra quyền trước khi download/xóa file
- **Cần làm:**
  - Chỉ cho phép upload nếu là participant
  - Chỉ cho phép xóa file nếu là người upload hoặc admin/responsible

### 4. Xem Nhiệm Vụ ❌
- Hiện tại ai cũng xem được tất cả nhiệm vụ
- **Cần làm:** 
  - Chỉ cho phép xem nhiệm vụ nếu là participant hoặc admin
  - Ẩn thông tin nhạy cảm nếu không có quyền

### 5. Cập Nhật Trạng Thái Nhiệm Vụ ❌
- Không có kiểm tra quyền trước khi cập nhật status
- **Cần làm:** Chỉ admin hoặc responsible mới được cập nhật status

### 6. Ghim Nhiệm Vụ ❌
- Không có kiểm tra quyền
- **Cần làm:** Chỉ admin hoặc responsible mới được ghim

---

## 📋 Quy Tắc Phân Quyền Hiện Tại

### Roles được sử dụng:
1. **Admin** - Có tất cả quyền
2. **Responsible** - Người chịu trách nhiệm trong task (role = "responsible" trong TaskParticipant)
3. **Participant** - Người tham gia task (role = "participant")
4. **Assigned** - Người được giao checklist item

### Logic phân quyền hiện tại:
```java
// Admin có tất cả quyền
isAdmin() → true → có mọi quyền

// Responsible có quyền quản lý checklist
isResponsibleInTask() → true → có thể tạo/sửa/xóa checklist

// Người được giao có thể đánh dấu hoàn thành item
isAssignedToChecklistItem() → true → có thể complete item
```

---

## 🎯 Khuyến Nghị

### Ưu tiên CAO:
1. **Thêm phân quyền cho Chat**
   - Chỉ participant mới được gửi tin nhắn
   - Chỉ người gửi hoặc admin/responsible mới được edit/delete

2. **Thêm phân quyền cho File**
   - Chỉ participant mới được upload
   - Chỉ người upload hoặc admin/responsible mới được xóa

3. **Thêm phân quyền cho Xem Nhiệm Vụ**
   - Chỉ participant hoặc admin mới xem được
   - Ẩn thông tin nhạy cảm nếu không có quyền

### Ưu tiên TRUNG BÌNH:
4. **Thêm phân quyền cho Cập Nhật Status**
   - Chỉ admin hoặc responsible mới được cập nhật

5. **Thêm phân quyền cho Ghim Nhiệm Vụ**
   - Chỉ admin hoặc responsible mới được ghim

### Khi implement Tạo/Sửa/Xóa Task:
6. **Thêm phân quyền cho CRUD Task**
   - Chỉ admin hoặc project manager mới được tạo/sửa/xóa task

---

## 📝 Code Example - Cách Thêm Phân Quyền

### Ví dụ: Thêm phân quyền cho Chat

```java
// Trong TaskChatActivity.java
private boolean canSendMessage() {
    // Chỉ participant, admin, hoặc responsible mới được gửi
    return isParticipantInTask() || isAdmin() || isResponsibleInTask();
}

private boolean canEditMessage(TaskComment message) {
    // Chỉ người gửi, admin, hoặc responsible mới được edit
    return message.getUserId().equals(currentUserId) || 
           isAdmin() || 
           isResponsibleInTask();
}

// Áp dụng khi gửi tin nhắn
btnSend.setOnClickListener(v -> {
    if (!canSendMessage()) {
        Toast.makeText(this, "Bạn không có quyền gửi tin nhắn", Toast.LENGTH_SHORT).show();
        return;
    }
    sendMessage();
});
```

---

## Kết Luận

**Hiện trạng:** Chỉ có phân quyền cơ bản cho Checklist, thiếu phân quyền cho các chức năng quan trọng khác.

**Rủi ro:** Người dùng có thể thực hiện các thao tác không được phép (gửi chat, upload file, xem nhiệm vụ không liên quan).

**Cần làm:** Bổ sung phân quyền cho tất cả các chức năng, đặc biệt là Chat, File, và Xem nhiệm vụ.

