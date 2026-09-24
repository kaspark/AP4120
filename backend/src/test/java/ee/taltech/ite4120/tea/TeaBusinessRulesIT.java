package ee.taltech.ite4120.tea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ee.taltech.ite4120.tea.model.Tea;
import ee.taltech.ite4120.tea.model.TeaUser;
import ee.taltech.ite4120.tea.service.TeaService;
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
 * Business tests from docs/src/specifications/TEA.01. They also prove the
 * migrations run from an empty database.
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class TeaBusinessRulesIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    TeaService teas;

    @Test
    void expiryDateMustNotBeBeforePurchaseDate() {
        Tea tea = tea();
        tea.setExpiryDate(tea.getPurchaseDate().minusDays(1));

        assertThatThrownBy(() -> teas.create(tea))
                .isInstanceOf(ApiClientException.class)
                .hasMessageContaining("expiryDate must not be before purchaseDate");
    }

    @Test
    void quantityMustNotBeNegative() {
        Tea tea = tea();
        tea.setQuantity(-1);

        assertThatThrownBy(() -> teas.create(tea))
                .isInstanceOf(ApiClientException.class)
                .hasMessageContaining("quantity must not be negative");
    }

    @Test
    void anEmailIsUniqueAmongActiveUsers() {
        TeaUser first = teas.createUser(user("ada@example.com"));
        assertThat(first.getId()).isNotNull();

        assertThatThrownBy(() -> teas.createUser(user("ada@example.com")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
    }

    private Tea tea() {
        TeaUser owner = teas.createUser(user("owner-" + System.nanoTime() + "@example.com"));
        Long black = teas.requireCategory("Black").getId();
        Tea tea = new Tea();
        tea.setName("Earl Grey");
        tea.setBrand("Twinings");
        tea.setCategoryId(black);
        tea.setOwnerId(owner.getId());
        tea.setPurchaseDate(LocalDate.of(2026, 9, 1));
        tea.setExpiryDate(LocalDate.of(2027, 9, 1));
        tea.setQuantity(45);
        tea.setUnit("bags");
        return tea;
    }

    private static TeaUser user(String email) {
        TeaUser user = new TeaUser();
        user.setName("Test User");
        user.setEmail(email);
        return user;
    }
}
