package com.example.financialmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;
    private LinearLayout errorContainer;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
        
        authManager = new AuthManager(this);
        
        // Kiểm tra nếu được redirect từ auth error
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("auth_expired", false)) {
            showError("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }
        
        // Kiểm tra nếu đã đăng nhập (nếu không phải từ auth error)
        if (authManager.isLoggedIn() && (intent == null || !intent.getBooleanExtra("auth_expired", false))) {
            navigateToMain();
        }
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        errorContainer = findViewById(R.id.error_container);
        tvError = findViewById(R.id.tv_error);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        
        // Toggle password visibility
        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
        
        // Forgot password
        findViewById(R.id.tv_forgot_password).setOnClickListener(v -> handleForgotPassword());
        findViewById(R.id.btn_forgot_password).setOnClickListener(v -> handleForgotPassword());
        
        // Register
        findViewById(R.id.tv_register).setOnClickListener(v -> handleRegister());
        
        // Test account cards
        findViewById(R.id.card_admin).setOnClickListener(v -> {
            etEmail.setText("admin@test.com");
            etPassword.setText("123456");
            hideError();
        });
        
        findViewById(R.id.card_sales).setOnClickListener(v -> {
            etEmail.setText("sales@example.com");
            etPassword.setText("123456");
            hideError();
        });
    }
    
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_visibility);
        }
        isPasswordVisible = !isPasswordVisible;
        etPassword.setSelection(etPassword.getText().length());
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        hideError();
        showLoading(true);
        authManager.login(email, password, this);
    }
    
    private void showError(String message) {
        errorContainer.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }
    
    private void hideError() {
        errorContainer.setVisibility(View.GONE);
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
            showError("Lỗi đăng nhập: " + error);
        });
    }
    
    private void handleForgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
    
    private void handleRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
