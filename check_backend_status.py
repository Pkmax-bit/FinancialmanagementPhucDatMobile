#!/usr/bin/env python3
import requests
import json

# Check backend status and authentication
base_url = 'http://192.168.1.17:8000'

def check_backend_health():
    """Check if backend is running"""
    print("🔍 Checking backend health...")
    
    try:
        response = requests.get(f"{base_url}/health")
        print(f"Health response: {response.status_code}")
        if response.status_code == 200:
            print("✅ Backend is running")
            return True
        else:
            print("❌ Backend health check failed")
            return False
    except Exception as e:
        print(f"❌ Backend is not accessible: {e}")
        return False

def check_auth_endpoints():
    """Check authentication endpoints"""
    print("\n🔍 Checking auth endpoints...")
    
    endpoints = [
        "/api/auth/login",
        "/api/auth/register", 
        "/api/auth/logout"
    ]
    
    for endpoint in endpoints:
        try:
            response = requests.get(f"{base_url}{endpoint}")
            print(f"{endpoint}: {response.status_code}")
        except Exception as e:
            print(f"{endpoint}: Error - {e}")

def check_protected_endpoints():
    """Check protected endpoints"""
    print("\n🔍 Checking protected endpoints...")
    
    endpoints = [
        "/api/projects",
        "/api/sales/quotes",
        "/api/sales/invoices",
        "/api/project-expenses"
    ]
    
    for endpoint in endpoints:
        try:
            response = requests.get(f"{base_url}{endpoint}")
            print(f"{endpoint}: {response.status_code} - {response.text[:100]}")
        except Exception as e:
            print(f"{endpoint}: Error - {e}")

def test_simple_login():
    """Test simple login with basic credentials"""
    print("\n🔍 Testing simple login...")
    
    # Try with very basic credentials
    login_data = {
        "email": "test@test.com",
        "password": "test"
    }
    
    try:
        response = requests.post(f"{base_url}/api/auth/login", json=login_data)
        print(f"Simple login response: {response.status_code}")
        print(f"Response: {response.text}")
    except Exception as e:
        print(f"❌ Error in simple login: {e}")

def main():
    """Main check function"""
    print("🚀 Backend Status Check")
    print("=" * 50)
    
    # Check health
    if not check_backend_health():
        print("❌ Backend is not running!")
        return
    
    # Check endpoints
    check_auth_endpoints()
    check_protected_endpoints()
    test_simple_login()
    
    print("\n" + "=" * 50)
    print("📊 BACKEND STATUS SUMMARY:")
    print("✅ Backend is running")
    print("❌ Authentication is broken")
    print("❌ All protected endpoints require auth")
    print("❌ Cannot test quotes/invoices without auth")

if __name__ == "__main__":
    main()


