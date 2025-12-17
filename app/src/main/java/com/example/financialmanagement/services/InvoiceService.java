package com.example.financialmanagement.services;

import android.content.Context;
import android.util.Log;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import com.example.financialmanagement.utils.ErrorHandler;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;

/**
 * Invoice Service - API service cho quản lý hóa đơn
 * Tương ứng với API endpoint /api/invoices
 */
public class InvoiceService {
    
    private static final String TAG = "InvoiceService";
    private InvoiceApi invoiceApi;
    
    public interface InvoiceCallback {
        void onSuccess(List<Invoice> invoices);
        void onSuccess(Invoice invoice);
        void onSuccess();
        void onError(String error);
    }
    
    public interface InvoiceApi {
        @GET(NetworkConfig.Endpoints.INVOICES)
        Call<List<Invoice>> getAllInvoices(@QueryMap Map<String, Object> params);
        
        @GET(NetworkConfig.Endpoints.INVOICES + "/{id}")
        Call<Invoice> getInvoiceById(@Path("id") String id);
        
        @POST(NetworkConfig.Endpoints.INVOICES)
        Call<Invoice> createInvoice(@Body Invoice invoice);
        
        @PUT(NetworkConfig.Endpoints.INVOICES + "/{id}")
        Call<Invoice> updateInvoice(@Path("id") String id, @Body Invoice invoice);
        
        @DELETE(NetworkConfig.Endpoints.INVOICES + "/{id}")
        Call<Void> deleteInvoice(@Path("id") String id);
        
        @POST(NetworkConfig.Endpoints.INVOICES + "/{id}/mark-paid")
        Call<Invoice> markAsPaid(@Path("id") String id);
        
        @POST(NetworkConfig.Endpoints.INVOICES + "/{id}/send")
        Call<Invoice> sendToCustomer(@Path("id") String id);
        
        @POST(NetworkConfig.Endpoints.INVOICES + "/{id}/payment")
        Call<Invoice> addPayment(@Path("id") String id, @Body Invoice.Payment payment);
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
                    List<Invoice> invoices = response.body();
                    Log.d(TAG, "getAllInvoices success - Total invoices: " + (invoices != null ? invoices.size() : 0));
                    if (invoices != null && !invoices.isEmpty()) {
                        // Log first few invoices for debugging
                        for (int i = 0; i < Math.min(3, invoices.size()); i++) {
                            Invoice inv = invoices.get(i);
                            String customerName = inv.getCustomer() != null ? inv.getCustomer().getName() : "N/A";
                            String projectName = inv.getProject() != null ? inv.getProject().getName() : "N/A";
                            Log.d(TAG, String.format("Invoice %d: ID=%s, Number=%s, Customer=%s, Project=%s", 
                                i+1, inv.getId(), inv.getInvoiceNumber(), customerName, projectName));
                        }
                    }
                    callback.onSuccess(invoices);
                } else {
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "getAllInvoices", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<List<Invoice>> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "getAllInvoices", t);
                callback.onError(error);
            }
        });
    }
    
    public void getInvoiceById(String id, InvoiceCallback callback) {
        Call<Invoice> call = invoiceApi.getInvoiceById(id);
        call.enqueue(new Callback<Invoice>() {
            @Override
            public void onResponse(Call<Invoice> call, Response<Invoice> response) {
                if (response.isSuccessful()) {
                    Invoice invoice = response.body();
                    if (invoice != null) {
                        String customerName = invoice.getCustomer() != null ? invoice.getCustomer().getName() : "N/A";
                        String projectName = invoice.getProject() != null ? invoice.getProject().getName() : "N/A";
                        Log.d(TAG, String.format("getInvoiceById success: ID=%s, Number=%s, Customer=%s, Project=%s, Items=%d", 
                            invoice.getId(), invoice.getInvoiceNumber(), customerName, projectName,
                            invoice.getItems() != null ? invoice.getItems().size() : 0));
                    }
                    callback.onSuccess(invoice);
                } else {
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "getInvoiceById", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "getInvoiceById", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "createInvoice", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "createInvoice", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "updateInvoice", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "updateInvoice", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "deleteInvoice", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "deleteInvoice", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "markAsPaid", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "markAsPaid", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "sendToCustomer", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "sendToCustomer", t);
                callback.onError(error);
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
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "addPayment", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Invoice> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "addPayment", t);
                callback.onError(error);
            }
        });
    }
}
