package com.example.financialmanagement.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.activities.ProjectFormActivity;
import com.example.financialmanagement.adapters.ProjectsAdapter;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.services.ProjectService;
import com.example.financialmanagement.utils.ApiDebugger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ProjectsFragment extends Fragment implements ProjectsAdapter.ProjectClickListener {

    private static final int REQUEST_CODE_ADD_PROJECT = 1001;
    private static final int REQUEST_CODE_EDIT_PROJECT = 1002;

    private RecyclerView rvProjects;
    private FloatingActionButton fabAddProject;
    private LinearLayout layoutEmptyState;
    private TextView tvTotalProjects;
    private TextView tvActiveProjects;
    
    private List<Project> projects;
    private ProjectsAdapter projectsAdapter;
    private ProjectService projectService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadProjects();
        
        return view;
    }

    private void initializeViews(View view) {
        rvProjects = view.findViewById(R.id.rv_projects);
        fabAddProject = view.findViewById(R.id.fab_add_project);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        tvTotalProjects = view.findViewById(R.id.tv_total_projects);
        tvActiveProjects = view.findViewById(R.id.tv_active_projects);
        
        projects = new ArrayList<>();
        projectService = new ProjectService(getContext());
    }

    private void setupRecyclerView() {
        projectsAdapter = new ProjectsAdapter(projects, this);
        rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProjects.setAdapter(projectsAdapter);
    }

    private void setupListeners() {
        fabAddProject.setOnClickListener(v -> {
            createNewProject();
        });
    }

    /**
     * Load projects from API
     */
    private void loadProjects() {
        ApiDebugger.logAuth("Loading projects", true);
        
        projectService.getProjects(new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projectsList) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        projects.clear();
                        projects.addAll(projectsList);
                        projectsAdapter.updateProjects(projects);
                        updateStatistics();
                        updateEmptyState();
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
                        ApiDebugger.logAuth("Projects load failed: " + error, false);
                        
                        // Check if it's an authentication error
                        if (error.contains("403") || error.contains("401")) {
                            Toast.makeText(getContext(), "Không có quyền truy cập. Đang tải dữ liệu mẫu...", Toast.LENGTH_LONG).show();
                            loadSampleProjects();
                        } else {
                            Toast.makeText(getContext(), "Lỗi tải dự án: " + error, Toast.LENGTH_SHORT).show();
                            loadSampleProjects();
                        }
                    });
                }
            }
        });
    }

    /**
     * Load sample projects as fallback
     */
    private void loadSampleProjects() {
        ApiDebugger.logAuth("Loading sample projects as fallback", true);
        
        List<Project> sampleProjects = new ArrayList<>();
        
        // Create sample projects
        Project project1 = new Project("PRJ001", "Dự án Website ABC", "CUST001");
        project1.setCustomerName("Công ty ABC");
        project1.setStatus("active");
        project1.setPriority("high");
        project1.setBudget(100000000.0);
        project1.setAssignedTo("Nguyễn Văn A");
        project1.setDescription("Phát triển website thương mại điện tử");
        sampleProjects.add(project1);
        
        Project project2 = new Project("PRJ002", "Dự án Mobile App", "CUST002");
        project2.setCustomerName("Nguyễn Văn B");
        project2.setStatus("completed");
        project2.setPriority("medium");
        project2.setBudget(50000000.0);
        project2.setAssignedTo("Trần Thị C");
        project2.setDescription("Ứng dụng di động quản lý tài chính");
        sampleProjects.add(project2);
        
        Project project3 = new Project("PRJ003", "Dự án ERP System", "CUST003");
        project3.setCustomerName("Cơ quan DEF");
        project3.setStatus("on_hold");
        project3.setPriority("low");
        project3.setBudget(200000000.0);
        project3.setAssignedTo("Lê Văn D");
        project3.setDescription("Hệ thống quản lý doanh nghiệp");
        sampleProjects.add(project3);
        
        // Update adapter with sample data
        projects.clear();
        projects.addAll(sampleProjects);
        projectsAdapter.updateProjects(projects);
        updateStatistics();
        updateEmptyState();
        
        Toast.makeText(getContext(), "Đã tải dữ liệu mẫu (" + sampleProjects.size() + " dự án)", Toast.LENGTH_SHORT).show();
    }

    /**
     * Update statistics
     */
    private void updateStatistics() {
        int totalProjects = projects.size();
        int activeProjects = 0;
        
        for (Project project : projects) {
            if ("active".equals(project.getStatus())) {
                activeProjects++;
            }
        }
        
        tvTotalProjects.setText(String.valueOf(totalProjects));
        tvActiveProjects.setText(String.valueOf(activeProjects));
    }

    /**
     * Update empty state visibility
     */
    private void updateEmptyState() {
        if (projects.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvProjects.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvProjects.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Create new project
     */
    public void createNewProject() {
        Intent intent = new Intent(getContext(), ProjectFormActivity.class);
        intent.putExtra(ProjectFormActivity.EXTRA_MODE, ProjectFormActivity.MODE_CREATE);
        startActivityForResult(intent, REQUEST_CODE_ADD_PROJECT);
    }

    // ProjectClickListener implementation
    @Override
    public void onProjectClick(Project project) {
        // Navigate to project detail
        Toast.makeText(getContext(), "Chi tiết dự án: " + project.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProjectEdit(Project project) {
        Intent intent = new Intent(getContext(), ProjectFormActivity.class);
        intent.putExtra(ProjectFormActivity.EXTRA_PROJECT, project);
        intent.putExtra(ProjectFormActivity.EXTRA_MODE, ProjectFormActivity.MODE_EDIT);
        startActivityForResult(intent, REQUEST_CODE_EDIT_PROJECT);
    }

    @Override
    public void onProjectDelete(Project project) {
        showDeleteConfirmationDialog(project);
    }

    /**
     * Show delete confirmation dialog
     */
    private void showDeleteConfirmationDialog(Project project) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa dự án")
                .setMessage("Bạn có chắc chắn muốn xóa dự án \"" + project.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteProject(project);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Delete project
     */
    private void deleteProject(Project project) {
        projectService.deleteProject(project.getId(), new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                // Not used
            }
            
            @Override
            public void onSuccess(Project project) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Xóa dự án thành công", Toast.LENGTH_SHORT).show();
                        loadProjects(); // Reload projects
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi xóa dự án: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD_PROJECT || requestCode == REQUEST_CODE_EDIT_PROJECT) {
                // Reload projects after add/edit
                loadProjects();
            }
        }
    }
}