package com.example.financialmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.activities.ProjectDetailActivity;
import com.example.financialmanagement.adapters.RecentProjectsAdapter;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.services.ProjectService;
import com.example.financialmanagement.services.ExpenseService;
import com.example.financialmanagement.services.UserService;
import com.example.financialmanagement.models.User;
import com.example.financialmanagement.utils.CurrencyFormatter;
import com.example.financialmanagement.utils.ApiDebugger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Dashboard Fragment - Màn hình tổng quan
 * Hiển thị thống kê và dự án gần đây
 */
public class DashboardFragment extends Fragment {

    private TextView tvUserName, tvUserRole, tvTotalSpent, tvBudgetProgress, tvRemainingBudget;
    private TextView tvFoodAmount, tvTransportAmount, tvBillsAmount;
    private RecyclerView rvRecentProjects;
    private RecentProjectsAdapter recentProjectsAdapter;
    private ProjectService projectService;
    private ExpenseService expenseService;
    private UserService userService;
    private ProgressBar progressBudget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
            
            initializeViews(view);
            setupRecyclerView();
            loadDashboardData();
            
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi tải Dashboard: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return inflater.inflate(R.layout.fragment_dashboard, container, false);
        }
    }

    private void initializeViews(View view) {
        try {
            tvUserName = view.findViewById(R.id.tv_user_name);
            tvUserRole = view.findViewById(R.id.tv_user_role);
            tvTotalSpent = view.findViewById(R.id.tv_total_spent);
            progressBudget = view.findViewById(R.id.progress_budget);

            rvRecentProjects = view.findViewById(R.id.rv_recent_projects);
            
            // Check for null views
            if (tvUserName == null) {
                throw new RuntimeException("tvUserName not found");
            }
            if (tvUserRole == null) {
                throw new RuntimeException("tvUserRole not found");
            }
            if (tvTotalSpent == null) {
                throw new RuntimeException("tvTotalSpent not found");
            }
            if (progressBudget == null) {
                throw new RuntimeException("progressBudget not found");
            }
            if (rvRecentProjects == null) {
                throw new RuntimeException("rvRecentProjects not found");
            }
            
            // Initialize services
            if (getContext() != null) {
                projectService = new ProjectService(getContext());
                expenseService = new ExpenseService(getContext());
                userService = new UserService(getContext());
            } else {
                throw new RuntimeException("Context is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khởi tạo views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        try {
            recentProjectsAdapter = new RecentProjectsAdapter(new ArrayList<>(), new RecentProjectsAdapter.ProjectClickListener() {
                @Override
                public void onProjectClick(Project project) {
                    // Navigate to project detail
                    Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
                    intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_ID, project.getId());
                    intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_NAME, project.getName());
                    startActivity(intent);
                }
            });
            rvRecentProjects.setLayoutManager(new LinearLayoutManager(getContext()));
            rvRecentProjects.setAdapter(recentProjectsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi setup RecyclerView: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadDashboardData() {
        try {
            // Load user information
            loadUserInfo();
            
            // Load dashboard statistics
            loadDashboardStats();
            
            // Load recent projects
            loadRecentProjects();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadUserInfo() {
        // For now, skip API call and show default user info
        // TODO: Implement proper user info API when backend is ready
        ApiDebugger.logAuth("Skipping user info API call - using default values", true);
        
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvUserName.setText("Người dùng");
                tvUserRole.setText("Nhân viên");
            });
        }
    }

    private void loadDashboardStats() {
        // Load monthly spending data
        loadMonthlySpending();
        
        // Load category spending
        loadCategorySpending();
        
        // Load budget progress
        loadBudgetProgress();
    }
    
    private void loadMonthlySpending() {
        // Simulate monthly spending data from database
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvTotalSpent.setText("$3,580");
                progressBudget.setProgress(89);
            });
        }
    }
    
    private void loadCategorySpending() {
        // Simulate category spending data from database
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvFoodAmount.setText("$1,250");
                tvTransportAmount.setText("$890");
                tvBillsAmount.setText("$720");
            });
        }
    }
    
    private void loadBudgetProgress() {
        // Simulate budget progress calculation
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // Update progress bar based on spending vs budget
                progressBudget.setProgress(89);
            });
        }
    }
    
    
    

    private void loadRecentProjects() {
        // Use getAllProjects with limit to get recent projects
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 5);
        params.put("sort", "created_at");
        params.put("order", "desc");
        
        ApiDebugger.logRequest("GET", "Recent Projects", null, params);
        
        projectService.getAllProjects(params, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        recentProjectsAdapter.updateProjects(projects);
                        ApiDebugger.logResponse(200, "Success", "Recent projects loaded: " + projects.size());
                    });
                }
            }
            
            @Override
            public void onSuccess(Project project) {
                // Not used in this context
            }
            
            @Override
            public void onSuccess() {
                // Not used in this context
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi tải dự án gần đây: " + error, Toast.LENGTH_SHORT).show();
                        ApiDebugger.logError("loadRecentProjects", new Exception(error));
                    });
                }
            }
        });
    }
    
    private void loadProjectCostSummary() {
        // Load project cost summary for dashboard
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000);
        
        ApiDebugger.logRequest("GET", "Project Cost Summary", null, params);
        
        projectService.getAllProjects(params, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (projects != null) {
                            double totalBudget = 0;
                            double totalActualCost = 0;
                            int activeProjects = 0;
                            
                            for (Project project : projects) {
                                if (project.getBudget() != null) {
                                    totalBudget += project.getBudget();
                                }
                                if (project.getActualCost() != null) {
                                    totalActualCost += project.getActualCost();
                                }
                                if ("active".equals(project.getStatus())) {
                                    activeProjects++;
                                }
                            }
                            
                            // Update UI with cost information
                            updateCostSummary(totalBudget, totalActualCost, activeProjects);
                            
                            ApiDebugger.logResponse(200, "Success", 
                                "Total Budget: " + totalBudget + ", Actual Cost: " + totalActualCost);
                        }
                    });
                }
            }
            
            @Override
            public void onSuccess(Project project) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        ApiDebugger.logError("loadProjectCostSummary", new Exception(error));
                    });
                }
            }
        });
    }
    
    private void updateCostSummary(double totalBudget, double totalActualCost, int activeProjects) {
        // Update dashboard with cost information
        // This will be displayed in the statistics cards
        ApiDebugger.logResponse(200, "Cost Summary", 
            "Budget: " + CurrencyFormatter.format(totalBudget) + 
            ", Actual: " + CurrencyFormatter.format(totalActualCost) + 
            ", Active: " + activeProjects);
    }
}
