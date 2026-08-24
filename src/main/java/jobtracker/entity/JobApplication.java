package jobtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    private LocalDate appliedDate;

    @Column(length = 1000)
    private String notes;

    public JobApplication() {
    }

    public JobApplication(String role, Company company, ApplicationStatus status, LocalDate appliedDate) {
        this.role = role;
        this.company = company;
        this.status = status;
        this.appliedDate = appliedDate;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}