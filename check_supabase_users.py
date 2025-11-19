#!/usr/bin/env python3
import requests
import json

# Check users in Supabase
supabase_url = "https://mfmijckzlhevduwfigkl.supabase.co"
supabase_service_key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1mbWlqY2t6bGhldmR1d2ZpZ2tsIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1NjUzOTExMiwiZXhwIjoyMDcyMTE1MTEyfQ.rlFwoXK_Yls7kRxL_lYqYWe3huJhs0V60Wa4Ddd7Ero"

# Check users table
users_url = f"{supabase_url}/rest/v1/users"
headers = {
    "apikey": supabase_service_key,
    "Authorization": f"Bearer {supabase_service_key}",
    "Content-Type": "application/json"
}

try:
    print("🔍 Checking users in Supabase...")
    response = requests.get(users_url, headers=headers)
    print(f'Users response: {response.status_code}')
    print(f'Response: {response.text}')
    
    if response.status_code == 200:
        users = response.json()
        print(f'✅ Found {len(users)} users in database')
        for user in users:
            print(f'  User: {user.get("email", "N/A")} - Active: {user.get("is_active", "N/A")}')
    else:
        print(f'❌ Failed to get users: {response.text}')
        
except Exception as e:
    print(f'❌ Error: {e}')


