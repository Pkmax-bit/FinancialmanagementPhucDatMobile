package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.TeamAdapter;
import com.example.financialmanagement.models.TeamMember;
import com.example.financialmanagement.services.ProjectService;

import java.util.ArrayList;
import java.util.List;

public class ProjectTeamFragment extends Fragment {
    
    private static final String ARG_PROJECT_ID = "project_id";
    
    private String projectId;
    private RecyclerView rvTeam;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TeamAdapter teamAdapter;
    private ProjectService projectService;
    private List<TeamMember> teamList = new ArrayList<>();
    
    public static ProjectTeamFragment newInstance(String projectId) {
        ProjectTeamFragment fragment = new ProjectTeamFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, projectId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectId = getArguments().getString(ARG_PROJECT_ID);
        }
        projectService = new ProjectService(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_team, container, false);
        
        rvTeam = view.findViewById(R.id.rv_team);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        
        setupRecyclerView();
        loadTeam();
        
        return view;
    }
    
    private void setupRecyclerView() {
        rvTeam.setLayoutManager(new LinearLayoutManager(requireContext()));
        teamAdapter = new TeamAdapter(requireContext(), teamList);
        rvTeam.setAdapter(teamAdapter);
    }
    
    private void loadTeam() {
        if (projectId == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        
        projectService.getProjectTeam(projectId, new ProjectService.TeamCallback() {
            @Override
            public void onSuccess(List<TeamMember> team) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        teamList.clear();
                        if (team != null && !team.isEmpty()) {
                            teamList.addAll(team);
                            teamAdapter.notifyDataSetChanged();
                            tvEmpty.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("Lỗi: " + error);
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    public void updateTeam(List<TeamMember> team) {
        if (team != null) {
            teamList.clear();
            teamList.addAll(team);
            if (teamAdapter != null) {
                teamAdapter.notifyDataSetChanged();
            }
        }
    }
}
