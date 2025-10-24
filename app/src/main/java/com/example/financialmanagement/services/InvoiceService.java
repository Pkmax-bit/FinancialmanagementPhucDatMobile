package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Invoice Service - API service cho quản lý hóa đơn
 * Tương ứng với API endpoint /api/invoices
 */
public class InvoiceService {
    
    private InvoiceApi invoiceApi;
    
    public interface InvoiceCallback {
        void onSuccess(List<Invoice> invoices);
        void onSuccess(Invoice invoice);
        void onSuccess();
        void onError(String error);
    }
    
    public interface InvoiceApi {
        Call<List<Invoice>> getAllInvoices(Map<String, Object> params);
        Call<Invoice> getInvoiceById(String id);
        Call<Invoice> createInvoice(Invoice invoice);
        Call<Invoice> updateInvoice(String id, Invoice invoice);
        Call<Void> deleteInvoice(String id);
        Call<Invoice> markAsPaid(String id);
        Call<Invoice> sendToCustomer(String id);
        Call<Invoice> addPayment(String id, Invoice.Payment payment);
    }
    
    public InvoiceService(Context context) {
        invoiceApi = ApiClient.getRetrofit(context).create(InvoiceApi.class);
    }
    
    public void getAllInvoices(Map<String, Object> params, InvoiceCallback callback) {
        Call<List<Invoice>> call = invoiceApi.getAllInvoices(params);
        call.enqueue(new Callback<List<Invoice>>() {
            @Override
            public void onResponse(Call<List<Invoice>> call, Response<List<Invoice>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải hóa đơn: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<Invoice>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void getInvoiceById(String id, InvoiceCallback callback) {
        Call<Invoice> call = invoiceApi.getInvoiceById(id);
        call.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải hóa đơn: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void createInvoice(Invoice invoice, InvoiceCallback callback) {
        Call<Invoice> call = invoiceApi.createInvoice(invoice);
        call.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tạo hóa đơn: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void updateInvoice(String id, Invoice invoice, InvoiceCallback callback) {
        Call<Invoice> call = invoiceApi.updateInvoice(id, invoice);
        call.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi cập nhật hóa đơn: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void deleteInvoice(String id, InvoiceCallback callback) {
        Call<Void> call = invoiceApi.deleteInvoice(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Lỗi xóa hóa đơn: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void markAsPaid(String id, InvoiceCallback callback) {
        Call<Invoice> call = invoiceApi.markAsPaid(id);
        call.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi đánh dấu đã thanh toán: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void sendToCustomer(String id, InvoiceCallback callback) {
        Call<Invoice> call = invoiceApi.sendToCustomer(id);
        call.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi gửi hóa đơn: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    public void addPayment(String id, Invoice.Payment payment, InvoiceCallback callback) {
        Call<Invoice> call = invoiceApi.addPayment(id, payment);
        call.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi thêm thanh toán: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
