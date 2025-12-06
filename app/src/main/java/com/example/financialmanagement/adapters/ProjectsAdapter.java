package com.example.financialmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Project;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {

    private static List<Project> projects;
    private ProjectClickListener clickListener;

    public interface ProjectClickListener {
        void onProjectClick(Project project);
        void onProjectEdit(Project project);
        void onProjectDelete(Project project);
    }

    public ProjectsAdapter(List<Project> projects, ProjectClickListener clickListener) {
        ProjectsAdapter.projects = projects;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.bind(project);
    }

    @Override
    public int getItemCount() {
        return projects != null ? projects.size() : 0;
    }

    public void updateProjects(List<Project> newProjects) {
        ProjectsAdapter.projects = newProjects;
        notifyDataSetChanged();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.chip.Chip chipProjectCode;
        com.google.android.material.chip.Chip chipStatus;
        TextView tvProjectName;
        TextView tvCustomerName;
        TextView tvDateRange;
        TextView tvProgressLabel;
        com.google.android.material.progressindicator.LinearProgressIndicator progressIndicator;
        TextView tvBudget;
        TextView tvSpent;

        ProjectViewHolder(@NonNull View itemView, ProjectClickListener listener) {
            super(itemView);
            chipProjectCode = itemView.findViewById(R.id.chip_project_code);
            chipStatus = itemView.findViewById(R.id.chip_status);
            tvProjectName = itemView.findViewById(R.id.tv_project_name);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvDateRange = itemView.findViewById(R.id.tv_date_range);
            tvProgressLabel = itemView.findViewById(R.id.tv_progress_label);
            progressIndicator = itemView.findViewById(R.id.progress_indicator);
            tvBudget = itemView.findViewById(R.id.tv_budget);
            tvSpent = itemView.findViewById(R.id.tv_spent);

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onProjectClick(projects.get(position));
                    }
                }
            });
        }

        void bind(Project project) {
            // Project code
            chipProjectCode.setText(project.getProjectCode() != null ? project.getProjectCode() : "N/A");
            
            // Status
            String status = project.getStatus() != null ? project.getStatus() : "active";
            chipStatus.setText(getStatusText(status));
            chipStatus.setChipBackgroundColorResource(getStatusColor(status));
            
            // Project name
            tvProjectName.setText(project.getName());
            
            // Customer name - use customer_name field instead of nested object
            if (project.getCustomerName() != null && !project.getCustomerName().isEmpty()) {
                tvCustomerName.setText(project.getCustomerName());
            } else {
                tvCustomerName.setText("Chưa có khách hàng");
            }
            
            // Date range - format Date objects to String
            String dateRange = "";
            if (project.getStartDate() != null) {
                dateRange = formatDate(project.getStartDate());
                if (project.getEndDate() != null) {
                    dateRange += " - " + formatDate(project.getEndDate());
                }
            }
            tvDateRange.setText(dateRange.isEmpty() ? "Chưa có thời gian" : dateRange);
            
            // Progress (can be calculated or from project data)
            int progress = calculateProgress(project);
            tvProgressLabel.setText("Tiến độ: " + progress + "%");
            progressIndicator.setProgress(progress);
            
            // Budget
            tvBudget.setText(formatCurrency(project.getBudget()));
            
            // Spent (if available, otherwise show 0)
            tvSpent.setText(formatCurrency(0.0)); // Can be enhanced with actual data
        }

        private String formatDate(java.util.Date date) {
            if (date == null) return "";
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            return sdf.format(date);
        }

        private String getStatusText(String status) {
            switch (status.toLowerCase()) {
                case "active":
                    return "Đang hoạt động";
                case "completed":
                    return "Hoàn thành";
                case "on_hold":
                    return "Tạm dừng";
                case "cancelled":
                    return "Đã hủy";
                default:
                    return status;
            }
        }

        private int getStatusColor(String status) {
            switch (status.toLowerCase()) {
                case "active":
                    return R.color.md_success;
                case "completed":
                    return R.color.md_primary;
                case "on_hold":
                    return R.color.md_warning;
                case "cancelled":
                    return R.color.md_error;
                default:
                    return R.color.md_outline;
            }
        }

        private int calculateProgress(Project project) {
            // Simple progress calculation - can be enhanced
            if (project.getStatus() != null) {
                switch (project.getStatus().toLowerCase()) {
                    case "completed":
                        return 100;
                    case "active":
                        return 50;
                    case "on_hold":
                        return 25;
                    default:
                        return 0;
                }
            }
            return 0;
        }

        private String formatCurrency(Double amount) {
            if (amount == null) amount = 0.0;
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return formatter.format(amount);
        }
    }
}