package com.example.financialmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.financialmanagement.adapters.CustomersAdapter;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.services.CustomerService;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.activities.LoginActivity;
import com.example.financialmanagement.activities.CustomerFormActivity;
import com.example.financialmanagement.activities.CustomerDetailActivity;
import com.example.financialmanagement.utils.CurrencyFormatter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Customers Fragment - Màn hình quản lý khách hàng
 * Hiển thị danh sách khách hàng với thống kê và bộ lọc
 */
public class CustomersFragment extends Fragment implements CustomersAdapter.CustomerClickListener {

    private static final int REQUEST_CODE_ADD_CUSTOMER = 1001;
    private static final int REQUEST_CODE_EDIT_CUSTOMER = 1002;
    private static final int REQUEST_CODE_VIEW_CUSTOMER = 1003;

    // UI elements
    private RecyclerView rvCustomers;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private EditText etSearch;
    
    // Statistics
    private TextView tvTotalCustomers, tvActiveCustomers, tvUnpaidAmount, tvCompanyCustomers;
    private TextView tvCustomerCount;
    
    // Filters
    private ChipGroup chipGroupFilter;
    private Chip chipAll, chipActive, chipProspect, chipInactive;
    
    private ExtendedFloatingActionButton fabAddCustomer;
    
    private CustomersAdapter customersAdapter;
    private CustomerService customerService;
    private AuthManager authManager;
    
    private List<Customer> allCustomers = new ArrayList<>();
    private String currentFilter = "all";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupSearch();
        setupFilterChips();
        loadCustomers();
        
        return view;
    }

    private void initializeViews(View view) {
        customerService = new CustomerService(getContext());
        authManager = new AuthManager(getContext());
        
        // Main views
        rvCustomers = view.findViewById(R.id.rv_customers);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        emptyState = view.findViewById(R.id.empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
        etSearch = view.findViewById(R.id.et_search);
        
        // Statistics
        tvTotalCustomers = view.findViewById(R.id.tv_total_customers);
        tvActiveCustomers = view.findViewById(R.id.tv_active_customers);
        tvUnpaidAmount = view.findViewById(R.id.tv_unpaid_amount);
        tvCompanyCustomers = view.findViewById(R.id.tv_company_customers);
        tvCustomerCount = view.findViewById(R.id.tv_customer_count);
        
        // Filters
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        chipAll = view.findViewById(R.id.chip_all);
        chipActive = view.findViewById(R.id.chip_active);
        chipProspect = view.findViewById(R.id.chip_prospect);
        chipInactive = view.findViewById(R.id.chip_inactive);
        
        // FAB
        fabAddCustomer = view.findViewById(R.id.fab_add_customer);
        fabAddCustomer.setOnClickListener(v -> addNewCustomer());
    }

    private void setupRecyclerView() {
        customersAdapter = new CustomersAdapter(new ArrayList<>(), this);
        rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustomers.setAdapter(customersAdapter);
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.md_primary);
        swipeRefresh.setOnRefreshListener(this::loadCustomers);
    }
    
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                filterAndDisplayCustomers();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipAll.setChecked(true);
                return;
            }
            
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_all) {
                currentFilter = "all";
            } else if (checkedId == R.id.chip_active) {
                currentFilter = "active";
            } else if (checkedId == R.id.chip_prospect) {
                currentFilter = "prospect";
            } else if (checkedId == R.id.chip_inactive) {
                currentFilter = "inactive";
            }
            
            filterAndDisplayCustomers();
        });
    }

    private void loadCustomers() {
        if (!authManager.isLoggedIn()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        
        Map<String, Object> params = new HashMap<>();
        
        customerService.getAllCustomers(params, new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allCustomers = customers != null ? customers : new ArrayList<>();
                        updateStatistics();
                        filterAndDisplayCustomers();
                        showLoading(false);
                        swipeRefresh.setRefreshing(false);
                    });
                }
            }
            
            @Override
            public void onSuccess(Customer customer) {}

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        swipeRefresh.setRefreshing(false);
                        
                        if (error.contains("401") || error.contains("403")) {
                            loadPublicCustomers();
                        } else {
                            Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                            showEmptyState(true);
                        }
                    });
                }
            }
        });
    }
    
    private void updateStatistics() {
        int total = allCustomers.size();
        int active = 0;
        int company = 0;
        double unpaid = 0;
        
        for (Customer c : allCustomers) {
            if ("active".equalsIgnoreCase(c.getStatus())) active++;
            if ("company".equalsIgnoreCase(c.getCustomerType())) company++;
            if (c.getDebt() != null) unpaid += c.getDebt();
        }
        
        tvTotalCustomers.setText(String.valueOf(total));
        tvActiveCustomers.setText(String.valueOf(active));
        tvCompanyCustomers.setText(String.valueOf(company));
        tvUnpaidAmount.setText(CurrencyFormatter.formatShort(unpaid));
    }
    
    private void filterAndDisplayCustomers() {
        List<Customer> filtered = new ArrayList<>();
        
        for (Customer c : allCustomers) {
            // Apply status filter
            boolean matchesFilter = currentFilter.equals("all") || 
                (c.getStatus() != null && c.getStatus().equalsIgnoreCase(currentFilter));
            
            // Apply search
            boolean matchesSearch = searchQuery.isEmpty() ||
                (c.getName() != null && c.getName().toLowerCase().contains(searchQuery)) ||
                (c.getCustomerCode() != null && c.getCustomerCode().toLowerCase().contains(searchQuery)) ||
                (c.getEmail() != null && c.getEmail().toLowerCase().contains(searchQuery)) ||
                (c.getPhone() != null && c.getPhone().toLowerCase().contains(searchQuery));
            
            if (matchesFilter && matchesSearch) {
                filtered.add(c);
            }
        }
        
        customersAdapter.updateCustomers(filtered);
        tvCustomerCount.setText(filtered.size() + " khách hàng");
        showEmptyState(filtered.isEmpty());
    }
    
    private void showEmptyState(boolean show) {
        if (emptyState != null) {
            emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvCustomers != null) {
            rvCustomers.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    
    private void loadPublicCustomers() {
        customerService.getCustomersPublic(new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allCustomers = customers != null ? customers : new ArrayList<>();
                        updateStatistics();
                        filterAndDisplayCustomers();
                    });
                }
            }
            
            @Override
            public void onSuccess(Customer customer) {}
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showEmptyState(true);
                    });
                }
            }
        });
    }

    @Override
    public void onCustomerClick(Customer customer) {
        Intent intent = new Intent(getContext(), CustomerDetailActivity.class);
        intent.putExtra(CustomerDetailActivity.EXTRA_CUSTOMER, customer);
        startActivityForResult(intent, REQUEST_CODE_VIEW_CUSTOMER);
    }

    @Override
    public void onCustomerEdit(Customer customer) {
        Intent intent = new Intent(getContext(), CustomerFormActivity.class);
        intent.putExtra(CustomerFormActivity.EXTRA_MODE, CustomerFormActivity.MODE_EDIT);
        intent.putExtra(CustomerFormActivity.EXTRA_CUSTOMER, customer);
        startActivityForResult(intent, REQUEST_CODE_EDIT_CUSTOMER);
    }

    @Override
    public void onCustomerDelete(Customer customer) {
        new AlertDialog.Builder(getContext())
            .setTitle("Xóa khách hàng")
            .setMessage("Bạn có chắc chắn muốn xóa \"" + customer.getName() + "\"?")
            .setPositiveButton("Xóa", (d, w) -> deleteCustomer(customer))
            .setNegativeButton("Hủy", null)
            .show();
    }

    @Override
    public void onCustomerCreateProject(Customer customer) {
        Toast.makeText(getContext(), "Tạo dự án cho: " + customer.getName(), Toast.LENGTH_SHORT).show();
    }
    
    private void deleteCustomer(Customer customer) {
        customerService.deleteCustomer(customer.getId(), new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {}
            
            @Override
            public void onSuccess(Customer customer) {}
            
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                        loadCustomers();
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            loadCustomers();
        }
    }
    
    public void addNewCustomer() {
        Intent intent = new Intent(getContext(), CustomerFormActivity.class);
        intent.putExtra(CustomerFormActivity.EXTRA_MODE, CustomerFormActivity.MODE_CREATE);
        startActivityForResult(intent, REQUEST_CODE_ADD_CUSTOMER);
    }
}
