package com.example.financialmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financialmanagement.R;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.auth.AuthCallback;

/**
 * Login Activity - Màn hình đăng nhập
 * Xử lý authentication với Supabase
 */
public class LoginActivity extends AppCompatActivity implements AuthCallback {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
        
        authManager = new AuthManager(this);
        
        // Kiểm tra nếu đã đăng nhập
        if (authManager.isLoggedIn()) {
            navigateToMain();
        }
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
        
        // Thêm click listener cho forgot password
        findViewById(R.id.tv_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });
        
        // Register functionality removed - not needed for this app
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        authManager.login(email, password, this);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthSuccess() {
        runOnUiThread(() -> {
            showLoading(false);
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            navigateToMain();
        });
    }

    @Override
    public void onAuthError(String error) {
        runOnUiThread(() -> {
            showLoading(false);
            Toast.makeText(this, "Lỗi đăng nhập: " + error, Toast.LENGTH_LONG).show();
        });
    }
    
    private void handleForgotPassword() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email để reset mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // TODO: Implement forgot password functionality
        Toast.makeText(this, "Chức năng quên mật khẩu sẽ được triển khai", Toast.LENGTH_SHORT).show();
    }
    
    private void handleRegister() {
        // TODO: Navigate to register activity
        Toast.makeText(this, "Chức năng đăng ký sẽ được triển khai", Toast.LENGTH_SHORT).show();
    }
}
