package com.example.financialmanagement.network.api;

import com.example.financialmanagement.models.MaterialAdjustmentRule;
import com.example.financialmanagement.network.NetworkConfig;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductRuleApi {
    @GET(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULES)
    Call<List<MaterialAdjustmentRule>> getAllRules();

    @GET(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULES + "/{id}")
    Call<MaterialAdjustmentRule> getRuleById(@Path("id") String id);

    @POST(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULES)
    Call<MaterialAdjustmentRule> createRule(@Body MaterialAdjustmentRule rule);

    @PUT(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULES + "/{id}")
    Call<MaterialAdjustmentRule> updateRule(@Path("id") String id, @Body MaterialAdjustmentRule rule);

    @DELETE(NetworkConfig.Endpoints.MATERIAL_ADJUSTMENT_RULES + "/{id}")
    Call<Void> deleteRule(@Path("id") String id);
}
