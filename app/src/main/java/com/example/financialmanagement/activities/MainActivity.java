package com.example.financialmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.financialmanagement.R;
import com.example.financialmanagement.fragments.DashboardFragment;
import com.example.financialmanagement.fragments.ProjectsFragment;
import com.example.financialmanagement.fragments.RevenueFragment;
import com.example.financialmanagement.fragments.ExpensesFragment;
import com.example.financialmanagement.fragments.ReportsFragment;
import com.example.financialmanagement.fragments.CustomersFragment;
import com.example.financialmanagement.fragments.QuotesFragment;
import com.example.financialmanagement.fragments.InvoicesFragment;
import com.example.financialmanagement.fragments.SettingsFragment;
import com.example.financialmanagement.auth.AuthManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

/**
 * Main Activity - Entry point của ứng dụng
 * Quản lý navigation giữa các màn hình chính
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private AuthManager authManager;
    private boolean isSettingUpNavigation = false;
    private boolean isUpdatingFromDrawer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Kiểm tra authentication trước khi khởi tạo UI
            authManager = new AuthManager(this);
            if (!authManager.isLoggedIn()) {
                navigateToLogin();
                return;
            }
            
            setContentView(R.layout.drawer_layout);
            
            initializeViews();
            setupToolbar();
            setupDrawer();
            setupBottomNavigation();
            
            // Load Dashboard fragment mặc định
            if (savedInstanceState == null) {
                loadFragment(new DashboardFragment());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Log error and show toast
            android.widget.Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        
        // Check for null views with better error messages
        if (bottomNavigationView == null) {
            android.util.Log.e("MainActivity", "BottomNavigationView not found in layout");
            throw new RuntimeException("BottomNavigationView not found - check if R.id.bottom_navigation exists in drawer_layout.xml");
        }
        if (navigationView == null) {
            android.util.Log.e("MainActivity", "NavigationView not found in layout");
            throw new RuntimeException("NavigationView not found - check if R.id.nav_view exists in drawer_layout.xml");
        }
        if (drawerLayout == null) {
            android.util.Log.e("MainActivity", "DrawerLayout not found in layout");
            throw new RuntimeException("DrawerLayout not found - check if R.id.drawer_layout exists in drawer_layout.xml");
        }
        if (toolbar == null) {
            android.util.Log.e("MainActivity", "Toolbar not found in layout");
            throw new RuntimeException("Toolbar not found - check if R.id.toolbar exists in drawer_layout.xml");
        }
        
        android.util.Log.d("MainActivity", "All views initialized successfully");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView == null) {
            android.util.Log.e("MainActivity", "BottomNavigationView is null!");
            return;
        }
        
        try {
            isSettingUpNavigation = true;
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
            
            // Set default fragment
            loadFragment(new DashboardFragment());
            
            // Use post to ensure the view is fully initialized
            bottomNavigationView.post(() -> {
                bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
                isSettingUpNavigation = false;
            });
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error setting up bottom navigation: " + e.getMessage(), e);
            isSettingUpNavigation = false;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Prevent circular calls during setup
        if (isSettingUpNavigation) {
            return false;
        }
        
        int itemId = item.getItemId();
        
        // Handle navigation based on source
        if (isFromBottomNavigation(item)) {
            return handleBottomNavigation(itemId);
        } else {
            return handleDrawerNavigation(itemId);
        }
    }
    
    private boolean isFromBottomNavigation(MenuItem item) {
        return bottomNavigationView != null && bottomNavigationView.getMenu().findItem(item.getItemId()) != null;
    }
    
    private boolean handleBottomNavigation(int itemId) {
        Fragment selectedFragment = getFragmentForItem(itemId);
        String title = getTitleForItem(itemId);
        
        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            updateToolbarTitle(title);
            return true;
        }
        return false;
    }
    
    private boolean handleDrawerNavigation(int itemId) {
        // Handle special drawer-only items first
        if (itemId == R.id.nav_logout) {
            showLogoutDialog();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.nav_profile) {
            Toast.makeText(this, "Chức năng hồ sơ cá nhân sẽ được triển khai", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.nav_help) {
            Toast.makeText(this, "Chức năng trợ giúp sẽ được triển khai", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (itemId == R.id.nav_about) {
            Toast.makeText(this, "Chức năng giới thiệu sẽ được triển khai", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        
        // Handle navigation items that exist in both drawer and bottom nav
        Fragment selectedFragment = getFragmentForItem(itemId);
        String title = getTitleForItem(itemId);
        
        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            updateToolbarTitle(title);
            
            // Update bottom navigation if the item exists there
            if (itemId == R.id.nav_dashboard || itemId == R.id.nav_projects || 
                itemId == R.id.nav_revenue || itemId == R.id.nav_expenses || 
                itemId == R.id.nav_reports) {
                updateBottomNavigation(itemId);
            }
            
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        
        return false;
    }
    
    private Fragment getFragmentForItem(int itemId) {
        if (itemId == R.id.nav_dashboard) {
            return new DashboardFragment();
        } else if (itemId == R.id.nav_projects) {
            return new ProjectsFragment();
        } else if (itemId == R.id.nav_revenue) {
            return new RevenueFragment();
        } else if (itemId == R.id.nav_expenses) {
            return new ExpensesFragment();
        } else if (itemId == R.id.nav_reports) {
            return new ReportsFragment();
        } else if (itemId == R.id.nav_customers) {
            return new CustomersFragment();
        } else if (itemId == R.id.nav_quotes) {
            return new QuotesFragment();
        } else if (itemId == R.id.nav_invoices) {
            return new InvoicesFragment();
        } else if (itemId == R.id.nav_settings) {
            return new SettingsFragment();
        } else if (itemId == R.id.nav_employees) {
            return new com.example.financialmanagement.fragments.EmployeesFragment();
        } else if (itemId == R.id.nav_tasks) {
            return new com.example.financialmanagement.fragments.TasksFragment();
        }
        return null;
    }
    
    private String getTitleForItem(int itemId) {
        if (itemId == R.id.nav_dashboard) return "Dashboard";
        else if (itemId == R.id.nav_projects) return "Dự án";
        else if (itemId == R.id.nav_revenue) return "Doanh thu";
        else if (itemId == R.id.nav_expenses) return "Chi phí";
        else if (itemId == R.id.nav_reports) return "Báo cáo";
        else if (itemId == R.id.nav_customers) return "Khách hàng";
        else if (itemId == R.id.nav_quotes) return "Báo giá";
        else if (itemId == R.id.nav_invoices) return "Hóa đơn";
        else if (itemId == R.id.nav_settings) return "Cài đặt";
        else if (itemId == R.id.nav_employees) return "Nhân viên";
        else if (itemId == R.id.nav_tasks) return "Công việc";
        return "Dashboard";
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void updateToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void updateBottomNavigation(int itemId) {
        if (bottomNavigationView == null || isSettingUpNavigation) {
            return;
        }
        
        // Only update bottom navigation if the item exists in bottom nav
        if (itemId == R.id.nav_dashboard || itemId == R.id.nav_projects || 
            itemId == R.id.nav_revenue || itemId == R.id.nav_expenses || 
            itemId == R.id.nav_reports) {
            bottomNavigationView.setSelectedItemId(itemId);
        }
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.action_logout) {
            showLogoutDialog();
            return true;
        } else if (itemId == R.id.action_settings) {
            // TODO: Navigate to settings
            Toast.makeText(this, "Chức năng cài đặt sẽ được triển khai", Toast.LENGTH_SHORT).show();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void performLogout() {
        try {
            authManager.logout();
            Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi đăng xuất: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    /**
     * Lấy AuthManager instance để các fragment có thể sử dụng
     */
    public AuthManager getAuthManager() {
        return authManager;
    }
}
