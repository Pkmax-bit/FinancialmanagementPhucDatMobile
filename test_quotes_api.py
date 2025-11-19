#!/usr/bin/env python3
import requests
import json

# Test quotes API
base_url = 'http://192.168.1.17:8000'

def test_quotes_api():
    """Test quotes API endpoint"""
    print("🔍 Testing quotes API...")
    
    try:
        # Test without authentication first
        response = requests.get(f"{base_url}/api/sales/quotes")
        print(f"Quotes API response (no auth): {response.status_code}")
        print(f"Response: {response.text[:200]}...")
        
        if response.status_code == 401:
            print("✅ API requires authentication (expected)")
        elif response.status_code == 200:
            quotes = response.json()
            print(f"✅ Found {len(quotes)} quotes")
            for i, quote in enumerate(quotes[:3]):
                print(f"  Quote {i+1}: {quote.get('quote_number', 'N/A')} - {quote.get('total_amount', 'N/A')}")
        else:
            print(f"❌ Unexpected response: {response.text}")
            
    except Exception as e:
        print(f"❌ Error testing quotes API: {e}")

def test_quotes_with_auth():
    """Test quotes API with authentication"""
    print("\n🔍 Testing quotes API with authentication...")
    
    # Try to get a valid token first
    # For now, we'll test with a mock token
    headers = {
        "Authorization": "Bearer mock_token",
        "Content-Type": "application/json"
    }
    
    try:
        response = requests.get(f"{base_url}/api/sales/quotes", headers=headers)
        print(f"Quotes API response (with auth): {response.status_code}")
        print(f"Response: {response.text[:200]}...")
        
        if response.status_code == 200:
            quotes = response.json()
            print(f"✅ Found {len(quotes)} quotes with auth")
            for i, quote in enumerate(quotes[:3]):
                print(f"  Quote {i+1}: {quote.get('quote_number', 'N/A')} - {quote.get('total_amount', 'N/A')}")
        else:
            print(f"❌ Auth failed: {response.text}")
            
    except Exception as e:
        print(f"❌ Error testing quotes API with auth: {e}")

def test_quotes_database():
    """Test quotes in database directly"""
    print("\n🔍 Testing quotes in database...")
    
    # This would require direct database access
    # For now, we'll just show the expected structure
    print("Expected quote structure:")
    print("- id: UUID")
    print("- quote_number: String")
    print("- project_id: UUID")
    print("- customer_id: UUID")
    print("- total_amount: Decimal")
    print("- status: String")
    print("- valid_until: Date")
    print("- created_at: Timestamp")

if __name__ == "__main__":
    test_quotes_api()
    test_quotes_with_auth()
    test_quotes_database()


