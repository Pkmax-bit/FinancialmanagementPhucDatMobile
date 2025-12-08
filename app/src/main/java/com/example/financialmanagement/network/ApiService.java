package com.example.financialmanagement.network;

import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.models.Project;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiService {
    
    // Projects
    @GET("projects")
    Call<List<Project>> getProjects(@QueryMap Map<String, Object> options);

    @GET("projects/{id}")
    Call<Project> getProject(@Path("id") String id);

    @POST("projects")
    Call<Project> createProject(@Body Project project);

    @PUT("projects/{id}")
    Call<Project> updateProject(@Path("id") String id, @Body Project project);

    @DELETE("projects/{id}")
    Call<Void> deleteProject(@Path("id") String id);

    // Customers
    @GET("customers")
    Call<List<Customer>> getCustomers(@QueryMap Map<String, Object> options);
    
    @GET("customers/public-list")
    Call<List<Customer>> getPublicCustomers();

    @GET("customers/{id}")
    Call<Customer> getCustomer(@Path("id") String id);

    @POST("customers")
    Call<Customer> createCustomer(@Body Customer customer);

    @PUT("customers/{id}")
    Call<Customer> updateCustomer(@Path("id") String id, @Body Customer customer);

    @DELETE("customers/{id}")
    Call<Void> deleteCustomer(@Path("id") String id);
}
