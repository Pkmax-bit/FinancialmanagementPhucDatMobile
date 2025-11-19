#!/usr/bin/env python3
import requests
import json

# Create test user and test quotes
base_url = 'http://192.168.1.17:8000'

def create_test_user():
    """Create a test user"""
    print("🔍 Creating test user...")
    
    user_data = {
        "email": "testuser@example.com",
        "password": "test123",
        "full_name": "Test User",
        "role": "admin"
    }
    
    try:
        response = requests.post(f"{base_url}/api/auth/register", json=user_data)
        print(f"Register response: {response.status_code}")
        print(f"Response: {response.text}")
        
        if response.status_code == 200:
            print("✅ User created successfully!")
            return True
        else:
            print(f"❌ Registration failed: {response.text}")
            return False
            
    except Exception as e:
        print(f"❌ Error creating user: {e}")
        return False

def test_login():
    """Test login with created user"""
    print("\n🔍 Testing login...")
    
    login_data = {
        "email": "testuser@example.com",
        "password": "test123"
    }
    
    try:
        response = requests.post(f"{base_url}/api/auth/login", json=login_data)
        print(f"Login response: {response.status_code}")
        
        if response.status_code == 200:
            auth_data = response.json()
            token = auth_data.get('access_token')
            print(f"✅ Login successful! Token: {token[:50]}...")
            return token
        else:
            print(f"❌ Login failed: {response.text}")
            return None
            
    except Exception as e:
        print(f"❌ Error logging in: {e}")
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
            for i, quote in enumerate(quotes[:3]):
                print(f"  Quote {i+1}: {quote.get('quote_number', 'N/A')} - {quote.get('total_amount', 'N/A')}")
            return quotes
        else:
            print(f"❌ Quotes failed: {response.text}")
            return None
            
    except Exception as e:
        print(f"❌ Error getting quotes: {e}")
        return None

def create_test_quote(token):
    """Create a test quote"""
    print("\n🔍 Creating test quote...")
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    quote_data = {
        "quote_number": "QUO-TEST-001",
        "project_id": "test-project-id",
        "customer_id": "test-customer-id",
        "total_amount": 1000000.0,
        "status": "draft",
        "valid_until": "2024-12-31",
        "description": "Test quote for mobile app"
    }
    
    try:
        response = requests.post(f"{base_url}/api/sales/quotes", json=quote_data, headers=headers)
        print(f"Create quote response: {response.status_code}")
        print(f"Response: {response.text}")
        
        if response.status_code == 200 or response.status_code == 201:
            print("✅ Quote created successfully!")
            return True
        else:
            print(f"❌ Quote creation failed: {response.text}")
            return False
            
    except Exception as e:
        print(f"❌ Error creating quote: {e}")
        return False

def main():
    """Main test function"""
    print("🚀 Testing Quotes API with Authentication")
    print("=" * 50)
    
    # Step 1: Create test user
    if not create_test_user():
        print("❌ Cannot proceed without user")
        return
    
    # Step 2: Login
    token = test_login()
    if not token:
        print("❌ Cannot proceed without token")
        return
    
    # Step 3: Test quotes
    quotes = test_quotes_with_token(token)
    
    # Step 4: Create test quote
    create_test_quote(token)
    
    # Step 5: Test quotes again
    print("\n🔍 Testing quotes after creating new quote...")
    test_quotes_with_token(token)

if __name__ == "__main__":
    main()


