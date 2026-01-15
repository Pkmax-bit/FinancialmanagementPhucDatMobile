package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * Model for message reaction summary
 */
public class MessageReaction implements Serializable {
    
    @SerializedName("emoji")
    private String emoji;
    
    @SerializedName("count")
    private int count;
    
    @SerializedName("users")
    private List<String> users; // List of user IDs who reacted
    
    @SerializedName("user_names")
    private List<String> userNames;
    
    public MessageReaction() {}
    
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public List<String> getUsers() { return users; }
    public void setUsers(List<String> users) { this.users = users; }
    
    public List<String> getUserNames() { return userNames; }
    public void setUserNames(List<String> userNames) { this.userNames = userNames; }
    
    /**
     * Check if current user has reacted
     */
    public boolean hasUserReacted(String userId) {
        return users != null && users.contains(userId);
    }
}


