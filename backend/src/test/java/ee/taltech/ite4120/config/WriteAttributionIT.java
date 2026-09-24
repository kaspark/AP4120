package ee.taltech.ite4120.config;

import static org.assertj.core.api.Assertions.assertThat;

import ee.taltech.ite4120.tea.model.Tea;
import ee.taltech.ite4120.tea.model.TeaUser;
import ee.taltech.ite4120.tea.service.TeaService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * The database records who wrote a row. {@link JdbcConfig} hands the signed-in
 * user to {@code core.set_user()} and the platform trigger stamps it.
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class WriteAttributionIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    TeaService teas;

    @AfterEach
    void signOut() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rowsRememberWhoCreatedAndWhoLastChangedThem() {
        signIn("alice");
        Tea created = teas.create(tea());
        assertThat(created.getSysCreatedBy()).isEqualTo("alice");
        assertThat(created.getSysModifiedBy()).isEqualTo("alice");

        signIn("bob");
        Tea update = tea();
        update.setName("Renamed by Bob");
        update.setOwnerId(created.getOwnerId());
        Tea updated = teas.update(created.getId(), update);
        assertThat(updated.getSysCreatedBy()).as("creation is history — it keeps the first author").isEqualTo("alice");
        assertThat(updated.getSysModifiedBy()).isEqualTo("bob");
    }

    @Test
    void outsideARequestTheApplicationItselfIsTheAuthor() {
        SecurityContextHolder.clearContext();
        Tea created = teas.create(tea());
        assertThat(created.getSysCreatedBy()).isEqualTo(JdbcConfig.APPLICATION_USER);
    }

    private Tea tea() {
        TeaUser owner = teas.createUser(user("who-" + System.nanoTime() + "@example.com"));
        Tea tea = new Tea();
        tea.setName("Sencha");
        tea.setBrand("Ippodo");
        tea.setCategoryId(teas.requireCategory("Green").getId());
        tea.setOwnerId(owner.getId());
        tea.setPurchaseDate(LocalDate.of(2026, 1, 1));
        tea.setExpiryDate(LocalDate.of(2027, 1, 1));
        tea.setQuantity(20);
        tea.setUnit("g");
        return tea;
    }

    private static TeaUser user(String email) {
        TeaUser user = new TeaUser();
        user.setName("Attributed user");
        user.setEmail(email);
        return user;
    }

    private static void signIn(String user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }
}
