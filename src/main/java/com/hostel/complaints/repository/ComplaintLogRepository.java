package com.hostel.complaints.repository;

import com.hostel.complaints.model.Complaint;
import com.hostel.complaints.model.ComplaintLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintLogRepository extends JpaRepository<ComplaintLog, Long> {
    List<ComplaintLog> findByComplaintOrderByTimestampDesc(Complaint complaint);
}
