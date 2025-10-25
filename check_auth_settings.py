#!/usr/bin/env python3
import requests
import json

# Check Supabase Auth settings
supabase_url = "https://mfmijckzlhevduwfigkl.supabase.co"
supabase_service_key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1mbWlqY2t6bGhldmR1d2ZpZ2tsIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1NjUzOTExMiwiZXhwIjoyMDcyMTE1MTEyfQ.rlFwoXK_Yls7kRxL_lYqYWe3huJhs0V60Wa4Ddd7Ero"

# Check auth users
auth_users_url = f"{supabase_url}/auth/v1/admin/users"
headers = {
    "apikey": supabase_service_key,
    "Authorization": f"Bearer {supabase_service_key}",
    "Content-Type": "application/json"
}

try:
    print("🔍 Checking Supabase Auth users...")
    response = requests.get(auth_users_url, headers=headers)
    print(f'Auth users response: {response.status_code}')
    print(f'Response: {response.text}')
    
    if response.status_code == 200:
        users = response.json()
        print(f'✅ Found {len(users.get("users", []))} auth users')
        for user in users.get("users", []):
            print(f'  Auth User: {user.get("email", "N/A")} - Confirmed: {user.get("email_confirmed_at") is not None}')
    else:
        print(f'❌ Failed to get auth users: {response.text}')
        
except Exception as e:
    print(f'❌ Error: {e}')
