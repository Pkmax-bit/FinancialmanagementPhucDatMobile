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
import com.example.financialmanagement.activities.AddEditProductCategoryActivity;
import com.example.financialmanagement.adapters.ProductCategoryAdapter;
import com.example.financialmanagement.models.ProductCategory;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.ProductCategoryApi;
import com.example.financialmanagement.utils.ErrorHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProductCategoriesFragment extends Fragment {

    private RecyclerView rvCategories;
    private ProductCategoryAdapter adapter;
    private ProductCategoryApi categoryApi;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    
    private static final int REQUEST_ADD_EDIT_CATEGORY = 1002;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_categories, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadCategories();
        
        return view;
    }

    private void initializeViews(View view) {
        rvCategories = view.findViewById(R.id.rv_product_categories);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_categories);
        progressBar = view.findViewById(R.id.pb_loading_categories);
        tvEmpty = view.findViewById(R.id.tv_empty_categories);
        fabAdd = view.findViewById(R.id.fab_add_category);
        
        if (getContext() != null) {
            categoryApi = ApiClient.getRetrofit(getContext()).create(ProductCategoryApi.class);
        }
        
        swipeRefreshLayout.setOnRefreshListener(this::loadCategories);
        
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditProductCategoryActivity.class);
            startActivityForResult(intent, REQUEST_ADD_EDIT_CATEGORY);
        });
    }

    private void setupRecyclerView() {
        adapter = new ProductCategoryAdapter(new ArrayList<>(), category -> {
            Intent intent = new Intent(getContext(), AddEditProductCategoryActivity.class);
            intent.putExtra(AddEditProductCategoryActivity.EXTRA_CATEGORY_ID, category.getId());
            startActivityForResult(intent, REQUEST_ADD_EDIT_CATEGORY);
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setAdapter(adapter);
    }

    private void loadCategories() {
        if (categoryApi == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        
        categoryApi.getAllCategories().enqueue(new Callback<List<ProductCategory>>() {
            @Override
            public void onResponse(Call<List<ProductCategory>> call, Response<List<ProductCategory>> response) {
                if (getActivity() == null) return;
                
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                     List<ProductCategory> categories = response.body();
                     if (!categories.isEmpty()) {
                        adapter.updateCategories(categories);
                        rvCategories.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                    } else {
                        adapter.updateCategories(new ArrayList<>());
                        rvCategories.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi tải danh mục: " + ErrorHandler.parseError(response), Toast.LENGTH_SHORT).show();
                    if (adapter.getItemCount() == 0) {
                       tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductCategory>> call, Throwable t) {
                if (getActivity() == null) return;
                
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Lỗi tải danh mục: " + ErrorHandler.parseError(t), Toast.LENGTH_SHORT).show();
                if (adapter.getItemCount() == 0) {
                   tvEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EDIT_CATEGORY && resultCode == RESULT_OK) {
            loadCategories();
        }
    }
}
