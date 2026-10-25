package com.hostel.complaints.model;

public enum ComplaintCategory {
    ELECTRICAL("Electrical", "bi-lightning-charge"),
    PLUMBING("Plumbing", "bi-droplet"),
    CARPENTRY("Carpentry", "bi-hammer"),
    INTERNET_WIFI("Internet & Wi-Fi", "bi-wifi"),
    CLEANING_HOUSEKEEPING("Housekeeping & Cleaning", "bi-stars"),
    MESS_FOOD("Mess & Dining", "bi-cup-hot"),
    SECURITY("Security & Safety", "bi-shield-check"),
    OTHER("Other Issues", "bi-question-circle");

    private final String displayName;
    private final String iconClass;

    ComplaintCategory(String displayName, String iconClass) {
        this.displayName = displayName;
        this.iconClass = iconClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconClass() {
        return iconClass;
    }
}
