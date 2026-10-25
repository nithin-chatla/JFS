package com.hostel.complaints.repository;

import com.hostel.complaints.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByActiveTrueOrderByPublishedAtDesc();
    List<Notice> findAllByOrderByPublishedAtDesc();
}
