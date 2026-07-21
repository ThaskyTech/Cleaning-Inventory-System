/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cleanpro.desktopapp.dao;

import cleanpro.desktopapp.model.Material;
import cleanpro.desktopapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    /*
     * Retrieves all materials from the database.
     */
    public List<Material> getAll() {

        List<Material> materials = new ArrayList<>();

        String sql = """
                SELECT material_id,
                       material_name,
                       material_description,
                       supplier_id,
                       current_quantity,
                       reorder_level,
                       maximum_stock_level,
                       unit_price,
                       unit
                FROM materials
                ORDER BY material_id
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                materials.add(mapMaterial(resultSet));
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve materials from the database.",
                    exception
            );
        }

        return materials;
    }


    /*
     * Retrieves one material using its primary key.
     *
     * Returns null when the material does not exist.
     */
    public Material getById(int materialId) {

        String sql = """
                SELECT material_id,
                       material_name,
                       material_description,
                       supplier_id,
                       current_quantity,
                       reorder_level,
                       maximum_stock_level,
                       unit_price,
                       unit
                FROM materials
                WHERE material_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, materialId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapMaterial(resultSet);
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve material with ID "
                            + materialId + ".",
                    exception
            );
        }

        return null;
    }


    /*
     * Inserts a new material.
     *
     * material_id is excluded because PostgreSQL generates it.
     */
    public void insert(Material material) {

        String sql = """
                INSERT INTO materials (
                    material_name,
                    material_description,
                    supplier_id,
                    current_quantity,
                    reorder_level,
                    maximum_stock_level,
                    unit_price,
                    unit
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            setMaterialParameters(statement, material);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException(
                        "Material was not inserted."
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not insert material into the database.",
                    exception
            );
        }
    }


    /*
     * Updates an existing material.
     *
     * Returns true when the material was updated.
     * Returns false when the material ID does not exist.
     */
    public boolean update(Material material) {

        String sql = """
                UPDATE materials
                SET material_name = ?,
                    material_description = ?,
                    supplier_id = ?,
                    current_quantity = ?,
                    reorder_level = ?,
                    maximum_stock_level = ?,
                    unit_price = ?,
                    unit = ?
                WHERE material_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            setMaterialParameters(statement, material);

            statement.setInt(
                    9,
                    material.getMaterialId()
            );

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not update material with ID "
                            + material.getMaterialId() + ".",
                    exception
            );
        }
    }


    /*
     * Deletes a material using its primary key.
     *
     * Returns true when a row was deleted.
     * Returns false when the material ID did not exist.
     */
    public boolean delete(int materialId) {

        String sql = """
                DELETE FROM materials
                WHERE material_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, materialId);

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not delete material with ID "
                            + materialId
                            + ". The material may be linked to an existing stock issuance.",
                    exception
            );
        }
    }


    /*
     * Searches for materials by material name.
     *
     * PostgreSQL ILIKE performs a case-insensitive search.
     */
    public List<Material> searchByName(String keyword) {

        List<Material> materials = new ArrayList<>();

        String sql = """
                SELECT material_id,
                       material_name,
                       material_description,
                       supplier_id,
                       current_quantity,
                       reorder_level,
                       maximum_stock_level,
                       unit_price,
                       unit
                FROM materials
                WHERE material_name ILIKE ?
                ORDER BY material_name
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            String searchValue =
                    keyword == null ? "" : keyword.trim();

            statement.setString(
                    1,
                    "%" + searchValue + "%"
            );

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    materials.add(mapMaterial(resultSet));
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not search for materials.",
                    exception
            );
        }

        return materials;
    }


    /*
     * Converts the current ResultSet row into a Material object.
     */
    private Material mapMaterial(
            ResultSet resultSet
    ) throws SQLException {

        return new Material(
                resultSet.getInt("material_id"),
                resultSet.getString("material_name"),
                resultSet.getString("material_description"),
                resultSet.getInt("supplier_id"),
                resultSet.getInt("current_quantity"),
                resultSet.getInt("reorder_level"),
                resultSet.getInt("maximum_stock_level"),
                resultSet.getBigDecimal("unit_price"),
                resultSet.getString("unit")
        );
    }


    /*
     * Places Material values into a PreparedStatement.
     *
     * Used by both insert() and update().
     */
    private void setMaterialParameters(
            PreparedStatement statement,
            Material material
    ) throws SQLException {

        statement.setString(
                1,
                material.getMaterialName()
        );

        setNullableString(
                statement,
                2,
                material.getMaterialDescription(),
                Types.VARCHAR
        );

        statement.setInt(
                3,
                material.getSupplierId()
        );

        statement.setInt(
                4,
                material.getCurrentQuantity()
        );

        statement.setInt(
                5,
                material.getReorderLevel()
        );

        statement.setInt(
                6,
                material.getMaximumStockLevel()
        );

        if (material.getUnitPrice() == null) {
            statement.setNull(
                    7,
                    Types.NUMERIC
            );
        } else {
            statement.setBigDecimal(
                    7,
                    material.getUnitPrice()
            );
        }

        setNullableString(
                statement,
                8,
                material.getUnit(),
                Types.VARCHAR
        );
    }


    /*
     * Handles optional String values.
     */
    private void setNullableString(
            PreparedStatement statement,
            int parameterIndex,
            String value,
            int sqlType
    ) throws SQLException {

        if (value == null || value.isBlank()) {
            statement.setNull(
                    parameterIndex,
                    sqlType
            );
        } else {
            statement.setString(
                    parameterIndex,
                    value.trim()
            );
        }
    }
}
