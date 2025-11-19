#!/usr/bin/env python3
import requests
import json

# Test with confirmed users
confirmed_users = [
    {"email": "xuong@gmail.com", "password": "password123"},
    {"email": "phannguyendangkhoa0915@gmail.com", "password": "password123"},
    {"email": "worker@test.com", "password": "password123"},
    {"email": "customer@test.com", "password": "password123"},
    {"email": "transport@test.com", "password": "password123"},
    {"email": "workshop@test.com", "password": "password123"},
    {"email": "sales@example.com", "password": "password123"},
    {"email": "admin@test.com", "password": "password123"},
    {"email": "admin@example.com", "password": "password123"}
]

base_url = 'http://192.168.1.17:8000/api/auth/login'

for user in confirmed_users:
    try:
        print(f"🔍 Testing login with {user['email']}...")
        response = requests.post(base_url, json=user)
        print(f'Login response: {response.status_code}')
        
        if response.status_code == 200:
            data = response.json()
            print(f'✅ LOGIN SUCCESS with {user["email"]}!')
            print(f'Token: {data.get("access_token", "N/A")[:50]}...')
            
            # Test projects with token
            headers = {
                "Authorization": f"Bearer {data.get('access_token')}",
                "Content-Type": "application/json"
            }
            
            print(f"\n🔍 Testing projects with {user['email']}...")
            projects_response = requests.get('http://192.168.1.17:8000/api/projects', headers=headers)
            print(f'Projects response: {projects_response.status_code}')
            if projects_response.status_code == 200:
                projects = projects_response.json()
                print(f'✅ Projects loaded: {len(projects)} projects')
                if len(projects) > 0:
                    print(f'Sample project: {projects[0].get("name", "N/A")} (ID: {projects[0].get("id", "N/A")})')
            else:
                print(f'❌ Projects failed: {projects_response.text}')
                
            # Test quotes
            print(f"\n🔍 Testing quotes with {user['email']}...")
            quotes_response = requests.get('http://192.168.1.17:8000/api/sales/quotes', headers=headers)
            print(f'Quotes response: {quotes_response.status_code}')
            if quotes_response.status_code == 200:
                quotes = quotes_response.json()
                print(f'✅ Quotes loaded: {len(quotes)} quotes')
                if len(quotes) > 0:
                    print(f'Sample quote: {quotes[0].get("quote_number", "N/A")} (Project: {quotes[0].get("project_id", "N/A")})')
            else:
                print(f'❌ Quotes failed: {quotes_response.text}')
                
            # Test invoices
            print(f"\n🔍 Testing invoices with {user['email']}...")
            invoices_response = requests.get('http://192.168.1.17:8000/api/sales/invoices', headers=headers)
            print(f'Invoices response: {invoices_response.status_code}')
            if invoices_response.status_code == 200:
                invoices = invoices_response.json()
                print(f'✅ Invoices loaded: {len(invoices)} invoices')
                if len(invoices) > 0:
                    print(f'Sample invoice: {invoices[0].get("invoice_number", "N/A")} (Project: {invoices[0].get("project_id", "N/A")})')
            else:
                print(f'❌ Invoices failed: {invoices_response.text}')
                
            # Test project expenses
            print(f"\n🔍 Testing project expenses with {user['email']}...")
            expenses_response = requests.get('http://192.168.1.17:8000/api/project-expenses', headers=headers)
            print(f'Project expenses response: {expenses_response.status_code}')
            if expenses_response.status_code == 200:
                expenses = expenses_response.json()
                print(f'✅ Project expenses loaded: {len(expenses)} expenses')
                if len(expenses) > 0:
                    print(f'Sample expense: {expenses[0].get("description", "N/A")} (Project: {expenses[0].get("project_id", "N/A")})')
            else:
                print(f'❌ Project expenses failed: {expenses_response.text}')
            
            print(f"\n🎉 SUCCESS! User {user['email']} can access all APIs!")
            break
        else:
            print(f'❌ Login failed: {response.text[:100]}')
            
    except Exception as e:
        print(f'❌ Error with {user["email"]}: {e}')


