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
import com.example.financialmanagement.adapters.QuotesAdapter;
import com.example.financialmanagement.models.Quote;

import java.util.ArrayList;
import java.util.List;

public class ProjectQuotesFragment extends Fragment {
    
    private static final String ARG_PROJECT_ID = "project_id";
    
    private String projectId;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private QuotesAdapter quotesAdapter;
    private List<Quote> quotes = new ArrayList<>();
    
    public static ProjectQuotesFragment newInstance(String projectId) {
        ProjectQuotesFragment fragment = new ProjectQuotesFragment();
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
        View view = inflater.inflate(R.layout.fragment_project_quotes, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        tvEmpty = view.findViewById(R.id.tv_empty);
        
        setupRecyclerView();
        
        return view;
    }
    
    private void setupRecyclerView() {
        quotesAdapter = new QuotesAdapter(quotes, new QuotesAdapter.QuoteClickListener() {
            @Override
            public void onQuoteClick(Quote quote) {
                // Handle quote click - could open quote detail
            }
            
            @Override
            public void onQuoteSendToCustomer(Quote quote) {
                // Handle send to customer
            }
            
            @Override
            public void onQuoteConvertToInvoice(Quote quote) {
                // Handle convert to invoice
            }
            
            @Override
            public void onQuoteApprove(Quote quote) {
                // Handle approve quote
            }
            
            @Override
            public void onQuoteDelete(Quote quote) {
                // Handle delete quote
            }
            
            @Override
            public void onQuoteEdit(Quote quote) {
                // Handle edit quote
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(quotesAdapter);
    }
    
    public void updateQuotes(List<Quote> newQuotes) {
        System.out.println("ProjectQuotesFragment: updateQuotes called with " + (newQuotes != null ? newQuotes.size() : "null") + " quotes");
        
        if (newQuotes != null) {
            quotes.clear();
            quotes.addAll(newQuotes);
        } else {
            quotes.clear();
        }
        
        System.out.println("ProjectQuotesFragment: quotes list now has " + quotes.size() + " items");
        
        if (quotesAdapter != null) {
            quotesAdapter.updateQuotes(quotes);
            System.out.println("ProjectQuotesFragment: adapter updated");
        } else {
            System.out.println("ProjectQuotesFragment: quotesAdapter is null!");
        }
        
        if (tvEmpty != null) {
            tvEmpty.setVisibility(quotes.isEmpty() ? View.VISIBLE : View.GONE);
            System.out.println("ProjectQuotesFragment: empty view visibility set to " + (quotes.isEmpty() ? "VISIBLE" : "GONE"));
        } else {
            System.out.println("ProjectQuotesFragment: tvEmpty is null!");
        }
    }
}
