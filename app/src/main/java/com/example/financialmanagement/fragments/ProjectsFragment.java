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
import com.example.financialmanagement.activities.LoginActivity;
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
                            Toast.makeText(getContext(), "Không có quyền truy cập. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                            // Redirect to login
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        } else {
                            Toast.makeText(getContext(), "Lỗi tải dự án: " + error, Toast.LENGTH_SHORT).show();
                        }
                        
                        // Show empty state when error occurs
                        updateEmptyState();
                    });
                }
            }
        });
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