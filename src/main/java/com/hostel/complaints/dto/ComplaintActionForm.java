package com.hostel.complaints.dto;

import com.hostel.complaints.model.ComplaintStatus;

public class ComplaintActionForm {

    private ComplaintStatus status;
    private Long assignedStaffId;
    private String remarks;

    public ComplaintActionForm() {
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public Long getAssignedStaffId() {
        return assignedStaffId;
    }

    public void setAssignedStaffId(Long assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
