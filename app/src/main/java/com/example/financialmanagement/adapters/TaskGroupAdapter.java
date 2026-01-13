package com.example.financialmanagement.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.TaskGroup;
import com.example.financialmanagement.views.AvatarStackView;
import java.util.List;

public class TaskGroupAdapter extends RecyclerView.Adapter<TaskGroupAdapter.ViewHolder> {

    private Context context;
    private List<TaskGroup> taskGroups;
    private OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(TaskGroup group);
    }

    public TaskGroupAdapter(Context context, List<TaskGroup> taskGroups, OnGroupClickListener listener) {
        this.context = context;
        this.taskGroups = taskGroups;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskGroup group = taskGroups.get(position);
        
        holder.textGroupName.setText(group.getName());
        
        // Display task count with label
        int taskCount = group.getTaskCount();
        int completedCount = group.getCompletedCount();
        if (taskCount > 0) {
            holder.textTaskCount.setText(taskCount + " nhiệm vụ (" + completedCount + " hoàn thành)");
            holder.textTaskCount.setVisibility(View.VISIBLE);
        } else {
            holder.textTaskCount.setText("0 nhiệm vụ");
            holder.textTaskCount.setVisibility(View.VISIBLE);
        }
        
        holder.textGroupIcon.setText(group.getIcon());
        
        try {
            if (group.getColor() != null) {
                int color = Color.parseColor(group.getColor());
                holder.iconContainer.getBackground().setTint(adjustAlpha(color, 0.15f));
                holder.textGroupIcon.setTextColor(color);
            }
        } catch (Exception e) {
            // Fallback to default colors
        }
        
        holder.avatarStack.setMembers(group.getMembers());
        
        // Simulating last activity since it might not be in the model yet
        holder.textLastActivity.setText("Cập nhật: " + (group.getUpdatedAt() != null ? group.getUpdatedAt() : "vừa xong"));
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGroupClick(group);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskGroups.size();
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textGroupName, textTaskCount, textGroupIcon, textLastActivity;
        AvatarStackView avatarStack;
        View iconContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textGroupName = itemView.findViewById(R.id.text_group_name);
            textTaskCount = itemView.findViewById(R.id.text_task_count);
            textGroupIcon = itemView.findViewById(R.id.text_group_icon);
            textLastActivity = itemView.findViewById(R.id.text_last_activity);
            avatarStack = itemView.findViewById(R.id.avatar_stack);
            iconContainer = itemView.findViewById(R.id.icon_container);
        }
    }
}
