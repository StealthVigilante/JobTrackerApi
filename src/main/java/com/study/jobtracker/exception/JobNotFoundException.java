package com.study.jobtracker.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long id) {
        super("Job not found with Id :"+id);
    }
}
