package ru.spbu.lda.data.utils;

import com.mysql.jdbc.Driver;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLUtils {

    public static final SQLUtils INSTANCE = new SQLUtils();

    private Connection connection = null;

    @NotNull
    public Connection getConnection() {
        if (connection == null) {
            connection = rawConnect();
        }

        return connection;
    }

    @NotNull
    private Connection rawConnect() {
        try {
            DriverManager.registerDriver(new Driver());

            connection = DriverManager.getConnection("jdbc:mysql://localhost/crawler", "root", "штеукафсу");

            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
