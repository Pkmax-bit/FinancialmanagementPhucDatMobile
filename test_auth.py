#!/usr/bin/env python3
"""
Test script để kiểm tra authentication và API endpoints
"""

import requests
import json

# Configuration
BASE_URL = "http://192.168.1.17:8000"
API_BASE = f"{BASE_URL}/api"

def test_health():
    """Test health endpoint"""
    print("🔍 Testing health endpoint...")
    try:
        response = requests.get(f"{BASE_URL}/health")
        print(f"✅ Health check: {response.status_code}")
        print(f"Response: {response.json()}")
        return True
    except Exception as e:
        print(f"❌ Health check failed: {e}")
        return False

def test_login():
    """Test login endpoint"""
    print("\n🔍 Testing login endpoint...")
    try:
        login_data = {
            "email": "admin@example.com",
            "password": "password123"
        }
        response = requests.post(f"{API_BASE}/auth/login", json=login_data)
        print(f"Login response: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Login successful!")
            print(f"Token: {data.get('access_token', 'N/A')[:50]}...")
            return data.get('access_token')
        else:
            print(f"❌ Login failed: {response.text}")
            return None
    except Exception as e:
        print(f"❌ Login test failed: {e}")
        return None

def test_projects_with_auth(token):
    """Test projects endpoint with authentication"""
    print("\n🔍 Testing projects endpoint with auth...")
    try:
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        response = requests.get(f"{API_BASE}/projects", headers=headers)
        print(f"Projects response: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Projects loaded: {len(data)} projects")
            for i, project in enumerate(data[:3]):  # Show first 3 projects
                print(f"  Project {i+1}: {project.get('name', 'N/A')} (ID: {project.get('id', 'N/A')})")
            return data
        else:
            print(f"❌ Projects failed: {response.text}")
            return None
    except Exception as e:
        print(f"❌ Projects test failed: {e}")
        return None

def test_quotes_with_auth(token):
    """Test quotes endpoint with authentication"""
    print("\n🔍 Testing quotes endpoint with auth...")
    try:
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        response = requests.get(f"{API_BASE}/sales/quotes", headers=headers)
        print(f"Quotes response: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Quotes loaded: {len(data)} quotes")
            for i, quote in enumerate(data[:3]):  # Show first 3 quotes
                print(f"  Quote {i+1}: {quote.get('quote_number', 'N/A')} (Project: {quote.get('project_id', 'N/A')})")
            return data
        else:
            print(f"❌ Quotes failed: {response.text}")
            return None
    except Exception as e:
        print(f"❌ Quotes test failed: {e}")
        return None

def test_invoices_with_auth(token):
    """Test invoices endpoint with authentication"""
    print("\n🔍 Testing invoices endpoint with auth...")
    try:
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        response = requests.get(f"{API_BASE}/sales/invoices", headers=headers)
        print(f"Invoices response: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Invoices loaded: {len(data)} invoices")
            for i, invoice in enumerate(data[:3]):  # Show first 3 invoices
                print(f"  Invoice {i+1}: {invoice.get('invoice_number', 'N/A')} (Project: {invoice.get('project_id', 'N/A')})")
            return data
        else:
            print(f"❌ Invoices failed: {response.text}")
            return None
    except Exception as e:
        print(f"❌ Invoices test failed: {e}")
        return None

def test_project_expenses_with_auth(token):
    """Test project expenses endpoint with authentication"""
    print("\n🔍 Testing project expenses endpoint with auth...")
    try:
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        response = requests.get(f"{API_BASE}/project-expenses", headers=headers)
        print(f"Project expenses response: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Project expenses loaded: {len(data)} expenses")
            for i, expense in enumerate(data[:3]):  # Show first 3 expenses
                print(f"  Expense {i+1}: {expense.get('description', 'N/A')} (Project: {expense.get('project_id', 'N/A')})")
            return data
        else:
            print(f"❌ Project expenses failed: {response.text}")
            return None
    except Exception as e:
        print(f"❌ Project expenses test failed: {e}")
        return None

def main():
    """Main test function"""
    print("🚀 Starting API Authentication Test")
    print("=" * 50)
    
    # Test health
    if not test_health():
        print("❌ Backend server is not running!")
        return
    
    # Test login
    token = test_login()
    if not token:
        print("❌ Authentication failed!")
        return
    
    # Test authenticated endpoints
    projects = test_projects_with_auth(token)
    quotes = test_quotes_with_auth(token)
    invoices = test_invoices_with_auth(token)
    expenses = test_project_expenses_with_auth(token)
    
    # Summary
    print("\n" + "=" * 50)
    print("📊 TEST SUMMARY:")
    print(f"✅ Health: OK")
    print(f"✅ Authentication: OK")
    print(f"✅ Projects: {len(projects) if projects else 0} items")
    print(f"✅ Quotes: {len(quotes) if quotes else 0} items")
    print(f"✅ Invoices: {len(invoices) if invoices else 0} items")
    print(f"✅ Expenses: {len(expenses) if expenses else 0} items")
    
    if projects and len(projects) > 0:
        print(f"\n🎯 Sample project data:")
        project = projects[0]
        print(f"  ID: {project.get('id')}")
        print(f"  Name: {project.get('name')}")
        print(f"  Status: {project.get('status')}")
        print(f"  Description: {project.get('description', 'N/A')}")

if __name__ == "__main__":
    main()
