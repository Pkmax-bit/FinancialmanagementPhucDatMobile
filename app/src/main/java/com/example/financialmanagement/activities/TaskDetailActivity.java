package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Task;
import com.example.financialmanagement.services.TaskService;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, tvStatus, tvPriority, tvDueDate, tvAssignee;
    private TaskService taskService;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initializeViews();
        
        taskId = getIntent().getStringExtra("task_id");
        if (taskId != null) {
            taskService = new TaskService(this);
            loadTaskDetails(taskId);
        } else {
            Toast.makeText(this, "Task ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        tvTitle = findViewById(R.id.tv_detail_title);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvPriority = findViewById(R.id.tv_detail_priority);
        tvDueDate = findViewById(R.id.tv_detail_due_date);
        tvAssignee = findViewById(R.id.tv_detail_assignee);
    }

    private void loadTaskDetails(String id) {
        taskService.getTask(id, new TaskService.TaskCallback<Task>() {
            @Override
            public void onSuccess(Task task) {
                tvTitle.setText(task.getTitle());
                tvDescription.setText(task.getDescription());
                tvStatus.setText(task.getStatusDisplayName());
                tvPriority.setText(task.getPriorityDisplayName());
                tvDueDate.setText(task.getDueDate());
                tvAssignee.setText(task.getAssignee() != null ? task.getAssignee().getFullName() : "Unassigned");
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskDetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
