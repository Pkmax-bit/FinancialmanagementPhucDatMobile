package com.example.financialmanagement.fragments;

import android.content.Intent;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.financialmanagement.R;
import com.example.financialmanagement.activities.AddEditProductRuleActivity;
import com.example.financialmanagement.adapters.ProductRuleAdapter;
import com.example.financialmanagement.models.MaterialAdjustmentRule;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.ProductRuleApi;
import com.example.financialmanagement.utils.ErrorHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProductRulesFragment extends Fragment {

    private RecyclerView rvRules;
    private ProductRuleAdapter adapter;
    private ProductRuleApi ruleApi;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    
    private static final int REQUEST_ADD_EDIT_RULE = 1003;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_rules, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadRules();
        
        return view;
    }

    private void initializeViews(View view) {
        rvRules = view.findViewById(R.id.rv_product_rules);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_rules);
        progressBar = view.findViewById(R.id.pb_loading_rules);
        tvEmpty = view.findViewById(R.id.tv_empty_rules);
        fabAdd = view.findViewById(R.id.fab_add_rule);
        
        if (getContext() != null) {
            ruleApi = ApiClient.getRetrofit(getContext()).create(ProductRuleApi.class);
        }
        
        swipeRefreshLayout.setOnRefreshListener(this::loadRules);
        
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditProductRuleActivity.class);
            startActivityForResult(intent, REQUEST_ADD_EDIT_RULE);
        });
    }

    private void setupRecyclerView() {
        adapter = new ProductRuleAdapter(new ArrayList<>(), rule -> {
            Intent intent = new Intent(getContext(), AddEditProductRuleActivity.class);
            intent.putExtra(AddEditProductRuleActivity.EXTRA_RULE_ID, rule.getId());
            startActivityForResult(intent, REQUEST_ADD_EDIT_RULE);
        });
        rvRules.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRules.setAdapter(adapter);
    }

    private void loadRules() {
        if (ruleApi == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        
        ruleApi.getAllRules().enqueue(new Callback<List<MaterialAdjustmentRule>>() {
            @Override
            public void onResponse(Call<List<MaterialAdjustmentRule>> call, Response<List<MaterialAdjustmentRule>> response) {
                if (getActivity() == null) return;
                
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<MaterialAdjustmentRule> rules = response.body();
                    if (!rules.isEmpty()) {
                        adapter.updateRules(rules);
                        rvRules.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                    } else {
                        adapter.updateRules(new ArrayList<>());
                        rvRules.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi tải quy tắc: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                    if (adapter.getItemCount() == 0) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MaterialAdjustmentRule>> call, Throwable t) {
                if (getActivity() == null) return;
                
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Lỗi tải quy tắc: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
                if (adapter.getItemCount() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EDIT_RULE && resultCode == RESULT_OK) {
            loadRules();
        }
    }
}
