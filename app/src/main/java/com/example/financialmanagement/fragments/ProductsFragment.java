package com.example.financialmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private TextView tvProductCount;
    private EditText etSearch;
    private FloatingActionButton fabAdd;
    
    private List<ProductCategory> categories = new ArrayList<>();
    private Map<String, String> categoryMap = new HashMap<>(); // id -> name
    private Map<String, List<Product>> groupedProducts = new TreeMap<>(); // sorted by category name
    private Set<String> expandedCategories = new HashSet<>(); // Track expanded category names
    
    // Handler for debouncing search
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private String searchQuery = "";
    
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
        tvProductCount = view.findViewById(R.id.tv_product_count);
        etSearch = view.findViewById(R.id.et_search_products);
        fabAdd = view.findViewById(R.id.fab_add_product);
        
        if (getContext() != null) {
            productApi = ApiClient.getRetrofit(getContext()).create(ProductApi.class);
            productCategoryApi = ApiClient.getRetrofit(getContext()).create(ProductCategoryApi.class);
        }
        
        swipeRefreshLayout.setOnRefreshListener(this::loadCategoriesAndProducts);
        
        setupSearch();
        
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditProductActivity.class);
            startActivityForResult(intent, REQUEST_ADD_EDIT_PRODUCT);
        });
    }
    
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous search runnable
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                // Debounce search - wait 300ms after user stops typing
                searchRunnable = () -> {
                    searchQuery = s.toString().toLowerCase().trim();
                    filterAndDisplayProducts();
                };
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
        
        filterAndDisplayProducts();
    }
    
    private void filterAndDisplayProducts() {
        // Run filtering on background thread for large datasets
        new Thread(() -> {
            Map<String, List<Product>> filteredGrouped = new TreeMap<>();
            int totalCount = 0;
            
            for (Map.Entry<String, List<Product>> entry : groupedProducts.entrySet()) {
                String catName = entry.getKey();
                List<Product> filteredProducts = new ArrayList<>();
                
                for (Product p : entry.getValue()) {
                    // Filter by search query
                    boolean matchesSearch = searchQuery.isEmpty() ||
                        (p.getName() != null && p.getName().toLowerCase().contains(searchQuery)) ||
                        (p.getSku() != null && p.getSku().toLowerCase().contains(searchQuery)) ||
                        (catName != null && catName.toLowerCase().contains(searchQuery));
                    
                    if (matchesSearch) {
                        filteredProducts.add(p);
                        totalCount++;
                    }
                }
                
                if (!filteredProducts.isEmpty()) {
                    filteredGrouped.put(catName, filteredProducts);
                }
            }
            
            // Build display items
            List<GroupedProductAdapter.DisplayItem> displayItems = new ArrayList<>();
            List<String> sortedCategories = new ArrayList<>(filteredGrouped.keySet());
            Collections.sort(sortedCategories);
            
            for (String catName : sortedCategories) {
                List<Product> products = filteredGrouped.get(catName);
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
            
            // Update UI on main thread
            if (getActivity() != null) {
                // Create final variables for lambda
                final int finalTotalCount = totalCount;
                final List<GroupedProductAdapter.DisplayItem> finalDisplayItems = displayItems;
                final String finalSearchQuery = searchQuery;
                
                getActivity().runOnUiThread(() -> {
                    adapter.updateItems(finalDisplayItems);
                    if (tvProductCount != null) {
                        tvProductCount.setText(finalTotalCount + " sản phẩm");
                    }
                    if (finalDisplayItems.isEmpty()) {
                        rvProducts.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText(finalSearchQuery.isEmpty() ? "Chưa có sản phẩm nào" : "Không tìm thấy sản phẩm");
                    } else {
                        rvProducts.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }
    
    private void updateAdapterData() {
        filterAndDisplayProducts();
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
