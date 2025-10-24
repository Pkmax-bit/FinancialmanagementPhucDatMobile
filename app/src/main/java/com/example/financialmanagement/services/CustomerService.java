package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

/**
 * Customer Service - Service quản lý khách hàng
 * Xử lý các API calls liên quan đến khách hàng
 */
public class CustomerService {
    
    private Context context;
    private CustomerApi customerApi;
    
    public CustomerService(Context context) {
        this.context = context;
        this.customerApi = ApiClient.getRetrofit(context).create(CustomerApi.class);
    }
    
    /**
     * Lấy tất cả khách hàng
     */
    public void getAllCustomers(CustomerCallback callback) {
        getAllCustomers(null, callback);
    }
    
    /**
     * Lấy khách hàng với parameters
     */
    public void getAllCustomers(Map<String, Object> params, CustomerCallback callback) {
        Call<List<Customer>> call = customerApi.getCustomers(params);
        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải danh sách khách hàng: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Lấy khách hàng theo ID
     */
    public void getCustomer(String customerId, CustomerCallback callback) {
        Call<Customer> call = customerApi.getCustomer(customerId);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải khách hàng: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Tạo khách hàng mới
     */
    public void createCustomer(Customer customer, CustomerCallback callback) {
        Call<Customer> call = customerApi.createCustomer(customer);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tạo khách hàng: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Cập nhật khách hàng
     */
    public void updateCustomer(String customerId, Customer customer, CustomerCallback callback) {
        Call<Customer> call = customerApi.updateCustomer(customerId, customer);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi cập nhật khách hàng: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Xóa khách hàng
     */
    public void deleteCustomer(String customerId, CustomerCallback callback) {
        Call<Void> call = customerApi.deleteCustomer(customerId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Lỗi xóa khách hàng: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Lấy khách hàng công khai (không cần auth)
     */
    public void getCustomersPublic(CustomerCallback callback) {
        Call<List<Customer>> call = customerApi.getCustomersPublic();
        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải danh sách khách hàng: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Customer API Interface
     */
    public interface CustomerApi {
        @GET(NetworkConfig.Endpoints.CUSTOMERS)
        Call<List<Customer>> getCustomers(@QueryMap Map<String, Object> params);
        
        @GET(NetworkConfig.Endpoints.CUSTOMER_DETAIL)
        Call<Customer> getCustomer(@Path("id") String customerId);
        
        @POST(NetworkConfig.Endpoints.CUSTOMERS)
        Call<Customer> createCustomer(@Body Customer customer);
        
        @PUT(NetworkConfig.Endpoints.CUSTOMER_DETAIL)
        Call<Customer> updateCustomer(@Path("id") String customerId, @Body Customer customer);
        
        @DELETE(NetworkConfig.Endpoints.CUSTOMER_DETAIL)
        Call<Void> deleteCustomer(@Path("id") String customerId);
        
        @GET(NetworkConfig.Endpoints.CUSTOMERS_PUBLIC)
        Call<List<Customer>> getCustomersPublic();
    }
    
    /**
     * Customer Callback Interface
     */
    public interface CustomerCallback {
        void onSuccess(List<Customer> customers);
        void onSuccess(Customer customer);
        void onError(String error);
        
        // Default implementations to avoid ambiguous calls
        default void onSuccess() {
            onSuccess((List<Customer>) null);
        }
    }
}
