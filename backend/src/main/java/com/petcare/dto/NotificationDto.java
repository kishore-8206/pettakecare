package com.petcare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class NotificationDto {
    private String id;
    @NotBlank
    private String title;
    @NotBlank
    private String message;
    private String type;
    private String targetRole;
    private String timestamp;
    @JsonProperty("isRead")
    private boolean isRead;
    private String link;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    @JsonProperty("isRead")
    public boolean isRead() { return isRead; }
    @JsonProperty("isRead")
    public void setRead(boolean read) { isRead = read; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}
