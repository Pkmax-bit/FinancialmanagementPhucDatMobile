package com.example.financialmanagement.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.financialmanagement.R;
import com.example.financialmanagement.services.AiAnalysisService;
import com.example.financialmanagement.utils.ApiDebugger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Scan Expense Fragment - Màn hình quét chi phí
 * Chụp ảnh hoặc chọn ảnh từ thư viện và phân tích bằng AI
 */
public class ScanExpenseFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private static final int REQUEST_CAMERA_CAPTURE = 1002;
    private static final int REQUEST_GALLERY_PICK = 1003;

    private Button btnTakePhoto, btnSelectFromGallery;
    private ImageView ivCapturedImage;
    private TextView tvAnalysisResult;
    private AiAnalysisService aiAnalysisService;
    private Bitmap currentBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_scan_expense, container, false);
            
            initializeViews(view);
            setupClickListeners();
            initializeServices();
            
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi tải Quét chi phí: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return inflater.inflate(R.layout.fragment_scan_expense, container, false);
        }
    }

    private void initializeViews(View view) {
        try {
            btnTakePhoto = view.findViewById(R.id.btn_take_photo);
            btnSelectFromGallery = view.findViewById(R.id.btn_select_from_gallery);
            ivCapturedImage = view.findViewById(R.id.iv_captured_image);
            tvAnalysisResult = view.findViewById(R.id.tv_analysis_result);
            
            // Check for null views
            if (btnTakePhoto == null) {
                throw new RuntimeException("btnTakePhoto not found");
            }
            if (btnSelectFromGallery == null) {
                throw new RuntimeException("btnSelectFromGallery not found");
            }
            if (ivCapturedImage == null) {
                throw new RuntimeException("ivCapturedImage not found");
            }
            if (tvAnalysisResult == null) {
                throw new RuntimeException("tvAnalysisResult not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khởi tạo views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissionAndCapture();
            }
        });

        btnSelectFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });
    }

    private void initializeServices() {
        try {
            if (getContext() != null) {
                aiAnalysisService = new AiAnalysisService(getContext());
            } else {
                throw new RuntimeException("Context is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khởi tạo services: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), 
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            captureImageFromCamera();
        }
    }

    private void captureImageFromCamera() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
            } else {
                Toast.makeText(getContext(), "Không thể mở camera", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi mở camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImageFromGallery() {
        try {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_GALLERY_PICK);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi mở thư viện ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImageFromCamera();
            } else {
                Toast.makeText(getContext(), "Cần quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_CAPTURE && data != null) {
                handleCameraResult(data);
            } else if (requestCode == REQUEST_GALLERY_PICK && data != null) {
                handleGalleryResult(data);
            }
        }
    }

    private void handleCameraResult(Intent data) {
        try {
            Bundle extras = data.getExtras();
            if (extras != null) {
                currentBitmap = (Bitmap) extras.get("data");
                if (currentBitmap != null) {
                    displayImage(currentBitmap);
                    analyzeImage(currentBitmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi xử lý ảnh từ camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGalleryResult(Intent data) {
        try {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedImageUri);
                currentBitmap = BitmapFactory.decodeStream(inputStream);
                if (currentBitmap != null) {
                    displayImage(currentBitmap);
                    analyzeImage(currentBitmap);
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi đọc ảnh từ thư viện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayImage(Bitmap bitmap) {
        try {
            ivCapturedImage.setImageBitmap(bitmap);
            ivCapturedImage.setVisibility(View.VISIBLE);
            tvAnalysisResult.setText("Đang phân tích ảnh...");
            tvAnalysisResult.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi hiển thị ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void analyzeImage(Bitmap bitmap) {
        try {
            // Convert bitmap to byte array for API
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();

            ApiDebugger.logRequest("POST", "AI Analysis", null, "Image size: " + imageBytes.length + " bytes");

            aiAnalysisService.analyzeExpenseImage(imageBytes, new AiAnalysisService.AnalysisCallback() {
                @Override
                public void onSuccess(String result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvAnalysisResult.setText(result);
                            ApiDebugger.logResponse(200, "Success", "AI Analysis completed");
                            Toast.makeText(getContext(), "Phân tích hoàn thành", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvAnalysisResult.setText("Lỗi phân tích: " + error);
                            ApiDebugger.logError("analyzeImage", new Exception(error));
                            Toast.makeText(getContext(), "Lỗi phân tích: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            tvAnalysisResult.setText("Lỗi xử lý ảnh: " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}


