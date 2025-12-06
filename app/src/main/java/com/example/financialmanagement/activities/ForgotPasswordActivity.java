package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financialmanagement.R;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.auth.AuthCallback;

/**
 * Forgot Password Activity - Màn hình quên mật khẩu
 */
public class ForgotPasswordActivity extends AppCompatActivity implements AuthCallback {

    private EditText etEmail;
    private Button btnResetPassword;
    private TextView tvBackToLogin;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        setupClickListeners();
        
        authManager = new AuthManager(this);
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performResetPassword();
            }
        });
        
        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void performResetPassword() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        authManager.resetPassword(email, this);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnResetPassword.setEnabled(!show);
    }

    @Override
    public void onAuthSuccess() {
        runOnUiThread(() -> {
            showLoading(false);
            Toast.makeText(this, "Đã gửi email khôi phục mật khẩu. Vui lòng kiểm tra hộp thư.", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    @Override
    public void onAuthError(String error) {
        runOnUiThread(() -> {
            showLoading(false);
            Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
        });
    }
}
