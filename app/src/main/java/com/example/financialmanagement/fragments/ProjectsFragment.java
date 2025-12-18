package com.example.financialmanagement.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.financialmanagement.activities.LoginActivity;
import com.example.financialmanagement.activities.ProjectFormActivity;
import com.example.financialmanagement.adapters.ProjectsAdapter;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.services.ProjectService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ProjectsFragment extends Fragment implements ProjectsAdapter.ProjectClickListener {

    private static final int REQUEST_CODE_ADD_PROJECT = 1001;
    private static final int REQUEST_CODE_EDIT_PROJECT = 1002;

    private RecyclerView rvProjects;
    private SwipeRefreshLayout swipeRefresh;
    private ExtendedFloatingActionButton fabAddProject;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private EditText etSearch;
    private TextView tvProjectCount;
    
    private ChipGroup chipGroupFilter;
    private Chip chipAll, chipActive, chipCompleted, chipOnHold;
    
    private List<Project> allProjects = new ArrayList<>();
    private ProjectsAdapter projectsAdapter;
    private ProjectService projectService;
    
    private String currentFilter = "all";
    private String searchQuery = "";
    
    // Handler for debouncing search
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupSearch();
        setupFilterChips();
        loadProjects();
        
        return view;
    }

    private void initializeViews(View view) {
        rvProjects = view.findViewById(R.id.rv_projects);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        fabAddProject = view.findViewById(R.id.fab_add_project);
        emptyState = view.findViewById(R.id.empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
        etSearch = view.findViewById(R.id.et_search);
        tvProjectCount = view.findViewById(R.id.tv_project_count);
        
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        chipAll = view.findViewById(R.id.chip_all);
        chipActive = view.findViewById(R.id.chip_active);
        chipCompleted = view.findViewById(R.id.chip_completed);
        chipOnHold = view.findViewById(R.id.chip_on_hold);
        
        projectService = new ProjectService(getContext());
        
        fabAddProject.setOnClickListener(v -> createNewProject());
    }

    private void setupRecyclerView() {
        projectsAdapter = new ProjectsAdapter(new ArrayList<>(), this);
        rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProjects.setAdapter(projectsAdapter);
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.md_primary);
        swipeRefresh.setOnRefreshListener(this::loadProjects);
    }
    
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous search runnable
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                // Debounce search - wait 300ms after user stops typing
                searchRunnable = () -> {
                    searchQuery = s.toString().toLowerCase().trim();
                    filterAndDisplayProjects();
                };
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipAll.setChecked(true);
                return;
            }
            
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_all) {
                currentFilter = "all";
            } else if (checkedId == R.id.chip_active) {
                currentFilter = "active";
            } else if (checkedId == R.id.chip_completed) {
                currentFilter = "completed";
            } else if (checkedId == R.id.chip_on_hold) {
                currentFilter = "on_hold";
            }
            
            filterAndDisplayProjects();
        });
    }

    private void loadProjects() {
        showLoading(true);
        
        projectService.getProjects(new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projectsList) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allProjects = projectsList != null ? projectsList : new ArrayList<>();
                        filterAndDisplayProjects();
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
                        
                        if (error.contains("403") || error.contains("401")) {
                            Toast.makeText(getContext(), "Không có quyền truy cập", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            if (getActivity() != null) getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                            showEmptyState(true);
                        }
                    });
                }
            }
        });
    }
    
    private void filterAndDisplayProjects() {
        // Run filtering on background thread for large datasets
        new Thread(() -> {
            List<Project> filtered = new ArrayList<>();
            
            for (Project p : allProjects) {
                boolean matchesFilter = currentFilter.equals("all") || 
                    (p.getStatus() != null && p.getStatus().equalsIgnoreCase(currentFilter));
                
                boolean matchesSearch = searchQuery.isEmpty() ||
                    (p.getName() != null && p.getName().toLowerCase().contains(searchQuery)) ||
                    (p.getProjectCode() != null && p.getProjectCode().toLowerCase().contains(searchQuery)) ||
                    (p.getCustomerName() != null && p.getCustomerName().toLowerCase().contains(searchQuery));
                
                if (matchesFilter && matchesSearch) {
                    filtered.add(p);
                }
            }
            
            // Update UI on main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    projectsAdapter.updateProjects(filtered);
                    if (tvProjectCount != null) {
                        tvProjectCount.setText(filtered.size() + " dự án");
                    }
                    showEmptyState(filtered.isEmpty());
                });
            }
        }).start();
    }
    
    private void showEmptyState(boolean show) {
        if (emptyState != null) {
            emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvProjects != null) {
            rvProjects.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void createNewProject() {
        Intent intent = new Intent(getContext(), ProjectFormActivity.class);
        intent.putExtra(ProjectFormActivity.EXTRA_MODE, ProjectFormActivity.MODE_CREATE);
        startActivityForResult(intent, REQUEST_CODE_ADD_PROJECT);
    }

    @Override
    public void onProjectClick(Project project) {
        Toast.makeText(getContext(), "Chi tiết: " + project.getName(), Toast.LENGTH_SHORT).show();
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
        new AlertDialog.Builder(getContext())
            .setTitle("Xóa dự án")
            .setMessage("Bạn có chắc muốn xóa \"" + project.getName() + "\"?")
            .setPositiveButton("Xóa", (d, w) -> deleteProject(project))
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void deleteProject(Project project) {
        projectService.deleteProject(project.getId(), new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {}
            
            @Override
            public void onSuccess(Project project) {}
            
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                        loadProjects();
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            loadProjects();
        }
    }
}