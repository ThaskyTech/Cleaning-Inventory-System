/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cleanpro.desktopapp.dao;

import cleanpro.desktopapp.model.Supplier;
import cleanpro.desktopapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    /*
     * Retrieves all suppliers from the database.
     */
    public List<Supplier> getAll() {

        List<Supplier> suppliers = new ArrayList<>();

        String sql = """
                SELECT supplier_id,
                       supplier_code,
                       supplier_name,
                       contact_person,
                       phone_number,
                       email,
                       address,
                       city,
                       province,
                       postal_code
                FROM suppliers
                ORDER BY supplier_name
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                suppliers.add(mapSupplier(resultSet));
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve suppliers from the database.",
                    exception
            );
        }

        return suppliers;
    }


    /*
     * Retrieves one supplier using the supplier's primary key.
     *
     * Returns null when no matching supplier exists.
     */
    public Supplier getById(int supplierId) {

        String sql = """
                SELECT supplier_id,
                       supplier_code,
                       supplier_name,
                       contact_person,
                       phone_number,
                       email,
                       address,
                       city,
                       province,
                       postal_code
                FROM suppliers
                WHERE supplier_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, supplierId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapSupplier(resultSet);
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve supplier with ID "
                            + supplierId + ".",
                    exception
            );
        }

        return null;
    }


    /*
     * Inserts a new supplier.
     *
     * supplier_id is omitted because PostgreSQL generates it.
     */
    public void insert(Supplier supplier) {

        String sql = """
                INSERT INTO suppliers (
                    supplier_code,
                    supplier_name,
                    contact_person,
                    phone_number,
                    email,
                    address,
                    city,
                    province,
                    postal_code
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            setSupplierParameters(statement, supplier);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException(
                        "Supplier was not inserted."
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not insert supplier into the database.",
                    exception
            );
        }
    }


    /*
     * Updates an existing supplier.
     *
     * Returns true when the supplier was updated.
     * Returns false when the supplier ID does not exist.
     */
    public boolean update(Supplier supplier) {

        String sql = """
                UPDATE suppliers
                SET supplier_code = ?,
                    supplier_name = ?,
                    contact_person = ?,
                    phone_number = ?,
                    email = ?,
                    address = ?,
                    city = ?,
                    province = ?,
                    postal_code = ?
                WHERE supplier_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            setSupplierParameters(statement, supplier);

            statement.setInt(
                    10,
                    supplier.getSupplierId()
            );

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not update supplier with ID "
                            + supplier.getSupplierId() + ".",
                    exception
            );
        }
    }


    /*
     * Deletes a supplier using its primary key.
     *
     * Returns true when a row was deleted.
     * Returns false when the supplier ID did not exist.
     */
    public boolean delete(int supplierId) {

        String sql = """
                DELETE FROM suppliers
                WHERE supplier_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, supplierId);

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not delete supplier with ID "
                            + supplierId
                            + ". The supplier may be linked to existing materials.",
                    exception
            );
        }
    }


    /*
     * Converts the current ResultSet row into a Supplier object.
     */
    private Supplier mapSupplier(
            ResultSet resultSet
    ) throws SQLException {

        return new Supplier(
                resultSet.getInt("supplier_id"),
                resultSet.getString("supplier_code"),
                resultSet.getString("supplier_name"),
                resultSet.getString("contact_person"),
                resultSet.getString("phone_number"),
                resultSet.getString("email"),
                resultSet.getString("address"),
                resultSet.getString("city"),
                resultSet.getString("province"),
                resultSet.getString("postal_code")
        );
    }


    /*
     * Places Supplier values into a PreparedStatement.
     *
     * Used by both insert() and update().
     */
    private void setSupplierParameters(
            PreparedStatement statement,
            Supplier supplier
    ) throws SQLException {

        statement.setString(
                1,
                supplier.getSupplierCode()
        );

        statement.setString(
                2,
                supplier.getSupplierName()
        );

        setNullableString(
                statement,
                3,
                supplier.getContactPerson()
        );

        setNullableString(
                statement,
                4,
                supplier.getPhoneNumber()
        );

        setNullableString(
                statement,
                5,
                supplier.getEmail()
        );

        setNullableString(
                statement,
                6,
                supplier.getAddress()
        );

        setNullableString(
                statement,
                7,
                supplier.getCity()
        );

        setNullableString(
                statement,
                8,
                supplier.getProvince()
        );

        setNullableString(
                statement,
                9,
                supplier.getPostalCode()
        );
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
