package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.financialmanagement.R;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.utils.ApiDebugger;

/**
 * Dashboard Fragment - Màn hình tổng quan
 * Hiển thị thông tin cơ bản và chào mừng người dùng
 */
public class DashboardFragment extends Fragment {

    private TextView tvWelcome, tvUserInfo, tvAppInfo;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        initializeViews(view);
        loadUserInfo();
        
        return view;
    }

    private void initializeViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvUserInfo = view.findViewById(R.id.tv_user_info);
        tvAppInfo = view.findViewById(R.id.tv_app_info);
        authManager = new AuthManager(getContext());
    }

    private void loadUserInfo() {
        // Debug logging
        ApiDebugger.logAuth("Loading user info in Dashboard", authManager.isLoggedIn());
        
        if (authManager.isLoggedIn()) {
            String userName = authManager.getUserName();
            String userEmail = authManager.getUserEmail();
            String userRole = authManager.getUserRole();
            
            tvWelcome.setText("Chào mừng, " + (userName != null ? userName : "Người dùng") + "!");
            tvUserInfo.setText("Email: " + (userEmail != null ? userEmail : "Chưa xác định") + 
                             "\nVai trò: " + (userRole != null ? userRole : "Chưa xác định"));
            
            ApiDebugger.logAuth("User loaded: " + userName, true);
        } else {
            tvWelcome.setText("Chào mừng đến với Phúc Đạt Financial Management!");
            tvUserInfo.setText("Vui lòng đăng nhập để sử dụng đầy đủ tính năng");
            
            ApiDebugger.logAuth("User not logged in", false);
        }
        
        tvAppInfo.setText("Ứng dụng quản lý tài chính Phúc Đạt\nPhiên bản 1.0.0");
    }
}