package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Product;
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
 * Product Service - API service cho quản lý sản phẩm
 * Tương ứng với API endpoint /api/sales/products
 */
public class ProductService {
    
    private static final String TAG = "ProductService";
    private ProductApi productApi;
    
    public interface ProductCallback {
        void onSuccess(List<Product> products);
        void onSuccess(Product product);
        void onSuccess();
        void onError(String error);
    }
    
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
    
    public ProductService(Context context) {
        productApi = ApiClient.getRetrofit(context).create(ProductApi.class);
    }
    
    public void getAllProducts(Map<String, Object> params, ProductCallback callback) {
        Call<List<Product>> call = productApi.getAllProducts(params);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "getAllProducts", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "getAllProducts", t);
                callback.onError(error);
            }
        });
    }
    
    public void getProductById(String id, ProductCallback callback) {
        Call<Product> call = productApi.getProductById(id);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "getProductById", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "getProductById", t);
                callback.onError(error);
            }
        });
    }
    
    public void createProduct(Product product, ProductCallback callback) {
        Call<Product> call = productApi.createProduct(product);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "createProduct", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "createProduct", t);
                callback.onError(error);
            }
        });
    }

    public void updateProduct(String id, Product product, ProductCallback callback) {
        Call<Product> call = productApi.updateProduct(id, product);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "updateProduct", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "updateProduct", t);
                callback.onError(error);
            }
        });
    }

    public void deleteProduct(String id, ProductCallback callback) {
        Call<Void> call = productApi.deleteProduct(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    String error = ErrorHandler.parseError(response);
                    ErrorHandler.logError(TAG, "deleteProduct", response);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                ErrorHandler.logError(TAG, "deleteProduct", t);
                callback.onError(error);
            }
        });
    }
}
