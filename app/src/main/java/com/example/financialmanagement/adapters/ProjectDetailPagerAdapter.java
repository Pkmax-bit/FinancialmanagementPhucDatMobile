package com.example.financialmanagement.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.financialmanagement.fragments.ProjectQuotesFragment;
import com.example.financialmanagement.fragments.ProjectInvoicesFragment;
import com.example.financialmanagement.fragments.ProjectExpensesFragment;
import com.example.financialmanagement.fragments.ProjectOverviewFragment;
import com.example.financialmanagement.fragments.ProjectTasksFragment;
import com.example.financialmanagement.fragments.ProjectTeamFragment;

public class ProjectDetailPagerAdapter extends FragmentStateAdapter {
    
    private String projectId;
    private ProjectQuotesFragment quotesFragment;
    private ProjectInvoicesFragment invoicesFragment;
    private ProjectExpensesFragment expensesFragment;
    private ProjectTasksFragment tasksFragment;
    private ProjectTeamFragment teamFragment;
    private ProjectOverviewFragment overviewFragment;

    public ProjectDetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, String projectId) {
        super(fragmentActivity);
        this.projectId = projectId;
        
        // Initialize fragments
        quotesFragment = ProjectQuotesFragment.newInstance(projectId);
        invoicesFragment = ProjectInvoicesFragment.newInstance(projectId);
        expensesFragment = ProjectExpensesFragment.newInstance(projectId);
        tasksFragment = ProjectTasksFragment.newInstance(projectId);
        teamFragment = ProjectTeamFragment.newInstance(projectId);
        overviewFragment = ProjectOverviewFragment.newInstance(projectId);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return overviewFragment;
            case 1:
                return quotesFragment;
            case 2:
                return invoicesFragment;
            case 3:
                return expensesFragment;
            case 4:
                return tasksFragment;
            case 5:
                return teamFragment;
            default:
                return overviewFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
    
    public void updateQuotes(java.util.List<com.example.financialmanagement.models.Quote> quotes) {
        if (quotesFragment != null) {
            quotesFragment.updateQuotes(quotes);
        }
    }
    
    public void updateInvoices(java.util.List<com.example.financialmanagement.models.Invoice> invoices) {
        if (invoicesFragment != null) {
            invoicesFragment.updateInvoices(invoices);
        }
    }
    
    public void updateExpenses(java.util.List<com.example.financialmanagement.models.ProjectExpense> expenses) {
        if (expensesFragment != null) {
            expensesFragment.updateExpenses(expenses);
        }
    }

    public void updateTasks(java.util.List<com.example.financialmanagement.models.Task> tasks) {
        if (tasksFragment != null) {
            tasksFragment.updateTasks(tasks);
        }
    }

    public void updateTeam(java.util.List<com.example.financialmanagement.models.TeamMember> team) {
        if (teamFragment != null) {
            teamFragment.updateTeam(team);
        }
    }
}
