package ee.taltech.ite4120.config;

import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The two endpoints {@code @helex/core} MockAuthProvider probes on startup.
 * Active only with mock auth.
 */
@RestController
@RequestMapping("/api/uma")
@ConditionalOnProperty(name = "auth.mock.enabled", havingValue = "true")
public class MockUmaController {

    @GetMapping("/auth/mock-users")
    public List<String> mockUsers() {
        return List.of("superadmin", "teacher", "student", "viewer");
    }

    @GetMapping("/userinfo")
    public Map<String, Object> userinfo(Authentication authentication) {
        String user = authentication == null ? "guest" : String.valueOf(authentication.getPrincipal());
        return Map.of(
                "userId", user,
                "firstName", user,
                "lastName", "(mock)",
                "fullName", user + " (mock)",
                "privileges", List.of("*"));
    }
}
