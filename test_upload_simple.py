#!/usr/bin/env python3
"""
Script test đơn giản để test upload file
"""

import requests
import json

# Cấu hình - THAY ĐỔI CÁC GIÁ TRỊ NÀY
BASE_URL = "http://localhost:8000/api"  # URL của API backend
TASK_ID = "your-task-id-here"  # ID của task để test
AUTH_TOKEN = "your-token-here"  # Token từ Supabase

def test_upload_image():
    """Test upload hình ảnh"""
    print("📤 Test upload hình ảnh...")
    
    # Tạo file test đơn giản
    test_data = b'\x89PNG\r\n\x1a\n'  # PNG header đơn giản
    
    files = {
        'file': ('test_image.png', test_data, 'image/png')
    }
    
    headers = {
        'Authorization': f'Bearer {AUTH_TOKEN}'
    }
    
    try:
        response = requests.post(
            f"{BASE_URL}/tasks/{TASK_ID}/attachments",
            files=files,
            headers=headers
        )
        
        print(f"Status: {response.status_code}")
        if response.status_code in [200, 201]:
            print("✅ Upload thành công!")
            print(json.dumps(response.json(), indent=2, ensure_ascii=False))
            return True
        else:
            print(f"❌ Upload thất bại: {response.text}")
            return False
    except Exception as e:
        print(f"❌ Lỗi: {e}")
        return False

def test_upload_file():
    """Test upload file"""
    print("\n📤 Test upload file...")
    
    # Tạo file text test
    test_data = "Đây là file test upload\nTest UTF-8: Tiếng Việt".encode('utf-8')
    
    files = {
        'file': ('test_file.txt', test_data, 'text/plain')
    }
    
    headers = {
        'Authorization': f'Bearer {AUTH_TOKEN}'
    }
    
    try:
        response = requests.post(
            f"{BASE_URL}/tasks/{TASK_ID}/attachments",
            files=files,
            headers=headers
        )
        
        print(f"Status: {response.status_code}")
        if response.status_code in [200, 201]:
            print("✅ Upload thành công!")
            print(json.dumps(response.json(), indent=2, ensure_ascii=False))
            return True
        else:
            print(f"❌ Upload thất bại: {response.text}")
            return False
    except Exception as e:
        print(f"❌ Lỗi: {e}")
        return False

if __name__ == "__main__":
    print("=" * 60)
    print("TEST UPLOAD FILE/HÌNH")
    print("=" * 60)
    print(f"BASE_URL: {BASE_URL}")
    print(f"TASK_ID: {TASK_ID}")
    print("=" * 60)
    
    if AUTH_TOKEN == "your-token-here" or TASK_ID == "your-task-id-here":
        print("\n⚠️  Vui lòng cập nhật AUTH_TOKEN và TASK_ID trong file này!")
        print("   Hoặc chạy: python test_upload_file.py (script đầy đủ hơn)")
    else:
        test_upload_image()
        test_upload_file()


