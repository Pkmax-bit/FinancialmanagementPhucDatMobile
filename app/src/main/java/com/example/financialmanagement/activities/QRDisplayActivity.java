package com.example.financialmanagement.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.financialmanagement.R;
import com.example.financialmanagement.services.QRLoginService;
import com.example.financialmanagement.utils.ApiDebugger;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.HashMap;
import java.util.Map;

/**
 * QR Display Activity - Hiển thị QR code để đăng nhập web
 */
public class QRDisplayActivity extends AppCompatActivity {
    
    private ImageView ivQRCode;
    private TextView tvStatus, tvExpiresAt;
    private ProgressBar progressBar;
    private QRLoginService qrLoginService;
    private Handler handler;
    private Runnable statusCheckRunnable;
    private String currentSessionId;
    private boolean isPolling = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_display);
        
        setupToolbar();
        initializeViews();
        qrLoginService = new QRLoginService(this);
        handler = new Handler(Looper.getMainLooper());
        
        generateQRCode();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Đăng nhập Web bằng QR");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }
    
    private void initializeViews() {
        ivQRCode = findViewById(R.id.iv_qr_code);
        tvStatus = findViewById(R.id.tv_status);
        tvExpiresAt = findViewById(R.id.tv_expires_at);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void generateQRCode() {
        showLoading(true);
        tvStatus.setText("Đang tạo mã QR...");
        
        qrLoginService.generateQRCode(new QRLoginService.QRGenerateCallback() {
            @Override
            public void onSuccess(QRLoginService.QRGenerateResponse response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    currentSessionId = response.getSessionId();
                    String qrData = response.getQrCode();
                    
                    // Generate QR code bitmap
                    Bitmap qrBitmap = generateQRBitmap(qrData);
                    if (qrBitmap != null) {
                        ivQRCode.setImageBitmap(qrBitmap);
                        ivQRCode.setVisibility(View.VISIBLE);
                        tvStatus.setText("Quét mã QR này trên web để đăng nhập");
                        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        
                        // Start polling for status
                        startStatusPolling();
                    } else {
                        tvStatus.setText("Lỗi tạo mã QR");
                        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                    
                    // Show expiry time
                    if (response.getExpiresAt() != null) {
                        tvExpiresAt.setText("Hết hạn: " + formatExpiryTime(response.getExpiresAt()));
                        tvExpiresAt.setVisibility(View.VISIBLE);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    tvStatus.setText("Lỗi: " + error);
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    Toast.makeText(QRDisplayActivity.this, "Lỗi tạo QR: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private Bitmap generateQRBitmap(String data) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            
            int size = 512; // QR code size in pixels
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 
                        getResources().getColor(android.R.color.black) : 
                        getResources().getColor(android.R.color.white));
                }
            }
            
            return bitmap;
        } catch (WriterException e) {
            ApiDebugger.logError("generateQRBitmap", e);
            return null;
        }
    }
    
    private void startStatusPolling() {
        if (currentSessionId == null || isPolling) return;
        
        isPolling = true;
        statusCheckRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPolling || currentSessionId == null) return;
                
                qrLoginService.checkQRStatus(currentSessionId, new QRLoginService.QRStatusCallback() {
                    @Override
                    public void onSuccess(QRLoginService.QRStatusResponse response) {
                        runOnUiThread(() -> {
                            String status = response.getStatus();
                            
                            if ("verified".equals(status) || "completed".equals(status)) {
                                // Web has scanned and logged in
                                tvStatus.setText("✓ Đã đăng nhập thành công trên web!");
                                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                stopStatusPolling();
                                
                                // Auto close after 2 seconds
                                handler.postDelayed(() -> finish(), 2000);
                            } else if ("expired".equals(status)) {
                                tvStatus.setText("Mã QR đã hết hạn. Vui lòng tạo mã mới.");
                                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                stopStatusPolling();
                            } else {
                                // Still pending, continue polling
                                handler.postDelayed(statusCheckRunnable, 3000); // Poll every 3 seconds
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        // Continue polling even on error
                        handler.postDelayed(statusCheckRunnable, 5000); // Retry after 5 seconds
                    }
                });
            }
        };
        
        // Start polling after 3 seconds
        handler.postDelayed(statusCheckRunnable, 3000);
    }
    
    private void stopStatusPolling() {
        isPolling = false;
        if (statusCheckRunnable != null) {
            handler.removeCallbacks(statusCheckRunnable);
        }
    }
    
    private String formatExpiryTime(String expiresAt) {
        try {
            // Parse ISO format and format for display
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date date = inputFormat.parse(expiresAt.replace("Z", "").split("\\.")[0]);
            return outputFormat.format(date);
        } catch (Exception e) {
            return expiresAt;
        }
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        ivQRCode.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStatusPolling();
    }
}

