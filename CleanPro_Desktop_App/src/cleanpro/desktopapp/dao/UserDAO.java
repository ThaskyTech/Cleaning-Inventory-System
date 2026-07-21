/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cleanpro.desktopapp.dao;

import cleanpro.desktopapp.model.User;
import cleanpro.desktopapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

public class UserDAO {

    /*
     * Retrieves a user using their username.
     *
     * This method is used during login.
     * It returns null when no matching user exists.
     */
    public User getByUsername(String username) {

        String sql = """
                SELECT user_id,
                       first_name,
                       last_name,
                       email,
                       phone_number,
                       username,
                       password_hash,
                       role_id,
                       is_active,
                       last_login
                FROM users
                WHERE LOWER(username) = LOWER(?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    username == null ? null : username.trim()
            );

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve the user with username: "
                            + username,
                    exception
            );
        }

        return null;
    }


    /*
     * Checks whether a username already exists.
     *
     * The comparison is case-insensitive so Marco and marco
     * cannot be registered as separate usernames.
     */
    public boolean usernameExists(String username) {

        String sql = """
                SELECT 1
                FROM users
                WHERE LOWER(username) = LOWER(?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    username == null ? null : username.trim()
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not check whether the username already exists.",
                    exception
            );
        }
    }


    /*
     * Checks whether an email address already exists.
     *
     * The comparison is case-insensitive.
     */
    public boolean emailExists(String email) {

        String sql = """
                SELECT 1
                FROM users
                WHERE LOWER(email) = LOWER(?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    email == null ? null : email.trim()
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not check whether the email address already exists.",
                    exception
            );
        }
    }


    /*
     * Inserts a new user into the database.
     *
     * user_id is excluded because PostgreSQL generates it.
     * The password stored here is already hashed by LoginService.
     */
    public void insert(User user) {

        String sql = """
                INSERT INTO users (
                    first_name,
                    last_name,
                    email,
                    phone_number,
                    username,
                    password_hash,
                    role_id,
                    is_active,
                    last_login
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            setUserParameters(statement, user);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException(
                        "User was not inserted."
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not insert the user into the database.",
                    exception
            );
        }
    }


    /*
     * Updates the user's last successful login date and time.
     */
    public void updateLastLogin(
            int userId,
            LocalDateTime loginTime
    ) {

        String sql = """
                UPDATE users
                SET last_login = ?
                WHERE user_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            if (loginTime == null) {
                statement.setNull(
                        1,
                        Types.TIMESTAMP
                );
            } else {
                statement.setTimestamp(
                        1,
                        Timestamp.valueOf(loginTime)
                );
            }

            statement.setInt(2, userId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException(
                        "Could not update the last login because user ID "
                                + userId
                                + " does not exist."
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not update the last login for user ID "
                            + userId + ".",
                    exception
            );
        }
    }


    /*
     * Converts the current ResultSet row into a User object.
     */
    private User mapUser(
            ResultSet resultSet
    ) throws SQLException {

        Timestamp lastLoginTimestamp =
                resultSet.getTimestamp("last_login");

        LocalDateTime lastLogin =
                lastLoginTimestamp == null
                        ? null
                        : lastLoginTimestamp.toLocalDateTime();

        return new User(
                resultSet.getInt("user_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("email"),
                resultSet.getString("phone_number"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getInt("role_id"),
                resultSet.getBoolean("is_active"),
                lastLogin
        );
    }


    /*
     * Places the User values into the INSERT statement.
     */
    private void setUserParameters(
            PreparedStatement statement,
            User user
    ) throws SQLException {

        statement.setString(
                1,
                user.getFirstName().trim()
        );

        statement.setString(
                2,
                user.getLastName().trim()
        );

        statement.setString(
                3,
                user.getEmail().trim()
        );

        setNullableString(
                statement,
                4,
                user.getPhoneNumber()
        );

        statement.setString(
                5,
                user.getUsername().trim()
        );

        statement.setString(
                6,
                user.getPasswordHash()
        );

        statement.setInt(
                7,
                user.getRoleId()
        );

        statement.setBoolean(
                8,
                user.isActive()
        );

        if (user.getLastLogin() == null) {
            statement.setNull(
                    9,
                    Types.TIMESTAMP
            );
        } else {
            statement.setTimestamp(
                    9,
                    Timestamp.valueOf(
                            user.getLastLogin()
                    )
            );
        }
    }


    /*
     * Handles optional String values.
     */
    private void setNullableString(
            PreparedStatement statement,
            int parameterIndex,
            String value
    ) throws SQLException {

        if (value == null || value.isBlank()) {
            statement.setNull(
                    parameterIndex,
                    Types.VARCHAR
            );
        } else {
            statement.setString(
                    parameterIndex,
                    value.trim()
            );
        }
    }
}
