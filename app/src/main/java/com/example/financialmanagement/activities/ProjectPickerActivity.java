package com.example.financialmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.ProjectAdapter;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.models.ProjectListResponse;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.SalesApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectPickerActivity extends AppCompatActivity implements ProjectAdapter.ProjectClickListener {

    public static final String EXTRA_CUSTOMER_ID = "extra_customer_id";
    public static final String EXTRA_PROJECT_ID = "extra_project_id";
    public static final String EXTRA_PROJECT_NAME = "extra_project_name";
    public static final String EXTRA_PROJECT_CODE = "extra_project_code";

    private RecyclerView rvProjects;
    private ProjectAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private EditText etSearch;
    private List<Project> allProjects = new ArrayList<>();
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_picker);

        customerId = getIntent().getStringExtra(EXTRA_CUSTOMER_ID);
        if (customerId == null || customerId.isEmpty()) {
            Toast.makeText(this, "Không có thông tin khách hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvProjects = findViewById(R.id.rv_projects);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tv_empty);
        etSearch = findViewById(R.id.et_search);

        rvProjects.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectAdapter(new ArrayList<>(), this);
        rvProjects.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProjects(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadProjects();
    }

    private void loadProjects() {
        progressBar.setVisibility(View.VISIBLE);
        SalesApi salesApi = ApiClient.getRetrofit(this).create(SalesApi.class);
        Call<ProjectListResponse> call = salesApi.getProjectsByCustomer(customerId);
        
        call.enqueue(new Callback<ProjectListResponse>() {
            @Override
            public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allProjects = response.body().getProjects();
                    if (allProjects == null) allProjects = new ArrayList<>();
                    updateList(allProjects);
                } else {
                    // It's possible the customer has no projects, or API error
                    // If 404/500, assume empty or error
                    updateList(new ArrayList<>());
                    if (response.code() != 404) {
                        Toast.makeText(ProjectPickerActivity.this, "Không thể tải danh sách dự án", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectListResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProjectPickerActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProjects(String query) {
        List<Project> filteredList = new ArrayList<>();
        for (Project project : allProjects) {
            String name = project.getName() != null ? project.getName().toLowerCase() : "";
            String code = project.getProjectCode() != null ? project.getProjectCode().toLowerCase() : "";
            
            if (name.contains(query.toLowerCase()) || code.contains(query.toLowerCase())) {
                filteredList.add(project);
            }
        }
        updateList(filteredList);
    }

    private void updateList(List<Project> projects) {
        adapter.updateProjects(projects);
        if (projects.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvProjects.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvProjects.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProjectClick(Project project) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_PROJECT_ID, project.getId());
        resultIntent.putExtra(EXTRA_PROJECT_NAME, project.getName());
        resultIntent.putExtra(EXTRA_PROJECT_CODE, project.getProjectCode());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
