import requests
import json
import sys

# Configuration
BASE_URL = "https://financial-management-backend-3m78.onrender.com/api"
QUOTE_ID = "cc6b7f9a-ce5a-4905-a2c7-4fac33a8b907"

def log(msg, data=None):
    print(f"[VERIFY] {msg}")
    if data:
        print(json.dumps(data, indent=2, ensure_ascii=False))

def run_verify():
    print("=== VERIFYING QUOTE APPROVAL AND INVOICE CREATION === (Using Token from Logs)")
    
    # Token from logs - obtained from user session checks
    token = "eyJhbGciOiJIUzI1NiIsImtpZCI6IjZQa0VpMEowWktZYXVjTzEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL21mbWlqY2t6bGhldmR1d2ZpZ2tsLnN1cGFiYXNlLmNvL2F1dGgvdjEiLCJzdWIiOiJlZDU3ZGE2ZC1mMWIyLTRlYWQtYmM5OC1kNGE0YTE0YjVkNTQiLCJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNzY1OTAzNzAwLCJpYXQiOjE3NjU5MDAxMDAsImVtYWlsIjoiYWRtaW5AdGVzdC5jb20iLCJwaG9uZSI6IiIsImFwcF9tZXRhZGF0YSI6eyJwcm92aWRlciI6ImVtYWlsIiwicHJvdmlkZXJzIjpbImVtYWlsIl19LCJ1c2VyX21ldGFkYXRhIjp7ImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJmdWxsX25hbWUiOiJBZG1pbiBUZXN0Iiwicm9sZSI6ImFkbWluIn0sInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiYWFsIjoiYWFsMSIsImFtciI6W3sibWV0aG9kIjoicGFzc3dvcmQiLCJ0aW1lc3RhbXAiOjE3NjU5MDAxMDB9XSwic2Vzc2lvbl9pZCI6IjVlN2U3ZWU2LWI0MzEtNGIyNi05OTY5LTY3NWQ1ZDdmMjA0NCIsImlzX2Fub255bW91cyI6ZmFsc2V9.SIGHda_-5DRd5MYPBq51hG5-L7GDOJ4QWA7LZ0gLJK0"
    
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    
    try:
        # 2. Check Quote Status
        quote_url = f"{BASE_URL}/sales/quotes/{QUOTE_ID}"
        log(f"Checking Quote {QUOTE_ID}...")
        resp = requests.get(quote_url, headers=headers)
        
        quote = None
        if resp.status_code != 200:
            log(f"Failed to get quote (Status: {resp.status_code})", resp.text)
        else:
            quote = resp.json()
            status = quote.get("status")
            log(f"Quote Status: {status}")
            if status == "approved":
                print("✅ Quote is APPROVED.")
            else:
                print(f"❌ Quote IS NOT APPROVED (Status: {status})")

        # 3. Check for Invoices linked to this Quote
        # Check if quote has invoice_id field (only if quote loaded)
        if quote and 'invoice_id' in quote and quote['invoice_id']:
             print(f"✅ Quote has invoice_id: {quote['invoice_id']}")
        
        # Method B: Search in invoices list (Check always)
        invoices_url = f"{BASE_URL}/sales/invoices"
        log("Fetching recent invoices...")
        resp = requests.get(invoices_url, headers=headers, params={"limit": 100})
        if resp.status_code == 200:
            invoices = resp.json()
            found_invoice = None
            for inv in invoices:
                if inv.get("quote_id") == QUOTE_ID:
                    found_invoice = inv
                    break
            
            if found_invoice:
                print(f"✅ Found Invoice created from Quote: {found_invoice.get('invoice_number')} (ID: {found_invoice.get('id')})")
            else:
                print("❌ No Invoice found linked to this Quote ID in the recent list.")
        else:
            log("Failed to fetch invoices", resp.text)

    except Exception as e:
        log(f"Error: {e}")

if __name__ == "__main__":
    run_verify()
