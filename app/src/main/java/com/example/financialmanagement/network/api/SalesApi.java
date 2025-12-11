package com.example.financialmanagement.network.api;

import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.models.Invoice;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.models.ProjectListResponse;
import com.example.financialmanagement.models.Quote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SalesApi {
    @GET("customers")
    Call<List<Customer>> getCustomers();

    @POST("sales/quotes")
    Call<Quote> createQuote(@Body Quote quote);

    @GET("sales/invoices")
    Call<List<Invoice>> getInvoices();

    @POST("sales/invoices")
    Call<Invoice> createInvoice(@Body Invoice invoice);

    @GET("sales/projects/by-customer/{customerId}")
    Call<ProjectListResponse> getProjectsByCustomer(@Path("customerId") String customerId);
}
