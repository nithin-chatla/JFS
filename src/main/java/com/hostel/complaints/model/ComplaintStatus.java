package com.hostel.complaints.model;

public enum ComplaintStatus {
    PENDING("Pending Review", "status-pending", "bi-hourglass-split"),
    ASSIGNED("Assigned", "status-assigned", "bi-person-check"),
    IN_PROGRESS("In Progress", "status-in-progress", "bi-gear-wide-connected"),
    RESOLVED("Resolved", "status-resolved", "bi-check-circle-fill"),
    REJECTED("Rejected", "status-rejected", "bi-x-circle-fill"),
    REOPENED("Reopened", "status-reopened", "bi-arrow-counterclockwise");

    private final String displayName;
    private final String statusClass;
    private final String iconClass;

    ComplaintStatus(String displayName, String statusClass, String iconClass) {
        this.displayName = displayName;
        this.statusClass = statusClass;
        this.iconClass = iconClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public String getIconClass() {
        return iconClass;
    }
}
