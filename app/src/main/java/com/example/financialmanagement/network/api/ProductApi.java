package com.example.financialmanagement.network.api;

import com.example.financialmanagement.models.Product;
import com.example.financialmanagement.network.NetworkConfig;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductApi {
    @GET(NetworkConfig.Endpoints.PRODUCTS)
    Call<List<Product>> getAllProducts(@QueryMap Map<String, Object> params);
    
    @GET(NetworkConfig.Endpoints.PRODUCT_DETAIL)
    Call<Product> getProductById(@Path("id") String id);
    
    @POST(NetworkConfig.Endpoints.PRODUCTS)
    Call<Product> createProduct(@Body Product product);
    
    @PUT(NetworkConfig.Endpoints.PRODUCT_DETAIL)
    Call<Product> updateProduct(@Path("id") String id, @Body Product product);
    
    @DELETE(NetworkConfig.Endpoints.PRODUCT_DETAIL)
    Call<Void> deleteProduct(@Path("id") String id);
}
