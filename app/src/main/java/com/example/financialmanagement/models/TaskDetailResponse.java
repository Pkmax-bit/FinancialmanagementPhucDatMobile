package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import com.example.financialmanagement.models.TaskAssignment;

public class TaskDetailResponse implements Serializable {
    @SerializedName("task")
    private Task task;
    
    @SerializedName("comments")
    private List<TaskComment> comments;
    
    @SerializedName("participants")
    private List<TaskParticipant> participants;
    
    @SerializedName("checklists")
    private List<TaskChecklist> checklists;
    
    @SerializedName("sub_tasks")
    private List<Task> subTasks;

    @SerializedName("quotes")
    private List<Quote> quotes;

    @SerializedName("expenses")
    private List<ProjectExpense> expenses;

    @SerializedName("assignments")
    private List<TaskAssignment> assignments;

    public TaskDetailResponse() {}

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

    public List<TaskComment> getComments() { return comments; }
    public void setComments(List<TaskComment> comments) { this.comments = comments; }

    public List<TaskParticipant> getParticipants() { return participants; }
    public void setParticipants(List<TaskParticipant> participants) { this.participants = participants; }

    public List<TaskChecklist> getChecklists() { return checklists; }
    public void setChecklists(List<TaskChecklist> checklists) { this.checklists = checklists; }

    public List<Task> getSubTasks() { return subTasks; }
    public void setSubTasks(List<Task> subTasks) { this.subTasks = subTasks; }

    public List<Quote> getQuotes() { return quotes; }
    public void setQuotes(List<Quote> quotes) { this.quotes = quotes; }

    public List<ProjectExpense> getExpenses() { return expenses; }
    public void setExpenses(List<ProjectExpense> expenses) { this.expenses = expenses; }

    public List<TaskAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<TaskAssignment> assignments) { this.assignments = assignments; }
}
