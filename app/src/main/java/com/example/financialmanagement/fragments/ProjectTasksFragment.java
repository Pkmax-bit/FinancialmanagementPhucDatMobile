package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.TaskAdapter;
import com.example.financialmanagement.models.Task;
import com.example.financialmanagement.models.Employee;
import com.example.financialmanagement.services.ProjectService;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectTasksFragment extends Fragment {
    
    private static final String ARG_PROJECT_ID = "project_id";
    
    private String projectId;
    private RecyclerView rvTasks;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TaskAdapter taskAdapter;
    private ProjectService projectService;
    private List<Task> tasksList = new ArrayList<>();
    private List<Task> filteredTasksList = new ArrayList<>();
    
    // Filter components
    private TextInputEditText editSearch;
    private Chip chipDateFilter, chipEmployeeFilter, chipStatusFilter, chipClearFilters;
    
    // Filter states
    private String searchQuery = "";
    private Long startDateMillis = null;
    private Long endDateMillis = null;
    private String selectedEmployeeId = null;
    private String selectedEmployeeName = null;
    private String selectedStatus = null;
    
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
        
        // Filter components
        editSearch = view.findViewById(R.id.edit_search);
        chipDateFilter = view.findViewById(R.id.chip_date_filter);
        chipEmployeeFilter = view.findViewById(R.id.chip_employee_filter);
        chipStatusFilter = view.findViewById(R.id.chip_status_filter);
        chipClearFilters = view.findViewById(R.id.chip_clear_filters);
        
        setupRecyclerView();
        setupFilters();
        loadTasks();
        
        return view;
    }
    
    private void setupRecyclerView() {
        rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        taskAdapter = new TaskAdapter(requireContext(), filteredTasksList);
        rvTasks.setAdapter(taskAdapter);
    }
    
    private void setupFilters() {
        // Search text listener
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                applyFilters();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // Date filter
        chipDateFilter.setOnClickListener(v -> showDateRangePicker());
        
        // Employee filter
        chipEmployeeFilter.setOnClickListener(v -> showEmployeeFilter());
        
        // Status filter
        chipStatusFilter.setOnClickListener(v -> showStatusFilter());
        
        // Clear filters
        chipClearFilters.setOnClickListener(v -> clearAllFilters());
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
                            applyFilters(); // Apply filters after loading
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
            applyFilters();
        }
    }
    
    /**
     * Apply all active filters
     */
    private void applyFilters() {
        filteredTasksList.clear();
        
        for (Task task : tasksList) {
            boolean matchesFilters = true;
            
            // Search by title and description
            if (!searchQuery.isEmpty()) {
                String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
                String description = task.getDescription() != null ? task.getDescription().toLowerCase() : "";
                String searchLower = searchQuery.toLowerCase();
                if (!title.contains(searchLower) && !description.contains(searchLower)) {
                    matchesFilters = false;
                }
            }
            
            // Filter by date range
            if (startDateMillis != null && endDateMillis != null && task.getDueDate() != null && !task.getDueDate().isEmpty()) {
                try {
                    // Try multiple date formats
                    String dueDateStr = task.getDueDate();
                    Date taskDate = null;
                    
                    // Try ISO format first (yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
                    SimpleDateFormat[] formats = {
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                        new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                        new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    };
                    
                    for (SimpleDateFormat sdf : formats) {
                        try {
                            taskDate = sdf.parse(dueDateStr);
                            if (taskDate != null) break;
                        } catch (Exception ignored) {}
                    }
                    
                    if (taskDate != null) {
                        // Compare only date part (ignore time)
                        long taskTime = taskDate.getTime();
                        // Add 1 day to endDateMillis to include the full end date
                        long endDateInclusive = endDateMillis + (24 * 60 * 60 * 1000) - 1;
                        if (taskTime < startDateMillis || taskTime > endDateInclusive) {
                            matchesFilters = false;
                        }
                    } else {
                        // If date parsing fails, exclude the task
                        matchesFilters = false;
                    }
                } catch (Exception e) {
                    // If date parsing fails, exclude the task
                    matchesFilters = false;
                }
            }
            
            // Filter by employee
            if (selectedEmployeeId != null) {
                String assigneeId = task.getAssigneeId();
                if (assigneeId == null || !assigneeId.equals(selectedEmployeeId)) {
                    matchesFilters = false;
                }
            }
            
            // Filter by status
            if (selectedStatus != null) {
                String taskStatus = task.getStatus();
                if (taskStatus == null || !taskStatus.equals(selectedStatus)) {
                    matchesFilters = false;
                }
            }
            
            if (matchesFilters) {
                filteredTasksList.add(task);
            }
        }
        
        if (taskAdapter != null) {
            taskAdapter.notifyDataSetChanged();
        }
        
        // Update empty view
        if (filteredTasksList.isEmpty() && !tasksList.isEmpty()) {
            tvEmpty.setText("Không tìm thấy nhiệm vụ phù hợp");
            tvEmpty.setVisibility(View.VISIBLE);
        } else if (tasksList.isEmpty()) {
            tvEmpty.setText("Không có nhiệm vụ nào");
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        
        // Show/hide clear button
        boolean hasFilters = !searchQuery.isEmpty() || startDateMillis != null || 
                           selectedEmployeeId != null || selectedStatus != null;
        chipClearFilters.setVisibility(hasFilters ? View.VISIBLE : View.GONE);
    }
    
    /**
     * Show date range picker
     */
    private void showDateRangePicker() {
        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> dateRangePicker = 
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Chọn khoảng thời gian")
                .build();
        
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            startDateMillis = selection.first;
            endDateMillis = selection.second;
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateRange = sdf.format(new Date(startDateMillis)) + " - " + 
                             sdf.format(new Date(endDateMillis));
            chipDateFilter.setText("Thời gian: " + dateRange);
            applyFilters();
        });
        
        dateRangePicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");
    }
    
    /**
     * Show employee filter dialog
     */
    private void showEmployeeFilter() {
        // Get project team members
        projectService.getProjectTeam(projectId, new ProjectService.TeamCallback() {
            @Override
            public void onSuccess(List<com.example.financialmanagement.models.TeamMember> teamMembers) {
                // Convert TeamMember to simplified list
                List<Employee> employees = new ArrayList<>();
                for (com.example.financialmanagement.models.TeamMember tm : teamMembers) {
                    Employee emp = new Employee();
                    emp.setId(tm.getEmployeeId() != null ? tm.getEmployeeId() : tm.getId());
                    emp.setFirstName(tm.getName());
                    emp.setLastName("");
                    employees.add(emp);
                }
                if (employees == null || employees.isEmpty()) {
                    Toast.makeText(requireContext(), "Chưa có thành viên nào", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String[] employeeNames = new String[employees.size() + 1];
                employeeNames[0] = "Tất cả nhân viên";
                for (int i = 0; i < employees.size(); i++) {
                    employeeNames[i + 1] = employees.get(i).getFullName();
                }
                
                new AlertDialog.Builder(requireContext())
                    .setTitle("Lọc theo nhân viên")
                    .setItems(employeeNames, (dialog, which) -> {
                        if (which == 0) {
                            // Clear employee filter
                            selectedEmployeeId = null;
                            selectedEmployeeName = null;
                            chipEmployeeFilter.setText("Nhân viên");
                        } else {
                            Employee selectedEmp = employees.get(which - 1);
                            selectedEmployeeId = selectedEmp.getId();
                            selectedEmployeeName = selectedEmp.getFullName();
                            chipEmployeeFilter.setText("NV: " + selectedEmployeeName);
                        }
                        applyFilters();
                    })
                    .show();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Lỗi tải danh sách nhân viên", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Show status filter dialog
     */
    private void showStatusFilter() {
        String[] statuses = {"Tất cả", "Todo", "In Progress", "Completed", "Cancelled"};
        String[] statusValues = {null, "todo", "in_progress", "completed", "cancelled"};
        
        new AlertDialog.Builder(requireContext())
            .setTitle("Lọc theo trạng thái")
            .setItems(statuses, (dialog, which) -> {
                selectedStatus = statusValues[which];
                if (selectedStatus == null) {
                    chipStatusFilter.setText("Trạng thái");
                } else {
                    chipStatusFilter.setText("TT: " + statuses[which]);
                }
                applyFilters();
            })
            .show();
    }
    
    /**
     * Clear all filters
     */
    private void clearAllFilters() {
        searchQuery = "";
        startDateMillis = null;
        endDateMillis = null;
        selectedEmployeeId = null;
        selectedEmployeeName = null;
        selectedStatus = null;
        
        editSearch.setText("");
        chipDateFilter.setText("Thời gian");
        chipEmployeeFilter.setText("Nhân viên");
        chipStatusFilter.setText("Trạng thái");
        chipClearFilters.setVisibility(View.GONE);
        
        applyFilters();
    }
}
