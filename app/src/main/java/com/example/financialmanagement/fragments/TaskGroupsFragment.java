package com.example.financialmanagement.fragments;

import android.content.Intent;
import com.example.financialmanagement.activities.TasksByGroupActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.TaskGroupAdapter;
import com.example.financialmanagement.models.TaskGroup;
import com.example.financialmanagement.services.TaskService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class TaskGroupsFragment extends Fragment implements TaskGroupAdapter.OnGroupClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TaskService taskService;
    private TaskGroupAdapter adapter;
    private List<TaskGroup> taskGroupList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_groups, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_groups);
        swipeRefreshLayout.setOnRefreshListener(this::loadTaskGroups);
        
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_group);
        fabAdd.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng thêm nhóm sẽ được triển khai", Toast.LENGTH_SHORT).show();
        });
        
        taskService = new TaskService(getContext());
        loadTaskGroups();
        
        return view;
    }

    private void loadTaskGroups() {
        swipeRefreshLayout.setRefreshing(true);
        taskService.getTaskGroups(new TaskService.TaskCallback<List<TaskGroup>>() {
            @Override
            public void onSuccess(List<TaskGroup> result) {
                swipeRefreshLayout.setRefreshing(false);
                taskGroupList.clear();
                taskGroupList.addAll(result);
                
                if (adapter == null) {
                    adapter = new TaskGroupAdapter(getContext(), taskGroupList, TaskGroupsFragment.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGroupClick(TaskGroup group) {
        Intent intent = new Intent(getContext(), TasksByGroupActivity.class);
        intent.putExtra("group_id", group.getId());
        intent.putExtra("group_name", group.getName());
        startActivity(intent);
    }
}
