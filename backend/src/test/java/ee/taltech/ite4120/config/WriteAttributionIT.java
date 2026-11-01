package ee.taltech.ite4120.config;

import static org.assertj.core.api.Assertions.assertThat;

import ee.taltech.ite4120.parking.model.Permit;
import ee.taltech.ite4120.parking.model.Person;
import ee.taltech.ite4120.parking.service.PermitService;
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
    PermitService permits;

    @AfterEach
    void signOut() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rowsRememberWhoCreatedAndWhoLastChangedThem() {
        signIn("alice");
        Permit created = permits.create(permit());
        assertThat(created.getSysCreatedBy()).isEqualTo("alice");
        assertThat(created.getSysModifiedBy()).isEqualTo("alice");

        signIn("bob");
        Permit update = permit();
        update.setDescription("Updated by Bob");
        update.setHolderId(created.getHolderId());
        update.setGrantedById(created.getGrantedById());
        Permit updated = permits.update(created.getId(), update);
        assertThat(updated.getSysCreatedBy()).as("creation is history — it keeps the first author").isEqualTo("alice");
        assertThat(updated.getSysModifiedBy()).isEqualTo("bob");
    }

    @Test
    void outsideARequestTheApplicationItselfIsTheAuthor() {
        SecurityContextHolder.clearContext();
        Permit created = permits.create(permit());
        assertThat(created.getSysCreatedBy()).isEqualTo(JdbcConfig.APPLICATION_USER);
    }

    private Permit permit() {
        Person holder = permits.createPerson(person("who-" + System.nanoTime() + "@example.com"));
        Permit permit = new Permit();
        permit.setPermitTypeId(permits.requirePermitType("Staff").getId());
        permit.setHolderId(holder.getId());
        permit.setGrantedById(holder.getId());
        permit.setStartDate(LocalDate.of(2026, 1, 1));
        permit.setEndDate(LocalDate.of(2027, 1, 1));
        permit.setDescription("Staff parking by the main building.");
        return permit;
    }

    private static Person person(String email) {
        Person person = new Person();
        person.setName("Attributed person");
        person.setEmail(email);
        return person;
    }

    private static void signIn(String user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }
}
