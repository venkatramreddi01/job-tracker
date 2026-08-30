package jobtracker;

import jobtracker.dto.JobApplicationRequest;
import jobtracker.entity.ApplicationStatus;
import jobtracker.entity.Company;
import jobtracker.entity.JobApplication;
import jobtracker.entity.User;
import jobtracker.repository.CompanyRepository;
import jobtracker.repository.JobApplicationRepository;
import jobtracker.repository.UserRepository;
import jobtracker.service.JobApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PROBLEM: Verify JobApplicationService's business logic works correctly, in
 * isolation from a real database.
 *
 * APPROACH: Mock the three repositories with Mockito. Control exactly what they
 * return for each test, then check the service behaves correctly given that input.
 *
 * WHY IT WORKS: Testing against mocks means we're only testing OUR logic (duplicate
 * checks, ownership checks) — not MySQL, not network calls, not real data.
 */
@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobApplicationService jobApplicationService;

    private User testUser;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        testUser = new User("venkatram", "test@example.com", "hashedpassword");
        testUser.setId(1L);

        testCompany = new Company("Google", "google.com");
        testCompany.setId(1L);
    }

    @Test
    void create_ShouldSaveApplication_WhenUserAndCompanyExist() {
        JobApplicationRequest request = new JobApplicationRequest();
        request.setRole("SDE-1");
        request.setCompanyId(1L);
        request.setStatus(ApplicationStatus.APPLIED);
        request.setAppliedDate(LocalDate.now());

        when(userRepository.findByUsername("venkatram")).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        JobApplication result = jobApplicationService.create(request, "venkatram");

        assertNotNull(result);
        assertEquals("SDE-1", result.getRole());
        assertEquals(testUser, result.getUser());
        assertEquals(testCompany, result.getCompany());
        verify(jobApplicationRepository, times(1)).save(any(JobApplication.class));
    }

    @Test
    void create_ShouldThrowException_WhenUserNotFound() {
        JobApplicationRequest request = new JobApplicationRequest();
        request.setCompanyId(1L);

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            jobApplicationService.create(request, "ghost");
        });

        verify(jobApplicationRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenCompanyNotFound() {
        JobApplicationRequest request = new JobApplicationRequest();
        request.setCompanyId(999L);

        when(userRepository.findByUsername("venkatram")).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            jobApplicationService.create(request, "venkatram");
        });

        verify(jobApplicationRepository, never()).save(any());
    }

    @Test
    void getOneForUser_ShouldReturnApplication_WhenUserOwnsIt() {
        JobApplication application = new JobApplication("SDE-1", testCompany, testUser, ApplicationStatus.APPLIED, LocalDate.now());
        application.setId(1L);

        when(jobApplicationRepository.findById(1L)).thenReturn(Optional.of(application));

        JobApplication result = jobApplicationService.getOneForUser(1L, "venkatram");

        assertEquals(application, result);
    }

    @Test
    void getOneForUser_ShouldThrowSecurityException_WhenUserDoesNotOwnIt() {
        User otherUser = new User("someone_else", "other@example.com", "hash");
        JobApplication application = new JobApplication("SDE-1", testCompany, otherUser, ApplicationStatus.APPLIED, LocalDate.now());
        application.setId(1L);

        when(jobApplicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThrows(SecurityException.class, () -> {
            jobApplicationService.getOneForUser(1L, "venkatram");
        });
    }
}