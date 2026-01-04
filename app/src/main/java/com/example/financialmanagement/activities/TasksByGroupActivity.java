package com.example.financialmanagement.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.TaskAdapter;
import com.example.financialmanagement.models.Task;
import com.example.financialmanagement.services.TaskService;
import java.util.ArrayList;
import java.util.List;

public class TasksByGroupActivity extends AppCompatActivity {

    private String groupId;
    private String groupName;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TaskService taskService;
    private TaskAdapter adapter;
    private List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_by_group);

        groupId = getIntent().getStringExtra("group_id");
        groupName = getIntent().getStringExtra("group_name");

        setupToolbar();
        initializeViews();

        taskService = new TaskService(this);
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
    }

    private void loadTasks() {
        swipeRefreshLayout.setRefreshing(true);
        taskService.getTasksByGroup(groupId, new TaskService.TaskCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> result) {
                swipeRefreshLayout.setRefreshing(false);
                taskList.clear();
                taskList.addAll(result);
                
                if (adapter == null) {
                    adapter = new TaskAdapter(TasksByGroupActivity.this, taskList);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(TasksByGroupActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
