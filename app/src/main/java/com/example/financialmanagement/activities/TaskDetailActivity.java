package com.example.financialmanagement.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.models.Task;
import com.example.financialmanagement.models.TaskDetailResponse;
import com.example.financialmanagement.models.TaskParticipant;
import com.example.financialmanagement.models.TaskAssignment;
import com.example.financialmanagement.services.TaskService;
import com.example.financialmanagement.views.ExpandableSectionView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.LinearLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList; // Added import
import android.widget.ImageView; // Added import
import com.example.financialmanagement.models.TaskComment; // Added import
import com.example.financialmanagement.utils.FileIconHelper; // Added import

public class TaskDetailActivity extends AppCompatActivity {

    private String taskId;
    private TaskService taskService;
    private ProgressBar progressRing;
    private TextView textProgressPercentage;
    private TextView textTaskTitle;
    private TextView textStatusBadge;
    private TextView textPriorityBadge;
    private View viewPriorityDot;
    private TextView textSubtasksCount;
    private TextView textAccountablePerson;
    private LinearLayout layoutAccountablePerson;
    
    // Sections
    private ExpandableSectionView sectionProjectInfo;
    private ExpandableSectionView sectionAssignments;
    private ExpandableSectionView sectionTeam;
    private ExpandableSectionView sectionSubtasks;
    private ExpandableSectionView sectionFiles; // Added declaration
    private ExpandableSectionView sectionQuotes;
    private ExpandableSectionView sectionCosts;

    private List<com.example.financialmanagement.models.TaskChecklist> allChecklists; // Changed originalChecklists to allChecklists
    private String currentSubtaskFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_enhanced);

        taskId = getIntent().getStringExtra("task_id");
        if (taskId == null) {
            Toast.makeText(this, "Không tìm thấy mã nhiệm vụ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupToolbar();
        
        taskService = new TaskService(this);
        loadTaskDetails();
    }

    private void initializeViews() {
        progressRing = findViewById(R.id.progress_ring);
        textProgressPercentage = findViewById(R.id.text_progress_percentage);
        textTaskTitle = findViewById(R.id.text_task_title);
        textStatusBadge = findViewById(R.id.text_status_badge);
        textPriorityBadge = findViewById(R.id.text_priority_badge);
        viewPriorityDot = findViewById(R.id.view_priority_dot);
        textSubtasksCount = findViewById(R.id.text_subtasks_count);
        textAccountablePerson = findViewById(R.id.text_accountable_person);
        layoutAccountablePerson = findViewById(R.id.layout_accountable_person);
        
        sectionProjectInfo = findViewById(R.id.section_project_info);
        sectionAssignments = findViewById(R.id.section_assignments);
        sectionTeam = findViewById(R.id.section_team);
        sectionSubtasks = findViewById(R.id.section_subtasks);
        sectionFiles = findViewById(R.id.section_files);
        sectionQuotes = findViewById(R.id.section_quotes);
        sectionCosts = findViewById(R.id.section_costs);

        FloatingActionButton fabChat = findViewById(R.id.fab_chat);
        fabChat.setOnClickListener(v -> openChat());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void loadTaskDetails() {
        taskService.getTaskDetail(taskId, new TaskService.TaskCallback<TaskDetailResponse>() {
            @Override
            public void onSuccess(TaskDetailResponse response) {
                // Pre-process title to extract files if present
                Task task = response.getTask();
                List<String> extractedFiles = extractFileUrlsFromText(task.getTitle());
                if (!extractedFiles.isEmpty()) {
                    // Clean title
                    String cleanTitle = task.getTitle().replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                    task.setTitle(cleanTitle);
                }

                bindTaskData(task);
                bindOverview(task, task.getProject());
                // bindAssignments(response.getAssignments());
                bindTeamData(response.getParticipants());
                bindSubtasks(response.getChecklists());
                
                // Combine comments and extracted files
                List<TaskComment> allComments = response.getComments();
                if (allComments == null) allComments = new ArrayList<>();
                
                // Convert extracted file URLs to TaskComment objects roughly for display
                for (String url : extractedFiles) {
                    TaskComment fileComment = new TaskComment();
                    fileComment.setType("file");
                    fileComment.setFileUrl(url);
                    allComments.add(fileComment);
                }
                
                bindFileAttachments(allComments);
                bindQuotes(response.getQuotes());
                bindCosts(response.getExpenses());

                // Debug logging
                android.util.Log.d("TaskDetailActivity", "Assignments: " + (response.getAssignments() != null ? response.getAssignments().size() : 0));
                android.util.Log.d("TaskDetailActivity", "Quotes: " + (response.getQuotes() != null ? response.getQuotes().size() : 0));
                android.util.Log.d("TaskDetailActivity", "Expenses: " + (response.getExpenses() != null ? response.getExpenses().size() : 0));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskDetailActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> extractFileUrlsFromText(String text) {
        List<String> urls = new ArrayList<>();
        if (text == null) return urls;

        // Pattern: [FILE_URLS: url1, url2] or similar
        // Adjust regex to capture content inside [FILE_URLS: ...]
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[FILE_URLS: (.*?)\\]");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            String urlContent = matcher.group(1);
            if (urlContent != null) {
                // Helper to split by comma if multiple urls are present? 
                // Assuming simple case based on user image: just one long url or space separated?
                // The image shows: "https://...xlsx?" so it might be a single URL.
                // Let's treat it as single or comma separated
                String[] parts = urlContent.split(",");
                for (String part : parts) {
                    String cleanUrl = part.trim();
                    // Remove trailing ? or ] if regex was greedy (regex above is non-greedy .*?)
                    if (!cleanUrl.isEmpty()) {
                        urls.add(cleanUrl);
                    }
                }
            }
        }
        return urls;
    }

    private void bindTaskData(Task task) {
        textTaskTitle.setText(task.getTitle());

        // Display assigned person if available
        String assignedName = task.getAssigneeName();
        if (assignedName != null && !assignedName.isEmpty()) {
            layoutAccountablePerson.setVisibility(View.VISIBLE);
            textAccountablePerson.setText("Được giao: " + assignedName);
        } else if (task.getAccountablePersonName() != null && !task.getAccountablePersonName().isEmpty()) {
            layoutAccountablePerson.setVisibility(View.VISIBLE);
            textAccountablePerson.setText(task.getAccountablePersonName());
        } else {
            layoutAccountablePerson.setVisibility(View.GONE);
        }
        // ... rest of bindTaskData
        textProgressPercentage.setText(task.getProgress() + "%");
        progressRing.setProgress(task.getProgress());
        
        textStatusBadge.setText(task.getStatusDisplayName());
        textPriorityBadge.setText(task.getPriorityDisplayName());
        
        // Update badges colors based on status
        int statusBgColor;
        int statusTextColor;
        switch (task.getStatus() != null ? task.getStatus() : "") {
            case "done":
            case "completed":
                statusBgColor = getResources().getColor(R.color.task_status_completed_bg);
                statusTextColor = getResources().getColor(R.color.task_status_completed_text);
                break;
            case "in_progress":
                statusBgColor = getResources().getColor(R.color.task_status_in_progress_bg);
                statusTextColor = getResources().getColor(R.color.task_status_in_progress_text);
                break;
            case "todo":
                statusBgColor = getResources().getColor(R.color.task_status_todo_bg);
                statusTextColor = getResources().getColor(R.color.task_status_todo_text);
                break;
            default:
                statusBgColor = getResources().getColor(R.color.task_status_todo_bg);
                statusTextColor = getResources().getColor(R.color.task_status_todo_text);
                break;
        }
        textStatusBadge.getBackground().setTint(statusBgColor);
        textStatusBadge.setTextColor(statusTextColor);

        // Priority colors
        int priorityColor;
        switch (task.getPriority() != null ? task.getPriority() : "") {
            case "high":
            case "urgent":
                priorityColor = getResources().getColor(R.color.task_priority_high);
                break;
            case "medium":
                priorityColor = getResources().getColor(R.color.task_priority_medium);
                break;
            case "low":
                priorityColor = getResources().getColor(R.color.task_priority_low);
                break;
            default:
                priorityColor = getResources().getColor(R.color.gray_600);
                break;
        }
        viewPriorityDot.getBackground().setTint(priorityColor);
    }

    private void bindOverview(Task task, Project project) {
        if (project == null && task == null) {
            sectionProjectInfo.setVisibility(View.GONE);
            return;
        }

        sectionProjectInfo.setVisibility(View.VISIBLE); // Ensure it's visible if project exists
        sectionProjectInfo.expand(); // Use default expanded state for Overview

        View view = LayoutInflater.from(this).inflate(R.layout.view_project_info, sectionProjectInfo.getContainer(), false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        java.text.NumberFormat currencyFormat = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        // Bind Task Information
        if (task != null) {
            ((TextView) view.findViewById(R.id.text_task_status)).setText(task.getStatusDisplayName());
            ((TextView) view.findViewById(R.id.text_task_priority)).setText(task.getPriorityDisplayName());

            // Task dates - only due date available
            ((TextView) view.findViewById(R.id.text_task_start_date)).setText("--");

            String taskDueDate = task.getDueDate();
            ((TextView) view.findViewById(R.id.text_task_due_date)).setText(taskDueDate != null ? taskDueDate.substring(0, 10) : "--");
        }

        // Bind Project Information
        if (project != null) {
            TextView textCustomerName = view.findViewById(R.id.text_customer_name);
            if (project.getCustomerName() != null) {
                textCustomerName.setText(project.getCustomerName());
            } else {
                textCustomerName.setText("Khách hàng lẻ");
            }

            // ((TextView) view.findViewById(R.id.text_responsible_person)).setText(project.getAssignedTo() != null ? project.getAssignedTo() : "Chưa cập nhật");


            Date startDate = project.getStartDate();
            ((TextView) view.findViewById(R.id.text_start_date)).setText(startDate != null ? dateFormat.format(startDate) : "--");

            Date endDate = project.getEndDate();
            ((TextView) view.findViewById(R.id.text_end_date)).setText(endDate != null ? dateFormat.format(endDate) : "--");

            String budgetStr = currencyFormat.format(project.getBudget() != null ? project.getBudget() : 0);
            ((TextView) view.findViewById(R.id.text_project_budget)).setText(budgetStr);
            
            ((TextView) view.findViewById(R.id.text_payment_type)).setText(project.getBillingTypeDisplayName());

            String actualCostStr = currencyFormat.format(project.getActualCost() != null ? project.getActualCost() : 0);
            ((TextView) view.findViewById(R.id.text_actual_cost)).setText(actualCostStr);

            Double remaining = project.getRemainingBudget();
            String differenceStr = currencyFormat.format(remaining != null ? remaining : 0);
            TextView textDifference = view.findViewById(R.id.text_project_difference);
            textDifference.setText(differenceStr);
            
            if (remaining != null && remaining < 0) {
                 textDifference.setTextColor(Color.RED);
            } else {
                 textDifference.setTextColor(getResources().getColor(R.color.task_success));
            }

            TextView progressPercentText = view.findViewById(R.id.text_project_progress_percent);
            ProgressBar progressBar = view.findViewById(R.id.progress_project);

            int projectProgress = project.getProgress() != null ? project.getProgress() : 0;

            progressPercentText.setText(projectProgress + "%");
            progressBar.setProgress(projectProgress);
        }

        sectionProjectInfo.setContentView(view);
    }

    private void bindAssignments(List<TaskAssignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            // Hide assignment section or show no assignments
            return;
        }

        // Create assignment section if not exists
        if (sectionAssignments == null) {
            sectionAssignments = findViewById(R.id.section_assignments);
        }

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 8, 0, 8);

        for (TaskAssignment assignment : assignments) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_participant, container, false);
            TextView initials = view.findViewById(R.id.text_member_initials);
            TextView name = view.findViewById(R.id.text_member_name);
            TextView role = view.findViewById(R.id.text_member_role);

            String assignmentName = assignment.getAssignedToName() != null && !assignment.getAssignedToName().isEmpty()
                ? assignment.getAssignedToName() : "Nhân viên";
            name.setText(assignmentName);
            role.setText(assignment.getStatusDisplayName());

            if (assignmentName.length() >= 1) {
                initials.setText(assignmentName.substring(0, 1).toUpperCase());
                initials.getBackground().setTint(getAvatarColor(assignmentName));
            }

            container.addView(view);
            android.util.Log.d("TaskDetailActivity", "Assignment: " + assignmentName + " - " + assignment.getStatusDisplayName());
        }

        if (sectionAssignments != null) {
            sectionAssignments.setContentView(container);
            sectionAssignments.setVisibility(View.VISIBLE);
        }
    }

    private void bindTeamData(List<TaskParticipant> participants) {
        if (participants == null || participants.isEmpty()) {
            sectionTeam.setVisibility(View.GONE);
            return;
        }
        
        LinearLayout teamContainer = new LinearLayout(this);
        teamContainer.setOrientation(LinearLayout.VERTICAL);
        teamContainer.setPadding(0, 8, 0, 8); // Removed horizontal padding handled by parent

        for (TaskParticipant p : participants) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_participant, teamContainer, false);
            TextView initials = view.findViewById(R.id.text_member_initials);
            TextView name = view.findViewById(R.id.text_member_name);
            TextView role = view.findViewById(R.id.text_member_role);

            String employeeName = p.getEmployeeName() != null && !p.getEmployeeName().isEmpty() ? p.getEmployeeName() : "Nhân viên";
            name.setText(employeeName);
            role.setText(p.getRoleDisplayName()); // Use new method

            String nameText = employeeName;
            if (nameText != null && nameText.length() >= 1) {
                initials.setText(nameText.substring(0, 1).toUpperCase());
            }

            int color = getAvatarColor(nameText);
            initials.getBackground().setTint(color);

            teamContainer.addView(view);
        }
        sectionTeam.setContentView(teamContainer);
    }
    
    private void bindQuotes(List<com.example.financialmanagement.models.Quote> quotes) {
        if (quotes == null || quotes.isEmpty()) {
            sectionQuotes.setVisibility(View.GONE);
            return;
        }

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 8, 0, 8);

        for (com.example.financialmanagement.models.Quote quote : quotes) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_quote_mini, container, false);
            TextView code = view.findViewById(R.id.text_quote_code);
            TextView amount = view.findViewById(R.id.text_quote_amount);

            code.setText(quote.getQuoteNumber());
            
            // Format format currency
            java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
            String amountStr = format.format(quote.getTotalAmount() != null ? quote.getTotalAmount() : 0);
            amount.setText(amountStr);

            container.addView(view);
        }
        sectionQuotes.setContentView(container);
    }

    private void bindCosts(List<com.example.financialmanagement.models.ProjectExpense> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            sectionCosts.setVisibility(View.GONE);
            return;
        }

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 8, 0, 8);

        for (com.example.financialmanagement.models.ProjectExpense expense : expenses) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_cost_mini, container, false);
            TextView title = view.findViewById(R.id.text_cost_title);
            TextView amount = view.findViewById(R.id.text_cost_amount);

            title.setText(expense.getDescription());
            
            java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
            String amountStr = format.format(expense.getAmount() != null ? expense.getAmount() : 0);
            amount.setText(amountStr);

            container.addView(view);
        }
        sectionCosts.setContentView(container);
    }

    private int getAvatarColor(String name) {
        if (name == null || name.isEmpty()) return Color.GRAY;
        int hash = name.hashCode();
        int[] colors = {
            Color.parseColor("#0075FF"), Color.parseColor("#34C759"), 
            Color.parseColor("#FF9500"), Color.parseColor("#FF3B30"),
            Color.parseColor("#AF52DE"), Color.parseColor("#5856D6")
        };
        return colors[Math.abs(hash) % colors.length];
    }

    private void bindSubtasks(List<com.example.financialmanagement.models.TaskChecklist> checklists) {
        this.allChecklists = checklists; 
        
        // Initialize filter view if needed
        if (filterHeaderView == null) {
            filterHeaderView = createFilterHeaderView();
        }
        
        // Calculate counts for filters
        int total = 0;
        int todo = 0;
        int completed = 0;
        
        if (allChecklists != null) {
            for (com.example.financialmanagement.models.TaskChecklist checklist : allChecklists) {
                if (checklist.getItems() != null) {
                    for (com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem item : checklist.getItems()) {
                        total++;
                        if (item.isCompleted()) completed++;
                        else todo++;
                    }
                }
            }
        }
        
        // Refresh filter header with counts
        updateFilterHeader(total, todo, completed);
        applySubtaskFilter();
    }

    private View filterHeaderView; // Keep reference to refresh
    private LinearLayout filterContainerLayout;

    private void updateFilterHeader(int total, int todo, int completed) {
         if (filterContainerLayout == null) return;
         
         filterContainerLayout.removeAllViews();
         
         String[] filters = {"all", "todo", "completed"};
         String[] labels = {
             "Tất cả (" + total + ")", 
             "Cần làm (" + todo + ")", 
             "Hoàn thành (" + completed + ")"
         };

        for (int i = 0; i < filters.length; i++) {
            final String filter = filters[i];
            TextView chip = new TextView(this);
            chip.setText(labels[i]);
            chip.setPadding(32, 12, 32, 12);
            chip.setTextSize(12);
            chip.setGravity(android.view.Gravity.CENTER);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 0);
            chip.setLayoutParams(params);

            if (currentSubtaskFilter.equals(filter)) {
                chip.setBackground(getResources().getDrawable(R.drawable.bg_badge_rounded));
                chip.getBackground().setTint(getResources().getColor(R.color.task_accent));
                chip.setTextColor(Color.WHITE);
            } else {
                chip.setBackground(getResources().getDrawable(R.drawable.bg_badge_rounded));
                chip.getBackground().setTint(getResources().getColor(R.color.task_status_todo_bg));
                chip.setTextColor(getResources().getColor(R.color.task_status_todo_text));
            }

            chip.setOnClickListener(v -> {
                currentSubtaskFilter = filter;
                bindSubtasks(allChecklists); // Re-bind to refresh filter UI and list
            });

            filterContainerLayout.addView(chip);
        }
    }

    private void applySubtaskFilter() {
        if (allChecklists == null || allChecklists.isEmpty()) { 
            sectionSubtasks.setVisibility(View.GONE);
            return;
        }

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 8, 0, 8); 

        // Add Filter Header
        if (filterHeaderView == null) {
            filterHeaderView = createFilterHeaderView();
        }
        if (filterHeaderView.getParent() != null) {
            ((ViewGroup)filterHeaderView.getParent()).removeView(filterHeaderView);
        }
        container.addView(filterHeaderView);

        for (com.example.financialmanagement.models.TaskChecklist checklist : allChecklists) { 
            if (checklist.getItems() == null || checklist.getItems().isEmpty()) continue;
            
            // Collect matching items first
            List<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> matchingItems = new ArrayList<>();
            for (com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem item : checklist.getItems()) {
                 if (currentSubtaskFilter.equals("completed") && !item.isCompleted()) continue;
                 if (currentSubtaskFilter.equals("todo") && item.isCompleted()) continue;
                 matchingItems.add(item);
            }
            
            if (matchingItems.isEmpty()) continue;

            // Create Checklist Group Container
            LinearLayout groupContainer = new LinearLayout(this);
            groupContainer.setOrientation(LinearLayout.VERTICAL);
            groupContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT));
            
            // --- HEADER ---
            android.widget.RelativeLayout headerLayout = new android.widget.RelativeLayout(this);
            headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT));
            headerLayout.setPadding(16, 24, 16, 24);
            headerLayout.setBackgroundResource(R.drawable.bg_rounded_gray); // Use a light background
            // Add margin to header
            LinearLayout.LayoutParams headerParams = (LinearLayout.LayoutParams) headerLayout.getLayoutParams();
            headerParams.setMargins(0, 8, 0, 8);
            headerLayout.setLayoutParams(headerParams);

            // Title
            TextView titleView = new TextView(this);
            titleView.setId(View.generateViewId());
            titleView.setText(checklist.getTitle() + " (" + matchingItems.size() + ")");
            titleView.setTextSize(14);
            titleView.setTypeface(null, android.graphics.Typeface.BOLD);
            titleView.setTextColor(getResources().getColor(R.color.task_priority_medium));
            
            android.widget.RelativeLayout.LayoutParams titleParams = new android.widget.RelativeLayout.LayoutParams(
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT, 
                android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
            titleParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_START);
            titleParams.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
            headerLayout.addView(titleView, titleParams);

            // Arrow Icon
            ImageView arrowIcon = new ImageView(this);
            arrowIcon.setImageResource(R.drawable.ic_chevron_right); // Default collapsed
            arrowIcon.setColorFilter(getResources().getColor(R.color.gray_600));
            
            android.widget.RelativeLayout.LayoutParams arrowParams = new android.widget.RelativeLayout.LayoutParams(
                48, 48); // Fixed small size
            arrowParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
            arrowParams.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
            headerLayout.addView(arrowIcon, arrowParams);

            groupContainer.addView(headerLayout);

            // --- ITEMS CONTAINER ---
            LinearLayout itemsContainer = new LinearLayout(this);
            itemsContainer.setOrientation(LinearLayout.VERTICAL);
            itemsContainer.setVisibility(View.GONE); // Default HIDDEN
            
            // Populate Items
            for (com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem item : matchingItems) {
                View view = LayoutInflater.from(this).inflate(R.layout.item_subtask, itemsContainer, false);
                TextView title = view.findViewById(R.id.text_subtask_title);
                TextView status = view.findViewById(R.id.text_subtask_status);
                TextView initials = view.findViewById(R.id.text_subtask_assignee_initials);
                TextView name = view.findViewById(R.id.text_subtask_assignee_name);
                TextView progressText = view.findViewById(R.id.text_subtask_progress);
                LinearLayout filesLayout = view.findViewById(R.id.layout_subtask_files);

                // Clean Title & Extract Files
                String rawTitle = item.getTitle();
                if (rawTitle == null) rawTitle = "";
                
                List<String> fileUrls = extractFileUrlsFromText(rawTitle);
                String cleanTitle = rawTitle.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();

                title.setText(cleanTitle);
                
                List<com.example.financialmanagement.models.ChecklistItemAssignment> assignments = item.getAssignments();
                LinearLayout assignmentsContainer = view.findViewById(R.id.layout_subtask_assignments);
                LinearLayout singleAssigneeRow = view.findViewById(R.id.linearLayout_assignee_row);

                if (assignments != null && !assignments.isEmpty()) {
                    assignmentsContainer.setVisibility(View.VISIBLE);
                    singleAssigneeRow.setVisibility(View.GONE);
                    assignmentsContainer.removeAllViews();
                    for (com.example.financialmanagement.models.ChecklistItemAssignment assignment : assignments) {
                        View assignmentView = LayoutInflater.from(this).inflate(R.layout.item_subtask_assignment, assignmentsContainer, false);
                        TextView aInitials = assignmentView.findViewById(R.id.text_assignment_initials);
                        TextView aName = assignmentView.findViewById(R.id.text_assignment_name);
                        TextView aRole = assignmentView.findViewById(R.id.text_assignment_role);

                        String empName = assignment.getEmployeeName() != null && !assignment.getEmployeeName().isEmpty() ? assignment.getEmployeeName() : "Nhân viên";
                        aName.setText(empName);
                        aInitials.setText(empName.substring(0, 1).toUpperCase());
                        aInitials.getBackground().setTint(getAvatarColor(empName));
                        aRole.setText(assignment.getRoleDisplayName());
                        
                        // Style role badge based on type
                        if ("accountable".equals(assignment.getResponsibilityType())) {
                            aRole.getBackground().setTint(getResources().getColor(R.color.task_priority_high));
                            aRole.setTextColor(Color.WHITE);
                        } else if ("responsible".equals(assignment.getResponsibilityType())) {
                            aRole.getBackground().setTint(getResources().getColor(R.color.task_accent));
                            aRole.setTextColor(Color.WHITE);
                        }

                        assignmentsContainer.addView(assignmentView);
                    }
                } else {
                    assignmentsContainer.setVisibility(View.GONE);
                    singleAssigneeRow.setVisibility(View.VISIBLE);
                    String assigneeName = item.getAssigneeName() != null && !item.getAssigneeName().isEmpty() ? item.getAssigneeName() : "Chưa phân công";
                    name.setText(assigneeName);
                    String aNameTxt = item.getAssigneeName();
                    if (aNameTxt != null && !aNameTxt.isEmpty()) {
                        initials.setText(aNameTxt.substring(0, 1).toUpperCase());
                        initials.getBackground().setTint(getAvatarColor(aNameTxt));
                    } else {
                        initials.setText("?");
                        initials.getBackground().setTint(Color.LTGRAY);
                    }
                }
                
                if (item.isCompleted()) {
                    status.setText("Hoàn thành");
                    status.getBackground().setTint(getResources().getColor(R.color.task_status_completed_bg));
                    status.setTextColor(getResources().getColor(R.color.task_status_completed_text));
                    progressText.setText("100%");
                } else {
                    status.setText("Cần làm");
                    status.getBackground().setTint(getResources().getColor(R.color.task_status_todo_bg));
                    status.setTextColor(getResources().getColor(R.color.task_status_todo_text));
                    progressText.setText("0%");
                }

                // Bind Files
                if (!fileUrls.isEmpty()) {
                    filesLayout.setVisibility(View.VISIBLE);
                    filesLayout.removeAllViews();
                    for (String url : fileUrls) {
                         View fileView = LayoutInflater.from(this).inflate(R.layout.item_file_attachment, filesLayout, false);
                         
                         ImageView imageFileIcon = fileView.findViewById(R.id.image_file_icon);
                         TextView textFileName = fileView.findViewById(R.id.text_file_name);
                         TextView textFileType = fileView.findViewById(R.id.text_file_type);
                         View fileContainer = fileView.findViewById(R.id.file_attachment_container);
                         
                         imageFileIcon.setImageResource(FileIconHelper.getFileIconResource(url));
                         textFileName.setText(FileIconHelper.getFileName(url));
                         textFileType.setText(FileIconHelper.getFileTypeLabel(url));
                         
                         fileContainer.setOnClickListener(v -> {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(android.net.Uri.parse(url));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Không thể mở file", Toast.LENGTH_SHORT).show();
                            }
                        });
                         
                         filesLayout.addView(fileView);
                    }
                } else {
                    filesLayout.setVisibility(View.GONE);
                }
                
                itemsContainer.addView(view);
            }

            groupContainer.addView(itemsContainer);
            container.addView(groupContainer);

            // Toggle Click Listener
            headerLayout.setOnClickListener(v -> {
                if (itemsContainer.getVisibility() == View.VISIBLE) {
                    itemsContainer.setVisibility(View.GONE);
                    arrowIcon.animate().rotation(0).setDuration(200).start();
                } else {
                    itemsContainer.setVisibility(View.VISIBLE);
                    arrowIcon.animate().rotation(90).setDuration(200).start();
                }
            });
        }
        sectionSubtasks.setContentView(container);
    }

    private View createFilterHeaderView() {
        android.widget.HorizontalScrollView scrollView = new android.widget.HorizontalScrollView(this);
        scrollView.setHorizontalScrollBarEnabled(false);
        filterContainerLayout = new LinearLayout(this); // Initialize class member
        filterContainerLayout.setOrientation(LinearLayout.HORIZONTAL);
        filterContainerLayout.setPadding(0, 0, 0, 16);
        
        scrollView.addView(filterContainerLayout);
        return scrollView;
    }

    private void openChat() {
        Intent intent = new Intent(this, TaskChatActivity.class);
        intent.putExtra("taskId", taskId); 
        intent.putExtra("task_title", textTaskTitle.getText().toString());
        startActivity(intent);
    }
    
    private void bindFileAttachments(List<TaskComment> comments) {
        if (comments == null) {
            sectionFiles.setVisibility(View.GONE);
            return;
        }
        
        // Filter comments to get only file attachments
        List<TaskComment> fileAttachments = new ArrayList<>();
        for (TaskComment comment : comments) {
            if ("file".equals(comment.getType()) && comment.getFileUrl() != null) {
                fileAttachments.add(comment);
            }
        }
        
        if (fileAttachments.isEmpty()) {
            sectionFiles.setVisibility(View.GONE);
            return;
        }
        
        // Create container for file items
        LinearLayout filesContainer = new LinearLayout(this);
        filesContainer.setOrientation(LinearLayout.VERTICAL);
        filesContainer.setPadding(0, 8, 0, 8); // No horizontal padding
        
        // Add each file attachment
        for (TaskComment fileComment : fileAttachments) {
            View fileView = LayoutInflater.from(this).inflate(R.layout.item_file_attachment, filesContainer, false);
            
            ImageView imageFileIcon = fileView.findViewById(R.id.image_file_icon);
            TextView textFileName = fileView.findViewById(R.id.text_file_name);
            TextView textFileType = fileView.findViewById(R.id.text_file_type);
            View container = fileView.findViewById(R.id.file_attachment_container);
            
            // Set file icon based on file type
            int iconRes = FileIconHelper.getFileIconResource(fileComment.getFileUrl());
            imageFileIcon.setImageResource(iconRes);
            
            // Set file name
            String fileName = FileIconHelper.getFileName(fileComment.getFileUrl());
            textFileName.setText(fileName);
            
            // Set file type label
            String fileType = FileIconHelper.getFileTypeLabel(fileComment.getFileUrl());
            textFileType.setText(fileType);
            
            // Add click listener to open file
            container.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(android.net.Uri.parse(fileComment.getFileUrl()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Không thể mở file", Toast.LENGTH_SHORT).show();
                }
            });
            
            filesContainer.addView(fileView);
        }
        
        sectionFiles.setContentView(filesContainer);
        sectionFiles.setVisibility(View.VISIBLE);
    }
}
