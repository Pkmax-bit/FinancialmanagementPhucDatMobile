package com.example.financialmanagement.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.QuotesAdapter;
import com.example.financialmanagement.models.Quote;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.services.QuoteService;
import com.example.financialmanagement.services.ProjectService;
import com.example.financialmanagement.services.CustomerService;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.utils.ApiDebugger;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Quotes Fragment - Màn hình quản lý báo giá
 * Bước 3 trong quy trình: Tạo khách hàng → Dự án → Báo giá → Hóa đơn → Chi phí → Báo cáo
 * Hiển thị danh sách báo giá và cho phép thao tác CRUD với approval workflow
 */
public class QuotesFragment extends Fragment implements QuotesAdapter.QuoteClickListener {

    private RecyclerView rvQuotes;
    private QuotesAdapter quotesAdapter;
    private QuoteService quoteService;
    private ProjectService projectService;
    private CustomerService customerService;
    private AuthManager authManager;
    
    // Filter views
    private View layoutFilterHeader;
    private View layoutFilterContent;
    private ImageView ivFilterExpand;
    private TextView tvClearFilters;
    private Spinner spinnerProject;
    private Spinner spinnerCustomer;
    private Spinner spinnerStatus;
    private MaterialButton btnDateFrom;
    private MaterialButton btnDateTo;
    private MaterialButton btnApplyFilter;
    private View scrollActiveFilters;
    private ChipGroup chipGroupFilters;
    private TextInputEditText etSearchQuotes;
    private TextView tvTotalQuotes;
    private TextView tvApprovedQuotes;
    private View layoutEmptyState;
    
    // Filter data
    private List<Project> projectList = new ArrayList<>();
    private List<Customer> customerList = new ArrayList<>();
    private List<Quote> allQuotes = new ArrayList<>();
    private List<Quote> filteredQuotes = new ArrayList<>();
    
    // Filter values
    private String selectedProjectId = null;
    private String selectedCustomerId = null;
    private String selectedStatus = null;
    private Date dateFrom = null;
    private Date dateTo = null;
    private String searchQuery = "";
    
    private boolean isFilterExpanded = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupFilters();
        loadFilterData();
        loadQuotes();
        
        return view;
    }

    private void initializeViews(View view) {
        rvQuotes = view.findViewById(R.id.rv_quotes);
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAddQuote = view.findViewById(R.id.fab_add_quote);
        
        // Filter views
        layoutFilterHeader = view.findViewById(R.id.layout_filter_header);
        layoutFilterContent = view.findViewById(R.id.layout_filter_content);
        ivFilterExpand = view.findViewById(R.id.iv_filter_expand);
        tvClearFilters = view.findViewById(R.id.tv_clear_filters);
        spinnerProject = view.findViewById(R.id.spinner_project);
        spinnerCustomer = view.findViewById(R.id.spinner_customer);
        spinnerStatus = view.findViewById(R.id.spinner_status);
        btnDateFrom = view.findViewById(R.id.btn_date_from);
        btnDateTo = view.findViewById(R.id.btn_date_to);
        btnApplyFilter = view.findViewById(R.id.btn_apply_filter);
        scrollActiveFilters = view.findViewById(R.id.scroll_active_filters);
        chipGroupFilters = view.findViewById(R.id.chip_group_filters);
        etSearchQuotes = view.findViewById(R.id.et_search_quotes);
        tvTotalQuotes = view.findViewById(R.id.tv_total_quotes);
        tvApprovedQuotes = view.findViewById(R.id.tv_approved_quotes);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        
        fabAddQuote.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getContext(), com.example.financialmanagement.activities.AddEditQuoteActivity.class);
            startActivity(intent);
        });

        quoteService = new QuoteService(getContext());
        projectService = new ProjectService(getContext());
        customerService = new CustomerService(getContext());
        authManager = new AuthManager(getContext());
    }

    private void setupRecyclerView() {
        quotesAdapter = new QuotesAdapter(new ArrayList<>(), this);
        rvQuotes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuotes.setAdapter(quotesAdapter);
    }

    private void setupFilters() {
        // Toggle filter expansion
        layoutFilterHeader.setOnClickListener(v -> toggleFilterExpansion());
        
        // Clear filters
        tvClearFilters.setOnClickListener(v -> clearAllFilters());
        
        // Date pickers
        btnDateFrom.setOnClickListener(v -> showDatePicker(true));
        btnDateTo.setOnClickListener(v -> showDatePicker(false));
        
        // Apply filter
        btnApplyFilter.setOnClickListener(v -> applyFilters());
        
        // Setup status spinner
        String[] statuses = {"Tất cả trạng thái", "Nháp", "Đã gửi", "Đã duyệt", "Từ chối", "Đã chuyển đổi"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        
        // Search text watcher
        etSearchQuotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                filterQuotes();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void toggleFilterExpansion() {
        isFilterExpanded = !isFilterExpanded;
        layoutFilterContent.setVisibility(isFilterExpanded ? View.VISIBLE : View.GONE);
        ivFilterExpand.setRotation(isFilterExpanded ? 180 : 0);
    }

    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        if (isFromDate && dateFrom != null) {
            calendar.setTime(dateFrom);
        } else if (!isFromDate && dateTo != null) {
            calendar.setTime(dateTo);
        }
        
        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            Date selectedDate = selected.getTime();
            
            if (isFromDate) {
                dateFrom = selectedDate;
                btnDateFrom.setText(dateFormat.format(selectedDate));
            } else {
                dateTo = selectedDate;
                btnDateTo.setText(dateFormat.format(selectedDate));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        
        dialog.show();
    }

    private void loadFilterData() {
        // Load projects
        projectService.getAllProjects(new HashMap<>(), new ProjectService.ProjectCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        projectList.clear();
                        projectList.addAll(projects);
                        setupProjectSpinner();
                    });
                }
            }
            
            @Override
            public void onSuccess(Project project) {}
            
            @Override
            public void onSuccess() {}
            
            @Override
            public void onError(String error) {
                android.util.Log.e("QuotesFragment", "Error loading projects: " + error);
            }
        });
        
        // Load customers
        customerService.getAllCustomers(new HashMap<>(), new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        customerList.clear();
                        customerList.addAll(customers);
                        setupCustomerSpinner();
                    });
                }
            }
            
            @Override
            public void onSuccess(Customer customer) {}
            
            @Override
            public void onSuccess() {}
            
            @Override
            public void onError(String error) {
                android.util.Log.e("QuotesFragment", "Error loading customers: " + error);
            }
        });
    }

    private void setupProjectSpinner() {
        List<String> projectNames = new ArrayList<>();
        projectNames.add("Tất cả dự án");
        for (Project project : projectList) {
            projectNames.add(project.getName() != null ? project.getName() : "N/A");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, projectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProject.setAdapter(adapter);
        
        spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedProjectId = null;
                } else {
                    selectedProjectId = projectList.get(position - 1).getId();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedProjectId = null;
            }
        });
    }

    private void setupCustomerSpinner() {
        List<String> customerNames = new ArrayList<>();
        customerNames.add("Tất cả khách hàng");
        for (Customer customer : customerList) {
            customerNames.add(customer.getName() != null ? customer.getName() : "N/A");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, customerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCustomer.setAdapter(adapter);
        
        spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedCustomerId = null;
                } else {
                    selectedCustomerId = customerList.get(position - 1).getId();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCustomerId = null;
            }
        });
    }

    private void applyFilters() {
        // Get status
        int statusPosition = spinnerStatus.getSelectedItemPosition();
        if (statusPosition == 0) {
            selectedStatus = null;
        } else {
            String[] statusValues = {"draft", "sent", "approved", "rejected", "converted"};
            selectedStatus = statusValues[statusPosition - 1];
        }
        
        filterQuotes();
        updateActiveFiltersChips();
        
        // Collapse filter panel
        if (isFilterExpanded) {
            toggleFilterExpansion();
        }
    }

    private void filterQuotes() {
        filteredQuotes.clear();
        
        for (Quote quote : allQuotes) {
            boolean matches = true;
            
            // Search filter
            if (!searchQuery.isEmpty()) {
                String quoteNumber = quote.getQuoteNumber() != null ? quote.getQuoteNumber().toLowerCase() : "";
                String customerName = quote.getCustomer() != null && quote.getCustomer().getName() != null 
                    ? quote.getCustomer().getName().toLowerCase() : "";
                String projectName = quote.getProject() != null && quote.getProject().getName() != null 
                    ? quote.getProject().getName().toLowerCase() : "";
                
                String searchLower = searchQuery.toLowerCase();
                if (!quoteNumber.contains(searchLower) && 
                    !customerName.contains(searchLower) && 
                    !projectName.contains(searchLower)) {
                    matches = false;
                }
            }
            
            // Project filter
            if (matches && selectedProjectId != null) {
                String quoteProjectId = quote.getProjectId();
                if (quoteProjectId == null || !quoteProjectId.equals(selectedProjectId)) {
                    matches = false;
                }
            }
            
            // Customer filter
            if (matches && selectedCustomerId != null) {
                String quoteCustomerId = quote.getCustomerId();
                if (quoteCustomerId == null || !quoteCustomerId.equals(selectedCustomerId)) {
                    matches = false;
                }
            }
            
            // Status filter
            if (matches && selectedStatus != null) {
                String quoteStatus = quote.getStatus();
                if (quoteStatus == null || !quoteStatus.equalsIgnoreCase(selectedStatus)) {
                    matches = false;
                }
            }
            
            // Date from filter
            if (matches && dateFrom != null) {
                Date quoteDate = quote.getIssueDate();
                if (quoteDate == null) quoteDate = quote.getQuoteDate();
                if (quoteDate == null || quoteDate.before(dateFrom)) {
                    matches = false;
                }
            }
            
            // Date to filter
            if (matches && dateTo != null) {
                Date quoteDate = quote.getIssueDate();
                if (quoteDate == null) quoteDate = quote.getQuoteDate();
                if (quoteDate == null || quoteDate.after(dateTo)) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredQuotes.add(quote);
            }
        }
        
        quotesAdapter.updateQuotes(filteredQuotes);
        updateStatistics();
        updateEmptyState();
    }

    private void updateActiveFiltersChips() {
        chipGroupFilters.removeAllViews();
        boolean hasFilters = false;
        
        // Project chip
        if (selectedProjectId != null) {
            hasFilters = true;
            String projectName = getProjectNameById(selectedProjectId);
            addFilterChip("Dự án: " + projectName, () -> {
                selectedProjectId = null;
                spinnerProject.setSelection(0);
                filterQuotes();
                updateActiveFiltersChips();
            });
        }
        
        // Customer chip
        if (selectedCustomerId != null) {
            hasFilters = true;
            String customerName = getCustomerNameById(selectedCustomerId);
            addFilterChip("KH: " + customerName, () -> {
                selectedCustomerId = null;
                spinnerCustomer.setSelection(0);
                filterQuotes();
                updateActiveFiltersChips();
            });
        }
        
        // Status chip
        if (selectedStatus != null) {
            hasFilters = true;
            addFilterChip("Trạng thái: " + getStatusText(selectedStatus), () -> {
                selectedStatus = null;
                spinnerStatus.setSelection(0);
                filterQuotes();
                updateActiveFiltersChips();
            });
        }
        
        // Date from chip
        if (dateFrom != null) {
            hasFilters = true;
            addFilterChip("Từ: " + dateFormat.format(dateFrom), () -> {
                dateFrom = null;
                btnDateFrom.setText("Từ ngày");
                filterQuotes();
                updateActiveFiltersChips();
            });
        }
        
        // Date to chip
        if (dateTo != null) {
            hasFilters = true;
            addFilterChip("Đến: " + dateFormat.format(dateTo), () -> {
                dateTo = null;
                btnDateTo.setText("Đến ngày");
                filterQuotes();
                updateActiveFiltersChips();
            });
        }
        
        scrollActiveFilters.setVisibility(hasFilters ? View.VISIBLE : View.GONE);
        tvClearFilters.setVisibility(hasFilters ? View.VISIBLE : View.GONE);
    }

    private void addFilterChip(String text, Runnable onClose) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> onClose.run());
        chipGroupFilters.addView(chip);
    }

    private String getProjectNameById(String projectId) {
        for (Project project : projectList) {
            if (project.getId() != null && project.getId().equals(projectId)) {
                return project.getName() != null ? project.getName() : "N/A";
            }
        }
        return "N/A";
    }

    private String getCustomerNameById(String customerId) {
        for (Customer customer : customerList) {
            if (customer.getId() != null && customer.getId().equals(customerId)) {
                return customer.getName() != null ? customer.getName() : "N/A";
            }
        }
        return "N/A";
    }

    private String getStatusText(String status) {
        if (status == null) return "N/A";
        switch (status.toLowerCase()) {
            case "draft": return "Nháp";
            case "sent": return "Đã gửi";
            case "approved": return "Đã duyệt";
            case "rejected": return "Từ chối";
            case "converted": return "Đã chuyển đổi";
            default: return status;
        }
    }

    private void clearAllFilters() {
        selectedProjectId = null;
        selectedCustomerId = null;
        selectedStatus = null;
        dateFrom = null;
        dateTo = null;
        searchQuery = "";
        
        spinnerProject.setSelection(0);
        spinnerCustomer.setSelection(0);
        spinnerStatus.setSelection(0);
        btnDateFrom.setText("Từ ngày");
        btnDateTo.setText("Đến ngày");
        etSearchQuotes.setText("");
        
        filterQuotes();
        updateActiveFiltersChips();
    }

    private void updateStatistics() {
        int total = filteredQuotes.size();
        int approved = 0;
        for (Quote quote : filteredQuotes) {
            if ("approved".equalsIgnoreCase(quote.getStatus())) {
                approved++;
            }
        }
        
        tvTotalQuotes.setText(String.valueOf(total));
        tvApprovedQuotes.setText(String.valueOf(approved));
    }

    private void updateEmptyState() {
        if (filteredQuotes.isEmpty()) {
            rvQuotes.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvQuotes.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadQuotes();
    }

    private void loadQuotes() {
        // Check authentication first
        if (!authManager.isLoggedIn()) {
            ApiDebugger.logAuth("User not logged in, cannot load quotes", false);
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem báo giá", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Load quotes based on user role
        Map<String, Object> params = new HashMap<>();
        
        // Add role-based filtering
        String userRole = authManager.getUserRole();
        if (userRole != null) {
            switch (userRole.toLowerCase()) {
                case "admin":
                    ApiDebugger.logAuth("Loading all quotes for Admin", true);
                    break;
                case "manager":
                    params.put("manager_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading manager quotes for user: " + authManager.getUserId(), true);
                    break;
                case "sales":
                    params.put("created_by", authManager.getUserId());
                    ApiDebugger.logAuth("Loading sales quotes for user: " + authManager.getUserId(), true);
                    break;
                default:
                    params.put("user_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading user quotes for user: " + authManager.getUserId(), true);
                    break;
            }
        }
        
        quoteService.getAllQuotes(params, new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allQuotes.clear();
                        allQuotes.addAll(quotes);
                        filterQuotes();
                        ApiDebugger.logResponse(200, "Success", "Quotes loaded: " + quotes.size());
                    });
                }
            }
            
            @Override
            public void onSuccess(Quote quote) {}

            @Override
            public void onSuccess() {}

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (error.contains("403") || error.contains("401") || error.contains("Not authenticated")) {
                            ApiDebugger.logAuth("Authentication failed, redirecting to login", false);
                            Toast.makeText(getContext(), "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                            authManager.logout();
                        } else {
                            Toast.makeText(getContext(), "Lỗi tải báo giá: " + error, Toast.LENGTH_SHORT).show();
                        }
                        ApiDebugger.logError("loadQuotes", new Exception(error));
                    });
                }
            }
        });
    }

    @Override
    public void onQuoteClick(Quote quote) {
        onQuoteViewDetails(quote);
    }

    @Override
    public void onQuoteEdit(Quote quote) {
        android.content.Intent intent = new android.content.Intent(getContext(), com.example.financialmanagement.activities.AddEditQuoteActivity.class);
        intent.putExtra("quote_id", quote.getId());
        startActivity(intent);
    }

    @Override
    public void onQuoteDelete(Quote quote) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Xóa báo giá")
                .setMessage("Bạn có chắc chắn muốn xóa báo giá " + (quote.getQuoteNumber() != null ? quote.getQuoteNumber() : "") + "?\n\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteQuote(quote))
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onQuoteApprove(Quote quote) {
        approveQuote(quote);
    }

    @Override
    public void onQuoteConvertToInvoice(Quote quote) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Chuyển đổi báo giá thành hóa đơn")
                .setMessage("Bạn có chắc chắn muốn chuyển báo giá " + quote.getQuoteNumber() + " thành hóa đơn?")
                .setPositiveButton("Chuyển đổi", (dialog, which) -> convertQuoteToInvoice(quote))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onQuoteSendToCustomer(Quote quote) {
        Toast.makeText(getContext(), "Tính năng gửi báo giá đang được phát triển", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuoteViewDetails(Quote quote) {
        android.content.Intent intent = new android.content.Intent(getContext(), com.example.financialmanagement.activities.QuoteDetailActivity.class);
        intent.putExtra(com.example.financialmanagement.activities.QuoteDetailActivity.EXTRA_QUOTE_ID, quote.getId());
        startActivity(intent);
    }

    @Override
    public void onQuoteReview(Quote quote) {
        approveQuote(quote);
    }

    @Override
    public void onQuoteExportPDF(Quote quote) {
        Toast.makeText(getContext(), "Tính năng xuất PDF đang được phát triển", Toast.LENGTH_SHORT).show();
    }

    private void deleteQuote(Quote quote) {
        if (quote.getId() == null || quote.getId().isEmpty()) {
            Toast.makeText(getContext(), "Không thể xóa báo giá: ID không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toast.makeText(getContext(), "Đang xóa báo giá...", Toast.LENGTH_SHORT).show();
        
        quoteService.deleteQuote(quote.getId(), new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {}

            @Override
            public void onSuccess(Quote quote) {}

            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đã xóa báo giá thành công", Toast.LENGTH_SHORT).show();
                        loadQuotes();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        String errorMessage = "Lỗi xóa báo giá";
                        if (error != null && !error.isEmpty()) {
                            errorMessage += ": " + error;
                        }
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void approveQuote(Quote quote) {
        quoteService.approveQuote(quote.getId(), new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {}

            @Override
            public void onSuccess(Quote quote) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đã duyệt báo giá thành công", Toast.LENGTH_SHORT).show();
                        loadQuotes();
                    });
                }
            }

            @Override
            public void onSuccess() {}

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi duyệt báo giá: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    private void convertQuoteToInvoice(Quote quote) {
        quoteService.convertToInvoice(quote.getId(), new QuoteService.QuoteCallback() {
            @Override
            public void onSuccess(List<Quote> quotes) {}

            @Override
            public void onSuccess(Quote quote) {}

            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đã chuyển đổi báo giá thành hóa đơn thành công", Toast.LENGTH_SHORT).show();
                        loadQuotes();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi chuyển đổi báo giá: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}
