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
import com.example.financialmanagement.activities.AddEditProductActivity;
import com.example.financialmanagement.adapters.GroupedProductAdapter;
import com.example.financialmanagement.models.Product;
import com.example.financialmanagement.models.ProductCategory;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.ProductApi;
import com.example.financialmanagement.network.api.ProductCategoryApi;
import com.example.financialmanagement.utils.ErrorHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProductsFragment extends Fragment {

    private RecyclerView rvProducts;
    private GroupedProductAdapter adapter;
    private ProductApi productApi;
    private ProductCategoryApi productCategoryApi;
    
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    
    private List<ProductCategory> categories = new ArrayList<>();
    private Map<String, String> categoryMap = new HashMap<>(); // id -> name
    private Map<String, List<Product>> groupedProducts = new TreeMap<>(); // sorted by category name
    private Set<String> expandedCategories = new HashSet<>(); // Track expanded category names
    
    private static final int REQUEST_ADD_EDIT_PRODUCT = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        
        try {
            initializeViews(view);
            setupRecyclerView();
            loadCategoriesAndProducts();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        return view;
    }

    private void initializeViews(View view) {
        rvProducts = view.findViewById(R.id.rv_products);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_products);
        progressBar = view.findViewById(R.id.pb_loading_products);
        tvEmpty = view.findViewById(R.id.tv_empty_products);
        fabAdd = view.findViewById(R.id.fab_add_product);
        
        if (getContext() != null) {
            productApi = ApiClient.getRetrofit(getContext()).create(ProductApi.class);
            productCategoryApi = ApiClient.getRetrofit(getContext()).create(ProductCategoryApi.class);
        }
        
        swipeRefreshLayout.setOnRefreshListener(this::loadCategoriesAndProducts);
        
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditProductActivity.class);
            startActivityForResult(intent, REQUEST_ADD_EDIT_PRODUCT);
        });
    }

    private void setupRecyclerView() {
        adapter = new GroupedProductAdapter(
            new ArrayList<>(), 
            product -> {
                Intent intent = new Intent(getContext(), AddEditProductActivity.class);
                intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_ID, product.getId());
                startActivityForResult(intent, REQUEST_ADD_EDIT_PRODUCT);
            },
            categoryName -> {
                // Toggle expand/collapse
                if (expandedCategories.contains(categoryName)) {
                    expandedCategories.remove(categoryName);
                } else {
                    expandedCategories.add(categoryName);
                }
                updateAdapterData();
            }
        );
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setAdapter(adapter);
    }
    
    private void loadCategoriesAndProducts() {
        if (productCategoryApi == null || productApi == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        
        // 1. Load Categories first
        productCategoryApi.getAllCategories().enqueue(new Callback<List<ProductCategory>>() {
            @Override
            public void onResponse(Call<List<ProductCategory>> call, Response<List<ProductCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    categoryMap.clear();
                    for (ProductCategory cat : categories) {
                        categoryMap.put(cat.getId(), cat.getName());
                    }
                    // After categories loaded, load products
                    loadProducts();
                } else {
                    handleError("Lỗi tải danh mục: " + ErrorHandler.parseError(response));
                    loadProducts(); // Try to load products anyway
                }
            }

            @Override
            public void onFailure(Call<List<ProductCategory>> call, Throwable t) {
                handleError("Lỗi tải danh mục: " + ErrorHandler.parseError(t));
                loadProducts(); // Try to load products anyway
            }
        });
    }

    private void loadProducts() {
        Map<String, Object> params = new HashMap<>();
        
        productApi.getAllProducts(params).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (getActivity() == null) return;
                
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    if (!products.isEmpty()) {
                        processProducts(products);
                        rvProducts.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                    } else {
                        adapter.updateItems(new ArrayList<>());
                        rvProducts.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    handleError("Lỗi tải sản phẩm: " + ErrorHandler.parseError(response));
                    if (adapter.getItemCount() == 0) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("Không thể tải danh sách sản phẩm");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (getActivity() == null) return;
                
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                handleError("Lỗi tải sản phẩm: " + ErrorHandler.parseError(t));
                if (adapter.getItemCount() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Không thể tải danh sách sản phẩm");
                }
            }
        });
    }
    
    private void processProducts(List<Product> products) {
        groupedProducts.clear();
        
        for (Product p : products) {
            String catName = "Khác";
            if (p.getCategoryId() != null && categoryMap.containsKey(p.getCategoryId())) {
                catName = categoryMap.get(p.getCategoryId());
            } else if (p.getCategory() != null && !p.getCategory().isEmpty()) {
                 // Fallback to name if ID mapping fails but name exists
                 catName = p.getCategory();
            }
            
            if (!groupedProducts.containsKey(catName)) {
                groupedProducts.put(catName, new ArrayList<>());
            }
            groupedProducts.get(catName).add(p);
        }
        
        updateAdapterData();
    }
    
    private void updateAdapterData() {
        List<GroupedProductAdapter.DisplayItem> displayItems = new ArrayList<>();
        
        // Sort categories by name
        List<String> sortedCategories = new ArrayList<>(groupedProducts.keySet());
        Collections.sort(sortedCategories);
        
        for (String catName : sortedCategories) {
            List<Product> products = groupedProducts.get(catName);
            boolean isExpanded = expandedCategories.contains(catName);
            
            // Add Header
            displayItems.add(new GroupedProductAdapter.DisplayItem.Header(
                catName, 
                products.size(), 
                isExpanded
            ));
            
            // Add Items if expanded
            if (isExpanded) {
                for (Product p : products) {
                    displayItems.add(new GroupedProductAdapter.DisplayItem.Item(p));
                }
            }
        }
        
        adapter.updateItems(displayItems);
    }

    private void handleError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EDIT_PRODUCT && resultCode == RESULT_OK) {
            loadCategoriesAndProducts();
        }
    }
}
