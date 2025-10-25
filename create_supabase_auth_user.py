#!/usr/bin/env python3
import requests
import json

# Create user directly in Supabase Auth
supabase_url = "https://mfmijckzlhevduwfigkl.supabase.co"
supabase_anon_key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1mbWlqY2t6bGhldmR1d2ZpZ2tsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTY1MzkxMTIsImV4cCI6MjA3MjExNTExMn0.VPFmvLghhO32JybxDzq-CGVQedgI-LN7Q07rwDhxU4E"

# Create user in Supabase Auth
auth_url = f"{supabase_url}/auth/v1/signup"
headers = {
    "apikey": supabase_anon_key,
    "Content-Type": "application/json"
}

user_data = {
    "email": "admin@phucdat.com",
    "password": "admin123",
    "data": {
        "full_name": "Admin User",
        "role": "admin"
    }
}

try:
    print("🔍 Creating user in Supabase Auth...")
    response = requests.post(auth_url, json=user_data, headers=headers)
    print(f'Supabase Auth signup response: {response.status_code}')
    print(f'Response: {response.text}')
    
    if response.status_code == 200:
        data = response.json()
        print(f'✅ User created in Supabase Auth!')
        print(f'User ID: {data.get("user", {}).get("id", "N/A")}')
        
        # Now test login
        login_url = f"{supabase_url}/auth/v1/token?grant_type=password"
        login_data = {
            "email": "admin@phucdat.com",
            "password": "admin123"
        }
        
        print("\n🔍 Testing login with Supabase Auth...")
        login_response = requests.post(login_url, json=login_data, headers=headers)
        print(f'Login response: {login_response.status_code}')
        
        if login_response.status_code == 200:
            login_data = login_response.json()
            print(f'✅ Supabase Auth login successful!')
            print(f'Access token: {login_data.get("access_token", "N/A")[:50]}...')
            
            # Test with backend API
            backend_headers = {
                "Authorization": f"Bearer {login_data.get('access_token')}",
                "Content-Type": "application/json"
            }
            
            print("\n🔍 Testing backend API with Supabase Auth token...")
            projects_response = requests.get('http://192.168.1.17:8000/api/projects', headers=backend_headers)
            print(f'Backend projects response: {projects_response.status_code}')
            if projects_response.status_code == 200:
                projects = projects_response.json()
                print(f'✅ Backend API works with Supabase Auth token!')
                print(f'Projects loaded: {len(projects)} projects')
            else:
                print(f'❌ Backend API failed: {projects_response.text}')
        else:
            print(f'❌ Supabase Auth login failed: {login_response.text}')
    else:
        print(f'❌ Supabase Auth signup failed: {response.text}')
        
except Exception as e:
    print(f'❌ Error: {e}')
