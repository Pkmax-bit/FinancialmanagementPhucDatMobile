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
import com.example.financialmanagement.utils.CurrencyFormatter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reports Fragment - Màn hình báo cáo
 * Hiển thị các biểu đồ và thống kê tài chính
 */
public class ReportsFragment extends Fragment {

    private TextView tvTotalRevenue, tvTotalExpenses, tvNetProfit;
    private PieChart pieChartExpenses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        
        initializeViews(view);
        loadReportsData();
        
        return view;
    }

    private void initializeViews(View view) {
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
        tvTotalExpenses = view.findViewById(R.id.tv_total_expenses);
        tvNetProfit = view.findViewById(R.id.tv_net_profit);
        pieChartExpenses = view.findViewById(R.id.pie_chart_expenses);
    }

    private void loadReportsData() {
        // Load financial summary
        loadFinancialSummary();
        
        // Load expense breakdown chart
        loadExpenseBreakdownChart();
    }

    private void loadFinancialSummary() {
        // TODO: Implement API calls to get financial data
        // For now, show placeholder data
        double totalRevenue = 25000000;
        double totalExpenses = 15000000;
        double netProfit = totalRevenue - totalExpenses;

        tvTotalRevenue.setText(CurrencyFormatter.format(totalRevenue));
        tvTotalExpenses.setText(CurrencyFormatter.format(totalExpenses));
        tvNetProfit.setText(CurrencyFormatter.format(netProfit));
    }

    private void loadExpenseBreakdownChart() {
        // TODO: Implement API calls to get expense breakdown data
        // For now, show sample data
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Nguyên vật liệu"));
        entries.add(new PieEntry(25f, "Nhân công"));
        entries.add(new PieEntry(20f, "Máy móc"));
        entries.add(new PieEntry(15f, "Khác"));

        PieDataSet dataSet = new PieDataSet(entries, "Phân bổ chi phí");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChartExpenses.setData(data);
        pieChartExpenses.setDescription(null);
        pieChartExpenses.animateY(1000);
        pieChartExpenses.invalidate();
    }
}
