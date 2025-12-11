package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.MaterialAdjustmentRule;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import com.example.financialmanagement.utils.ErrorHandler;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;

public class ProductRuleService {

    private static final String TAG = "ProductRuleService";
    private ProductRuleApi api;

    public interface RuleCallback {
        void onSuccess(List<MaterialAdjustmentRule> rules);
        void onSuccess(MaterialAdjustmentRule rule);
        void onSuccess();
        void onError(String error);
    }

    public interface ProductRuleApi {
        @GET(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULES)
        Call<List<MaterialAdjustmentRule>> getAllRules();

        @GET(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULE_DETAIL)
        Call<MaterialAdjustmentRule> getRuleById(@Path("id") String id);

        @POST(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULES)
        Call<MaterialAdjustmentRule> createRule(@Body MaterialAdjustmentRule rule);

        @PUT(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULE_DETAIL)
        Call<MaterialAdjustmentRule> updateRule(@Path("id") String id, @Body MaterialAdjustmentRule rule);

        @DELETE(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULE_DETAIL)
        Call<Void> deleteRule(@Path("id") String id);
    }

    public ProductRuleService(Context context) {
        api = ApiClient.getRetrofit(context).create(ProductRuleApi.class);
    }

    public void getAllRules(RuleCallback callback) {
        api.getAllRules().enqueue(new Callback<List<MaterialAdjustmentRule>>() {
            @Override
            public void onResponse(Call<List<MaterialAdjustmentRule>> call, Response<List<MaterialAdjustmentRule>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(ErrorHandler.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<List<MaterialAdjustmentRule>> call, Throwable t) {
                callback.onError(ErrorHandler.parseError(t));
            }
        });
    }

    public void getRuleById(String id, RuleCallback callback) {
        api.getRuleById(id).enqueue(new Callback<MaterialAdjustmentRule>() {
            @Override
            public void onResponse(Call<MaterialAdjustmentRule> call, Response<MaterialAdjustmentRule> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(ErrorHandler.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<MaterialAdjustmentRule> call, Throwable t) {
                callback.onError(ErrorHandler.parseError(t));
            }
        });
    }

    public void createRule(MaterialAdjustmentRule rule, RuleCallback callback) {
        api.createRule(rule).enqueue(new Callback<MaterialAdjustmentRule>() {
            @Override
            public void onResponse(Call<MaterialAdjustmentRule> call, Response<MaterialAdjustmentRule> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(ErrorHandler.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<MaterialAdjustmentRule> call, Throwable t) {
                callback.onError(ErrorHandler.parseError(t));
            }
        });
    }

    public void updateRule(String id, MaterialAdjustmentRule rule, RuleCallback callback) {
        api.updateRule(id, rule).enqueue(new Callback<MaterialAdjustmentRule>() {
            @Override
            public void onResponse(Call<MaterialAdjustmentRule> call, Response<MaterialAdjustmentRule> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(ErrorHandler.parseError(response));
                }
            }

            @Override
            public void onFailure(Call<MaterialAdjustmentRule> call, Throwable t) {
                callback.onError(ErrorHandler.parseError(t));
            }
        });
    }

    public void deleteRule(String id, RuleCallback callback) {
        api.deleteRule(id).enqueue(new Callback<Void>() {
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
