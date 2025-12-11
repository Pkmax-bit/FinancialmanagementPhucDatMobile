package com.example.financialmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.CustomersAdapter;
import com.example.financialmanagement.models.Customer;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.api.SalesApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerPickerActivity extends AppCompatActivity implements CustomersAdapter.CustomerClickListener {

    public static final String EXTRA_CUSTOMER_ID = "extra_customer_id";
    public static final String EXTRA_CUSTOMER_NAME = "extra_customer_name";

    private RecyclerView rvCustomers;
    private CustomersAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private EditText etSearch;
    private List<Customer> allCustomers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_picker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvCustomers = findViewById(R.id.rv_customers);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tv_empty);
        etSearch = findViewById(R.id.et_search);

        rvCustomers.setLayoutManager(new LinearLayoutManager(this));
        // Fix constructor: only list and listener
        adapter = new CustomersAdapter(new ArrayList<>(), this);
        rvCustomers.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadCustomers();
    }

    private void loadCustomers() {
        progressBar.setVisibility(View.VISIBLE);
        SalesApi salesApi = ApiClient.getSalesApi(this);
        Call<List<Customer>>call = salesApi.getCustomers();

        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allCustomers = response.body();
                    updateList(allCustomers);
                } else {
                    Toast.makeText(CustomerPickerActivity.this, "Không thể tải danh sách khách hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerPickerActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCustomers(String query) {
        List<Customer> filteredList = new ArrayList<>();
        for (Customer customer : allCustomers) {
            String name = customer.getName() != null ? customer.getName().toLowerCase() : "";
            String code = customer.getCustomerCode() != null ? customer.getCustomerCode().toLowerCase() : "";
            String phone = customer.getPhone() != null ? customer.getPhone() : "";
            
            if (name.contains(query.toLowerCase()) || code.contains(query.toLowerCase()) || phone.contains(query)) {
                filteredList.add(customer);
            }
        }
        updateList(filteredList);
    }

    private void updateList(List<Customer> customers) {
        adapter.updateCustomers(customers);
        if (customers.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvCustomers.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvCustomers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCustomerClick(Customer customer) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_CUSTOMER_ID, customer.getId());
        resultIntent.putExtra(EXTRA_CUSTOMER_NAME, customer.getName());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onCustomerEdit(Customer customer) {
        // Not used in picker
    }

    @Override
    public void onCustomerDelete(Customer customer) {
        // Not used in picker
    }

    @Override
    public void onCustomerCreateProject(Customer customer) {
        // Not used in picker
    }
}
