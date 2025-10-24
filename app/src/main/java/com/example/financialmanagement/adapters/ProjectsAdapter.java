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
import java.util.List;

/**
 * Projects Adapter - Adapter cho danh sách dự án
 * Hiển thị danh sách dự án trong RecyclerView
 */
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {
    
    private List<Project> projects;
    private ProjectClickListener clickListener;
    
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
        holder.bind(project);
    }
    
    @Override
    public int getItemCount() {
        return projects.size();
    }
    
    /**
     * Cập nhật danh sách dự án
     */
    public void updateProjects(List<Project> newProjects) {
        this.projects = newProjects;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder cho Project
     */
    public class ProjectViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvProjectName, tvProjectCode, tvStatus, tvBudget, tvProgress;
        
        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvProjectName = itemView.findViewById(R.id.tv_project_name);
            tvProjectCode = itemView.findViewById(R.id.tv_project_code);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvBudget = itemView.findViewById(R.id.tv_budget);
            tvProgress = itemView.findViewById(R.id.tv_progress);
            
            // Set click listeners
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onProjectClick(projects.get(getAdapterPosition()));
                    }
                }
            });
            
            // Set long click listener for context menu
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (clickListener != null) {
                        // TODO: Show context menu with edit/delete options
                        clickListener.onProjectEdit(projects.get(getAdapterPosition()));
                    }
                    return true;
                }
            });
        }
        
        public void bind(Project project) {
            tvProjectName.setText(project.getName());
            tvProjectCode.setText(project.getProjectCode());
            tvStatus.setText(project.getStatusDisplayName());
            tvBudget.setText(CurrencyFormatter.format(project.getBudget() != null ? project.getBudget() : 0));
            tvProgress.setText(project.getProgress() != null ? project.getProgress() + "%" : "0%");
            
            // Set status color
            setStatusColor(project.getStatus());
        }
        
        private void setStatusColor(String status) {
            int colorRes;
            switch (status) {
                case "active":
                    colorRes = R.color.status_active;
                    break;
                case "completed":
                    colorRes = R.color.status_completed;
                    break;
                case "on_hold":
                    colorRes = R.color.status_on_hold;
                    break;
                case "cancelled":
                    colorRes = R.color.status_cancelled;
                    break;
                default:
                    colorRes = R.color.status_default;
                    break;
            }
            tvStatus.setTextColor(itemView.getContext().getResources().getColor(colorRes));
        }
    }
    
    /**
     * Project Click Listener Interface
     */
    public interface ProjectClickListener {
        void onProjectClick(Project project);
        void onProjectEdit(Project project);
        void onProjectDelete(Project project);
    }
}
