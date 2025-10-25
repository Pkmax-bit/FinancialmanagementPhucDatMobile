#!/usr/bin/env python3
import requests
import json

# Test different login credentials
test_credentials = [
    {'email': 'admin@phucdat.com', 'password': 'admin123'},
    {'email': 'admin@example.com', 'password': 'admin123'},
    {'email': 'test@example.com', 'password': 'test123'},
    {'email': 'user@example.com', 'password': 'user123'},
    {'email': 'demo@example.com', 'password': 'demo123'},
    {'email': 'admin@phucdat.com', 'password': 'password123'},
    {'email': 'admin@example.com', 'password': 'password123'}
]

base_url = 'http://192.168.1.17:8000/api/auth/login'

for creds in test_credentials:
    try:
        response = requests.post(base_url, json=creds)
        print(f'Testing {creds["email"]}: {response.status_code}')
        if response.status_code == 200:
            print(f'✅ SUCCESS: {creds["email"]}')
            data = response.json()
            print(f'Token: {data.get("access_token", "N/A")[:50]}...')
            break
        else:
            print(f'❌ Failed: {response.text[:100]}')
    except Exception as e:
        print(f'❌ Error: {e}')
