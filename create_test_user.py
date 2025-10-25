#!/usr/bin/env python3
import requests
import json

# Test user registration
register_data = {
    "email": "test@example.com",
    "password": "test123",
    "full_name": "Test User",
    "role": "admin"
}

base_url = 'http://192.168.1.17:8000/api/auth/register'

try:
    print("🔍 Testing user registration...")
    response = requests.post(base_url, json=register_data)
    print(f'Register response: {response.status_code}')
    print(f'Response: {response.text}')
    
    if response.status_code == 200:
        print("✅ User created successfully!")
        
        # Now test login
        login_data = {
            "email": "test@example.com",
            "password": "test123"
        }
        
        print("\n🔍 Testing login with new user...")
        login_response = requests.post('http://192.168.1.17:8000/api/auth/login', json=login_data)
        print(f'Login response: {login_response.status_code}')
        
        if login_response.status_code == 200:
            data = login_response.json()
            print(f'✅ Login successful!')
            print(f'Token: {data.get("access_token", "N/A")[:50]}...')
            
            # Test projects with token
            headers = {
                "Authorization": f"Bearer {data.get('access_token')}",
                "Content-Type": "application/json"
            }
            
            print("\n🔍 Testing projects with auth...")
            projects_response = requests.get('http://192.168.1.17:8000/api/projects', headers=headers)
            print(f'Projects response: {projects_response.status_code}')
            if projects_response.status_code == 200:
                projects = projects_response.json()
                print(f'✅ Projects loaded: {len(projects)} projects')
            else:
                print(f'❌ Projects failed: {projects_response.text}')
        else:
            print(f'❌ Login failed: {login_response.text}')
    else:
        print(f'❌ Registration failed: {response.text}')
        
except Exception as e:
    print(f'❌ Error: {e}')
