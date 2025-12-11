package com.example.financialmanagement.network.api;

import com.example.financialmanagement.models.ProductCategory;
import com.example.financialmanagement.network.NetworkConfig;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductCategoryApi {
    @GET(NetworkConfig.Endpoints.PRODUCT_CATEGORIES)
    Call<List<ProductCategory>> getAllCategories();

    @GET(NetworkConfig.Endpoints.PRODUCT_CATEGORIES + "/{id}")
    Call<ProductCategory> getCategoryById(@Path("id") String id);

    @POST(NetworkConfig.Endpoints.PRODUCT_CATEGORIES)
    Call<ProductCategory> createCategory(@Body ProductCategory category);

    @PUT(NetworkConfig.Endpoints.PRODUCT_CATEGORIES + "/{id}")
    Call<ProductCategory> updateCategory(@Path("id") String id, @Body ProductCategory category);

    @DELETE(NetworkConfig.Endpoints.PRODUCT_CATEGORIES + "/{id}")
    Call<Void> deleteCategory(@Path("id") String id);
}
