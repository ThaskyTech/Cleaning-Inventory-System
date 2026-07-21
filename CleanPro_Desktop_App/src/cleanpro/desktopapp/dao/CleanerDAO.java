/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cleanpro.desktopapp.dao;

import cleanpro.desktopapp.model.Cleaner;
import cleanpro.desktopapp.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class CleanerDAO {

    /*
     * Returns all cleaners from the database.
     */
    public List<Cleaner> getAll() {

        List<Cleaner> cleaners = new ArrayList<>();

        String sql = """
                SELECT cleaner_id,
                       cleaner_number,
                       first_name,
                       last_name,
                       phone_number,
                       email,
                       employment_date,
                       is_active
                FROM cleaners
                ORDER BY cleaner_id
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                Cleaner cleaner = mapResultSetToCleaner(resultSet);
                cleaners.add(cleaner);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to retrieve cleaners from the database.",
                    exception
            );
        }

        return cleaners;
    }


    /*
     * Returns one cleaner using the cleaner's primary key.
     *
     * Returns null when no cleaner with the specified ID exists.
     */
    public Cleaner getById(int cleanerId) {

        String sql = """
                SELECT cleaner_id,
                       cleaner_number,
                       first_name,
                       last_name,
                       phone_number,
                       email,
                       employment_date,
                       is_active
                FROM cleaners
                WHERE cleaner_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, cleanerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToCleaner(resultSet);
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to retrieve cleaner with ID " + cleanerId + ".",
                    exception
            );
        }

        return null;
    }


    /*
     * Inserts a new cleaner into the database.
     *
     * cleaner_id is omitted because PostgreSQL should generate it.
     */
    public void insert(Cleaner cleaner) {

        String sql = """
                INSERT INTO cleaners (
                    cleaner_number,
                    first_name,
                    last_name,
                    phone_number,
                    email,
                    employment_date,
                    is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            setCleanerStatementValues(statement, cleaner);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted == 0) {
                throw new RuntimeException(
                        "The cleaner could not be inserted."
                );
            }

            /*
             * The generated cleaner ID can be retrieved here.
             *
             * Your Cleaner model currently has no setCleanerId() method,
             * so the generated ID cannot yet be stored back in the object.
             */
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    int generatedCleanerId = generatedKeys.getInt(1);

                    System.out.println(
                            "Cleaner inserted with ID: "
                                    + generatedCleanerId
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to insert cleaner into the database.",
                    exception
            );
        }
    }


    /*
     * Updates an existing cleaner.
     *
     * Returns true when a matching cleaner was updated.
     * Returns false when the cleaner ID does not exist.
     */
    public boolean update(Cleaner cleaner) {

        String sql = """
                UPDATE cleaners
                SET cleaner_number = ?,
                    first_name = ?,
                    last_name = ?,
                    phone_number = ?,
                    email = ?,
                    employment_date = ?,
                    is_active = ?
                WHERE cleaner_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            setCleanerStatementValues(statement, cleaner);

            statement.setInt(8, cleaner.getCleanerId());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to update cleaner with ID "
                            + cleaner.getCleanerId() + ".",
                    exception
            );
        }
    }


    /*
     * Deletes a cleaner using the primary key.
     *
     * Returns true when the cleaner was deleted.
     * Returns false when the cleaner did not exist.
     */
    public boolean delete(int cleanerId) {

        String sql = """
                DELETE FROM cleaners
                WHERE cleaner_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, cleanerId);

            int rowsDeleted = statement.executeUpdate();

            return rowsDeleted > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Unable to delete cleaner with ID "
                            + cleanerId
                            + ". The cleaner may be linked to a stock issuance.",
                    exception
            );
        }
    }


    /*
     * Places the cleaner object's values into a PreparedStatement.
     *
     * This helper is used by both insert() and update()
     * to avoid repeating the same statement-setting code.
     */
    private void setCleanerStatementValues(
            PreparedStatement statement,
            Cleaner cleaner
    ) throws SQLException {

        statement.setString(1, cleaner.getCleanerNumber());
        statement.setString(2, cleaner.getFirstName());
        statement.setString(3, cleaner.getLastName());

        setNullableString(
                statement,
                4,
                cleaner.getPhoneNumber()
        );

        setNullableString(
                statement,
                5,
                cleaner.getEmail()
        );

        if (cleaner.getEmploymentDate() != null) {
            statement.setDate(
                    6,
                    Date.valueOf(cleaner.getEmploymentDate())
            );
        } else {
            statement.setNull(
                    6,
                    java.sql.Types.DATE
            );
        }

        statement.setBoolean(
                7,
                cleaner.isActive()
        );
    }


    /*
     * Converts the current ResultSet row into a Cleaner object.
     */
    private Cleaner mapResultSetToCleaner(
            ResultSet resultSet
    ) throws SQLException {

        Date employmentDate =
                resultSet.getDate("employment_date");

        return new Cleaner(
                resultSet.getInt("cleaner_id"),
                resultSet.getString("cleaner_number"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("phone_number"),
                resultSet.getString("email"),
                employmentDate != null
                        ? employmentDate.toLocalDate()
                        : null,
                resultSet.getBoolean("is_active")
        );
    }


    /*
     * Correctly handles optional String values.
     */
    private void setNullableString(
            PreparedStatement statement,
            int parameterIndex,
            String value
    ) throws SQLException {

        if (value == null || value.isBlank()) {
            statement.setNull(
                    parameterIndex,
                    java.sql.Types.VARCHAR
            );
        } else {
            statement.setString(
                    parameterIndex,
                    value.trim()
            );
        }
    }
}