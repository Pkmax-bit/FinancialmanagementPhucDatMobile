package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ProjectsAdapter - Adapter cho danh sách dự án
 */
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {

    private List<Project> projects;
    private ProjectClickListener clickListener;

    public interface ProjectClickListener {
        void onProjectClick(Project project);
        void onProjectEdit(Project project);
        void onProjectDelete(Project project);
    }

    public ProjectsAdapter(List<Project> projects, ProjectClickListener clickListener) {
        this.projects = projects;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.bind(project, clickListener);
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void updateProjects(List<Project> newProjects) {
        this.projects = newProjects;
        notifyDataSetChanged();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProjectName;
        private TextView tvProjectCode;
        private TextView tvCustomerName;
        private TextView tvProjectStatus;
        private TextView tvProjectPriority;
        private TextView tvProjectBudget;
        private TextView tvProjectDates;
        private TextView tvAssignedTo;
        
        // Action buttons
        private com.google.android.material.button.MaterialButton btnEditProject;
        private com.google.android.material.button.MaterialButton btnDeleteProject;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.tv_project_name);
            tvProjectCode = itemView.findViewById(R.id.tv_project_code);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvProjectStatus = itemView.findViewById(R.id.tv_project_status);
            tvProjectPriority = itemView.findViewById(R.id.tv_project_priority);
            tvProjectBudget = itemView.findViewById(R.id.tv_project_budget);
            tvProjectDates = itemView.findViewById(R.id.tv_project_dates);
            tvAssignedTo = itemView.findViewById(R.id.tv_assigned_to);
            
            // Initialize action buttons
            btnEditProject = itemView.findViewById(R.id.btn_edit_project);
            btnDeleteProject = itemView.findViewById(R.id.btn_delete_project);
        }

        public void bind(Project project, ProjectClickListener clickListener) {
            // Basic information
            tvProjectName.setText(project.getName());
            tvProjectCode.setText(project.getProjectCode());
            tvCustomerName.setText(project.getCustomerName() != null ? project.getCustomerName() : "Chưa có khách hàng");
            
            // Status
            tvProjectStatus.setText(project.getStatusDisplayName());
            setStatusColor(project.getStatus());
            
            // Priority
            tvProjectPriority.setText(project.getPriorityDisplayName());
            setPriorityColor(project.getPriority());
            
            // Budget
            if (project.getBudget() != null) {
                tvProjectBudget.setText(CurrencyFormatter.format(project.getBudget()));
            } else {
                tvProjectBudget.setText("Chưa thiết lập");
            }
            
            // Dates
            String dateRange = formatDateRange(project.getStartDate(), project.getEndDate());
            tvProjectDates.setText(dateRange);
            
            // Assigned to
            if (project.getAssignedTo() != null && !project.getAssignedTo().isEmpty()) {
                tvAssignedTo.setText("Phụ trách: " + project.getAssignedTo());
                tvAssignedTo.setVisibility(View.VISIBLE);
            } else {
                tvAssignedTo.setVisibility(View.GONE);
            }
            
            // Click listeners
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onProjectClick(project);
                }
            });
            
            // Action button listeners
            btnEditProject.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onProjectEdit(project);
                }
            });
            
            btnDeleteProject.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onProjectDelete(project);
                }
            });
        }

        private void setStatusColor(String status) {
            int colorRes;
            switch (status) {
                case "active":
                    colorRes = R.color.color_success;
                    break;
                case "completed":
                    colorRes = R.color.color_info;
                    break;
                case "on_hold":
                    colorRes = R.color.color_warning;
                    break;
                case "cancelled":
                    colorRes = R.color.color_danger;
                    break;
                default:
                    colorRes = R.color.text_secondary;
                    break;
            }
            tvProjectStatus.setTextColor(itemView.getContext().getColor(colorRes));
        }

        private void setPriorityColor(String priority) {
            int colorRes;
            switch (priority) {
                case "high":
                    colorRes = R.color.color_danger;
                    break;
                case "medium":
                    colorRes = R.color.color_warning;
                    break;
                case "low":
                    colorRes = R.color.color_success;
                    break;
                default:
                    colorRes = R.color.text_secondary;
                    break;
            }
            tvProjectPriority.setTextColor(itemView.getContext().getColor(colorRes));
        }

        private String formatDateRange(Date startDate, Date endDate) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            StringBuilder dateRange = new StringBuilder();
            
            if (startDate != null) {
                dateRange.append("Từ: ").append(dateFormat.format(startDate));
            }
            
            if (endDate != null) {
                if (dateRange.length() > 0) {
                    dateRange.append(" - ");
                }
                dateRange.append("Đến: ").append(dateFormat.format(endDate));
            }
            
            if (dateRange.length() == 0) {
                return "Chưa thiết lập thời gian";
            }
            
            return dateRange.toString();
        }
    }
}