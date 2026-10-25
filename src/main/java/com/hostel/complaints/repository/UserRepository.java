package com.hostel.complaints.repository;

import com.hostel.complaints.model.Role;
import com.hostel.complaints.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByRoleOrderByFullNameAsc(Role role);
    long countByRole(Role role);
}
