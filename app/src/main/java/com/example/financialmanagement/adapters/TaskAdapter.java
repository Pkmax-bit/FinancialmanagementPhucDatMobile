package com.example.financialmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.activities.TaskDetailActivity;
import com.example.financialmanagement.models.Task;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> tasks;

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        
        // Clean title: remove [FILE_URLS: ...] tags
        String rawTitle = task.getTitle();
        String cleanTitle = rawTitle != null ? rawTitle.replaceAll("\\[FILE_URLS:.*?\\]", "").trim() : "";
        
        holder.tvTitle.setText(cleanTitle);
        holder.tvStatus.setText(task.getStatusDisplayName());
        holder.tvPriority.setText(task.getPriorityDisplayName());
        
        // Set colors based on priority/status if needed
        
        // Assignee Avatar
        com.example.financialmanagement.models.Employee assignee = task.getAssignee();
        if (assignee != null && assignee.getFirstName() != null) {
            holder.tvAssigneeAvatar.setVisibility(View.VISIBLE);
            
            // Get name for initials
            String name = assignee.getFirstName();
            if (assignee.getLastName() != null) {
                name = assignee.getLastName() + " " + assignee.getFirstName();
            }
            
            // Set initials
            String initials = "?";
            if (name != null && !name.isEmpty()) {
                String[] parts = name.trim().split("\\s+");
                if (parts.length > 0) {
                    String lastWord = parts[parts.length - 1];
                    if (!lastWord.isEmpty()) {
                        initials = lastWord.substring(0, 1).toUpperCase();
                    }
                }
            }
            holder.tvAssigneeAvatar.setText(initials);
            
            // Set background color
            holder.tvAssigneeAvatar.getBackground().setTint(getAvatarColor(name));
            
        } else {
            // Show question mark or hide? Let's show question mark for unassigned
             holder.tvAssigneeAvatar.setText("?");
             holder.tvAssigneeAvatar.getBackground().setTint(android.graphics.Color.LTGRAY);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getId());
            context.startActivity(intent);
        });
    }

    private int getAvatarColor(String name) {
        if (name == null || name.isEmpty()) return android.graphics.Color.GRAY;
        int hash = name.hashCode();
        int[] colors = {
            android.graphics.Color.parseColor("#0075FF"), android.graphics.Color.parseColor("#34C759"), 
            android.graphics.Color.parseColor("#FF9500"), android.graphics.Color.parseColor("#FF3B30"),
            android.graphics.Color.parseColor("#AF52DE"), android.graphics.Color.parseColor("#5856D6")
        };
        return colors[Math.abs(hash) % colors.length];
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStatus, tvPriority, tvAssigneeAvatar;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_task_title);
            tvStatus = itemView.findViewById(R.id.tv_task_status);
            tvPriority = itemView.findViewById(R.id.tv_task_priority);
            tvAssigneeAvatar = itemView.findViewById(R.id.tv_assignee_avatar);
        }
    }
}
