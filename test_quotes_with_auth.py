#!/usr/bin/env python3
import requests
import json

# Test quotes with proper authentication
base_url = 'http://192.168.1.17:8000'

def test_login_and_quotes():
    """Test login and then get quotes"""
    print("🔍 Testing login and quotes...")
    
    # Try to login with existing user
    login_data = {
        "email": "xuong@gmail.com",
        "password": "password123"
    }
    
    try:
        # Login
        print("1. Attempting login...")
        login_response = requests.post(f"{base_url}/api/auth/login", json=login_data)
        print(f"Login response: {login_response.status_code}")
        
        if login_response.status_code == 200:
            auth_data = login_response.json()
            token = auth_data.get('access_token')
            print(f"✅ Login successful! Token: {token[:50]}...")
            
            # Test quotes with token
            print("\n2. Testing quotes with token...")
            headers = {
                "Authorization": f"Bearer {token}",
                "Content-Type": "application/json"
            }
            
            quotes_response = requests.get(f"{base_url}/api/sales/quotes", headers=headers)
            print(f"Quotes response: {quotes_response.status_code}")
            
            if quotes_response.status_code == 200:
                quotes = quotes_response.json()
                print(f"✅ Found {len(quotes)} quotes!")
                for i, quote in enumerate(quotes[:5]):
                    print(f"  Quote {i+1}:")
                    print(f"    ID: {quote.get('id', 'N/A')}")
                    print(f"    Number: {quote.get('quote_number', 'N/A')}")
                    print(f"    Project: {quote.get('project_id', 'N/A')}")
                    print(f"    Customer: {quote.get('customer_id', 'N/A')}")
                    print(f"    Total: {quote.get('total_amount', 'N/A')}")
                    print(f"    Status: {quote.get('status', 'N/A')}")
                    print(f"    Valid Until: {quote.get('valid_until', 'N/A')}")
                    print()
            else:
                print(f"❌ Quotes failed: {quotes_response.text}")
                
            # Test invoices too
            print("\n3. Testing invoices with token...")
            invoices_response = requests.get(f"{base_url}/api/sales/invoices", headers=headers)
            print(f"Invoices response: {invoices_response.status_code}")
            
            if invoices_response.status_code == 200:
                invoices = invoices_response.json()
                print(f"✅ Found {len(invoices)} invoices!")
                for i, invoice in enumerate(invoices[:3]):
                    print(f"  Invoice {i+1}:")
                    print(f"    ID: {invoice.get('id', 'N/A')}")
                    print(f"    Number: {invoice.get('invoice_number', 'N/A')}")
                    print(f"    Project: {invoice.get('project_id', 'N/A')}")
                    print(f"    Customer: {invoice.get('customer_id', 'N/A')}")
                    print(f"    Total: {invoice.get('total_amount', 'N/A')}")
                    print(f"    Status: {invoice.get('status', 'N/A')}")
                    print()
            else:
                print(f"❌ Invoices failed: {invoices_response.text}")
                
        else:
            print(f"❌ Login failed: {login_response.text}")
            
    except Exception as e:
        print(f"❌ Error: {e}")

if __name__ == "__main__":
    test_login_and_quotes()
