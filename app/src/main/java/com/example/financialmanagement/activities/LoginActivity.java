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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.example.financialmanagement.R;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.auth.AuthCallback;
import com.example.financialmanagement.services.QRLoginService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

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
    private QRLoginService qrLoginService;
    private ActivityResultLauncher<Intent> qrScannerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
        
        authManager = new AuthManager(this);
        qrLoginService = new QRLoginService(this);
        
        // Setup QR scanner launcher
        qrScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleQRScannerResult
        );
        
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
        
        // QR Login button
        View btnQRLogin = findViewById(R.id.btn_qr_login);
        if (btnQRLogin != null) {
            btnQRLogin.setOnClickListener(v -> handleQRLogin());
        }
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
    
    private void handleQRLogin() {
        Intent intent = new Intent(this, QRScannerActivity.class);
        qrScannerLauncher.launch(intent);
    }
    
    private void handleQRScannerResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            String qrData = result.getData().getStringExtra("qr_data");
            if (qrData != null) {
                processQRCode(qrData);
            } else {
                showError("Không thể đọc dữ liệu QR code");
            }
        }
    }
    
    private void processQRCode(String qrData) {
        try {
            // Parse QR data (should be JSON)
            JSONObject qrJson = new JSONObject(qrData);
            String sessionId = qrJson.getString("session_id");
            String secretToken = qrJson.optString("secret_token", null);
            
            if (sessionId == null || sessionId.isEmpty()) {
                showError("QR code không hợp lệ");
                return;
            }
            
            hideError();
            showLoading(true);
            
            // First verify, then complete
            qrLoginService.verifyQRCode(sessionId, new QRLoginService.QRVerifyCallback() {
                @Override
                public void onSuccess(QRLoginService.QRVerifyResponse response) {
                    if (response.isSuccess()) {
                        // Now complete the login
                        if (secretToken != null && !secretToken.isEmpty()) {
                            completeQRLogin(sessionId, secretToken);
                        } else {
                            runOnUiThread(() -> {
                                showLoading(false);
                                showError("QR code thiếu thông tin secret_token");
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            showLoading(false);
                            showError(response.getMessage());
                        });
                    }
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        showError(error);
                    });
                }
            });
            
        } catch (Exception e) {
            showLoading(false);
            showError("Lỗi xử lý QR code: " + e.getMessage());
        }
    }
    
    private void completeQRLogin(String sessionId, String secretToken) {
        qrLoginService.completeQRLogin(sessionId, secretToken, new QRLoginService.QRCompleteCallback() {
            @Override
            public void onSuccess(QRLoginService.QRVerifyResponse response) {
                if (response.isSuccess() && response.getAccessToken() != null) {
                    // Save token and login
                    authManager.saveToken(
                        response.getAccessToken(),
                        response.getTokenType() != null ? response.getTokenType() : "bearer"
                    );
                    
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    });
                } else {
                    runOnUiThread(() -> {
                        showLoading(false);
                        showError(response.getMessage() != null ? response.getMessage() : "Đăng nhập thất bại");
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError(error);
                });
            }
        });
    }
}
