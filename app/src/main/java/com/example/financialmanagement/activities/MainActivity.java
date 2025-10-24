package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.financialmanagement.R;
import com.example.financialmanagement.fragments.DashboardFragment;
import com.example.financialmanagement.fragments.ProjectsFragment;
import com.example.financialmanagement.fragments.ExpensesFragment;
import com.example.financialmanagement.fragments.ReportsFragment;
import com.example.financialmanagement.fragments.SettingsFragment;
import com.example.financialmanagement.fragments.CustomersFragment;
import com.example.financialmanagement.fragments.QuotesFragment;
import com.example.financialmanagement.fragments.BudgetFragment;
import com.example.financialmanagement.fragments.InvoicesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

/**
 * Main Activity - Entry point của ứng dụng
 * Quản lý navigation giữa các màn hình chính
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

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
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        
        // Setup toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup drawer toggle
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.nav_header_title, R.string.nav_header_subtitle);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        
        int itemId = item.getItemId();
        
        // Handle bottom navigation items
        if (itemId == R.id.nav_dashboard) {
            selectedFragment = new DashboardFragment();
        } else if (itemId == R.id.nav_projects) {
            selectedFragment = new ProjectsFragment();
        } else if (itemId == R.id.nav_expenses) {
            selectedFragment = new ExpensesFragment();
        } else if (itemId == R.id.nav_reports) {
            selectedFragment = new ReportsFragment();
        } else if (itemId == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
        }
        // Handle drawer navigation items
        else if (itemId == R.id.nav_customers) {
            selectedFragment = new CustomersFragment();
        } else if (itemId == R.id.nav_quotes) {
            selectedFragment = new QuotesFragment();
        } else if (itemId == R.id.nav_budget) {
            selectedFragment = new BudgetFragment();
        } else if (itemId == R.id.nav_invoices) {
            selectedFragment = new InvoicesFragment();
        } else if (itemId == R.id.nav_help) {
            // Handle help
            return true;
        } else if (itemId == R.id.nav_about) {
            // Handle about
            return true;
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            // Close drawer if it's open
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
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
