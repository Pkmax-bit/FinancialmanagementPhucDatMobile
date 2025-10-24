package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.ProjectsAdapter;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.services.ProjectService;
import java.util.ArrayList;
import java.util.List;

/**
 * Projects Fragment - Màn hình quản lý dự án
 * Hiển thị danh sách dự án và cho phép thao tác CRUD
 */
public class ProjectsFragment extends Fragment implements ProjectsAdapter.ProjectClickListener {

    private RecyclerView rvProjects;
    private ProjectsAdapter projectsAdapter;
    private ProjectService projectService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadProjects();
        
        return view;
    }

    private void initializeViews(View view) {
        rvProjects = view.findViewById(R.id.rv_projects);
        projectService = new ProjectService(getContext());
    }

    private void setupRecyclerView() {
        projectsAdapter = new ProjectsAdapter(new ArrayList<>(), this);
        rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProjects.setAdapter(projectsAdapter);
    }

    private void loadProjects() {
        projectService.getAllProjects(new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        projectsAdapter.updateProjects(projects);
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
                        Toast.makeText(getContext(), "Lỗi tải dự án: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onProjectClick(Project project) {
        // Navigate to project detail
        // TODO: Implement navigation to project detail activity
        Toast.makeText(getContext(), "Xem chi tiết dự án: " + project.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProjectEdit(Project project) {
        // Navigate to edit project
        // TODO: Implement navigation to edit project activity
        Toast.makeText(getContext(), "Chỉnh sửa dự án: " + project.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProjectDelete(Project project) {
        // Show confirmation dialog and delete project
        // TODO: Implement delete confirmation dialog
        Toast.makeText(getContext(), "Xóa dự án: " + project.getName(), Toast.LENGTH_SHORT).show();
    }
}
