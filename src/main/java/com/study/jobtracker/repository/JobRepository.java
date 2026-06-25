package com.study.jobtracker.repository;

import com.study.jobtracker.model.Job;
import com.study.jobtracker.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job,Long> {
    List<Job> findByOwnerUsername(String username);
    Optional<Job> findByIdAndOwnerUsername(Long id, String username);
    List<Job> findByOwnerUsernameAndStatus(String username, JobStatus status);
}
