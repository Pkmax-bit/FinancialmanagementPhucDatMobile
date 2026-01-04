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

import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.services.ProjectService;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProjectOverviewFragment extends Fragment {
    
    private static final String ARG_PROJECT_ID = "project_id";
    
    private String projectId;
    private Project project;
    private ProjectService projectService;
    
    private TextView tvProjectCode, tvCustomer, tvManager, tvBilling;
    private TextView tvDates, tvProgressLabel;
    private ProgressBar pbProjectProgress;
    private TextView tvBudget, tvActualCost, tvRemaining;
    
    public static ProjectOverviewFragment newInstance(String projectId) {
        ProjectOverviewFragment fragment = new ProjectOverviewFragment();
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
        View view = inflater.inflate(R.layout.fragment_project_overview, container, false);
        
        initializeViews(view);
        loadProjectDetails();
        
        return view;
    }
    
    private void initializeViews(View view) {
        tvProjectCode = view.findViewById(R.id.tv_project_code);
        tvCustomer = view.findViewById(R.id.tv_customer);
        tvManager = view.findViewById(R.id.tv_manager);
        tvBilling = view.findViewById(R.id.tv_billing);
        tvDates = view.findViewById(R.id.tv_dates);
        tvProgressLabel = view.findViewById(R.id.tv_progress_label);
        pbProjectProgress = view.findViewById(R.id.pb_project_progress);
        tvBudget = view.findViewById(R.id.tv_budget);
        tvActualCost = view.findViewById(R.id.tv_actual_cost);
        tvRemaining = view.findViewById(R.id.tv_remaining);
    }
    
    private void loadProjectDetails() {
        if (projectId == null) return;
        
        projectService.getProjectById(projectId, new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(java.util.List<Project> projects) {}
            
            @Override
            public void onSuccess(Project project) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> updateUI(project));
                }
            }
            
            @Override
            public void onSuccess() {}
            
            @Override
            public void onError(String error) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    
    private void updateUI(Project project) {
        this.project = project;
        
        tvProjectCode.setText("Mã dự án: " + (project.getProjectCode() != null ? project.getProjectCode() : "-"));
        tvCustomer.setText("Khách hàng: " + (project.getCustomerName() != null ? project.getCustomerName() : "-"));
        tvManager.setText("Quản lý: " + (project.getAssignedTo() != null ? project.getAssignedTo() : "-"));
        tvBilling.setText("Loại hình: " + project.getBillingTypeDisplayName());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String start = project.getStartDate() != null ? sdf.format(project.getStartDate()) : "-";
        String end = project.getEndDate() != null ? sdf.format(project.getEndDate()) : "-";
        tvDates.setText("Thời gian: " + start + " đến " + end);
        
        int progress = project.getProgress() != null ? project.getProgress() : 0;
        tvProgressLabel.setText("Tiến độ: " + progress + "%");
        pbProjectProgress.setProgress(progress);
        
        tvBudget.setText(String.format("Tổng ngân sách: %,.0f VNĐ", project.getBudget() != null ? project.getBudget() : 0));
        tvActualCost.setText(String.format("Chi phí thực tế: %,.0f VNĐ", project.getActualCost() != null ? project.getActualCost() : 0));
        tvRemaining.setText(String.format("Còn lại: %,.0f VNĐ", project.getRemainingBudget() != null ? project.getRemainingBudget() : 0));
        
        if (project.isOverBudget()) {
            tvRemaining.setTextColor(requireContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvRemaining.setTextColor(requireContext().getResources().getColor(android.R.color.holo_green_dark));
        }
    }
}
