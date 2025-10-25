package com.example.financialmanagement.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.services.ProjectService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectFormActivity extends AppCompatActivity {

    public static final String EXTRA_PROJECT = "project";
    public static final String EXTRA_CUSTOMER = "customer";
    public static final String EXTRA_MODE = "mode";
    public static final String MODE_CREATE = "create";
    public static final String MODE_EDIT = "edit";

    private TextInputEditText etProjectCode;
    private TextInputEditText etProjectName;
    private Spinner spinnerCustomer;
    private Spinner spinnerStatus;
    private Spinner spinnerPriority;
    private TextInputEditText etProjectBudget;
    private TextInputEditText etStartDate;
    private TextInputEditText etEndDate;
    private TextInputEditText etAssignedTo;
    private TextInputEditText etProjectDescription;
    private TextInputEditText etProjectNotes;
    
    private MaterialButton btnSave;
    private MaterialButton btnCancel;
    private View progressBar;
    
    private Project project;
    private Customer customer;
    private String mode;
    private ProjectService projectService;
    private List<Customer> customers;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_form);
        
        initializeViews();
        setupToolbar();
        setupForm();
        setupListeners();
    }

    private void initializeViews() {
        etProjectCode = findViewById(R.id.et_project_code);
        etProjectName = findViewById(R.id.et_project_name);
        spinnerCustomer = findViewById(R.id.spinner_customer);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerPriority = findViewById(R.id.spinner_priority);
        etProjectBudget = findViewById(R.id.et_project_budget);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        etAssignedTo = findViewById(R.id.et_assigned_to);
        etProjectDescription = findViewById(R.id.et_project_description);
        etProjectNotes = findViewById(R.id.et_project_notes);
        
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        progressBar = findViewById(R.id.progress_bar);
        
        projectService = new ProjectService(this);
        customers = new ArrayList<>();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tạo dự án");
        }
    }

    private void setupForm() {
        // Get data from intent
        project = (Project) getIntent().getSerializableExtra(EXTRA_PROJECT);
        customer = (Customer) getIntent().getSerializableExtra(EXTRA_CUSTOMER);
        mode = getIntent().getStringExtra(EXTRA_MODE);
        
        if (mode == null) {
            mode = MODE_CREATE;
        }
        
        // Update toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mode.equals(MODE_EDIT) ? "Sửa dự án" : "Tạo dự án");
        }
        
        // Setup spinners
        setupStatusSpinner();
        setupPrioritySpinner();
        setupCustomerSpinner();
        
        // Populate form if editing
        if (project != null) {
            populateForm();
        } else if (mode.equals(MODE_CREATE)) {
            // Generate project code for new project
            generateProjectCode();
        }
    }

    private void setupStatusSpinner() {
        String[] statuses = {"Đang thực hiện", "Hoàn thành", "Tạm dừng", "Đã hủy"};
        String[] statusValues = {"active", "completed", "on_hold", "cancelled"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        
        // Set default value
        spinnerStatus.setSelection(0); // "Đang thực hiện"
    }

    private void setupPrioritySpinner() {
        String[] priorities = {"Cao", "Trung bình", "Thấp"};
        String[] priorityValues = {"high", "medium", "low"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
        
        // Set default value
        spinnerPriority.setSelection(1); // "Trung bình"
    }

    private void setupCustomerSpinner() {
        // For now, create sample customers
        // TODO: Load customers from API
        customers.add(new Customer("CUST001", "Công ty ABC", "company"));
        customers.add(new Customer("CUST002", "Nguyễn Văn A", "individual"));
        customers.add(new Customer("CUST003", "Cơ quan DEF", "government"));
        
        List<String> customerNames = new ArrayList<>();
        for (Customer customer : customers) {
            customerNames.add(customer.getName() + " (" + customer.getCustomerCode() + ")");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, customerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCustomer.setAdapter(adapter);
        
        // Set selected customer if provided
        if (customer != null) {
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getId().equals(customer.getId())) {
                    spinnerCustomer.setSelection(i);
                    break;
                }
            }
        }
    }

    private void populateForm() {
        if (project == null) return;
        
        etProjectCode.setText(project.getProjectCode());
        etProjectName.setText(project.getName());
        etProjectDescription.setText(project.getDescription());
        etProjectBudget.setText(project.getBudget() != null ? project.getBudget().toString() : "");
        etAssignedTo.setText(project.getAssignedTo());
        etProjectNotes.setText(project.getNotes());
        
        // Set dates
        if (project.getStartDate() != null) {
            etStartDate.setText(dateFormat.format(project.getStartDate()));
        }
        if (project.getEndDate() != null) {
            etEndDate.setText(dateFormat.format(project.getEndDate()));
        }
        
        // Set spinner selections
        setSpinnerSelection(spinnerStatus, project.getStatus());
        setSpinnerSelection(spinnerPriority, project.getPriority());
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toLowerCase().contains(value.toLowerCase())) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void generateProjectCode() {
        // Generate a simple project code (in real app, this would be from server)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String projectCode = "PRJ" + timestamp.substring(timestamp.length() - 6);
        etProjectCode.setText(projectCode);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateForm()) {
                saveProject();
            }
        });
        
        btnCancel.setOnClickListener(v -> {
            finish();
        });
        
        // Date picker listeners
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                editText.setText(dateFormat.format(selectedCalendar.getTime()));
            }, year, month, day);
        
        datePickerDialog.show();
    }

    private boolean validateForm() {
        boolean isValid = true;
        
        // Validate required fields
        if (etProjectCode.getText().toString().trim().isEmpty()) {
            etProjectCode.setError("Mã dự án là bắt buộc");
            isValid = false;
        }
        
        if (etProjectName.getText().toString().trim().isEmpty()) {
            etProjectName.setError("Tên dự án là bắt buộc");
            isValid = false;
        }
        
        if (spinnerCustomer.getSelectedItemPosition() == -1) {
            Toast.makeText(this, "Vui lòng chọn khách hàng", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        
        return isValid;
    }

    private void saveProject() {
        showProgress(true);
        
        Project projectData = createProjectFromForm();
        
        if (mode.equals(MODE_EDIT)) {
            updateProject(projectData);
        } else {
            createProject(projectData);
        }
    }

    private Project createProjectFromForm() {
        Project projectData = new Project();
        
        if (project != null) {
            projectData.setId(project.getId());
        }
        
        projectData.setProjectCode(etProjectCode.getText().toString().trim());
        projectData.setName(etProjectName.getText().toString().trim());
        projectData.setDescription(etProjectDescription.getText().toString().trim());
        projectData.setNotes(etProjectNotes.getText().toString().trim());
        projectData.setAssignedTo(etAssignedTo.getText().toString().trim());
        
        // Set customer
        int customerIndex = spinnerCustomer.getSelectedItemPosition();
        if (customerIndex >= 0 && customerIndex < customers.size()) {
            Customer selectedCustomer = customers.get(customerIndex);
            projectData.setCustomerId(selectedCustomer.getId());
            projectData.setCustomerName(selectedCustomer.getName());
        }
        
        // Set status
        String[] statusValues = {"active", "completed", "on_hold", "cancelled"};
        projectData.setStatus(statusValues[spinnerStatus.getSelectedItemPosition()]);
        
        // Set priority
        String[] priorityValues = {"high", "medium", "low"};
        projectData.setPriority(priorityValues[spinnerPriority.getSelectedItemPosition()]);
        
        // Parse numeric fields
        try {
            String budgetText = etProjectBudget.getText().toString().trim();
            if (!budgetText.isEmpty()) {
                projectData.setBudget(Double.parseDouble(budgetText));
            }
        } catch (NumberFormatException e) {
            // Handle invalid number
        }
        
        // Parse dates
        try {
            String startDateText = etStartDate.getText().toString().trim();
            if (!startDateText.isEmpty()) {
                projectData.setStartDate(dateFormat.parse(startDateText));
            }
            
            String endDateText = etEndDate.getText().toString().trim();
            if (!endDateText.isEmpty()) {
                projectData.setEndDate(dateFormat.parse(endDateText));
            }
        } catch (Exception e) {
            // Handle date parsing error
        }
        
        return projectData;
    }

    private void createProject(Project projectData) {
        projectService.createProject(projectData, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                // Not used
            }
            
            @Override
            public void onSuccess(Project project) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ProjectFormActivity.this, "Tạo dự án thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ProjectFormActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateProject(Project projectData) {
        projectService.updateProject(project.getId(), projectData, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                // Not used
            }
            
            @Override
            public void onSuccess(Project project) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ProjectFormActivity.this, "Cập nhật dự án thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onSuccess() {
                // Not used
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ProjectFormActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
        btnCancel.setEnabled(!show);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
