package io.github.kasperone.faktura;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Proves the production context starts on PostgreSQL 18 and that Flyway applies the schema.
 *
 * <p>Does not use the {@code test} profile. That profile still excludes the datasource so the other
 * tests run without Docker.
 */
@SpringBootTest
@Testcontainers
class PostgresFlywayContextTest {

  @Container @ServiceConnection
  static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

  @Autowired private JdbcTemplate jdbc;

  @Autowired private Environment environment;

  @Test
  void flywayAppliesBaselineOnPostgres18() {
    String serverVersion = jdbc.queryForObject("show server_version", String.class);
    assertTrue(serverVersion.startsWith("18."), serverVersion);

    Long applied =
        jdbc.queryForObject(
            "select count(*) from flyway_schema_history where version = '1' and success",
            Long.class);
    assertEquals(1L, applied);

    String note =
        jdbc.queryForObject("select note from schema_baseline where id = 1", String.class);
    assertEquals("phase 1 baseline", note);

    assertFalse(environment.getProperty("spring.jpa.open-in-view", Boolean.class));
    assertEquals("validate", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
  }
}
