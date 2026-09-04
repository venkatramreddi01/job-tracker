package jobtracker.controller;

import jobtracker.entity.Company;
import jobtracker.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public ResponseEntity<List<Company>> getAll() {
        return ResponseEntity.ok(companyRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Company company) {
        boolean alreadyExists = companyRepository.findAll().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(company.getName()));

        if (alreadyExists) {
            return ResponseEntity.badRequest().body("A company with this name already exists");
        }

        return ResponseEntity.ok(companyRepository.save(company));
    }
}