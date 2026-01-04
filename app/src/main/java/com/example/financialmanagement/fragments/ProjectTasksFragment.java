package com.example.financialmanagement.fragments;

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
import com.example.financialmanagement.adapters.TaskAdapter;
import com.example.financialmanagement.models.Task;
import com.example.financialmanagement.services.ProjectService;

import java.util.ArrayList;
import java.util.List;

public class ProjectTasksFragment extends Fragment {
    
    private static final String ARG_PROJECT_ID = "project_id";
    
    private String projectId;
    private RecyclerView rvTasks;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TaskAdapter taskAdapter;
    private ProjectService projectService;
    private List<Task> tasksList = new ArrayList<>();
    
    public static ProjectTasksFragment newInstance(String projectId) {
        ProjectTasksFragment fragment = new ProjectTasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, projectId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectId = getArguments().getString(ARG_PROJECT_ID);
        }
        projectService = new ProjectService(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_tasks, container, false);
        
        rvTasks = view.findViewById(R.id.rv_tasks);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        
        setupRecyclerView();
        loadTasks();
        
        return view;
    }
    
    private void setupRecyclerView() {
        rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        taskAdapter = new TaskAdapter(requireContext(), tasksList);
        // Assuming TaskAdapter has a click listener or we can implement it here
        rvTasks.setAdapter(taskAdapter);
    }
    
    private void loadTasks() {
        if (projectId == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        
        projectService.getProjectTasks(projectId, new ProjectService.TasksCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        tasksList.clear();
                        if (tasks != null && !tasks.isEmpty()) {
                            tasksList.addAll(tasks);
                            taskAdapter.notifyDataSetChanged();
                            tvEmpty.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("Lỗi: " + error);
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    public void updateTasks(List<Task> tasks) {
        if (tasks != null) {
            tasksList.clear();
            tasksList.addAll(tasks);
            if (taskAdapter != null) {
                taskAdapter.notifyDataSetChanged();
            }
        }
    }
}
