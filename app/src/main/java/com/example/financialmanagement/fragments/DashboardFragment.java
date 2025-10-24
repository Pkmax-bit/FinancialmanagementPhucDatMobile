package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.RecentProjectsAdapter;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.services.ProjectService;
import com.example.financialmanagement.services.ExpenseService;
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

    private TextView tvTotalProjects, tvTotalExpenses, tvTotalRevenue;
    private RecyclerView rvRecentProjects;
    private RecentProjectsAdapter recentProjectsAdapter;
    private ProjectService projectService;
    private ExpenseService expenseService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadDashboardData();
        
        return view;
    }

    private void initializeViews(View view) {
        tvTotalProjects = view.findViewById(R.id.tv_total_projects);
        tvTotalExpenses = view.findViewById(R.id.tv_total_expenses);
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
        rvRecentProjects = view.findViewById(R.id.rv_recent_projects);
        
        projectService = new ProjectService(getContext());
        expenseService = new ExpenseService(getContext());
    }

    private void setupRecyclerView() {
        recentProjectsAdapter = new RecentProjectsAdapter(new ArrayList<>());
        rvRecentProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentProjects.setAdapter(recentProjectsAdapter);
    }

    private void loadDashboardData() {
        // Load dashboard statistics
        loadDashboardStats();
        
        // Load recent projects
        loadRecentProjects();
    }

    private void loadDashboardStats() {
        // Load project statistics for project management
        loadTotalProjects();
        
        // Load active projects count
        loadActiveProjects();
        
        // Load completed projects count
        loadCompletedProjects();
    }
    
    private void loadTotalProjects() {
        // Get all projects to count them
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000); // Get all projects
        
        projectService.getAllProjects(params, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalProjects.setText(String.valueOf(projects.size()));
                        ApiDebugger.logResponse(200, "Success", "Total projects: " + projects.size());
                    });
                }
            }
            
            @Override
            public void onSuccess(Project project) {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalProjects.setText("0");
                        ApiDebugger.logError("loadTotalProjects", new Exception(error));
                    });
                }
            }
        });
    }
    
    private void loadActiveProjects() {
        // Get active projects count
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000);
        params.put("status", "active"); // Filter by active status
        
        ApiDebugger.logRequest("GET", "Active Projects", null, params);
        
        projectService.getAllProjects(params, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalExpenses.setText(String.valueOf(projects.size()));
                        ApiDebugger.logResponse(200, "Success", "Active projects: " + projects.size());
                    });
                }
            }
            
            @Override
            public void onSuccess(Project project) {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalExpenses.setText("0");
                        ApiDebugger.logError("loadActiveProjects", new Exception(error));
                    });
                }
            }
        });
    }
    
    private void loadCompletedProjects() {
        // Get completed projects count
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1000);
        params.put("status", "completed"); // Filter by completed status
        
        ApiDebugger.logRequest("GET", "Completed Projects", null, params);
        
        projectService.getAllProjects(params, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalRevenue.setText(String.valueOf(projects.size()));
                        ApiDebugger.logResponse(200, "Success", "Completed projects: " + projects.size());
                    });
                }
            }
            
            @Override
            public void onSuccess(Project project) {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvTotalRevenue.setText("0");
                        ApiDebugger.logError("loadCompletedProjects", new Exception(error));
                    });
                }
            }
        });
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
}
