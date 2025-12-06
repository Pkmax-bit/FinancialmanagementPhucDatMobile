package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.financialmanagement.R;
import com.example.financialmanagement.services.ReportService;
import java.util.Map;

public class ReportsFragment extends Fragment {

    private TextView tvTotalAssets, tvTotalLiabilities, tvTotalEquity;
    private ReportService reportService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        
        tvTotalAssets = view.findViewById(R.id.tv_total_assets);
        tvTotalLiabilities = view.findViewById(R.id.tv_total_liabilities);
        tvTotalEquity = view.findViewById(R.id.tv_total_equity);
        
        reportService = new ReportService(getContext());
        loadReports();
        
        return view;
    }

    private void loadReports() {
        reportService.getBalanceSheet(new ReportService.ReportCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                if (result != null) {
                    // Assuming structure matches API response
                    // This is a simplified example
                    tvTotalAssets.setText("Assets: " + result.get("total_assets"));
                    tvTotalLiabilities.setText("Liabilities: " + result.get("total_liabilities"));
                    tvTotalEquity.setText("Equity: " + result.get("total_equity"));
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}