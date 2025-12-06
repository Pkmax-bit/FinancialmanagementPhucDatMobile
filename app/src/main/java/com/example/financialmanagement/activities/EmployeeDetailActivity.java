package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Employee;
import com.example.financialmanagement.services.EmployeeService;

public class EmployeeDetailActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvPosition, tvDepartment;
    private EmployeeService employeeService;
    private String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);

        initializeViews();
        
        employeeId = getIntent().getStringExtra("employee_id");
        if (employeeId != null) {
            employeeService = new EmployeeService(this);
            loadEmployeeDetails(employeeId);
        } else {
            Toast.makeText(this, "Employee ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        tvName = findViewById(R.id.tv_detail_name);
        tvEmail = findViewById(R.id.tv_detail_email);
        tvPhone = findViewById(R.id.tv_detail_phone);
        tvPosition = findViewById(R.id.tv_detail_position);
        tvDepartment = findViewById(R.id.tv_detail_department);
    }

    private void loadEmployeeDetails(String id) {
        employeeService.getEmployee(id, new EmployeeService.EmployeeCallback<Employee>() {
            @Override
            public void onSuccess(Employee employee) {
                tvName.setText(employee.getFullName());
                tvEmail.setText(employee.getEmail());
                tvPhone.setText(employee.getPhone());
                tvPosition.setText(employee.getPosition() != null ? employee.getPosition().getTitle() : "N/A");
                tvDepartment.setText(employee.getDepartment() != null ? employee.getDepartment().getName() : "N/A");
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EmployeeDetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
