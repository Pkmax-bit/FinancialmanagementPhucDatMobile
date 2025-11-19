#!/usr/bin/env python3
import requests
import json

# Test quotes with proper email domain
base_url = 'http://192.168.1.17:8000'

def test_login_with_valid_user():
    """Test login with existing valid user"""
    print("🔍 Testing login with existing user...")
    
    # Try different existing users
    test_users = [
        {"email": "xuong@gmail.com", "password": "password123"},
        {"email": "phannguyendangkhoa0915@gmail.com", "password": "password123"},
        {"email": "admin@test.com", "password": "password123"},
        {"email": "sales@example.com", "password": "password123"}
    ]
    
    for user in test_users:
        try:
            print(f"Trying {user['email']}...")
            response = requests.post(f"{base_url}/api/auth/login", json=user)
            print(f"Login response: {response.status_code}")
            
            if response.status_code == 200:
                auth_data = response.json()
                token = auth_data.get('access_token')
                print(f"✅ Login successful with {user['email']}! Token: {token[:50]}...")
                return token
            else:
                print(f"❌ Login failed: {response.text[:100]}")
                
        except Exception as e:
            print(f"❌ Error with {user['email']}: {e}")
    
    return None

def test_quotes_with_token(token):
    """Test quotes with valid token"""
    print("\n🔍 Testing quotes with token...")
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    try:
        response = requests.get(f"{base_url}/api/sales/quotes", headers=headers)
        print(f"Quotes response: {response.status_code}")
        
        if response.status_code == 200:
            quotes = response.json()
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
            return quotes
        else:
            print(f"❌ Quotes failed: {response.text}")
            return None
            
    except Exception as e:
        print(f"❌ Error getting quotes: {e}")
        return None

def test_invoices_with_token(token):
    """Test invoices with valid token"""
    print("\n🔍 Testing invoices with token...")
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    try:
        response = requests.get(f"{base_url}/api/sales/invoices", headers=headers)
        print(f"Invoices response: {response.status_code}")
        
        if response.status_code == 200:
            invoices = response.json()
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
            return invoices
        else:
            print(f"❌ Invoices failed: {response.text}")
            return None
            
    except Exception as e:
        print(f"❌ Error getting invoices: {e}")
        return None

def main():
    """Main test function"""
    print("🚀 Testing Quotes and Invoices API")
    print("=" * 50)
    
    # Step 1: Login
    token = test_login_with_valid_user()
    if not token:
        print("❌ Cannot proceed without authentication")
        return
    
    # Step 2: Test quotes
    quotes = test_quotes_with_token(token)
    
    # Step 3: Test invoices
    invoices = test_invoices_with_token(token)
    
    # Summary
    print("\n" + "=" * 50)
    print("📊 SUMMARY:")
    print(f"✅ Authentication: SUCCESS")
    print(f"✅ Quotes: {len(quotes) if quotes else 0} items")
    print(f"✅ Invoices: {len(invoices) if invoices else 0} items")
    
    if quotes and len(quotes) > 0:
        print(f"\n🎯 Sample quote data:")
        quote = quotes[0]
        print(f"  ID: {quote.get('id')}")
        print(f"  Number: {quote.get('quote_number')}")
        print(f"  Total: {quote.get('total_amount')}")
        print(f"  Status: {quote.get('status')}")

if __name__ == "__main__":
    main()


