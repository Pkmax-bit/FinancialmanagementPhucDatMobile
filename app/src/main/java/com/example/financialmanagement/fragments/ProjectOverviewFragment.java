package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financialmanagement.R;

public class ProjectOverviewFragment extends Fragment {
    
    private static final String ARG_PROJECT_ID = "project_id";
    
    private String projectId;
    private TextView tvOverview;
    
    public static ProjectOverviewFragment newInstance(String projectId) {
        ProjectOverviewFragment fragment = new ProjectOverviewFragment();
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
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_overview, container, false);
        
        tvOverview = view.findViewById(R.id.tv_overview);
        
        setupOverview();
        
        return view;
    }
    
    private void setupOverview() {
        String overviewText = "Tổng quan dự án\n\n" +
                "• Xem chi tiết tất cả báo giá của dự án\n" +
                "• Quản lý hóa đơn và thanh toán\n" +
                "• Theo dõi chi phí kế hoạch và thực tế\n" +
                "• Phân tích chênh lệch ngân sách\n" +
                "• Báo cáo tài chính tổng hợp\n\n" +
                "Sử dụng các tab bên trên để xem chi tiết từng loại dữ liệu.";
        
        tvOverview.setText(overviewText);
    }
}
