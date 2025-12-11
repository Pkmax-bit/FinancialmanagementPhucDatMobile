package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.ProductCategory;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import com.example.financialmanagement.utils.ErrorHandler;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;

public class ProductCategoryService {

    private static final String TAG = "ProductCategoryService";
    private ProductCategoryApi api;

    public interface CategoryCallback {
        void onSuccess(List<ProductCategory> categories);
        void onSuccess(ProductCategory category);
        void onSuccess();
        void onError(String error);
    }

    public interface ProductCategoryApi {
        @GET(NetworkConfig.Endpoints.PRODUCT_CATEGORIES)
        Call<List<ProductCategory>> getAllCategories();

        @GET(NetworkConfig.Endpoints.PRODUCT_CATEGORY_DETAIL)
        Call<ProductCategory> getCategoryById(@Path("id") String id);

        @POST(NetworkConfig.Endpoints.PRODUCT_CATEGORIES)
        Call<ProductCategory> createCategory(@Body ProductCategory category);

        @PUT(NetworkConfig.Endpoints.PRODUCT_CATEGORY_DETAIL)
        Call<ProductCategory> updateCategory(@Path("id") String id, @Body ProductCategory category);

        @DELETE(NetworkConfig.Endpoints.PRODUCT_CATEGORY_DETAIL)
        Call<Void> deleteCategory(@Path("id") String id);
    }

    public ProductCategoryService(Context context) {
        api = ApiClient.getRetrofit(context).create(ProductCategoryApi.class);
    }

    public void getAllCategories(CategoryCallback callback) {
        api.getAllCategories().enqueue(new Callback<List<ProductCategory>>() {
            @Override
            public void onResponse(Call<List<ProductCategory>> call, Response<List<ProductCategory>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(ErrorHandler.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<List<ProductCategory>> call, Throwable t) {
                callback.onError(ErrorHandler.parseError(t));
            }
        });
    }

    public void getCategoryById(String id, CategoryCallback callback) {
        api.getCategoryById(id).enqueue(new Callback<ProductCategory>() {
            @Override
            public void onResponse(Call<ProductCategory> call, Response<ProductCategory> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(ErrorHandler.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<ProductCategory> call, Throwable t) {
                callback.onError(ErrorHandler.parseError(t));
            }
        });
    }

    public void createCategory(ProductCategory category, CategoryCallback callback) {
        api.createCategory(category).enqueue(new Callback<ProductCategory>() {
            @Override
            public void onResponse(Call<ProductCategory> call, Response<ProductCategory> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(ErrorHandler.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<ProductCategory> call, Throwable t) {
                callback.onError(ErrorHandler.parseError(t));
            }
        });
    }

    public void updateCategory(String id, ProductCategory category, CategoryCallback callback) {
        api.updateCategory(id, category).enqueue(new Callback<ProductCategory>() {
            @Override
            public void onResponse(Call<ProductCategory> call, Response<ProductCategory> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(ErrorHandler.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<ProductCategory> call, Throwable t) {
                callback.onError(ErrorHandler.parseError(t));
            }
        });
    }

    public void deleteCategory(String id, CategoryCallback callback) {
        api.deleteCategory(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(ErrorHandler.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(ErrorHandler.parseError(t));
            }
        });
    }
}
