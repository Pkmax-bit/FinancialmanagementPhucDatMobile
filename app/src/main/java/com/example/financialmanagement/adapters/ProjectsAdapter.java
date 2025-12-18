package com.example.financialmanagement.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.utils.CurrencyFormatter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        return new ProjectViewHolder(view, clickListener, projects);
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
        this.projects = newProjects;
        notifyDataSetChanged();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvProjectName, tvStatus, tvProjectCode, tvCustomerName, tvAssignedTo;
        TextView tvBudget, tvProgressPercent, tvDates;
        ProgressBar progressBar;
        ImageButton btnMenu;
        List<Project> projects;
        ProjectClickListener listener;

        ProjectViewHolder(@NonNull View itemView, ProjectClickListener listener, List<Project> projects) {
            super(itemView);
            this.projects = projects;
            this.listener = listener;

            tvProjectName = itemView.findViewById(R.id.tv_project_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvProjectCode = itemView.findViewById(R.id.tv_project_code);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvAssignedTo = itemView.findViewById(R.id.tv_assigned_to);
            tvBudget = itemView.findViewById(R.id.tv_budget);
            tvProgressPercent = itemView.findViewById(R.id.tv_progress_percent);
            tvDates = itemView.findViewById(R.id.tv_dates);
            progressBar = itemView.findViewById(R.id.progress_bar);
            btnMenu = itemView.findViewById(R.id.btn_menu);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && projects != null) {
                        listener.onProjectClick(projects.get(position));
                    }
                }
            });
        }

        void bind(Project project) {
            // Name
            tvProjectName.setText(project.getName() != null ? project.getName() : "");
            
            // Project code
            tvProjectCode.setText(project.getProjectCode() != null ? project.getProjectCode() : "N/A");
            
            // Customer name
            tvCustomerName.setText(project.getCustomerName() != null ? project.getCustomerName() : "Chưa có KH");

            // Assigned To
            tvAssignedTo.setText(project.getAssignedTo() != null ? project.getAssignedTo() : "Chưa có người phụ trách");
            
            // Status
            String status = project.getStatus() != null ? project.getStatus() : "active";
            setStatusBadge(status);
            
            // Budget
            Double budget = project.getBudget();
            tvBudget.setText(budget != null ? CurrencyFormatter.format(budget) : "0 ₫");
            
            // Progress
            int progress = getProgress(project);
            tvProgressPercent.setText(progress + "%");
            progressBar.setProgress(progress);
            
            // Dates
            String dates = formatDates(project);
            tvDates.setText(dates);
            
            // Menu button
            btnMenu.setOnClickListener(v -> showPopupMenu(v, project));
        }
        
        private void setStatusBadge(String status) {
            switch (status.toLowerCase()) {
                case "active":
                    tvStatus.setText("Đang hoạt động");
                    tvStatus.setTextColor(Color.parseColor("#10B981"));
                    tvStatus.setBackgroundResource(R.drawable.bg_status_active);
                    break;
                case "completed":
                    tvStatus.setText("Hoàn thành");
                    tvStatus.setTextColor(Color.parseColor("#6B7280"));
                    tvStatus.setBackgroundResource(R.drawable.bg_status_inactive);
                    break;
                case "on_hold":
                    tvStatus.setText("Tạm dừng");
                    tvStatus.setTextColor(Color.parseColor("#F59E0B"));
                    tvStatus.setBackgroundResource(R.drawable.bg_status_prospect);
                    break;
                case "cancelled":
                    tvStatus.setText("Đã hủy");
                    tvStatus.setTextColor(Color.parseColor("#EF4444"));
                    tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                    break;
                case "planning":
                    tvStatus.setText("Lập kế hoạch");
                    tvStatus.setTextColor(Color.parseColor("#3B82F6"));
                    tvStatus.setBackgroundResource(R.drawable.bg_status_planning);
                    break;
                default:
                    tvStatus.setText(status);
                    tvStatus.setTextColor(Color.parseColor("#6B7280"));
                    break;
            }
        }
        
        private int getProgress(Project project) {
            if (project.getProgress() != null) {
                return project.getProgress();
            }
            // Fallback based on status
            String status = project.getStatus() != null ? project.getStatus() : "";
            switch (status.toLowerCase()) {
                case "completed": return 100;
                case "active": return 50;
                case "on_hold": return 30;
                case "planning": return 10;
                default: return 0;
            }
        }
        
        // Cache SimpleDateFormat to avoid creating new instance each time
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        
        private String formatDates(Project project) {
            StringBuilder sb = new StringBuilder();
            if (project.getStartDate() != null) {
                synchronized (dateFormat) {
                    sb.append(dateFormat.format(project.getStartDate()));
                }
            }
            if (project.getEndDate() != null) {
                synchronized (dateFormat) {
                    sb.append(" - ").append(dateFormat.format(project.getEndDate()));
                }
            }
            return sb.length() > 0 ? sb.toString() : "Chưa có thời gian";
        }
        
        private void showPopupMenu(View anchor, Project project) {
            PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
            popup.inflate(R.menu.menu_project_item);
            
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_view) {
                    if (listener != null) listener.onProjectClick(project);
                    return true;
                } else if (id == R.id.action_edit) {
                    if (listener != null) listener.onProjectEdit(project);
                    return true;
                } else if (id == R.id.action_delete) {
                    if (listener != null) listener.onProjectDelete(project);
                    return true;
                }
                return false;
            });
            
            popup.show();
        }
    }
}