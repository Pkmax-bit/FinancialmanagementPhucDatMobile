#!/usr/bin/env python3
"""
Script test upload file/hình cho Android Task Attachment API
Test cả endpoint backend và so sánh với cách web upload
"""

import requests
import os
import sys
from pathlib import Path

# Cấu hình
BASE_URL = "http://localhost:8000/api"  # Thay đổi theo môi trường của bạn
# Hoặc: BASE_URL = "https://your-production-api.com/api"

# Token xác thực - cần lấy từ Supabase hoặc login
AUTH_TOKEN = None  # Sẽ được set sau khi login

def login_and_get_token(email: str, password: str):
    """Đăng nhập và lấy token"""
    global AUTH_TOKEN
    try:
        response = requests.post(
            f"{BASE_URL}/auth/login",
            json={"email": email, "password": password}
        )
        if response.status_code == 200:
            data = response.json()
            AUTH_TOKEN = data.get("access_token") or data.get("token")
            print(f"✅ Đăng nhập thành công")
            return AUTH_TOKEN
        else:
            print(f"❌ Lỗi đăng nhập: {response.status_code} - {response.text}")
            return None
    except Exception as e:
        print(f"❌ Lỗi kết nối: {e}")
        return None

def create_test_image():
    """Tạo file hình ảnh test (PNG đơn giản)"""
    # Tạo một PNG đơn giản 1x1 pixel
    png_data = bytes([
        0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,  # PNG signature
        0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,  # IHDR chunk
        0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,  # 1x1 pixel
        0x08, 0x02, 0x00, 0x00, 0x00, 0x90, 0x77, 0x53, 0xDE,
        0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54,  # IDAT chunk
        0x08, 0x99, 0x01, 0x01, 0x00, 0x00, 0x00, 0xFF, 0xFF,
        0x00, 0x00, 0x00, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00,
        0x00, 0x49, 0x45, 0x4E, 0x44, 0xAE, 0x42, 0x60, 0x82  # IEND
    ])
    
    test_file = Path("test_image.png")
    test_file.write_bytes(png_data)
    return test_file

def create_test_file():
    """Tạo file text test"""
    test_file = Path("test_document.txt")
    test_file.write_text("Đây là file test upload từ script Python\nTest UTF-8: Tiếng Việt có dấu")
    return test_file

def test_upload_file(task_id: str, file_path: Path, file_type: str = "image"):
    """Test upload file giống như Android và Web"""
    if not AUTH_TOKEN:
        print("❌ Chưa có token xác thực. Vui lòng đăng nhập trước.")
        return False
    
    if not file_path.exists():
        print(f"❌ File không tồn tại: {file_path}")
        return False
    
    print(f"\n📤 Test upload {file_type}: {file_path.name}")
    print(f"   Kích thước: {file_path.stat().st_size} bytes")
    
    try:
        # Cách 1: Giống Web - dùng FormData với requests
        with open(file_path, 'rb') as f:
            files = {'file': (file_path.name, f, get_content_type(file_path))}
            headers = {
                'Authorization': f'Bearer {AUTH_TOKEN}'
            }
            
            url = f"{BASE_URL}/tasks/{task_id}/attachments"
            print(f"   URL: {url}")
            
            response = requests.post(url, files=files, headers=headers)
            
            print(f"   Status Code: {response.status_code}")
            
            if response.status_code == 200 or response.status_code == 201:
                data = response.json()
                print(f"✅ Upload thành công!")
                print(f"   File URL: {data.get('file_url', 'N/A')}")
                print(f"   File Name: {data.get('file_name', 'N/A')}")
                print(f"   Original Name: {data.get('original_file_name', 'N/A')}")
                print(f"   File Type: {data.get('file_type', 'N/A')}")
                print(f"   File Size: {data.get('file_size', 'N/A')} bytes")
                return True
            else:
                print(f"❌ Upload thất bại!")
                print(f"   Response: {response.text}")
                return False
                
    except Exception as e:
        print(f"❌ Lỗi khi upload: {e}")
        import traceback
        traceback.print_exc()
        return False

def get_content_type(file_path: Path):
    """Xác định Content-Type từ extension"""
    ext = file_path.suffix.lower()
    content_types = {
        '.png': 'image/png',
        '.jpg': 'image/jpeg',
        '.jpeg': 'image/jpeg',
        '.gif': 'image/gif',
        '.pdf': 'application/pdf',
        '.txt': 'text/plain',
        '.doc': 'application/msword',
        '.docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        '.xls': 'application/vnd.ms-excel',
        '.xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    }
    return content_types.get(ext, 'application/octet-stream')

def test_multiple_uploads(task_id: str):
    """Test upload nhiều file"""
    print("\n" + "="*60)
    print("TEST UPLOAD NHIỀU FILE")
    print("="*60)
    
    # Tạo test files
    test_image = create_test_image()
    test_file = create_test_file()
    
    results = []
    
    # Test upload hình
    results.append(("Hình ảnh", test_upload_file(task_id, test_image, "image")))
    
    # Test upload file
    results.append(("File text", test_upload_file(task_id, test_file, "file")))
    
    # Cleanup
    if test_image.exists():
        test_image.unlink()
    if test_file.exists():
        test_file.unlink()
    
    # Tổng kết
    print("\n" + "="*60)
    print("KẾT QUẢ TEST")
    print("="*60)
    for file_type, success in results:
        status = "✅ THÀNH CÔNG" if success else "❌ THẤT BẠI"
        print(f"{file_type}: {status}")
    
    return all(result[1] for result in results)

def main():
    """Hàm main"""
    print("="*60)
    print("TEST UPLOAD FILE/HÌNH CHO ANDROID TASK ATTACHMENT API")
    print("="*60)
    
    # Yêu cầu thông tin
    print("\n📋 Cấu hình test:")
    email = input("Email đăng nhập (hoặc Enter để bỏ qua): ").strip()
    password = input("Password (hoặc Enter để bỏ qua): ").strip()
    task_id = input("Task ID để test upload: ").strip()
    
    if not task_id:
        print("❌ Cần Task ID để test upload")
        return
    
    # Đăng nhập nếu có thông tin
    if email and password:
        token = login_and_get_token(email, password)
        if not token:
            print("❌ Không thể đăng nhập. Kiểm tra lại thông tin.")
            return
    elif AUTH_TOKEN:
        print(f"✅ Sử dụng token hiện có")
    else:
        print("⚠️  Chưa có token. Bạn có thể set AUTH_TOKEN trong code hoặc đăng nhập.")
        use_existing = input("Bạn có muốn tiếp tục test với token hiện có? (y/n): ").strip().lower()
        if use_existing != 'y':
            return
    
    # Test upload
    success = test_multiple_uploads(task_id)
    
    if success:
        print("\n✅ TẤT CẢ TEST ĐỀU THÀNH CÔNG!")
    else:
        print("\n❌ MỘT SỐ TEST THẤT BẠI. Kiểm tra lại.")

if __name__ == "__main__":
    main()


<<<<<<< HEAD



=======
>>>>>>> parent of 8b1e2712 (chỉnh sửa , tối ưu và báo giá)
