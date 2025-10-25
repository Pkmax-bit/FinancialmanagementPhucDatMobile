#!/usr/bin/env python3
import requests
import json

# Test direct Supabase Auth
supabase_url = "https://mfmijckzlhevduwfigkl.supabase.co"
supabase_anon_key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1mbWlqY2t6bGhldmR1d2ZpZ2tsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTY1MzkxMTIsImV4cCI6MjA3MjExNTExMn0.VPFmvLghhO32JybxDzq-CGVQedgI-LN7Q07rwDhxU4E"

# Test Supabase Auth directly
auth_url = f"{supabase_url}/auth/v1/token?grant_type=password"
headers = {
    "apikey": supabase_anon_key,
    "Content-Type": "application/json"
}

login_data = {
    "email": "admin@phucdat.com",
    "password": "admin123"
}

try:
    print("🔍 Testing direct Supabase Auth...")
    response = requests.post(auth_url, json=login_data, headers=headers)
    print(f'Supabase Auth response: {response.status_code}')
    print(f'Response: {response.text}')
    
    if response.status_code == 200:
        data = response.json()
        print(f'✅ Supabase Auth successful!')
        print(f'Access token: {data.get("access_token", "N/A")[:50]}...')
        
        # Test with backend API using Supabase token
        backend_headers = {
            "Authorization": f"Bearer {data.get('access_token')}",
            "Content-Type": "application/json"
        }
        
        print("\n🔍 Testing backend API with Supabase token...")
        projects_response = requests.get('http://192.168.1.17:8000/api/projects', headers=backend_headers)
        print(f'Backend projects response: {projects_response.status_code}')
        if projects_response.status_code == 200:
            projects = projects_response.json()
            print(f'✅ Backend API works with Supabase token!')
            print(f'Projects loaded: {len(projects)} projects')
        else:
            print(f'❌ Backend API failed: {projects_response.text}')
    else:
        print(f'❌ Supabase Auth failed: {response.text}')
        
except Exception as e:
    print(f'❌ Error: {e}')
