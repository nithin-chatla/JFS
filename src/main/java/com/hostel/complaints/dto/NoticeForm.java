package com.hostel.complaints.dto;

import jakarta.validation.constraints.NotBlank;

public class NoticeForm {

    @NotBlank(message = "Notice title is required")
    private String title;

    @NotBlank(message = "Notice content is required")
    private String content;

    private String category = "General";

    public NoticeForm() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
