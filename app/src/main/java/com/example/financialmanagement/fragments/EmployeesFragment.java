package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.EmployeeAdapter;
import com.example.financialmanagement.models.Employee;
import com.example.financialmanagement.services.EmployeeService;
import java.util.List;

public class EmployeesFragment extends Fragment {

    private RecyclerView recyclerView;
    private EmployeeService employeeService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employees, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view_employees);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        employeeService = new EmployeeService(getContext());
        loadEmployees();
        
        return view;
    }

    private void loadEmployees() {
        employeeService.getEmployees(new EmployeeService.EmployeeCallback<List<Employee>>() {
            @Override
            public void onSuccess(List<Employee> result) {
                if (getActivity() != null) {
                    EmployeeAdapter adapter = new EmployeeAdapter(requireActivity(), result);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
