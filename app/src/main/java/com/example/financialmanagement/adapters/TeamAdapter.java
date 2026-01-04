package com.example.financialmanagement.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.models.TeamMember;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {

    private Context context;
    private List<TeamMember> teamMembers;

    public TeamAdapter(Context context, List<TeamMember> teamMembers) {
        this.context = context;
        this.teamMembers = teamMembers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeamMember member = teamMembers.get(position);
        
        holder.tvMemberName.setText(member.getName() != null ? member.getName() : "Không tên");
        holder.tvMemberRole.setText(member.getRole() != null ? member.getRole() : "Chưa xác định");
        
        String responsibility = member.getResponsibilityType();
        if (responsibility != null) {
            holder.tvMemberResponsibility.setText(getResponsibilityLabel(responsibility));
            holder.tvMemberResponsibility.setVisibility(View.VISIBLE);
        } else {
            holder.tvMemberResponsibility.setVisibility(View.GONE);
        }
        
        holder.tvMemberEmail.setText(member.getEmail() != null ? member.getEmail() : "");
        
        // Setup Avatar
        String name = member.getName();
        if (name != null && !name.isEmpty()) {
            String initials = getInitials(name);
            holder.tvAvatar.setText(initials);
            int color = getAvatarColor(name);
            GradientDrawable drawable = (GradientDrawable) holder.tvAvatar.getBackground();
            drawable.setColor(color);
        } else {
            holder.tvAvatar.setText("?");
        }
    }

    @Override
    public int getItemCount() {
        return teamMembers.size();
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        } else if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return "?";
    }

    private int getAvatarColor(String name) {
        int hash = name.hashCode();
        int[] colors = {
            0xFF1ABC9C, 0xFF2ECC71, 0xFF3498DB, 0xFF9B59B6, 0xFF34495E,
            0xFF16A085, 0xFF27AE60, 0xFF2980B9, 0xFF8E44AD, 0xFF2C3E50,
            0xFFF1C40F, 0xFFE67E22, 0xFFE74C3C, 0xFF95A5A6, 0xFFF39C12,
            0xFFD35400, 0xFFC0392B, 0xFF7F8C8D
        };
        return colors[Math.abs(hash) % colors.length];
    }
    
    private String getResponsibilityLabel(String type) {
        switch (type.toLowerCase()) {
            case "responsible": return "Thực hiện";
            case "accountable": return "Trách nhiệm";
            case "consulted": return "Tham vấn";
            case "informed": return "Theo dõi";
            default: return type;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvMemberName, tvMemberRole, tvMemberResponsibility, tvMemberEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvMemberName = itemView.findViewById(R.id.tv_member_name);
            tvMemberRole = itemView.findViewById(R.id.tv_member_role);
            tvMemberResponsibility = itemView.findViewById(R.id.tv_member_responsibility);
            tvMemberEmail = itemView.findViewById(R.id.tv_member_email);
        }
    }
}
