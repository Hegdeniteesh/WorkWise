package com.workwise.model;

public enum JobStatus {
    POSTED("Job Posted - Looking for workers"),
    APPLICATIONS_RECEIVED("Applications received from workers"),
    WORKER_ASSIGNED("Worker has been assigned"),
    IN_PROGRESS("Work is currently in progress"),
    COMPLETED("Work has been completed"),
    CANCELLED("Job has been cancelled"),
    PAYMENT_PENDING("Payment is pending"),
    PAYMENT_COMPLETED("Payment has been completed"),
    DISPUTED("Job is under dispute resolution");

    private final String description;

    JobStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
