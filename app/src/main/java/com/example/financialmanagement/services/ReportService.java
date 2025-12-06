package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;
import java.util.Map;

public class ReportService {
    private ReportApi reportApi;

    public ReportService(Context context) {
        this.reportApi = ApiClient.getRetrofit(context).create(ReportApi.class);
    }

    public void getBalanceSheet(final ReportCallback<Map<String, Object>> callback) {
        Call<Map<String, Object>> call = reportApi.getBalanceSheet();
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public interface ReportApi {
        @GET(NetworkConfig.Endpoints.REPORTS_BALANCE_SHEET)
        Call<Map<String, Object>> getBalanceSheet();
    }

    public interface ReportCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
