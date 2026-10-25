package com.hostel.complaints.repository;

import com.hostel.complaints.model.Complaint;
import com.hostel.complaints.model.ComplaintCategory;
import com.hostel.complaints.model.ComplaintStatus;
import com.hostel.complaints.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByComplaintNumber(String complaintNumber);

    List<Complaint> findByStudentOrderByCreatedAtDesc(User student);

    List<Complaint> findByAssignedStaffOrderByCreatedAtDesc(User staff);

    List<Complaint> findAllByOrderByCreatedAtDesc();

    List<Complaint> findByStatusOrderByCreatedAtDesc(ComplaintStatus status);

    List<Complaint> findByCategoryOrderByCreatedAtDesc(ComplaintCategory category);

    long countByStatus(ComplaintStatus status);

    long countByCategory(ComplaintCategory category);

    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category")
    List<Object[]> countComplaintsByCategory();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countComplaintsByStatus();

    @Query("SELECT c.priority, COUNT(c) FROM Complaint c GROUP BY c.priority")
    List<Object[]> countComplaintsByPriority();

    @Query("SELECT c.hostelBlock, COUNT(c) FROM Complaint c GROUP BY c.hostelBlock")
    List<Object[]> countComplaintsByHostelBlock();

    List<Complaint> findTop5ByOrderByCreatedAtDesc();

    List<Complaint> findTop5ByStudentOrderByCreatedAtDesc(User student);
}
