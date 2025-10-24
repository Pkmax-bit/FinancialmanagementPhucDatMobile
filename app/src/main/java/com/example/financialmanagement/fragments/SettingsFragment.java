package com.example.financialmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.financialmanagement.R;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.activities.LoginActivity;
import com.example.financialmanagement.utils.ApiDebugger;

/**
 * Settings Fragment - Màn hình cài đặt
 * Quản lý thông tin người dùng và cài đặt ứng dụng
 */
public class SettingsFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserId;
    private Button btnLogout, btnSyncData, btnClearCache;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        initializeViews(view);
        setupClickListeners();
        loadUserInfo();
        
        return view;
    }

    private void initializeViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserId = view.findViewById(R.id.tv_user_id);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnSyncData = view.findViewById(R.id.btn_sync_data);
        btnClearCache = view.findViewById(R.id.btn_clear_cache);
        
        authManager = new AuthManager(getContext());
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });

        btnSyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncData();
            }
        });

        btnClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
            }
        });
    }

    private void loadUserInfo() {
        // Load user information from AuthManager
        String userId = authManager.getUserId();
        String userEmail = authManager.getUserEmail();
        String userName = authManager.getUserName();
        
        // Debug logging
        ApiDebugger.logAuth("Loading user info", authManager.isLoggedIn());
        ApiDebugger.logAuth("User ID: " + userId, authManager.isLoggedIn());
        ApiDebugger.logAuth("User Email: " + userEmail, authManager.isLoggedIn());
        ApiDebugger.logAuth("User Name: " + userName, authManager.isLoggedIn());
        
        // Display user information
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        } else {
            tvUserName.setText("Chưa có tên");
        }
        
        if (userEmail != null && !userEmail.isEmpty()) {
            tvUserEmail.setText(userEmail);
        } else {
            tvUserEmail.setText("Chưa có email");
        }
        
        if (userId != null && !userId.isEmpty()) {
            tvUserId.setText("ID: " + userId);
        } else {
            tvUserId.setText("ID: Chưa có");
        }
        
        // Check if user is logged in
        if (!authManager.isLoggedIn()) {
            Toast.makeText(getContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void performLogout() {
        ApiDebugger.logAuth("Performing logout", false);
        
        authManager.logout();
        
        // Show logout confirmation
        Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        
        // Navigate to login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void syncData() {
        ApiDebugger.logAuth("Syncing data", authManager.isLoggedIn());
        
        // Show loading message
        Toast.makeText(getContext(), "Đang đồng bộ dữ liệu...", Toast.LENGTH_SHORT).show();
        
        // TODO: Implement actual data synchronization
        // This would involve:
        // 1. Sync projects from server
        // 2. Sync expenses from server
        // 3. Update local database
        // 4. Show success/error message
        
        // For now, just show a message
        Toast.makeText(getContext(), "Đồng bộ hoàn tất", Toast.LENGTH_SHORT).show();
    }

    private void clearCache() {
        ApiDebugger.logAuth("Clearing cache", authManager.isLoggedIn());
        
        // Show confirmation
        Toast.makeText(getContext(), "Đang xóa cache...", Toast.LENGTH_SHORT).show();
        
        // TODO: Implement actual cache clearing
        // This would involve:
        // 1. Clear local database
        // 2. Clear cached files
        // 3. Clear SharedPreferences (except auth data)
        // 4. Show success message
        
        // For now, just show a message
        Toast.makeText(getContext(), "Đã xóa cache", Toast.LENGTH_SHORT).show();
    }
}
