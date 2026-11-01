package ee.taltech.ite4120.parking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ee.taltech.ite4120.parking.model.Permit;
import ee.taltech.ite4120.parking.model.Person;
import ee.taltech.ite4120.parking.service.PermitService;
import java.time.LocalDate;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Business tests from docs/src/specifications/PARK.01-parking-permits.md. They
 * also prove the migrations run from an empty database.
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class ParkingBusinessRulesIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    PermitService permits;

    @Test
    void endDateMustNotBeBeforeStartDate() {
        Permit permit = permit();
        permit.setEndDate(permit.getStartDate().minusDays(1));

        assertThatThrownBy(() -> permits.create(permit))
                .isInstanceOf(ApiClientException.class)
                .hasMessageContaining("endDate must not be before startDate");
    }

    @Test
    void descriptionMustBeAtMost200Characters() {
        Permit permit = permit();
        permit.setDescription("x".repeat(201));

        assertThatThrownBy(() -> permits.create(permit))
                .isInstanceOf(ApiClientException.class)
                .hasMessageContaining("description must be at most 200 characters");
    }

    @Test
    void anEmailIsUniqueAmongActivePersons() {
        Person first = permits.createPerson(person("ada@example.com"));
        assertThat(first.getId()).isNotNull();

        assertThatThrownBy(() -> permits.createPerson(person("ada@example.com")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
    }

    private Permit permit() {
        Person holder = permits.createPerson(person("holder-" + System.nanoTime() + "@example.com"));
        Long resident = permits.requirePermitType("Resident").getId();
        Permit permit = new Permit();
        permit.setPermitTypeId(resident);
        permit.setHolderId(holder.getId());
        permit.setGrantedById(holder.getId());
        permit.setStartDate(LocalDate.of(2026, 9, 1));
        permit.setEndDate(LocalDate.of(2027, 8, 31));
        permit.setDescription("Resident bay for the academic year.");
        return permit;
    }

    private static Person person(String email) {
        Person person = new Person();
        person.setName("Test Person");
        person.setEmail(email);
        return person;
    }
}
