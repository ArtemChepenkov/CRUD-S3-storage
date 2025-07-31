package ru.chepenkov.storage.DBMigration;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseMigration {

    public static void runMigrations(String jdbcUrl, String username, String password) {

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.yaml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update(new Contexts());

            System.out.println("✅ Liquibase migration completed.");

            connection.close();

        } catch (SQLException | DatabaseException e) {
            System.err.println("DB fail");
            throw new RuntimeException(e);
        } catch (LiquibaseException e) {
            System.err.println("DB fail");
            throw new RuntimeException(e);
        }
    }
}
