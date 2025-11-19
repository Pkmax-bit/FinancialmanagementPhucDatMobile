#!/usr/bin/env python3
import requests
import json

# Test user registration with proper email
register_data = {
    "email": "admin@phucdat.com",
    "password": "admin123",
    "full_name": "Admin User",
    "role": "admin"
}

base_url = 'http://192.168.1.17:8000/api/auth/register'

try:
    print("🔍 Testing user registration with proper email...")
    response = requests.post(base_url, json=register_data)
    print(f'Register response: {response.status_code}')
    print(f'Response: {response.text}')
    
    if response.status_code == 200:
        print("✅ User created successfully!")
        
        # Now test login
        login_data = {
            "email": "admin@phucdat.com",
            "password": "admin123"
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
                if len(projects) > 0:
                    print(f'Sample project: {projects[0].get("name", "N/A")}')
            else:
                print(f'❌ Projects failed: {projects_response.text}')
                
            # Test quotes
            print("\n🔍 Testing quotes with auth...")
            quotes_response = requests.get('http://192.168.1.17:8000/api/sales/quotes', headers=headers)
            print(f'Quotes response: {quotes_response.status_code}')
            if quotes_response.status_code == 200:
                quotes = quotes_response.json()
                print(f'✅ Quotes loaded: {len(quotes)} quotes')
            else:
                print(f'❌ Quotes failed: {quotes_response.text}')
                
            # Test invoices
            print("\n🔍 Testing invoices with auth...")
            invoices_response = requests.get('http://192.168.1.17:8000/api/sales/invoices', headers=headers)
            print(f'Invoices response: {invoices_response.status_code}')
            if invoices_response.status_code == 200:
                invoices = invoices_response.json()
                print(f'✅ Invoices loaded: {len(invoices)} invoices')
            else:
                print(f'❌ Invoices failed: {invoices_response.text}')
                
            # Test project expenses
            print("\n🔍 Testing project expenses with auth...")
            expenses_response = requests.get('http://192.168.1.17:8000/api/project-expenses', headers=headers)
            print(f'Project expenses response: {expenses_response.status_code}')
            if expenses_response.status_code == 200:
                expenses = expenses_response.json()
                print(f'✅ Project expenses loaded: {len(expenses)} expenses')
            else:
                print(f'❌ Project expenses failed: {expenses_response.text}')
        else:
            print(f'❌ Login failed: {login_response.text}')
    else:
        print(f'❌ Registration failed: {response.text}')
        
except Exception as e:
    print(f'❌ Error: {e}')


