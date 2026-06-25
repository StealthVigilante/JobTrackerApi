package com.study.jobtracker.dto;

import com.study.jobtracker.model.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobRequest (
    @NotBlank(message = "Company cant be empty") String company,
    @NotBlank(message = "Role cant be blank") String role,
    @NotBlank(message = "Link cant be blank") String link,
    @NotNull(message = "Status cant be blank") JobStatus status
){}
