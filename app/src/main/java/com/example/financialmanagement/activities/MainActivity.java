package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import com.example.financialmanagement.R;
import com.example.financialmanagement.fragments.DashboardFragment;
import com.example.financialmanagement.fragments.SettingsFragment;
import com.example.financialmanagement.activities.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main Activity - Entry point của ứng dụng
 * Quản lý navigation giữa các màn hình chính
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupBottomNavigation();
        
        // Load Dashboard fragment mặc định
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        
        int itemId = item.getItemId();
        if (itemId == R.id.nav_login) {
            // Navigate to Login Activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.nav_dashboard) {
            selectedFragment = new DashboardFragment();
        } else if (itemId == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
