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
import android.widget.HorizontalScrollView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList; // Added import
import android.widget.ImageView; // Added import
import com.example.financialmanagement.models.TaskComment; // Added import
import com.example.financialmanagement.utils.FileIconHelper; // Added import
import com.bumptech.glide.Glide; // Added for image loading
import android.app.Dialog; // Added for dialog
import android.widget.EditText; // Added for dialog
import android.widget.Button; // Added for dialog
import android.widget.Spinner; // Added for dialog
import android.widget.ArrayAdapter; // Added for spinner
import android.widget.ImageButton; // Added for checklist header
import android.app.DownloadManager; // Added for download
import android.net.Uri; // Added for download
import android.os.Environment; // Added for download
import android.content.Context; // Added for download
import androidx.appcompat.app.AlertDialog; // Added for dialog
import com.example.financialmanagement.models.AssigneeWithRole; // Added for multi-assignee
import com.example.financialmanagement.models.AttachmentItem; // Added for attachments
import com.example.financialmanagement.adapters.AssigneeRoleAdapter; // Added for assignee adapter
import com.example.financialmanagement.adapters.AttachmentAdapter; // Added for attachment adapter
import androidx.recyclerview.widget.RecyclerView; // Added for recycler views
import androidx.recyclerview.widget.GridLayoutManager; // Added for grid layout
import android.app.DatePickerDialog; // Added for date picker
import java.util.Calendar; // Added for date handling
import android.content.Intent; // Added for file picker
import android.provider.MediaStore; // Added for image picker
import android.provider.DocumentsContract; // Added for file picker
import java.util.ArrayList; // Added for lists
import com.google.android.material.chip.Chip; // Added for priority chips
import android.view.ViewGroup; // Added for dialog sizing

public class TaskDetailActivity extends AppCompatActivity {

    private String taskId;
    private TaskService taskService;
    private ProgressBar progressRing;
    private TextView textProgressPercentage;
    private List<com.example.financialmanagement.models.TaskAttachment> currentTaskAttachments; // Store current task attachments
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
    private List<TaskParticipant> taskParticipants; // Store task participants for assignee selection

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
                taskParticipants = response.getParticipants(); // Store participants for assignee selection
                bindTeamData(response.getParticipants());
                
                // Store attachments for use in subtasks BEFORE binding subtasks
                currentTaskAttachments = response.getAttachments();
                android.util.Log.d("TaskDetailActivity", "Stored " + (currentTaskAttachments != null ? currentTaskAttachments.size() : 0) + " attachments for subtasks");
                
                bindSubtasks(response.getChecklists());
                
                // Load and display task attachments from response
                android.util.Log.d("TaskDetailActivity", "Attachments from API: " + (response.getAttachments() != null ? response.getAttachments().size() : 0));
                android.util.Log.d("TaskDetailActivity", "Comments: " + (response.getComments() != null ? response.getComments().size() : 0));
                android.util.Log.d("TaskDetailActivity", "Extracted files from title: " + (extractedFiles != null ? extractedFiles.size() : 0));
                bindFileAttachments(response.getAttachments(), response.getComments(), extractedFiles);
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

    private boolean isImageFile(String url) {
        if (url == null || url.isEmpty()) return false;
        String lowerUrl = url.toLowerCase();
        
        // Loại bỏ query parameters và fragment để check extension
        String urlWithoutParams = lowerUrl;
        int queryIndex = urlWithoutParams.indexOf('?');
        if (queryIndex > 0) {
            urlWithoutParams = urlWithoutParams.substring(0, queryIndex);
        }
        int fragmentIndex = urlWithoutParams.indexOf('#');
        if (fragmentIndex > 0) {
            urlWithoutParams = urlWithoutParams.substring(0, fragmentIndex);
        }
        
        // Check extension
        boolean hasImageExtension = urlWithoutParams.endsWith(".jpg") || urlWithoutParams.endsWith(".jpeg") || 
                                    urlWithoutParams.endsWith(".png") || urlWithoutParams.endsWith(".gif") || 
                                    urlWithoutParams.endsWith(".webp") || urlWithoutParams.endsWith(".bmp");
        
        // Check MIME type trong URL
        boolean hasImageMimeType = lowerUrl.contains("image/jpeg") || lowerUrl.contains("image/png") ||
                                   lowerUrl.contains("image/gif") || lowerUrl.contains("image/webp") ||
                                   lowerUrl.contains("image/bmp");
        
        return hasImageExtension || hasImageMimeType;
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
                // Split by comma first, then by space (to handle both formats)
                // Example: "url1, url2" or "url1 url2"
                String[] commaParts = urlContent.split(",");
                for (String commaPart : commaParts) {
                    // Split by space to handle space-separated URLs
                    String[] spaceParts = commaPart.trim().split("\\s+");
                    for (String part : spaceParts) {
                        String cleanUrl = part.trim();
                        // Remove trailing ? if present
                        if (cleanUrl.endsWith("?")) {
                            cleanUrl = cleanUrl.substring(0, cleanUrl.length() - 1);
                        }
                        // Only add if it looks like a URL
                        if (!cleanUrl.isEmpty() && (cleanUrl.startsWith("http://") || cleanUrl.startsWith("https://"))) {
                            urls.add(cleanUrl);
                        }
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
        sectionProjectInfo.collapse(); // Default collapsed state for Overview

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
        android.util.Log.d("TaskDetailActivity", "bindSubtasks: " + (checklists != null ? checklists.size() : 0) + " checklist(s)"); 
        
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
        
        // Calculate and update progress percentage based on checklist items
        int progressPercentage = 0;
        if (total > 0) {
            progressPercentage = (completed * 100) / total;
        }
        updateTaskProgress(progressPercentage);
        
        // Update subtasks count with percentage
        if (textSubtasksCount != null) {
            if (total > 0) {
                textSubtasksCount.setText(completed + "/" + total + " nhiệm vụ (" + progressPercentage + "%)");
            } else {
                textSubtasksCount.setText("Chưa có nhiệm vụ");
            }
        }
        
        // Refresh filter header with counts
        updateFilterHeader(total, todo, completed);
        applySubtaskFilter();
    }

    private void updateTaskProgress(int progressPercentage) {
        if (textProgressPercentage != null) {
            textProgressPercentage.setText(progressPercentage + "%");
        }
        if (progressRing != null) {
            progressRing.setProgress(progressPercentage);
        }
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
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 8, 0, 8);

        // Add header with + button to create new checklist
        View headerView = LayoutInflater.from(this).inflate(R.layout.view_subtasks_header, container, false);
        com.google.android.material.button.MaterialButton btnAddChecklist = headerView.findViewById(R.id.btn_add_checklist);
        btnAddChecklist.setOnClickListener(v -> showCreateChecklistDialog());
        container.addView(headerView);

        // Hide section if no checklists
        if (allChecklists == null || allChecklists.isEmpty()) {
            sectionSubtasks.setContentView(container);
            sectionSubtasks.setVisibility(View.VISIBLE);
            sectionSubtasks.expand(); // Expand the section to show header
            return;
        }

        // Add Filter Header
        if (filterHeaderView == null) {
            filterHeaderView = createFilterHeaderView();
        }
        if (filterHeaderView.getParent() != null) {
            ((ViewGroup)filterHeaderView.getParent()).removeView(filterHeaderView);
        }
        container.addView(filterHeaderView);

        for (com.example.financialmanagement.models.TaskChecklist checklist : allChecklists) {
            // Collect matching items first (hoặc tất cả items nếu không có filter)
            List<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem> matchingItems = new ArrayList<>();

            if (checklist.getItems() != null && !checklist.getItems().isEmpty()) {
                for (com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem item : checklist.getItems()) {
                     if (currentSubtaskFilter.equals("completed") && !item.isCompleted()) continue;
                     if (currentSubtaskFilter.equals("todo") && item.isCompleted()) continue;
                     matchingItems.add(item);
                }
            }

            // Hiển thị checklist ngay cả khi không có items phù hợp với filter
            // Chỉ skip nếu checklist hoàn toàn null (không bao giờ xảy ra trong thực tế)
            if (checklist == null) {
                continue;
            }

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

            // Title - hiển thị số lượng items phù hợp với filter
            int totalItems = checklist.getItems() != null ? checklist.getItems().size() : 0;
            String countText;
            if (currentSubtaskFilter.equals("all")) {
                countText = String.valueOf(totalItems);
            } else if (!matchingItems.isEmpty()) {
                countText = matchingItems.size() + "/" + totalItems;
            } else if (totalItems > 0) {
                countText = "0/" + totalItems; // Có items nhưng không phù hợp filter
            } else {
                countText = "0"; // Không có items
            }

            TextView titleView = new TextView(this);
            titleView.setId(View.generateViewId());
            titleView.setText(checklist.getTitle() + " (" + countText + ")");
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

            // Temporary layout params for arrow (will be updated after adding button)
            android.widget.RelativeLayout.LayoutParams tempArrowParams = new android.widget.RelativeLayout.LayoutParams(
                48, 48); // Fixed small size
            tempArrowParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
            tempArrowParams.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
            headerLayout.addView(arrowIcon, tempArrowParams);

            // Add Item Button (nút + để thêm checklist item) - đặt bên phải
            ImageButton btnAddItem = new ImageButton(this);
            btnAddItem.setId(View.generateViewId()); // Generate ID for reference
            btnAddItem.setImageResource(R.drawable.ic_add);
            btnAddItem.setBackgroundResource(R.drawable.bg_circle_primary);
            btnAddItem.setColorFilter(android.graphics.Color.WHITE);
            btnAddItem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            android.widget.RelativeLayout.LayoutParams addItemParams = new android.widget.RelativeLayout.LayoutParams(
                40, 40); // Button size
            addItemParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END);
            addItemParams.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
            addItemParams.rightMargin = 16; // Margin from right edge
            btnAddItem.setLayoutParams(addItemParams);
            headerLayout.addView(btnAddItem);

            // Update arrow icon to be left of add button
            android.widget.RelativeLayout.LayoutParams updatedArrowParams = new android.widget.RelativeLayout.LayoutParams(
                48, 48); // Fixed small size
            updatedArrowParams.addRule(android.widget.RelativeLayout.LEFT_OF, btnAddItem.getId());
            updatedArrowParams.addRule(android.widget.RelativeLayout.CENTER_VERTICAL);
            updatedArrowParams.rightMargin = 12; // Margin between arrow and add button
            arrowIcon.setLayoutParams(updatedArrowParams);

            // Set click listener for add item button
            btnAddItem.setOnClickListener(v -> showCreateChecklistItemDialog(checklist.getId(), checklist.getTitle()));

            groupContainer.addView(headerLayout);

            // --- ITEMS CONTAINER ---
            LinearLayout itemsContainer = new LinearLayout(this);
            itemsContainer.setOrientation(LinearLayout.VERTICAL);
            itemsContainer.setVisibility(View.GONE); // Default HIDDEN
            
            // Populate Items
            for (com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem item : matchingItems) {
                View view = LayoutInflater.from(this).inflate(R.layout.item_subtask, itemsContainer, false);
                com.google.android.material.checkbox.MaterialCheckBox checkboxCompleted = view.findViewById(R.id.checkbox_subtask_completed);
                TextView title = view.findViewById(R.id.text_subtask_title);
                TextView status = view.findViewById(R.id.text_subtask_status);
                TextView initials = view.findViewById(R.id.text_subtask_assignee_initials);
                TextView name = view.findViewById(R.id.text_subtask_assignee_name);
                TextView progressText = view.findViewById(R.id.text_subtask_progress);
                LinearLayout filesLayout = view.findViewById(R.id.layout_subtask_files);

                // Set checkbox state based on completion status (without triggering listener)
                checkboxCompleted.setOnCheckedChangeListener(null); // Remove listener first
                checkboxCompleted.setChecked(item.isCompleted());

                // Add click listener to toggle completion
                checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // Update completion status via API
                    taskService.updateChecklistItemCompletion(item.getId(), isChecked, new TaskService.TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
                        @Override
                        public void onSuccess(com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem updatedItem) {
                            // Reload task details to refresh the UI
                            loadTaskDetails();
                        }

                        @Override
                        public void onError(String error) {
                            // Revert checkbox state on error (without triggering listener)
                            checkboxCompleted.setOnCheckedChangeListener(null);
                            checkboxCompleted.setChecked(!isChecked);
                            checkboxCompleted.setOnCheckedChangeListener((btn, checked) -> {
                                taskService.updateChecklistItemCompletion(item.getId(), checked, new TaskService.TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
                                    @Override
                                    public void onSuccess(com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem updatedItem) {
                                        loadTaskDetails();
                                    }

                                    @Override
                                    public void onError(String err) {
                                        checkboxCompleted.setOnCheckedChangeListener(null);
                                        checkboxCompleted.setChecked(!checked);
                                        checkboxCompleted.setOnCheckedChangeListener((b, c) -> {
                                            taskService.updateChecklistItemCompletion(item.getId(), c, new TaskService.TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
                                                @Override
                                                public void onSuccess(com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem updatedItem) {
                                                    loadTaskDetails();
                                                }

                                                @Override
                                                public void onError(String e) {
                                                    Toast.makeText(TaskDetailActivity.this, "Lỗi cập nhật trạng thái: " + e, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        });
                                        Toast.makeText(TaskDetailActivity.this, "Lỗi cập nhật trạng thái: " + err, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                            Toast.makeText(TaskDetailActivity.this, "Lỗi cập nhật trạng thái: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                // Clean Title & Extract Files
                String rawTitle = item.getTitle();
                if (rawTitle == null) rawTitle = "";
                
                List<String> fileUrls = extractFileUrlsFromText(rawTitle);
                String cleanTitle = rawTitle.replaceAll("\\[FILE_URLS:.*?\\]", "").trim();
                
                // Thêm attachments từ task attachments nếu có checklist_item_id khớp với subtask này
                // Mỗi subtask chỉ hiển thị file/hình của chính nó (theo checklist_item_id)
                String currentItemId = item.getId();
                android.util.Log.d("TaskDetailActivity", "Subtask ID: " + currentItemId + ", Title: " + cleanTitle);
                
                if (currentTaskAttachments != null && !currentTaskAttachments.isEmpty() && currentItemId != null) {
                    android.util.Log.d("TaskDetailActivity", "Checking " + currentTaskAttachments.size() + " attachment(s) for subtask " + currentItemId);
                    int attachmentCount = 0;
                    int imageAttachmentCount = 0;
                    for (com.example.financialmanagement.models.TaskAttachment attachment : currentTaskAttachments) {
                        String attachmentChecklistItemId = attachment.getChecklistItemId();
                        android.util.Log.d("TaskDetailActivity", "  Attachment checklist_item_id: " + attachmentChecklistItemId + " (subtask ID: " + currentItemId + ", match: " + currentItemId.equals(attachmentChecklistItemId) + ")");
                        
                        // Kiểm tra xem attachment có thuộc về subtask này không
                        if (currentItemId.equals(attachmentChecklistItemId)) {
                            String attachmentUrl = attachment.getFileUrl();
                            if (attachmentUrl != null && !attachmentUrl.isEmpty()) {
                                // Loại bỏ dấu ? ở cuối URL nếu có
                                if (attachmentUrl.endsWith("?")) {
                                    attachmentUrl = attachmentUrl.substring(0, attachmentUrl.length() - 1);
                                }
                                
                                // Check if it's an image
                                boolean isImage = isImageFile(attachmentUrl);
                                android.util.Log.d("TaskDetailActivity", "    Attachment URL: " + attachmentUrl + " -> isImage: " + isImage);
                                
                                // Kiểm tra xem URL đã có trong fileUrls chưa (tránh trùng lặp)
                                boolean alreadyExists = false;
                                for (String existingUrl : fileUrls) {
                                    String cleanExisting = existingUrl.endsWith("?") ? existingUrl.substring(0, existingUrl.length() - 1) : existingUrl;
                                    if (cleanExisting.equals(attachmentUrl)) {
                                        alreadyExists = true;
                                        break;
                                    }
                                }
                                if (!alreadyExists) {
                                    fileUrls.add(attachmentUrl);
                                    attachmentCount++;
                                    if (isImage) {
                                        imageAttachmentCount++;
                                    }
                                    android.util.Log.d("TaskDetailActivity", "    Added attachment for subtask " + currentItemId + ": " + attachmentUrl + " (isImage: " + isImage + ")");
                                } else {
                                    android.util.Log.d("TaskDetailActivity", "    Attachment already exists in fileUrls: " + attachmentUrl);
                                }
                            }
                        }
                    }
                    android.util.Log.d("TaskDetailActivity", "Added " + attachmentCount + " attachment(s) (" + imageAttachmentCount + " image(s)) for subtask " + currentItemId);
                } else {
                    if (currentTaskAttachments == null) {
                        android.util.Log.d("TaskDetailActivity", "currentTaskAttachments is null");
                    } else if (currentTaskAttachments.isEmpty()) {
                        android.util.Log.d("TaskDetailActivity", "currentTaskAttachments is empty");
                    } else if (currentItemId == null) {
                        android.util.Log.d("TaskDetailActivity", "currentItemId is null");
                    }
                }
                
                // Debug logging
                android.util.Log.d("TaskDetailActivity", "Subtask: " + cleanTitle);
                android.util.Log.d("TaskDetailActivity", "Found " + fileUrls.size() + " file(s) in subtask (from content + attachments)");
                if (!fileUrls.isEmpty()) {
                    for (int i = 0; i < fileUrls.size(); i++) {
                        android.util.Log.d("TaskDetailActivity", "  File " + (i+1) + ": " + fileUrls.get(i));
                    }
                }

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

                // Bind Files and Images
                LinearLayout imageThumbnailsLayout = view.findViewById(R.id.layout_subtask_image_thumbnails);
                HorizontalScrollView scrollViewImages = view.findViewById(R.id.scrollview_subtask_images);
                
                if (!fileUrls.isEmpty()) {
                    android.util.Log.d("TaskDetailActivity", "Displaying " + fileUrls.size() + " file(s) for subtask: " + cleanTitle);
                    
                    // Tách hình ảnh và files
                    List<String> imageUrls = new ArrayList<>();
                    List<String> nonImageUrls = new ArrayList<>();
                    
                    android.util.Log.d("TaskDetailActivity", "Separating images and files from " + fileUrls.size() + " URL(s)");
                    for (String url : fileUrls) {
                        String cleanUrl = url;
                        if (cleanUrl != null && cleanUrl.endsWith("?")) {
                            cleanUrl = cleanUrl.substring(0, cleanUrl.length() - 1);
                        }
                        boolean isImage = isImageFile(cleanUrl);
                        android.util.Log.d("TaskDetailActivity", "  URL: " + cleanUrl + " -> isImage: " + isImage);
                        if (isImage) {
                            imageUrls.add(cleanUrl);
                        } else {
                            nonImageUrls.add(cleanUrl);
                        }
                    }
                    android.util.Log.d("TaskDetailActivity", "Separated: " + imageUrls.size() + " image(s), " + nonImageUrls.size() + " non-image file(s)");
                    
                    // Hiển thị hình ảnh thumbnail
                    if (!imageUrls.isEmpty()) {
                        android.util.Log.d("TaskDetailActivity", "Displaying " + imageUrls.size() + " image(s) as thumbnails for subtask: " + cleanTitle);
                        scrollViewImages.setVisibility(View.VISIBLE);
                        imageThumbnailsLayout.removeAllViews();
                        
                        for (String imageUrl : imageUrls) {
                            android.util.Log.d("TaskDetailActivity", "Creating thumbnail for image: " + imageUrl);
                            
                            // Tạo ImageView cho thumbnail
                            ImageView thumbnailView = new ImageView(this);
                            int thumbnailSize = (int) (80 * getResources().getDisplayMetrics().density); // 80dp
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
                            params.setMargins(0, 0, 8, 0); // Margin right 8dp
                            thumbnailView.setLayoutParams(params);
                            thumbnailView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            thumbnailView.setBackgroundResource(R.drawable.bg_input_field_modern);
                            thumbnailView.setPadding(4, 4, 4, 4);
                            thumbnailView.setContentDescription("Image: " + FileIconHelper.getFileName(imageUrl));
                            
                            // Load hình ảnh với Glide
                            final String finalImageUrl = imageUrl;
                            android.util.Log.d("TaskDetailActivity", "Loading image with Glide: " + finalImageUrl);
                            Glide.with(this)
                                .load(finalImageUrl)
                                .placeholder(R.drawable.ic_file_generic)
                                .error(R.drawable.ic_file_generic)
                                .centerCrop()
                                .override(thumbnailSize, thumbnailSize)
                                .into(thumbnailView);
                            
                            // Click để xem fullscreen
                            thumbnailView.setOnClickListener(v -> {
                                android.util.Log.d("TaskDetailActivity", "Thumbnail clicked, opening fullscreen: " + finalImageUrl);
                                showImageFullscreen(finalImageUrl, FileIconHelper.getFileName(finalImageUrl));
                            });
                            
                            imageThumbnailsLayout.addView(thumbnailView);
                            android.util.Log.d("TaskDetailActivity", "Thumbnail added to layout");
                        }
                    } else {
                        android.util.Log.d("TaskDetailActivity", "No images to display for subtask: " + cleanTitle);
                        scrollViewImages.setVisibility(View.GONE);
                    }
                    
                    // Hiển thị files không phải hình ảnh
                    if (!nonImageUrls.isEmpty()) {
                        filesLayout.setVisibility(View.VISIBLE);
                        filesLayout.removeAllViews();
                        
                        for (String url : nonImageUrls) {
                            View fileView = LayoutInflater.from(this).inflate(R.layout.item_file_attachment, filesLayout, false);
                            
                            ImageView imageFileIcon = fileView.findViewById(R.id.image_file_icon);
                            TextView textFileName = fileView.findViewById(R.id.text_file_name);
                            TextView textFileType = fileView.findViewById(R.id.text_file_type);
                            View fileContainer = fileView.findViewById(R.id.file_attachment_container);
                            
                            imageFileIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            imageFileIcon.setImageResource(FileIconHelper.getFileIconResource(url));
                            
                            textFileName.setText(FileIconHelper.getFileName(url));
                            textFileType.setText(FileIconHelper.getFileTypeLabel(url));
                            
                            final String finalUrl = url;
                            fileContainer.setOnClickListener(v -> {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(android.net.Uri.parse(finalUrl));
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
                } else {
                    android.util.Log.d("TaskDetailActivity", "No files to display for subtask: " + cleanTitle);
                    scrollViewImages.setVisibility(View.GONE);
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
    
    private void showCreateChecklistDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_checklist_simple);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Initialize views - chỉ cần title
        EditText editTitle = dialog.findViewById(R.id.edit_checklist_title);
        com.google.android.material.button.MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel);
        com.google.android.material.button.MaterialButton btnCreate = dialog.findViewById(R.id.btn_create);

        // Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Create button
        btnCreate.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề nhóm nhiệm vụ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create checklist đơn giản chỉ với title
            createChecklist(title);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showCreateChecklistItemDialog(String checklistId, String checklistTitle) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_todo_simple);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Initialize views - bỏ priority selection
        EditText editTitle = dialog.findViewById(R.id.edit_todo_title);
        EditText editDescription = dialog.findViewById(R.id.edit_todo_description);
        EditText editEstimatedHours = dialog.findViewById(R.id.edit_estimated_hours);
        EditText editDueDate = dialog.findViewById(R.id.edit_due_date);

        RecyclerView recyclerAssignees = dialog.findViewById(R.id.recycler_assignees);
        TextView textNoAssignees = dialog.findViewById(R.id.text_no_assignees);
        com.google.android.material.button.MaterialButton btnAddAssignee = dialog.findViewById(R.id.btn_add_assignee);
        RecyclerView recyclerAttachments = dialog.findViewById(R.id.recycler_attachments);
        com.google.android.material.button.MaterialButton btnAddImage = dialog.findViewById(R.id.btn_add_image);
        com.google.android.material.button.MaterialButton btnAddFile = dialog.findViewById(R.id.btn_add_file);
        com.google.android.material.button.MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel);
        com.google.android.material.button.MaterialButton btnCreate = dialog.findViewById(R.id.btn_create);

        // Initialize assignee list and adapter
        ArrayList<AssigneeWithRole> taskAssignees = new ArrayList<>();
        final AssigneeRoleAdapter[] assigneeAdapterRef = new AssigneeRoleAdapter[1];
        assigneeAdapterRef[0] = new AssigneeRoleAdapter(this, taskAssignees,
            assignee -> {
                taskAssignees.remove(assignee);
                assigneeAdapterRef[0].notifyDataSetChanged();
                updateAssigneeVisibility(recyclerAssignees, textNoAssignees, taskAssignees);
            });
        recyclerAssignees.setAdapter(assigneeAdapterRef[0]);
        updateAssigneeVisibility(recyclerAssignees, textNoAssignees, taskAssignees);

        // Initialize attachment list and adapter
        ArrayList<AttachmentItem> taskAttachments = new ArrayList<>();
        final AttachmentAdapter[] attachmentAdapterRef = new AttachmentAdapter[1];
        attachmentAdapterRef[0] = new AttachmentAdapter(this, taskAttachments,
            attachment -> {
                taskAttachments.remove(attachment);
                attachmentAdapterRef[0].notifyDataSetChanged();
            });
        recyclerAttachments.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerAttachments.setAdapter(attachmentAdapterRef[0]);

        // Due date picker
        editDueDate.setOnClickListener(v -> showDatePicker((dateString) -> editDueDate.setText(dateString)));

        // Add assignee button
        btnAddAssignee.setOnClickListener(v -> showAssigneeSelectionDialog(selectedAssignee -> {
            taskAssignees.add(selectedAssignee);
            assigneeAdapterRef[0].notifyDataSetChanged();
            updateAssigneeVisibility(recyclerAssignees, textNoAssignees, taskAssignees);
        }));

        // Add image button
        btnAddImage.setOnClickListener(v -> openImagePicker(result -> {
            for (android.net.Uri uri : result) {
                addAttachmentFromUri(uri, taskAttachments, attachmentAdapterRef[0], false); // Don't upload immediately when creating checklist item
            }
        }));

        // Add file button
        btnAddFile.setOnClickListener(v -> openFilePicker(result -> {
            for (android.net.Uri uri : result) {
                addAttachmentFromUri(uri, taskAttachments, attachmentAdapterRef[0], false); // Don't upload immediately when creating checklist item
            }
        }));

        // Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Create button
        btnCreate.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String estimatedHours = editEstimatedHours.getText().toString().trim();
            String dueDate = editDueDate.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề nhiệm vụ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create checklist item with data - bỏ priority (mặc định medium)
            createChecklistItemWithData(checklistId, title, description, "medium",
                estimatedHours, dueDate, taskAssignees, taskAttachments);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void createChecklist(String title) {
        taskService.createChecklist(taskId, title, new TaskService.TaskCallback<com.example.financialmanagement.models.TaskChecklist>() {
            @Override
            public void onSuccess(com.example.financialmanagement.models.TaskChecklist checklist) {
                Toast.makeText(TaskDetailActivity.this, "Đã tạo nhóm nhiệm vụ thành công", Toast.LENGTH_SHORT).show();
                // Reload task details to refresh the checklists
                loadTaskDetails();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskDetailActivity.this, "Lỗi tạo nhóm nhiệm vụ: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChecklistItemInChecklist(String checklistId, String title, String description, String assigneeId) {
        taskService.createChecklistItem(checklistId, title, description, assigneeId, new TaskService.TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
            @Override
            public void onSuccess(com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem item) {
                Toast.makeText(TaskDetailActivity.this, "Đã thêm nhiệm vụ thành công", Toast.LENGTH_SHORT).show();
                // Reload task details to refresh the checklist
                loadTaskDetails();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskDetailActivity.this, "Lỗi thêm nhiệm vụ: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChecklistWithData(String title, String description, List<AssigneeWithRole> assignees, List<AttachmentItem> attachments) {
        // For now, just create basic checklist. Advanced features would need backend support
        taskService.createChecklist(taskId, title, new TaskService.TaskCallback<com.example.financialmanagement.models.TaskChecklist>() {
            @Override
            public void onSuccess(com.example.financialmanagement.models.TaskChecklist checklist) {
                Toast.makeText(TaskDetailActivity.this, "Đã tạo nhóm nhiệm vụ thành công", Toast.LENGTH_SHORT).show();
                // Reload task details to refresh the checklists
                loadTaskDetails();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskDetailActivity.this, "Lỗi tạo nhóm nhiệm vụ: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChecklistItemWithData(String checklistId, String title, String description, String priority,
                                           String estimatedHours, String dueDate, List<AssigneeWithRole> assignees,
                                           List<AttachmentItem> attachments) {
        // Use the new method that supports multiple assignments
        taskService.createChecklistItemWithAssignments(checklistId, title, assignees, new TaskService.TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
            @Override
            public void onSuccess(com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem item) {
                Toast.makeText(TaskDetailActivity.this, "Đã thêm nhiệm vụ thành công", Toast.LENGTH_SHORT).show();

                // Upload attachments if any
                if (attachments != null && !attachments.isEmpty()) {
                    // Store original content (title) to append file URLs later
                    // Note: The item's content field may contain the title, we'll use title as base
                    String originalContent = title; // Use the title that was passed to create the item
                    uploadChecklistItemAttachments(item.getId(), attachments, originalContent);
                } else {
                    // Reload task details to refresh the checklist
                    loadTaskDetails();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TaskDetailActivity.this, "Lỗi thêm nhiệm vụ: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadChecklistItemAttachments(String checklistItemId, List<AttachmentItem> attachments) {
        uploadChecklistItemAttachments(checklistItemId, attachments, null);
    }
    
    private void uploadChecklistItemAttachments(String checklistItemId, List<AttachmentItem> attachments, String originalContent) {
        if (attachments == null || attachments.isEmpty()) {
            loadTaskDetails();
            return;
        }
        
        // Upload each attachment with checklist_item_id
        final int[] uploadedCount = {0};
        final int[] failedCount = {0};
        final int totalCount = attachments.size();
        final StringBuilder fileUrlsContent = new StringBuilder();
        
        for (AttachmentItem attachment : attachments) {
            if (attachment.getUploadStatus() == AttachmentItem.UploadStatus.SUCCESS && attachment.getUploadedUrl() != null) {
                // File already uploaded successfully, append to content
                fileUrlsContent.append(" [FILE_URLS: ").append(attachment.getUploadedUrl()).append("?]");
                uploadedCount[0]++;
            } else if (attachment.getUploadStatus() == AttachmentItem.UploadStatus.PENDING || 
                      attachment.getUploadStatus() == AttachmentItem.UploadStatus.ERROR) {
                // Upload file with checklist_item_id
                uploadAttachmentForChecklistItem(checklistItemId, attachment, new TaskService.TaskCallback<String>() {
                    @Override
                    public void onSuccess(String uploadedUrl) {
                        attachment.setUploadStatus(AttachmentItem.UploadStatus.SUCCESS);
                        attachment.setUploadedUrl(uploadedUrl);
                        fileUrlsContent.append(" [FILE_URLS: ").append(uploadedUrl).append("?]");
                        uploadedCount[0]++;
                        
                        // Check if all files are uploaded
                        if (uploadedCount[0] + failedCount[0] == totalCount) {
                            if (uploadedCount[0] == totalCount) {
                                // All files uploaded, update checklist item content
                                String finalContent = (originalContent != null ? originalContent : "") + fileUrlsContent.toString();
                                updateChecklistItemContent(checklistItemId, finalContent);
                            } else {
                                Toast.makeText(TaskDetailActivity.this, "Một số file chưa upload thành công", Toast.LENGTH_SHORT).show();
                                loadTaskDetails();
                            }
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        attachment.setUploadStatus(AttachmentItem.UploadStatus.ERROR);
                        attachment.setUploadError(error);
                        failedCount[0]++;
                        
                        // Check if all files are processed
                        if (uploadedCount[0] + failedCount[0] == totalCount) {
                            if (uploadedCount[0] > 0) {
                                // Some files uploaded, update content with successful ones
                                String finalContent = (originalContent != null ? originalContent : "") + fileUrlsContent.toString();
                                updateChecklistItemContent(checklistItemId, finalContent);
                            } else {
                                Toast.makeText(TaskDetailActivity.this, "Upload file thất bại: " + error, Toast.LENGTH_SHORT).show();
                                loadTaskDetails();
                            }
                        }
                    }
                });
            }
        }
        
        // If all files were already uploaded, update content immediately
        if (uploadedCount[0] == totalCount && failedCount[0] == 0) {
            String finalContent = (originalContent != null ? originalContent : "") + fileUrlsContent.toString();
            updateChecklistItemContent(checklistItemId, finalContent);
        }
    }
    
    private void uploadAttachmentForChecklistItem(String checklistItemId, AttachmentItem attachment, TaskService.TaskCallback<String> callback) {
        taskService.uploadTaskAttachment(this, taskId, attachment.getUri(), attachment.getFileName(), checklistItemId, callback);
    }

    private void updateChecklistItemContent(String checklistItemId, String additionalContent) {
        // Get current checklist item content first
        // For now, we'll append the additionalContent to existing content
        // In the future, we might want to get current content first and merge
        
        // Update checklist item content with file URLs
        taskService.updateChecklistItemContent(checklistItemId, additionalContent, new TaskService.TaskCallback<com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem>() {
            @Override
            public void onSuccess(com.example.financialmanagement.models.TaskChecklist.TaskChecklistItem updatedItem) {
                android.util.Log.d("TaskDetailActivity", "Successfully updated checklist item content with file URLs");
                // Reload task details to refresh the display
                loadTaskDetails();
            }
            
            @Override
            public void onError(String error) {
                android.util.Log.e("TaskDetailActivity", "Failed to update checklist item content: " + error);
                Toast.makeText(TaskDetailActivity.this, "Lỗi cập nhật nội dung: " + error, Toast.LENGTH_SHORT).show();
                // Still reload to show what we have
                loadTaskDetails();
            }
        });
    }

    private void updateAssigneeVisibility(RecyclerView recyclerView, TextView emptyText, List<AssigneeWithRole> assignees) {
        if (assignees.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    private void showAssigneeSelectionDialog(OnAssigneeSelectedListener listener) {
        if (taskParticipants == null || taskParticipants.isEmpty()) {
            Toast.makeText(this, "Không có thành viên nào trong dự án", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] employeeNames = new String[taskParticipants.size()];
        for (int i = 0; i < taskParticipants.size(); i++) {
            employeeNames[i] = taskParticipants.get(i).getEmployeeName();
        }

        new android.app.AlertDialog.Builder(this)
            .setTitle("Chọn thành viên")
            .setItems(employeeNames, (dialog, which) -> {
                TaskParticipant selected = taskParticipants.get(which);
                AssigneeWithRole assignee = new AssigneeWithRole(
                    selected.getEmployeeId(),
                    selected.getEmployeeName(),
                    selected.getRoleDisplayName()
                );
                listener.onAssigneeSelected(assignee);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void showDatePicker(OnDateSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                String dateString = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                listener.onDateSelected(dateString);
            }, year, month, day);

        datePickerDialog.show();
    }

    private void openImagePicker(OnFilesSelectedListener listener) {
        // Store listener for later use in onActivityResult
        currentFileListener = listener;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), REQUEST_IMAGE_PICK);
    }

    private void openFilePicker(OnFilesSelectedListener listener) {
        // Store listener for later use in onActivityResult
        currentFileListener = listener;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Chọn tệp"), REQUEST_FILE_PICK);
    }

    private void addAttachmentFromUri(android.net.Uri uri, List<AttachmentItem> attachments, AttachmentAdapter adapter) {
        addAttachmentFromUri(uri, attachments, adapter, true); // Default: upload immediately
    }
    
    private void addAttachmentFromUri(android.net.Uri uri, List<AttachmentItem> attachments, AttachmentAdapter adapter, boolean uploadImmediately) {
        try {
            android.content.ContentResolver contentResolver = getContentResolver();
            String mimeType = contentResolver.getType(uri);
            String fileName = getFileNameFromUri(uri);
            long fileSize = getFileSizeFromUri(uri);

            if (fileSize > 10 * 1024 * 1024) { // 10MB limit
                Toast.makeText(this, "File quá lớn (tối đa 10MB)", Toast.LENGTH_SHORT).show();
                return;
            }

            AttachmentItem attachment = new AttachmentItem(uri, fileName, mimeType, fileSize);

            // Add to list immediately for preview
            attachments.add(attachment);
            adapter.notifyDataSetChanged();

            // Start real upload process only if uploadImmediately is true
            if (uploadImmediately) {
                performFileUpload(attachment, adapter);
            }
            // Otherwise, keep status as PENDING and upload later with checklist_item_id

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi thêm file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileNameFromUri(android.net.Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private long getFileSizeFromUri(android.net.Uri uri) {
        long size = 0;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE);
                    if (sizeIndex >= 0) {
                        size = cursor.getLong(sizeIndex);
                    }
                }
            }
        }
        return size;
    }

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_FILE_PICK = 1002;
    private OnFilesSelectedListener currentFileListener;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && currentFileListener != null) {
            ArrayList<android.net.Uri> uris = new ArrayList<>();

            if (data.getClipData() != null) {
                // Multiple files selected
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    android.net.Uri uri = data.getClipData().getItemAt(i).getUri();
                    uris.add(uri);
                }
            } else if (data.getData() != null) {
                // Single file selected
                uris.add(data.getData());
            }

            if (!uris.isEmpty()) {
                // Call the listener with selected files
                currentFileListener.onFilesSelected(uris);
                currentFileListener = null; // Reset listener
            }
        }
    }

    // Interfaces for callbacks
    interface OnAssigneeSelectedListener {
        void onAssigneeSelected(AssigneeWithRole assignee);
    }

    interface OnDateSelectedListener {
        void onDateSelected(String dateString);
    }

    interface OnFilesSelectedListener {
        void onFilesSelected(ArrayList<android.net.Uri> uris);
    }

    private void performFileUpload(AttachmentItem attachment, AttachmentAdapter adapter) {
        // Set uploading state
        attachment.setUploadStatus(AttachmentItem.UploadStatus.UPLOADING);
        adapter.notifyDataSetChanged();

        // Upload file to server
        taskService.uploadTaskAttachment(this, taskId, attachment.getUri(), attachment.getFileName(), new TaskService.TaskCallback<String>() {
            @Override
            public void onSuccess(String uploadedUrl) {
                attachment.setUploadStatus(AttachmentItem.UploadStatus.SUCCESS);
                attachment.setUploadedUrl(uploadedUrl);
                adapter.notifyDataSetChanged();
                runOnUiThread(() -> {
                    Toast.makeText(TaskDetailActivity.this, "Upload thành công: " + attachment.getFileName(), Toast.LENGTH_SHORT).show();
                    // Reload task details để hiển thị file mới upload
                    loadTaskDetails();
                });
            }

            @Override
            public void onError(String error) {
                attachment.setUploadStatus(AttachmentItem.UploadStatus.ERROR);
                attachment.setUploadError(error);
                adapter.notifyDataSetChanged();
                runOnUiThread(() -> {
                    Toast.makeText(TaskDetailActivity.this, "Upload thất bại: " + attachment.getFileName(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void bindFileAttachments(List<com.example.financialmanagement.models.TaskAttachment> attachments, 
                                     List<TaskComment> comments, 
                                     List<String> extractedFiles) {
        android.util.Log.d("TaskDetailActivity", "=== bindFileAttachments ===");
        android.util.Log.d("TaskDetailActivity", "Attachments count: " + (attachments != null ? attachments.size() : 0));
        android.util.Log.d("TaskDetailActivity", "Comments count: " + (comments != null ? comments.size() : 0));
        android.util.Log.d("TaskDetailActivity", "Extracted files count: " + (extractedFiles != null ? extractedFiles.size() : 0));
        
        // Create container for file items
        LinearLayout filesContainer = new LinearLayout(this);
        filesContainer.setOrientation(LinearLayout.VERTICAL);
        filesContainer.setPadding(0, 8, 0, 8); // No horizontal padding
        
        boolean hasFiles = false;
        
        // 1. Add task attachments from response (chính thức từ API)
        if (attachments != null && !attachments.isEmpty()) {
            android.util.Log.d("TaskDetailActivity", "Processing " + attachments.size() + " attachment(s) from API");
            for (com.example.financialmanagement.models.TaskAttachment attachment : attachments) {
                if (attachment.getFileUrl() != null && !attachment.getFileUrl().isEmpty()) {
                    android.util.Log.d("TaskDetailActivity", "Adding attachment: " + attachment.getFileUrl());
                    addFileViewToContainer(filesContainer, attachment.getFileUrl(), 
                                         attachment.getOriginalFileName() != null ? attachment.getOriginalFileName() : attachment.getFileName());
                    hasFiles = true;
                }
            }
        }
        
        // 2. Add file attachments from comments (nếu có)
        if (comments != null) {
            int fileCommentCount = 0;
            for (TaskComment comment : comments) {
                if ("file".equals(comment.getType()) && comment.getFileUrl() != null && !comment.getFileUrl().isEmpty()) {
                    fileCommentCount++;
                    android.util.Log.d("TaskDetailActivity", "Adding file from comment: " + comment.getFileUrl());
                    addFileViewToContainer(filesContainer, comment.getFileUrl(), null);
                    hasFiles = true;
                }
            }
            android.util.Log.d("TaskDetailActivity", "Found " + fileCommentCount + " file comment(s)");
        }
        
        // 3. Add extracted files from title (nếu có)
        if (extractedFiles != null && !extractedFiles.isEmpty()) {
            android.util.Log.d("TaskDetailActivity", "Processing " + extractedFiles.size() + " extracted file(s) from title");
            for (String url : extractedFiles) {
                if (url != null && !url.isEmpty()) {
                    android.util.Log.d("TaskDetailActivity", "Adding extracted file: " + url);
                    addFileViewToContainer(filesContainer, url, null);
                    hasFiles = true;
                }
            }
        }
        
        android.util.Log.d("TaskDetailActivity", "Total files to display: " + (hasFiles ? "YES" : "NO"));
        
        if (!hasFiles) {
            sectionFiles.setVisibility(View.GONE);
            return;
        }
        
        sectionFiles.setContentView(filesContainer);
        sectionFiles.setVisibility(View.VISIBLE);
    }
    
    private void addFileViewToContainer(LinearLayout container, String fileUrl, String fileName) {
        // Loại bỏ dấu ? ở cuối URL nếu có
        String cleanUrl = fileUrl;
        if (cleanUrl != null && cleanUrl.endsWith("?")) {
            cleanUrl = cleanUrl.substring(0, cleanUrl.length() - 1);
        }
        
        if (cleanUrl == null || cleanUrl.isEmpty()) {
            android.util.Log.w("TaskDetailActivity", "Skipping empty file URL");
            return;
        }
        
        android.util.Log.d("TaskDetailActivity", "addFileViewToContainer: " + cleanUrl + " (isImage: " + isImageFile(cleanUrl) + ")");
        
        // Tạo biến final để sử dụng trong lambda
        final String finalCleanUrl = cleanUrl;
        
        View fileView = LayoutInflater.from(this).inflate(R.layout.item_file_attachment, container, false);
        
        ImageView imageFileIcon = fileView.findViewById(R.id.image_file_icon);
        TextView textFileName = fileView.findViewById(R.id.text_file_name);
        TextView textFileType = fileView.findViewById(R.id.text_file_type);
        View fileContainer = fileView.findViewById(R.id.file_attachment_container);
        
        // Kiểm tra nếu là file hình ảnh thì hiển thị thumbnail với Glide
        boolean isImage = isImageFile(finalCleanUrl);
        if (isImage) {
            android.util.Log.d("TaskDetailActivity", "Loading image with Glide in addFileViewToContainer: " + finalCleanUrl);
            // Hiển thị hình ảnh thực tế với Glide - tăng kích thước và chất lượng
            imageFileIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this)
                .load(finalCleanUrl)
                .placeholder(FileIconHelper.getFileIconResource(finalCleanUrl)) // Icon mặc định khi đang load
                .error(FileIconHelper.getFileIconResource(finalCleanUrl)) // Icon nếu lỗi
                .centerCrop()
                .override(200, 200) // Tăng kích thước thumbnail để dễ thấy hơn
                .into(imageFileIcon);
        } else {
            android.util.Log.d("TaskDetailActivity", "Using file icon (not image): " + finalCleanUrl);
            // Set file icon based on file type
            imageFileIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            int iconRes = FileIconHelper.getFileIconResource(finalCleanUrl);
            imageFileIcon.setImageResource(iconRes);
        }
        
        // Set file name - ưu tiên fileName từ parameter, nếu không thì extract từ URL
        String displayFileName = fileName;
        if (displayFileName == null || displayFileName.isEmpty()) {
            displayFileName = FileIconHelper.getFileName(finalCleanUrl);
        }
        textFileName.setText(displayFileName);
        
        // Set file type label
        String fileType = FileIconHelper.getFileTypeLabel(finalCleanUrl);
        textFileType.setText(fileType);
        
        // Tạo biến final để sử dụng trong lambda
        final String finalDisplayFileName = displayFileName;
        final boolean isImageFile = isImageFile(finalCleanUrl);
        
        // Add click listener to open file
        fileContainer.setOnClickListener(v -> {
            if (isImageFile) {
                // Nếu là hình ảnh, hiển thị fullscreen với tùy chọn tải về
                showImageFullscreen(finalCleanUrl, finalDisplayFileName);
            } else {
                // Nếu không phải hình ảnh, mở file như bình thường
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(android.net.Uri.parse(finalCleanUrl));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Không thể mở file", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        container.addView(fileView);
    }
    
    /**
     * Hiển thị hình ảnh fullscreen với tùy chọn tải về
     */
    private void showImageFullscreen(String imageUrl, String imageName) {
        // Tạo dialog fullscreen
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_viewer, null);
        dialog.setContentView(dialogView);
        
        ImageView imageFullscreen = dialogView.findViewById(R.id.image_fullscreen);
        TextView textImageName = dialogView.findViewById(R.id.text_image_name);
        ImageButton buttonDownload = dialogView.findViewById(R.id.button_download);
        ImageButton buttonClose = dialogView.findViewById(R.id.button_close);
        
        // Set tên hình ảnh
        if (imageName != null && !imageName.isEmpty()) {
            textImageName.setText(imageName);
        } else {
            textImageName.setText(FileIconHelper.getFileName(imageUrl));
        }
        
        // Load hình ảnh với Glide
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_file_generic)
            .error(R.drawable.ic_file_generic)
            .into(imageFullscreen);
        
        // Nút đóng
        buttonClose.setOnClickListener(v -> dialog.dismiss());
        
        // Nút tải về
        buttonDownload.setOnClickListener(v -> {
            downloadImage(imageUrl, imageName != null ? imageName : FileIconHelper.getFileName(imageUrl));
            Toast.makeText(this, "Đang tải về...", Toast.LENGTH_SHORT).show();
        });
        
        // Click vào hình ảnh để đóng
        imageFullscreen.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * Tải về hình ảnh
     */
    private void downloadImage(String imageUrl, String fileName) {
        try {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(imageUrl);
            
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle("Đang tải: " + fileName);
            request.setDescription("Tải về hình ảnh");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            
            // Lưu vào thư mục Pictures
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName);
            
            downloadManager.enqueue(request);
            
            Toast.makeText(this, "Đã bắt đầu tải về: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải về: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
