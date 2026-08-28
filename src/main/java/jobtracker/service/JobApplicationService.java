package jobtracker.service;

import jobtracker.dto.JobApplicationRequest;
import jobtracker.entity.Company;
import jobtracker.entity.JobApplication;
import jobtracker.entity.User;
import jobtracker.repository.CompanyRepository;
import jobtracker.repository.JobApplicationRepository;
import jobtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PROBLEM: CRUD operations for JobApplications, scoped to the logged-in user only.
 *
 * APPROACH: Every read/update/delete verifies job.getUser().getUsername() matches
 * the requesting username BEFORE acting, preventing IDOR (accessing others' data
 * by guessing IDs).
 *
 * WHY IT WORKS: A valid JWT proves WHO you are (authentication), but says nothing
 * about WHAT you're allowed to touch (authorization) — ownership must be checked
 * explicitly on every operation.
 *
 * TIME: O(1) per operation (indexed lookups) | SPACE: O(1)
 */
@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Autowired
    public JobApplicationService(JobApplicationRepository jobApplicationRepository,
                                 CompanyRepository companyRepository,
                                 UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public JobApplication create(JobApplicationRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        JobApplication application = new JobApplication(
                request.getRole(), company, user, request.getStatus(), request.getAppliedDate());
        application.setNotes(request.getNotes());

        return jobApplicationRepository.save(application);
    }

    public List<JobApplication> getAllForUser(String username) {
        return jobApplicationRepository.findByUserUsername(username);
    }

    public JobApplication getOneForUser(Long id, String username) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!application.getUser().getUsername().equals(username)) {
            throw new SecurityException("You do not have access to this application");
        }

        return application;
    }

    public JobApplication update(Long id, JobApplicationRequest request, String username) {
        JobApplication application = getOneForUser(id, username); // reuses ownership check above

        application.setRole(request.getRole());
        application.setStatus(request.getStatus());
        application.setAppliedDate(request.getAppliedDate());
        application.setNotes(request.getNotes());

        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found"));
            application.setCompany(company);
        }

        return jobApplicationRepository.save(application);
    }

    public void delete(Long id, String username) {
        JobApplication application = getOneForUser(id, username); // ownership check first
        jobApplicationRepository.delete(application);
    }
}