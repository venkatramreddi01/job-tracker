package jobtracker.controller;

import jobtracker.dto.JobApplicationResponse;
import jobtracker.dto.JobApplicationRequest;
import jobtracker.entity.JobApplication;
import jobtracker.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService service;

    @Autowired
    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody JobApplicationRequest request, Principal principal) {
        try {
            JobApplication created = service.create(request, principal.getName());
            return ResponseEntity.ok(new JobApplicationResponse(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAll(Principal principal) {
        List<JobApplicationResponse> responses = service.getAllForUser(principal.getName())
                .stream()
                .map(JobApplicationResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id, Principal principal) {
        try {
            JobApplication app = service.getOneForUser(id, principal.getName());
            return ResponseEntity.ok(new JobApplicationResponse(app));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody JobApplicationRequest request, Principal principal) {
        try {
            JobApplication updated = service.update(id, request, principal.getName());
            return ResponseEntity.ok(new JobApplicationResponse(updated));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Principal principal) {
        try {
            service.delete(id, principal.getName());
            return ResponseEntity.ok("Deleted successfully");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}