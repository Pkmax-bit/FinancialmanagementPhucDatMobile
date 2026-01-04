package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Member implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("initials")
    private String initials;
    
    @SerializedName("color")
    private String color;

    public Member() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getInitials() { 
        if (initials != null) return initials;
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return (parts[0].substring(0,1) + parts[parts.length-1].substring(0,1)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
    public void setInitials(String initials) { this.initials = initials; }

    public String getColor() { return color != null ? color : "#0075FF"; }
    public void setColor(String color) { this.color = color; }
}
