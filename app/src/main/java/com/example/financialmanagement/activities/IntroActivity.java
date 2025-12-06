package com.example.financialmanagement.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.IntroSliderAdapter;
import com.example.financialmanagement.models.IntroSlide;
import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    
    private ViewPager2 viewPager;
    private View[] dots;
    private Button btnNext;
    private Button btnSkip;
    private Button btnGetStarted;
    private IntroSliderAdapter adapter;
    private List<IntroSlide> slides;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if intro was already shown (COMMENTED FOR TESTING - always show intro)
        // SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        // if (prefs.getBoolean("intro_shown", false)) {
        //     navigateToLogin();
        //     return;
        // }
        
        setContentView(R.layout.activity_intro);
        
        initializeViews();
        setupSlides();
        setupViewPager();
        setupButtons();
    }
    
    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);
        btnGetStarted = findViewById(R.id.btn_get_started);
        
        // Initialize dots array
        dots = new View[4];
        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);
    }
    
    private void setupSlides() {
        slides = new ArrayList<>();
        slides.add(new IntroSlide(
            "Quản lý Tài chính Dễ dàng",
            "Theo dõi doanh thu, chi phí và lợi nhuận của doanh nghiệp một cách hiệu quả",
            R.drawable.ic_dashboard
        ));
        slides.add(new IntroSlide(
            "Quản lý Dự án Chuyên nghiệp",
            "Tạo và theo dõi tiến độ dự án, quản lý báo giá và hóa đơn",
            R.drawable.ic_projects
        ));
        slides.add(new IntroSlide(
            "Quản lý Khách hàng",
            "Lưu trữ thông tin khách hàng, lịch sử giao dịch và công nợ",
            R.drawable.ic_customers
        ));
        slides.add(new IntroSlide(
            "Báo cáo Chi tiết",
            "Phân tích dữ liệu với biểu đồ và báo cáo trực quan",
            R.drawable.ic_reports
        ));
    }
    
    private void setupViewPager() {
        adapter = new IntroSliderAdapter(slides);
        viewPager.setAdapter(adapter);
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
                updateUI(position);
            }
        });
    }
    
    private void updateDots(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setBackgroundResource(R.drawable.dot_selected);
            } else {
                dots[i].setBackgroundResource(R.drawable.dot_unselected);
            }
        }
    }
    
    private void setupButtons() {
        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < slides.size() - 1) {
                viewPager.setCurrentItem(current + 1);
            }
        });
        
        btnSkip.setOnClickListener(v -> finishIntro());
        btnGetStarted.setOnClickListener(v -> finishIntro());
    }
    
    private void updateUI(int position) {
        if (position == slides.size() - 1) {
            // Last slide
            btnNext.setVisibility(View.GONE);
            btnSkip.setVisibility(View.GONE);
            btnGetStarted.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnSkip.setVisibility(View.VISIBLE);
            btnGetStarted.setVisibility(View.GONE);
        }
    }
    
    private void finishIntro() {
        // Mark intro as shown
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        prefs.edit().putBoolean("intro_shown", true).apply();
        
        navigateToLogin();
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
