package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.TaskAdapter;
import com.example.financialmanagement.models.Task;
import com.example.financialmanagement.models.Employee;
import com.example.financialmanagement.services.TaskService;
import com.example.financialmanagement.services.ProjectService;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksByGroupActivity extends AppCompatActivity {

    private String groupId;
    private String groupName;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TaskService taskService;
    private ProjectService projectService;
    private TaskAdapter adapter;
    private List<Task> taskList = new ArrayList<>();
    private List<Task> filteredTasksList = new ArrayList<>();
    
    // Filter components
    private TextInputEditText editSearch;
    private Chip chipDateFilter, chipEmployeeFilter, chipStatusFilter, chipClearFilters;
    private TextView tvEmpty;
    
    // Filter states
    private String searchQuery = "";
    private Long startDateMillis = null;
    private Long endDateMillis = null;
    private String selectedEmployeeId = null;
    private String selectedEmployeeName = null;
    private String selectedStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_by_group);

        groupId = getIntent().getStringExtra("group_id");
        groupName = getIntent().getStringExtra("group_name");

        setupToolbar();
        initializeViews();

        taskService = new TaskService(this);
        projectService = new ProjectService(this);
        loadTasks();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(groupName != null ? groupName : "Nhiệm vụ");
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_tasks_by_group);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_tasks);
        swipeRefreshLayout.setOnRefreshListener(this::loadTasks);
        
        tvEmpty = findViewById(R.id.tv_empty);
        
        // Filter components
        editSearch = findViewById(R.id.edit_search);
        chipDateFilter = findViewById(R.id.chip_date_filter);
        chipEmployeeFilter = findViewById(R.id.chip_employee_filter);
        chipStatusFilter = findViewById(R.id.chip_status_filter);
        chipClearFilters = findViewById(R.id.chip_clear_filters);
        
        setupFilters();
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
        swipeRefreshLayout.setRefreshing(true);
        tvEmpty.setVisibility(View.GONE);
        
        taskService.getTasksByGroup(groupId, new TaskService.TaskCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> result) {
                swipeRefreshLayout.setRefreshing(false);
                taskList.clear();
                if (result != null && !result.isEmpty()) {
                    taskList.addAll(result);
                    applyFilters(); // Apply filters after loading
                    tvEmpty.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Không có nhiệm vụ nào");
                }
            }

            @Override
            public void onError(String error) {
                swipeRefreshLayout.setRefreshing(false);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Lỗi: " + error);
                Toast.makeText(TasksByGroupActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Apply all active filters
     */
    private void applyFilters() {
        filteredTasksList.clear();
        
        for (Task task : taskList) {
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
        
        if (adapter == null) {
            adapter = new TaskAdapter(TasksByGroupActivity.this, filteredTasksList);
            recyclerView.setAdapter(adapter);
        } else {
            // Create new adapter with filtered list
            adapter = new TaskAdapter(TasksByGroupActivity.this, filteredTasksList);
            recyclerView.setAdapter(adapter);
        }
        
        // Update empty view
        if (filteredTasksList.isEmpty() && !taskList.isEmpty()) {
            tvEmpty.setText("Không tìm thấy nhiệm vụ phù hợp");
            tvEmpty.setVisibility(View.VISIBLE);
        } else if (taskList.isEmpty()) {
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
        
        dateRangePicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
    }
    
    /**
     * Show employee filter dialog
     * Note: For tasks by group, we'll get employees from tasks themselves
     */
    private void showEmployeeFilter() {
        // Get unique employees from current tasks
        List<Employee> employees = new ArrayList<>();
        java.util.Set<String> seenIds = new java.util.HashSet<>();
        
        for (Task task : taskList) {
            if (task.getAssigneeId() != null && !task.getAssigneeId().isEmpty() && 
                !seenIds.contains(task.getAssigneeId())) {
                Employee emp = new Employee();
                emp.setId(task.getAssigneeId());
                emp.setFirstName(task.getAssigneeName() != null ? task.getAssigneeName() : "Nhân viên");
                emp.setLastName("");
                employees.add(emp);
                seenIds.add(task.getAssigneeId());
            }
        }
        
        if (employees.isEmpty()) {
            Toast.makeText(this, "Chưa có nhiệm vụ được giao cho nhân viên nào", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] employeeNames = new String[employees.size() + 1];
        employeeNames[0] = "Tất cả nhân viên";
        for (int i = 0; i < employees.size(); i++) {
            employeeNames[i + 1] = employees.get(i).getFullName();
        }
        
        new AlertDialog.Builder(this)
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
    
    /**
     * Show status filter dialog
     */
    private void showStatusFilter() {
        String[] statuses = {"Tất cả", "Todo", "In Progress", "Completed", "Cancelled"};
        String[] statusValues = {null, "todo", "in_progress", "completed", "cancelled"};
        
        new AlertDialog.Builder(this)
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
