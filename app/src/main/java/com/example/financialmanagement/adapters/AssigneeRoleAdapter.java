package com.example.financialmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.models.AssigneeWithRole;

import java.util.ArrayList;
import java.util.List;

public class AssigneeRoleAdapter extends RecyclerView.Adapter<AssigneeRoleAdapter.ViewHolder> {

    private Context context;
    private List<AssigneeWithRole> assignees;
    private OnAssigneeRemovedListener listener;

    public interface OnAssigneeRemovedListener {
        void onAssigneeRemoved(AssigneeWithRole assignee);
    }

    public AssigneeRoleAdapter(Context context, List<AssigneeWithRole> assignees, OnAssigneeRemovedListener listener) {
        this.context = context;
        this.assignees = assignees != null ? assignees : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignee_role, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssigneeWithRole assignee = assignees.get(position);

        // Set avatar
        if (assignee.getEmployeeName() != null && !assignee.getEmployeeName().isEmpty()) {
            holder.textAvatar.setText(assignee.getEmployeeName().substring(0, 1).toUpperCase());
        }

        // Set info
        holder.textEmployeeName.setText(assignee.getEmployeeName());
        holder.textEmployeeRole.setText(assignee.getEmployeeRole());

        // Setup role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
            R.array.responsibility_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerRole.setAdapter(adapter);

        // Set current selection
        String[] types = context.getResources().getStringArray(R.array.responsibility_types_values);
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(assignee.getResponsibilityType())) {
                holder.spinnerRole.setSelection(i);
                break;
            }
        }

        // Handle role change
        holder.spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] values = context.getResources().getStringArray(R.array.responsibility_types_values);
                assignee.setResponsibilityType(values[pos]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Handle remove
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAssigneeRemoved(assignee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignees.size();
    }

    public void addAssignee(AssigneeWithRole assignee) {
        assignees.add(assignee);
        notifyItemInserted(assignees.size() - 1);
    }

    public void removeAssignee(AssigneeWithRole assignee) {
        int position = assignees.indexOf(assignee);
        if (position >= 0) {
            assignees.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<AssigneeWithRole> getAssignees() {
        return assignees;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textAvatar;
        TextView textEmployeeName;
        TextView textEmployeeRole;
        Spinner spinnerRole;
        ImageButton btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            textAvatar = itemView.findViewById(R.id.text_avatar);
            textEmployeeName = itemView.findViewById(R.id.text_employee_name);
            textEmployeeRole = itemView.findViewById(R.id.text_employee_role);
            spinnerRole = itemView.findViewById(R.id.spinner_role);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}


