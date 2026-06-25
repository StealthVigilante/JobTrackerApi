package com.study.jobtracker.dto;

import com.study.jobtracker.model.JobStatus;

public record JobResponse(long id, String company, String role, String link, JobStatus status) {
}
