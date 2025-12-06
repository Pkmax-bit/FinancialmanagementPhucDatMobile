package com.example.financialmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.financialmanagement.R;
import com.example.financialmanagement.activities.ProjectDetailActivity;
import com.example.financialmanagement.adapters.RecentProjectsAdapter;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.services.ProjectService;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * Dashboard Fragment - Màn hình tổng quan
 * Hiển thị thống kê doanh thu, chi phí, dự án
 */
public class DashboardFragment extends Fragment {

    // Header views
    private TextView tvUserName, tvCurrentMonth, tvAvatar;
    
    // Main summary card
    private TextView tvTotalRevenue, tvTotalExpenses, tvProfit, tvProjectCount;
    
    // Stats cards
    private TextView tvTotalProjects, tvActiveProjects, tvCompletedProjects;
    
    // Recent projects
    private RecyclerView rvRecentProjects;
    private LinearLayout emptyState;
    private RecentProjectsAdapter recentProjectsAdapter;
    
    // Other
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private ProjectService projectService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadDashboardData();
        
        return view;
    }

    private void initializeViews(View view) {
        // Header
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvCurrentMonth = view.findViewById(R.id.tv_current_month);
        tvAvatar = view.findViewById(R.id.tv_avatar);
        
        // Main summary card
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
        tvTotalExpenses = view.findViewById(R.id.tv_total_expenses);
        tvProfit = view.findViewById(R.id.tv_profit);
        tvProjectCount = view.findViewById(R.id.tv_project_count);
        
        // Stats cards
        tvTotalProjects = view.findViewById(R.id.tv_total_projects);
        tvActiveProjects = view.findViewById(R.id.tv_active_projects);
        tvCompletedProjects = view.findViewById(R.id.tv_completed_projects);
        
        // Recent projects
        rvRecentProjects = view.findViewById(R.id.rv_recent_projects);
        emptyState = view.findViewById(R.id.empty_state);
        
        // Other
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        
        // Initialize service
        if (getContext() != null) {
            projectService = new ProjectService(getContext());
        }
        
        // Set current month
        SimpleDateFormat monthFormat = new SimpleDateFormat("'T'MM/yyyy", new Locale("vi", "VN"));
        if (tvCurrentMonth != null) {
            tvCurrentMonth.setText(monthFormat.format(new Date()));
        }
        
        // View all click
        TextView tvViewAll = view.findViewById(R.id.tv_view_all);
        if (tvViewAll != null) {
            tvViewAll.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Xem tất cả dự án", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void setupRecyclerView() {
        recentProjectsAdapter = new RecentProjectsAdapter(new ArrayList<>(), project -> {
            Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
            intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_ID, project.getId());
            intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_NAME, project.getName());
            startActivity(intent);
        });
        
        rvRecentProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentProjects.setAdapter(recentProjectsAdapter);
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.md_primary);
        swipeRefresh.setOnRefreshListener(this::loadDashboardData);
    }

    private void loadDashboardData() {
        showLoading(true);
        
        // Load user info
        loadUserInfo();
        
        // Load all projects to calculate statistics
        loadProjectStats();
    }

    private void loadUserInfo() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvUserName.setText("Admin");
                if (tvAvatar != null) {
                    tvAvatar.setText("A");
                }
            });
        }
    }

    private void loadProjectStats() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000);
        
        projectService.getAllProjects(params, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        calculateAndDisplayStats(projects);
                        displayRecentProjects(projects);
                        showLoading(false);
                        swipeRefresh.setRefreshing(false);
                    });
                }
            }
            
            @Override
            public void onSuccess(Project project) {}
            
            @Override
            public void onSuccess() {}
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        swipeRefresh.setRefreshing(false);
                        setDefaultValues();
                        showEmptyState(true);
                    });
                }
            }
        });
    }
    
    private void calculateAndDisplayStats(List<Project> projects) {
        double totalRevenue = 0;
        double totalExpenses = 0;
        int totalCount = 0;
        int activeCount = 0;
        int completedCount = 0;
        
        for (Project project : projects) {
            totalCount++;
            
            // Revenue from budget
            if (project.getBudget() != null) {
                totalRevenue += project.getBudget();
            }
            
            // Expenses from actual cost
            if (project.getActualCost() != null) {
                totalExpenses += project.getActualCost();
            }
            
            // Count by status
            String status = project.getStatus();
            if (status != null) {
                if ("active".equalsIgnoreCase(status) || "in_progress".equalsIgnoreCase(status)) {
                    activeCount++;
                } else if ("completed".equalsIgnoreCase(status) || "done".equalsIgnoreCase(status)) {
                    completedCount++;
                }
            }
        }
        
        double profit = totalRevenue - totalExpenses;
        
        // Update main summary card
        tvTotalRevenue.setText(CurrencyFormatter.format(totalRevenue));
        tvTotalExpenses.setText(CurrencyFormatter.format(totalExpenses));
        tvProfit.setText(CurrencyFormatter.format(profit));
        tvProjectCount.setText(String.valueOf(totalCount));
        
        // Update stats cards
        tvTotalProjects.setText(String.valueOf(totalCount));
        tvActiveProjects.setText(String.valueOf(activeCount));
        tvCompletedProjects.setText(String.valueOf(completedCount));
    }
    
    private void displayRecentProjects(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            showEmptyState(true);
            return;
        }
        
        showEmptyState(false);
        
        // Get first 5 projects
        List<Project> recentProjects = projects.size() > 5 
            ? projects.subList(0, 5) 
            : projects;
        
        recentProjectsAdapter.updateProjects(recentProjects);
    }
    
    private void showEmptyState(boolean show) {
        if (emptyState != null) {
            emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvRecentProjects != null) {
            rvRecentProjects.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void setDefaultValues() {
        tvTotalRevenue.setText("0 ₫");
        tvTotalExpenses.setText("0 ₫");
        tvProfit.setText("0 ₫");
        tvProjectCount.setText("0");
        tvTotalProjects.setText("0");
        tvActiveProjects.setText("0");
        tvCompletedProjects.setText("0");
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
