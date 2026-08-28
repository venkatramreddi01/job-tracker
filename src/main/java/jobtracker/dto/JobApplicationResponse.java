package jobtracker.dto;

import jobtracker.entity.ApplicationStatus;
import jobtracker.entity.JobApplication;
import java.time.LocalDate;

public class JobApplicationResponse {
    private Long id;
    private String role;
    private String companyName;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private String notes;

    public JobApplicationResponse(JobApplication application) {
        this.id = application.getId();
        this.role = application.getRole();
        this.companyName = application.getCompany().getName();
        this.status = application.getStatus();
        this.appliedDate = application.getAppliedDate();
        this.notes = application.getNotes();
    }

    public Long getId() { return id; }
    public String getRole() { return role; }
    public String getCompanyName() { return companyName; }
    public ApplicationStatus getStatus() { return status; }
    public LocalDate getAppliedDate() { return appliedDate; }
    public String getNotes() { return notes; }
}