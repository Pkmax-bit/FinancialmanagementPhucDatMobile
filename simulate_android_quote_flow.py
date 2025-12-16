import requests
import json
import sys

# Configuration
BASE_URL = "http://localhost:8000/api"
# Check if backend is running on port 8000 or 3000 (NetworkConfig says 8000 for local)
# But standard FastAPI often runs on 8000. 

EMAIL = "admin@test.com"
PASSWORD = "123456"

def log(msg, data=None):
    print(f"[TEST] {msg}")
    if data:
        print(json.dumps(data, indent=2, ensure_ascii=False))

def run_test():
    print("=== STARTING ANDROID QUOTE FLOW SIMULATION ===")
    
    # 1. Login
    login_url = f"{BASE_URL}/auth/login"
    login_payload = {
        "email": EMAIL,
        "password": PASSWORD
    }
    
    try:
        log(f"Attempting login to {login_url}...")
        response = requests.post(login_url, json=login_payload)
        
        if response.status_code != 200:
            log(f"Login failed: {response.status_code}", response.text)
            return
        
        token_data = response.json()
        access_token = token_data.get("access_token")
        
        if not access_token:
            log("Login successful but no access_token found!")
            return
            
        log("Login successful!")
        
        # Headers for subsequent requests
        headers = {
            "Authorization": f"Bearer {access_token}",
            "Content-Type": "application/json"
        }
        
        # 2. Get Quotes
        quotes_url = f"{BASE_URL}/sales/quotes"
        log(f"Fetching quotes from {quotes_url}...")
        
        response = requests.get(quotes_url, headers=headers)
        
        if response.status_code != 200:
            log(f"Get Quotes failed: {response.status_code}", response.text)
            return
            
        quotes = response.json()
        log(f"Found {len(quotes)} quotes.")
        
        if not quotes:
            log("No quotes found to test details.")
            return

        # 3. Get First Quote Detail
        first_quote = quotes[0]
        quote_id = first_quote.get("id")
        log(f"Testing detail for first quote ID: {quote_id}")
        
        detail_url = f"{BASE_URL}/sales/quotes/{quote_id}"
        response = requests.get(detail_url, headers=headers)
        
        if response.status_code != 200:
            log(f"Get Quote Detail failed: {response.status_code}", response.text)
            return
            
        quote_detail = response.json()
        log("Quote Detail retrieved successfully.")
        
        # Verify items
        items = quote_detail.get("quote_items", [])
        log(f"Quote has {len(items)} items.")
        
        if items:
            first_item = items[0]
            log("First Item Verification:", {
                "id": first_item.get("id"),
                "product_name": first_item.get("product_name", "N/A"),
                "category_name": first_item.get("category_name", "N/A"),
                "quantity": first_item.get("quantity")
            })
            
            if "category_name" in first_item:
                print("✅ PASSED: Quote item has 'category_name' enriched from backend.")
            else:
                print("⚠️ WARNING: Quote item MISSING 'category_name'.")
        
        print("=== TEST COMPLETED SUCCESSFULLY ===")
        
    except requests.exceptions.ConnectionError:
        log("❌ Connection Error: Is the backend server running at " + BASE_URL + "?")
    except Exception as e:
        log(f"❌ Unexpected Error: {e}")

if __name__ == "__main__":
    run_test()
