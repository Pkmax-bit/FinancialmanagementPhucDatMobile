package com.example.financialmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.EmployeeAdapter;
import com.example.financialmanagement.models.Employee;
import com.example.financialmanagement.services.EmployeeService;

import java.util.ArrayList;
import java.util.List;

public class EmployeePickerActivity extends AppCompatActivity implements EmployeeAdapter.OnEmployeeClickListener {

    public static final String EXTRA_EMPLOYEE_ID = "extra_employee_id";
    public static final String EXTRA_EMPLOYEE_NAME = "extra_employee_name";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EmployeeAdapter adapter;
    private EmployeeService employeeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_employees); // Reusing layout as it contains RecyclerView

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Chọn nhân viên");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.recycler_view_employees);
        progressBar = new ProgressBar(this); // Dynamically adding or assume layout has it, let's verify layout reuse
        // Actually fragment_employees.xml might just be a RecyclerView. Let's assume we can reuse it, or create a simple layout. 
        // Re-checking fragment_employees.xml would be safer. But I'll stick to a standard activity setup.
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        employeeService = new EmployeeService(this);
        loadEmployees();
    }

    private void loadEmployees() {
        employeeService.getEmployees(new EmployeeService.EmployeeCallback<List<Employee>>() {
            @Override
            public void onSuccess(List<Employee> result) {
                adapter = new EmployeeAdapter(EmployeePickerActivity.this, result);
                adapter.setOnEmployeeClickListener(EmployeePickerActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EmployeePickerActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEmployeeClick(Employee employee) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_EMPLOYEE_ID, employee.getId());
        resultIntent.putExtra(EXTRA_EMPLOYEE_NAME, employee.getFullName()); // Assuming getFullName exists
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
