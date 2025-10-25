package com.example.financialmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.CustomersAdapter;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.services.CustomerService;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.activities.LoginActivity;
import com.example.financialmanagement.activities.CustomerFormActivity;
import com.example.financialmanagement.utils.ApiDebugger;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Customers Fragment - Màn hình quản lý khách hàng
 * Bước 1 trong quy trình: Tạo khách hàng → Dự án → Báo giá → Hóa đơn → Chi phí → Báo cáo
 * Hiển thị danh sách khách hàng và cho phép thao tác CRUD
 */
public class CustomersFragment extends Fragment implements CustomersAdapter.CustomerClickListener {

    private static final int REQUEST_CODE_ADD_CUSTOMER = 1001;
    private static final int REQUEST_CODE_EDIT_CUSTOMER = 1002;

    private RecyclerView rvCustomers;
    private CustomersAdapter customersAdapter;
    private CustomerService customerService;
    private AuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customers, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        loadCustomers();
        
        return view;
    }

    private void initializeViews(View view) {
        rvCustomers = view.findViewById(R.id.rv_customers);
        customerService = new CustomerService(getContext());
        authManager = new AuthManager(getContext());
        
        // Setup FAB
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAddCustomer = 
            view.findViewById(R.id.fab_add_customer);
        fabAddCustomer.setOnClickListener(v -> addNewCustomer());
    }

    private void setupRecyclerView() {
        customersAdapter = new CustomersAdapter(new ArrayList<>(), this);
        rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustomers.setAdapter(customersAdapter);
    }

    private void loadCustomers() {
        // Check authentication first
        if (!authManager.isLoggedIn()) {
            ApiDebugger.logAuth("User not logged in, cannot load customers", false);
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem khách hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Debug authentication state
        String token = authManager.getAccessToken();
        String userId = authManager.getUserId();
        String userRole = authManager.getUserRole();
        
        ApiDebugger.logAuth("=== CUSTOMER LOAD DEBUG ===", true);
        ApiDebugger.logAuth("Token available: " + (token != null && !token.isEmpty()), true);
        ApiDebugger.logAuth("User ID: " + userId, true);
        ApiDebugger.logAuth("User Role: " + userRole, true);
        ApiDebugger.logAuth("Is Logged In: " + authManager.isLoggedIn(), true);
        
        // Load customers based on user role
        Map<String, Object> params = new HashMap<>();
        
        // Add role-based filtering
        if (userRole != null) {
            switch (userRole.toLowerCase()) {
                case "admin":
                    // Admin can see all customers
                    ApiDebugger.logAuth("Loading all customers for Admin", true);
                    break;
                case "manager":
                    // Manager can see customers for their projects
                    params.put("manager_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading manager customers for user: " + authManager.getUserId(), true);
                    break;
                case "sales":
                    // Sales can see customers they created
                    params.put("created_by", authManager.getUserId());
                    ApiDebugger.logAuth("Loading sales customers for user: " + authManager.getUserId(), true);
                    break;
                default:
                    // Default: show customers for user's projects
                    params.put("user_id", authManager.getUserId());
                    ApiDebugger.logAuth("Loading user customers for user: " + authManager.getUserId(), true);
                    break;
            }
        }
        
        customerService.getAllCustomers(params, new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        customersAdapter.updateCustomers(customers);
                        ApiDebugger.logResponse(200, "Success", "Customers loaded: " + customers.size());
                        
                        // Show role-based message
                        String roleMessage = getRoleBasedMessage(userRole, customers.size());
                        if (roleMessage != null) {
                            Toast.makeText(getContext(), roleMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            
            @Override
            public void onSuccess(Customer customer) {
                // Not used in this context
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Check if it's an authentication error
                        if (error.contains("403") || error.contains("401") || error.contains("Not authenticated")) {
                            ApiDebugger.logAuth("Authentication failed, trying public endpoint", false);
                            
                            // Try to load public customers as fallback first
                            Toast.makeText(getContext(), "Không có quyền truy cập. Đang tải dữ liệu công khai...", Toast.LENGTH_LONG).show();
                            loadPublicCustomers();
                            
                            // If public customers also fail, then redirect to login
                            // This will be handled in loadPublicCustomers if it fails
                        } else if (error.contains("Lỗi kết nối") || error.contains("Connection")) {
                            // Network connection error
                            Toast.makeText(getContext(), "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.", Toast.LENGTH_LONG).show();
                            
                            // Try to load public customers as fallback
                            loadPublicCustomers();
                        } else {
                            Toast.makeText(getContext(), "Lỗi tải khách hàng: " + error, Toast.LENGTH_SHORT).show();
                        }
                        
                        ApiDebugger.logError("loadCustomers", new Exception(error));
                    });
                }
            }
        });
    }
    
    private String getRoleBasedMessage(String userRole, int customerCount) {
        if (userRole == null) return null;
        
        switch (userRole.toLowerCase()) {
            case "admin":
                return "Admin: Đã tải " + customerCount + " khách hàng (tất cả)";
            case "manager":
                return "Manager: Đã tải " + customerCount + " khách hàng (dự án quản lý)";
            case "sales":
                return "Sales: Đã tải " + customerCount + " khách hàng (đã tạo)";
            default:
                return "Đã tải " + customerCount + " khách hàng";
        }
    }

    @Override
    public void onCustomerClick(Customer customer) {
        // Navigate to customer detail
        // TODO: Implement navigation to customer detail activity
        Toast.makeText(getContext(), "Xem chi tiết khách hàng: " + customer.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCustomerEdit(Customer customer) {
        // Navigate to edit customer
        Intent intent = new Intent(getContext(), CustomerFormActivity.class);
        intent.putExtra(CustomerFormActivity.EXTRA_MODE, CustomerFormActivity.MODE_EDIT);
        intent.putExtra(CustomerFormActivity.EXTRA_CUSTOMER, customer);
        startActivityForResult(intent, REQUEST_CODE_EDIT_CUSTOMER);
    }

    @Override
    public void onCustomerDelete(Customer customer) {
        // Show delete confirmation dialog
        showDeleteConfirmationDialog(customer);
    }

    @Override
    public void onCustomerCreateProject(Customer customer) {
        // Navigate to create project for this customer
        createProjectForCustomer(customer);
    }
    
    /**
     * Create project for specific customer
     */
    private void createProjectForCustomer(Customer customer) {
        // For now, show a toast message with customer info
        // TODO: Navigate to ProjectFormActivity with customer pre-selected
        Toast.makeText(getContext(), 
            "Tạo dự án cho khách hàng: " + customer.getName() + 
            " (" + customer.getCustomerCode() + ")", 
            Toast.LENGTH_LONG).show();
        
        // Future implementation:
        // Intent intent = new Intent(getContext(), ProjectFormActivity.class);
        // intent.putExtra(ProjectFormActivity.EXTRA_CUSTOMER, customer);
        // startActivityForResult(intent, REQUEST_CODE_ADD_PROJECT);
    }
    
    /**
     * Load public customers as fallback when authentication fails
     */
    private void loadPublicCustomers() {
        if (customerService == null) {
            customerService = new CustomerService(getContext());
        }
        
        ApiDebugger.logAuth("Trying to load public customers as fallback", true);
        
        customerService.getCustomersPublic(new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (customers != null && !customers.isEmpty()) {
                            // Update the adapter with new data
                            customersAdapter.updateCustomers(customers);
                            Toast.makeText(getContext(), "Đã tải danh sách khách hàng công khai", Toast.LENGTH_SHORT).show();
                            ApiDebugger.logAuth("Successfully loaded " + customers.size() + " public customers", true);
                        } else {
                            Toast.makeText(getContext(), "Không có dữ liệu khách hàng", Toast.LENGTH_SHORT).show();
                            ApiDebugger.logAuth("No public customers available", false);
                        }
                    });
                }
            }
            
            @Override
            public void onSuccess(Customer customer) {
                // Not used in this context
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        ApiDebugger.logAuth("Public customers also failed: " + error, false);
                        
                        // If even public customers fail, it might be a server issue
                        if (error.contains("403") || error.contains("401")) {
                            // Authentication issue - redirect to login
                            Toast.makeText(getContext(), "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                            authManager.logout();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Không thể tải danh sách khách hàng: " + error, Toast.LENGTH_SHORT).show();
                            // As a last resort, load sample data
                            loadSampleCustomers();
                        }
                    });
                }
            }
        });
    }
    
    /**
     * Load sample customers as last resort
     */
    private void loadSampleCustomers() {
        ApiDebugger.logAuth("Loading sample customers as last resort", true);
        
        List<Customer> sampleCustomers = new ArrayList<>();
        
        // Create sample customers
        Customer customer1 = new Customer("CUST001", "Công ty ABC", "company");
        customer1.setEmail("contact@abc.com");
        customer1.setPhone("0123456789");
        customer1.setAddress("123 Đường ABC, Quận 1");
        customer1.setCity("TP.HCM");
        customer1.setStatus("active");
        sampleCustomers.add(customer1);
        
        Customer customer2 = new Customer("CUST002", "Nguyễn Văn A", "individual");
        customer2.setEmail("nguyenvana@email.com");
        customer2.setPhone("0987654321");
        customer2.setAddress("456 Đường XYZ, Quận 2");
        customer2.setCity("TP.HCM");
        customer2.setStatus("active");
        sampleCustomers.add(customer2);
        
        Customer customer3 = new Customer("CUST003", "Cơ quan DEF", "government");
        customer3.setEmail("info@def.gov.vn");
        customer3.setPhone("0245678901");
        customer3.setAddress("789 Đường DEF, Quận 3");
        customer3.setCity("TP.HCM");
        customer3.setStatus("active");
        sampleCustomers.add(customer3);
        
        // Update adapter with sample data
        customersAdapter.updateCustomers(sampleCustomers);
        Toast.makeText(getContext(), "Đã tải dữ liệu mẫu (" + sampleCustomers.size() + " khách hàng)", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show delete confirmation dialog
     */
    private void showDeleteConfirmationDialog(Customer customer) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa khách hàng")
                .setMessage("Bạn có chắc chắn muốn xóa khách hàng \"" + customer.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteCustomer(customer);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Delete customer
     */
    private void deleteCustomer(Customer customer) {
        if (customerService == null) {
            customerService = new CustomerService(getContext());
        }
        
        customerService.deleteCustomer(customer.getId(), new CustomerService.CustomerCallback() {
            @Override
            public void onSuccess(List<Customer> customers) {
                // Not used
            }
            
            @Override
            public void onSuccess(Customer customer) {
                // Not used
            }
            
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Xóa khách hàng thành công", Toast.LENGTH_SHORT).show();
                        // Reload customers list
                        loadCustomers();
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Lỗi xóa khách hàng: " + error, Toast.LENGTH_LONG).show();
                        ApiDebugger.logError("deleteCustomer", new Exception(error));
                    });
                }
            }
        });
    }
    
    /**
     * Handle activity results
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD_CUSTOMER || requestCode == REQUEST_CODE_EDIT_CUSTOMER) {
                // Reload customers list after add/edit
                loadCustomers();
            }
        }
    }
    
    /**
     * Add new customer (called from FAB or menu)
     */
    public void addNewCustomer() {
        Intent intent = new Intent(getContext(), CustomerFormActivity.class);
        intent.putExtra(CustomerFormActivity.EXTRA_MODE, CustomerFormActivity.MODE_CREATE);
        startActivityForResult(intent, REQUEST_CODE_ADD_CUSTOMER);
    }
}
