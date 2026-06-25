package com.study.jobtracker.service;


import com.study.jobtracker.dto.JobRequest;
import com.study.jobtracker.dto.JobResponse;
import com.study.jobtracker.exception.JobNotFoundException;
import com.study.jobtracker.model.Job;
import com.study.jobtracker.model.JobStatus;
import com.study.jobtracker.model.User;
import com.study.jobtracker.repository.JobRepository;
import com.study.jobtracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public JobService(UserRepository userRepository, JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public Job getById(Long id,String username){
        return jobRepository.findByIdAndOwnerUsername(id,username).orElseThrow(()->new JobNotFoundException(id));
    }

    public List<Job> getAll(String username){
        return jobRepository.findByOwnerUsername(username);
    }

    public Job addJob(JobRequest request,String username){
        User owner = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("User not found"));
        Job job = new Job(null,request.company(),request.role(),request.link(),request.status(),owner);
        return jobRepository.save(job);
    }

    public List<Job> getAll(String username, JobStatus status){
        if(status == null){
            return jobRepository.findByOwnerUsername(username);
        }
        return jobRepository.findByOwnerUsernameAndStatus(username,status);
    }
    public Job update(Long id, JobRequest request, String username){
        Job job = jobRepository.findByIdAndOwnerUsername(id,username).orElseThrow(()-> new JobNotFoundException(id));
        job.setCompany(request.company());
        job.setRole(request.role());
        job.setLink(request.link());
        job.setStatus(request.status());
        return jobRepository.save(job);
    }

    public boolean delete(Long id,String username){
        Optional<Job> job = jobRepository.findByIdAndOwnerUsername(id,username);
        if(job.isPresent()){
            jobRepository.delete(job.get());
            return true;
        }
        return false;
    }
}
