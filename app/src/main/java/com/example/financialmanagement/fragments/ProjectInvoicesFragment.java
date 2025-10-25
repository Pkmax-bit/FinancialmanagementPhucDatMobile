package com.example.financialmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialmanagement.R;
import com.example.financialmanagement.adapters.InvoicesAdapter;
import com.example.financialmanagement.models.Invoice;

import java.util.ArrayList;
import java.util.List;

public class ProjectInvoicesFragment extends Fragment {
    
    private static final String ARG_PROJECT_ID = "project_id";
    
    private String projectId;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private InvoicesAdapter invoicesAdapter;
    private List<Invoice> invoices = new ArrayList<>();
    
    public static ProjectInvoicesFragment newInstance(String projectId) {
        ProjectInvoicesFragment fragment = new ProjectInvoicesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, projectId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectId = getArguments().getString(ARG_PROJECT_ID);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_invoices, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        tvEmpty = view.findViewById(R.id.tv_empty);
        
        setupRecyclerView();
        
        return view;
    }
    
    private void setupRecyclerView() {
        invoicesAdapter = new InvoicesAdapter(invoices, new InvoicesAdapter.InvoiceClickListener() {
            @Override
            public void onInvoiceClick(Invoice invoice) {
                // Handle invoice click - could open invoice detail
            }
            
            @Override
            public void onInvoiceAddPayment(Invoice invoice) {
                // Handle add payment
            }
            
            @Override
            public void onInvoiceSendToCustomer(Invoice invoice) {
                // Handle send to customer
            }
            
            @Override
            public void onInvoiceMarkAsPaid(Invoice invoice) {
                // Handle mark as paid
            }
            
            @Override
            public void onInvoiceDelete(Invoice invoice) {
                // Handle delete invoice
            }
            
            @Override
            public void onInvoiceEdit(Invoice invoice) {
                // Handle edit invoice
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(invoicesAdapter);
    }
    
    public void updateInvoices(List<Invoice> newInvoices) {
        System.out.println("ProjectInvoicesFragment: updateInvoices called with " + (newInvoices != null ? newInvoices.size() : "null") + " invoices");
        
        if (newInvoices != null) {
            invoices.clear();
            invoices.addAll(newInvoices);
        } else {
            invoices.clear();
        }
        
        System.out.println("ProjectInvoicesFragment: invoices list now has " + invoices.size() + " items");
        
        if (invoicesAdapter != null) {
            invoicesAdapter.updateInvoices(invoices);
            System.out.println("ProjectInvoicesFragment: adapter updated");
        } else {
            System.out.println("ProjectInvoicesFragment: invoicesAdapter is null!");
        }
        
        if (tvEmpty != null) {
            tvEmpty.setVisibility(invoices.isEmpty() ? View.VISIBLE : View.GONE);
            System.out.println("ProjectInvoicesFragment: empty view visibility set to " + (invoices.isEmpty() ? "VISIBLE" : "GONE"));
        } else {
            System.out.println("ProjectInvoicesFragment: tvEmpty is null!");
        }
    }
}
