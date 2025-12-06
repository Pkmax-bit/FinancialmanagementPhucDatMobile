package com.example.financialmanagement.fragments;

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
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.TaskAdapter;
import com.example.financialmanagement.models.Task;
import com.example.financialmanagement.services.TaskService;
import java.util.List;

public class TasksFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskService taskService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        taskService = new TaskService(getContext());
        loadTasks();
        
        return view;
    }

    private void loadTasks() {
        taskService.getTasks(new TaskService.TaskCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> result) {
                TaskAdapter adapter = new TaskAdapter(getContext(), result);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
