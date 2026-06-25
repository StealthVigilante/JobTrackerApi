package com.study.jobtracker.controller;


import com.study.jobtracker.dto.JobRequest;
import com.study.jobtracker.dto.JobResponse;
import com.study.jobtracker.model.Job;
import com.study.jobtracker.model.JobStatus;
import com.study.jobtracker.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    public JobResponse toResponse(Job job){
        return new JobResponse(job.getId(),job.getCompany(),job.getRole(),job.getLink(),job.getStatus());
    }


    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getById(@PathVariable Long id, Authentication authentication){
        Job job = jobService.getById(id,authentication.getName());
        return ResponseEntity.ok(toResponse(job));
    }

    @PostMapping
    public ResponseEntity<JobResponse> addJob(@Valid @RequestBody JobRequest request, Authentication authentication){
        String username = authentication.getName();
        Job job = jobService.addJob(request,username);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(job));
    }

    @GetMapping
    public List<JobResponse> getAll(@RequestParam(required = false) JobStatus status, Authentication authentication){
        List<Job> jobs = jobService.getAll(authentication.getName(), status);
        List<JobResponse> jobsResponse = new ArrayList<>();
        for(Job job:jobs){
            jobsResponse.add(toResponse(job));
        }
        return jobsResponse;
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> update(@PathVariable Long id,@Valid @RequestBody JobRequest request, Authentication authentication){
        Job job = jobService.update(id,request,authentication.getName());
        return ResponseEntity.ok(toResponse(job));

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,Authentication authentication){
        if(jobService.delete(id,authentication.getName())){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
