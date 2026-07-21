/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cleanpro.desktopapp.dao;

import cleanpro.desktopapp.model.Role;
import cleanpro.desktopapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    /*
     * Retrieves all roles from the database.
     */
    public List<Role> getAll() {

        List<Role> roles = new ArrayList<>();

        String sql = """
                SELECT role_id,
                       role_name,
                       role_description
                FROM roles
                ORDER BY role_name
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                roles.add(mapRole(resultSet));
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve roles from the database.",
                    exception
            );
        }

        return roles;
    }


    /*
     * Retrieves one role using its primary key.
     *
     * Returns null when no matching role exists.
     */
    public Role getById(int roleId) {

        String sql = """
                SELECT role_id,
                       role_name,
                       role_description
                FROM roles
                WHERE role_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, roleId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRole(resultSet);
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve role with ID "
                            + roleId + ".",
                    exception
            );
        }

        return null;
    }


    /*
     * Converts the current ResultSet row into a Role object.
     */
    private Role mapRole(
            ResultSet resultSet
    ) throws SQLException {

        return new Role(
                resultSet.getInt("role_id"),
                resultSet.getString("role_name"),
                resultSet.getString("role_description")
        );
    }
}
