package com.example.financialmanagement.services;

import android.content.Context;
import android.util.Log;
import com.example.financialmanagement.models.Employee;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public class EmployeeService {
    private static final String TAG = "EmployeeService";
    private EmployeeApi employeeApi;

    public EmployeeService(Context context) {
        this.employeeApi = ApiClient.getRetrofit(context).create(EmployeeApi.class);
    }

    public void getEmployees(final EmployeeCallback<List<Employee>> callback) {
        Call<List<Employee>> call = employeeApi.getEmployees();
        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void getEmployee(String id, final EmployeeCallback<Employee> callback) {
        Call<Employee> call = employeeApi.getEmployee(id);
        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public interface EmployeeApi {
        @GET(NetworkConfig.Endpoints.EMPLOYEES)
        Call<List<Employee>> getEmployees();

        @GET(NetworkConfig.Endpoints.EMPLOYEE_DETAIL)
        Call<Employee> getEmployee(@Path("id") String id);
    }

    public interface EmployeeCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
