package com.example.financialmanagement.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.financialmanagement.fragments.ProjectQuotesFragment;
import com.example.financialmanagement.fragments.ProjectInvoicesFragment;
import com.example.financialmanagement.fragments.ProjectExpensesFragment;
import com.example.financialmanagement.fragments.ProjectOverviewFragment;

public class ProjectDetailPagerAdapter extends FragmentStateAdapter {
    
    private String projectId;
    private ProjectQuotesFragment quotesFragment;
    private ProjectInvoicesFragment invoicesFragment;
    private ProjectExpensesFragment expensesFragment;
    private ProjectOverviewFragment overviewFragment;

    public ProjectDetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, String projectId) {
        super(fragmentActivity);
        this.projectId = projectId;
        
        // Initialize fragments
        quotesFragment = ProjectQuotesFragment.newInstance(projectId);
        invoicesFragment = ProjectInvoicesFragment.newInstance(projectId);
        expensesFragment = ProjectExpensesFragment.newInstance(projectId);
        overviewFragment = ProjectOverviewFragment.newInstance(projectId);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return quotesFragment;
            case 1:
                return invoicesFragment;
            case 2:
                return expensesFragment;
            case 3:
                return overviewFragment;
            default:
                return quotesFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
    
    public void updateQuotes(java.util.List<com.example.financialmanagement.models.Quote> quotes) {
        if (quotesFragment != null) {
            quotesFragment.updateQuotes(quotes);
        } else {
            System.out.println("ProjectDetailPagerAdapter: quotesFragment is null!");
        }
    }
    
    public void updateInvoices(java.util.List<com.example.financialmanagement.models.Invoice> invoices) {
        if (invoicesFragment != null) {
            invoicesFragment.updateInvoices(invoices);
        } else {
            System.out.println("ProjectDetailPagerAdapter: invoicesFragment is null!");
        }
    }
    
    public void updateExpenses(java.util.List<com.example.financialmanagement.models.ProjectExpense> expenses) {
        if (expensesFragment != null) {
            expensesFragment.updateExpenses(expenses);
        } else {
            System.out.println("ProjectDetailPagerAdapter: expensesFragment is null!");
        }
    }
}
