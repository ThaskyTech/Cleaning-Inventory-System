/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cleanpro.desktopapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/cleanpro_db";

    private static final String USERNAME = "postgres";

    private static final String PASSWORD = "your_postgresql_password";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }
}
