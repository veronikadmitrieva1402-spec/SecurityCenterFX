package ru.mirea.project.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = DatabaseManager.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (in == null) {
                throw new RuntimeException("Файл database.properties не найден в resources!");
            }
            props.load(in);
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки настроек БД", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );
    }
}