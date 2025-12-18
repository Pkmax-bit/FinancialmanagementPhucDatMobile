package com.example.financialmanagement.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.financialmanagement.R;
import com.example.financialmanagement.services.QRLoginService;
import com.example.financialmanagement.auth.AuthManager;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import android.widget.TextView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * QR Scanner Activity - Quét mã QR để đăng nhập
 */
public class QRScannerActivity extends AppCompatActivity {
    
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private PreviewView previewView;
    private TextView tvInstructions;
    private ExecutorService cameraExecutor;
    private Camera camera;
    private boolean qrProcessed = false; // Prevent multiple processing
    private QRLoginService qrLoginService;
    private AuthManager authManager;
    private boolean isForWebLogin = false; // Flag to determine if this is for web login
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        
        previewView = findViewById(R.id.preview_view);
        tvInstructions = findViewById(R.id.tv_instructions);
        cameraExecutor = Executors.newSingleThreadExecutor();
        qrLoginService = new QRLoginService(this);
        authManager = new AuthManager(this);
        
        // Check if this is for web login (called from MainActivity menu)
        isForWebLogin = getIntent().getBooleanExtra("for_web_login", false);
        
        // Update instructions based on purpose
        if (isForWebLogin) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Quét QR để đăng nhập Web");
            }
            tvInstructions.setText("Quét mã QR từ trang đăng nhập web để đăng nhập web");
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Quét QR để đăng nhập");
            }
            tvInstructions.setText("Đưa mã QR vào khung để quét");
        }
        
        // Request camera permission
        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }
    
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            new String[]{Manifest.permission.CAMERA},
            CAMERA_PERMISSION_REQUEST_CODE
        );
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Cần quyền camera để quét QR code", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
            ProcessCameraProvider.getInstance(this);
        
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                
                // Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                
                // Image Analysis for QR code scanning
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();
                
                imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        processImageProxy(imageProxy);
                    }
                });
                
                // Camera selector
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                
                // Unbind use cases before rebinding
                cameraProvider.unbindAll();
                
                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalysis
                );
                
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi khởi động camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    
    private void processImageProxy(ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }
        
        InputImage image = InputImage.fromMediaImage(
            imageProxy.getImage(),
            imageProxy.getImageInfo().getRotationDegrees()
        );
        
        com.google.mlkit.vision.barcode.BarcodeScanner scanner = 
            BarcodeScanning.getClient();
        
        scanner.process(image)
            .addOnSuccessListener(barcodes -> {
                for (Barcode barcode : barcodes) {
                    if (barcode.getValueType() == Barcode.TYPE_TEXT || 
                        barcode.getValueType() == Barcode.TYPE_URL) {
                        String qrData = barcode.getRawValue();
                        if (qrData != null) {
                            // Found QR code, return result
                            handleQRCodeScanned(qrData);
                            imageProxy.close();
                            return;
                        }
                    }
                }
                imageProxy.close();
            })
            .addOnFailureListener(e -> {
                // Error handling is done silently to avoid spam
                imageProxy.close();
            });
    }
    
    private void handleQRCodeScanned(String qrData) {
        if (qrProcessed) {
            return; // Prevent multiple processing
        }
        qrProcessed = true;

        // Parse QR data (should be JSON with session_id and secret_token)
        try {
            JsonObject qrJson = new Gson().fromJson(qrData, JsonObject.class);
            String sessionId = qrJson.get("session_id").getAsString();
            String secretToken = qrJson.get("secret_token").getAsString();
            String qrType = qrJson.has("type") ? qrJson.get("type").getAsString() : "login";
            
            if (isForWebLogin) {
                // This is for web login - Android quét QR từ web để đăng nhập web
                // QR type should be "web_login" or "login"
                if ("mobile_to_web_login".equals(qrType)) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "QR code không hợp lệ. Vui lòng quét QR từ trang đăng nhập web.", Toast.LENGTH_LONG).show();
                        qrProcessed = false;
                    });
                    return;
                }
                
                // Verify and complete QR login for web
                processWebLoginQR(sessionId, secretToken);
            } else {
                // This is for Android login - return result to LoginActivity
                if ("mobile_to_web_login".equals(qrType)) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "QR code không hợp lệ cho đăng nhập Android", Toast.LENGTH_SHORT).show();
                        qrProcessed = false;
                    });
                    return;
                }
                
                // Return result to LoginActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("qr_data", qrData);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Lỗi xử lý QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                qrProcessed = false; // Allow retry
            });
        }
    }
    
    private void processWebLoginQR(String sessionId, String secretToken) {
        // Check if user is logged in on Android
        if (!authManager.isLoggedIn()) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Vui lòng đăng nhập trên Android trước", Toast.LENGTH_LONG).show();
                qrProcessed = false;
            });
            return;
        }
        
        runOnUiThread(() -> {
            Toast.makeText(this, "Đang xác thực QR code...", Toast.LENGTH_SHORT).show();
        });
        
        // First verify
        qrLoginService.verifyQRCode(sessionId, secretToken, new QRLoginService.QRVerifyCallback() {
            @Override
            public void onSuccess(QRLoginService.QRVerifyResponse response) {
                if (response.isSuccess()) {
                    // Then complete with Android user's token
                    String androidToken = authManager.getAccessToken();
                    if (androidToken == null || androidToken.isEmpty()) {
                        runOnUiThread(() -> {
                            Toast.makeText(QRScannerActivity.this, 
                                "Vui lòng đăng nhập trên Android trước khi quét QR", 
                                Toast.LENGTH_LONG).show();
                            qrProcessed = false;
                        });
                        return;
                    }
                    qrLoginService.completeQRLogin(sessionId, secretToken, androidToken, new QRLoginService.QRCompleteCallback() {
                        @Override
                        public void onSuccess(QRLoginService.QRVerifyResponse response) {
                            runOnUiThread(() -> {
                                if (response.isSuccess()) {
                                    Toast.makeText(QRScannerActivity.this, 
                                        "✓ Đã đăng nhập thành công trên web!", 
                                        Toast.LENGTH_LONG).show();
                                    // Close after 2 seconds
                                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                                        finish();
                                    }, 2000);
                                } else {
                                    Toast.makeText(QRScannerActivity.this, 
                                        "Đăng nhập thất bại: " + response.getMessage(), 
                                        Toast.LENGTH_LONG).show();
                                    qrProcessed = false;
                                }
                            });
                        }
                        
                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(QRScannerActivity.this, 
                                    "Lỗi hoàn tất đăng nhập: " + error, 
                                    Toast.LENGTH_LONG).show();
                                qrProcessed = false;
                            });
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(QRScannerActivity.this, 
                            "Xác thực thất bại: " + response.getMessage(), 
                            Toast.LENGTH_LONG).show();
                        qrProcessed = false;
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(QRScannerActivity.this, 
                        "Lỗi xác thực: " + error, 
                        Toast.LENGTH_LONG).show();
                    qrProcessed = false;
                });
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}

