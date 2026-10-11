package com.campussphere.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Locale;

/** Ensures local and deployed application instances use the configured MySQL database. */
@Component
public class DatabaseStartupVerifier implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DatabaseStartupVerifier.class);
    private final DataSource dataSource;
    private final String expectedDatabase;

    public DatabaseStartupVerifier(DataSource dataSource,
                                   @Value("${MYSQL_DATABASE:campussphere}") String expectedDatabase) {
        this.dataSource = dataSource;
        this.expectedDatabase = expectedDatabase;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            String product = metadata.getDatabaseProductName();
            String catalog = connection.getCatalog();
            if (product == null || !product.toLowerCase(Locale.ROOT).contains("mysql")) {
                throw new IllegalStateException("CampusSphere requires MySQL; connected database product was " + product);
            }
            if (catalog == null || !expectedDatabase.equalsIgnoreCase(catalog)) {
                throw new IllegalStateException("CampusSphere requires database '" + expectedDatabase
                        + "'; connected catalog was '" + catalog + "'");
            }
            log.info("Database verified: product={}, version={}, catalog={}, driver={}",
                    product, metadata.getDatabaseProductVersion(), catalog, metadata.getDriverName());
        }
    }
}
