/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cleanpro.desktopapp.dao;

import cleanpro.desktopapp.model.StockIssuance;
import cleanpro.desktopapp.model.StockIssuanceItem;
import cleanpro.desktopapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

public class StockIssuanceDAO {

    /*
     * Retrieves every stock issuance and its associated items.
     */
    public List<StockIssuance> getAll() {

        List<StockIssuance> issuances = new ArrayList<>();

        String sql = """
                SELECT issuance_id,
                       issuance_number,
                       cleaner_id,
                       issued_by_user_id,
                       issuance_date,
                       status,
                       notes
                FROM stock_issuances
                ORDER BY issuance_date DESC
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                StockIssuance issuance =
                        mapStockIssuance(resultSet);

                List<StockIssuanceItem> items =
                        getItemsByIssuanceId(
                                connection,
                                issuance.getIssuanceId()
                        );

                issuance.setItems(items);
                issuances.add(issuance);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve stock issuances.",
                    exception
            );
        }

        return issuances;
    }


    /*
     * Retrieves one stock issuance using its primary key.
     *
     * Returns null if no matching issuance exists.
     */
    public StockIssuance getById(int issuanceId) {

        String sql = """
                SELECT issuance_id,
                       issuance_number,
                       cleaner_id,
                       issued_by_user_id,
                       issuance_date,
                       status,
                       notes
                FROM stock_issuances
                WHERE issuance_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, issuanceId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    StockIssuance issuance =
                            mapStockIssuance(resultSet);

                    issuance.setItems(
                            getItemsByIssuanceId(
                                    connection,
                                    issuanceId
                            )
                    );

                    return issuance;
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve stock issuance with ID "
                            + issuanceId + ".",
                    exception
            );
        }

        return null;
    }


    /*
     * Retrieves all stock issuances for one cleaner.
     */
    public List<StockIssuance> getByCleanerId(
            int cleanerId
    ) {

        List<StockIssuance> issuances =
                new ArrayList<>();

        String sql = """
                SELECT issuance_id,
                       issuance_number,
                       cleaner_id,
                       issued_by_user_id,
                       issuance_date,
                       status,
                       notes
                FROM stock_issuances
                WHERE cleaner_id = ?
                ORDER BY issuance_date DESC
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, cleanerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    StockIssuance issuance =
                            mapStockIssuance(resultSet);

                    issuance.setItems(
                            getItemsByIssuanceId(
                                    connection,
                                    issuance.getIssuanceId()
                            )
                    );

                    issuances.add(issuance);
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not retrieve issuance history for cleaner ID "
                            + cleanerId + ".",
                    exception
            );
        }

        return issuances;
    }


    /*
     * Inserts the stock issuance and all associated issuance items.
     *
     * A transaction is used so that either:
     *
     * 1. the issuance and every item are inserted, or
     * 2. none of them are inserted.
     */
    public void insert(StockIssuance issuance) {

        String issuanceSql = """
                INSERT INTO stock_issuances (
                    issuance_number,
                    cleaner_id,
                    issued_by_user_id,
                    issuance_date,
                    status,
                    notes
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String itemSql = """
                INSERT INTO stock_issuance_items (
                    issuance_id,
                    material_id,
                    quantity_issued,
                    unit_price_at_issue
                )
                VALUES (?, ?, ?, ?)
                """;

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            /*
             * Disable automatic commits so all inserts form
             * one database transaction.
             */
            connection.setAutoCommit(false);

            int generatedIssuanceId;

            /*
             * Insert the main stock_issuances record.
             */
            try (
                    PreparedStatement issuanceStatement =
                            connection.prepareStatement(
                                    issuanceSql,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                setIssuanceParameters(
                        issuanceStatement,
                        issuance
                );

                int affectedRows =
                        issuanceStatement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException(
                            "The stock issuance was not inserted."
                    );
                }

                /*
                 * Retrieve the issuance_id generated by PostgreSQL.
                 */
                try (
                        ResultSet generatedKeys =
                                issuanceStatement.getGeneratedKeys()
                ) {

                    if (!generatedKeys.next()) {
                        throw new SQLException(
                                "The generated issuance ID could not be retrieved."
                        );
                    }

                    generatedIssuanceId =
                            generatedKeys.getInt(1);
                }
            }

            /*
             * Insert every item belonging to the issuance.
             */
            try (
                    PreparedStatement itemStatement =
                            connection.prepareStatement(itemSql)
            ) {

                for (StockIssuanceItem item
                        : issuance.getItems()) {

                    itemStatement.setInt(
                            1,
                            generatedIssuanceId
                    );

                    itemStatement.setInt(
                            2,
                            item.getMaterialId()
                    );

                    itemStatement.setInt(
                            3,
                            item.getQuantityIssued()
                    );

                    if (item.getUnitPriceAtIssue() == null) {
                        itemStatement.setNull(
                                4,
                                Types.NUMERIC
                        );
                    } else {
                        itemStatement.setBigDecimal(
                                4,
                                item.getUnitPriceAtIssue()
                        );
                    }

                    itemStatement.addBatch();
                }

                if (!issuance.getItems().isEmpty()) {

                    int[] itemResults =
                            itemStatement.executeBatch();

                    for (int result : itemResults) {

                        if (result == Statement.EXECUTE_FAILED) {
                            throw new SQLException(
                                    "One or more stock issuance items could not be inserted."
                            );
                        }
                    }
                }
            }

            /*
             * Commit only after the main record and all items
             * have been inserted successfully.
             */
            connection.commit();

        } catch (SQLException exception) {

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(
                            rollbackException
                    );
                }
            }

            throw new RuntimeException(
                    "Could not insert the stock issuance.",
                    exception
            );

        } finally {

            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeException) {
                    // The main database work has already completed.
                }
            }
        }
    }


    /*
     * Retrieves the items associated with one issuance.
     *
     * The existing connection is passed into this method so
     * it does not create a separate database connection.
     */
    private List<StockIssuanceItem> getItemsByIssuanceId(
            Connection connection,
            int issuanceId
    ) throws SQLException {

        List<StockIssuanceItem> items =
                new ArrayList<>();

        String sql = """
                SELECT issuance_item_id,
                       issuance_id,
                       material_id,
                       quantity_issued,
                       unit_price_at_issue
                FROM stock_issuance_items
                WHERE issuance_id = ?
                ORDER BY issuance_item_id
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, issuanceId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    StockIssuanceItem item =
                            mapStockIssuanceItem(
                                    resultSet
                            );

                    items.add(item);
                }
            }
        }

        return items;
    }


    /*
     * Converts the current stock_issuances ResultSet row
     * into a StockIssuance object.
     */
    private StockIssuance mapStockIssuance(
            ResultSet resultSet
    ) throws SQLException {

        Timestamp issuanceTimestamp =
                resultSet.getTimestamp(
                        "issuance_date"
                );

        String statusValue =
                resultSet.getString("status");

        StockIssuance.Status status =
                StockIssuance.Status.valueOf(
                        statusValue.toUpperCase()
                );

        return new StockIssuance(
                resultSet.getInt("issuance_id"),
                resultSet.getString(
                        "issuance_number"
                ),
                resultSet.getInt("cleaner_id"),
                resultSet.getInt(
                        "issued_by_user_id"
                ),
                issuanceTimestamp == null
                        ? null
                        : issuanceTimestamp.toLocalDateTime(),
                status,
                resultSet.getString("notes")
        );
    }


    /*
     * Converts the current stock_issuance_items ResultSet
     * row into a StockIssuanceItem object.
     */
    private StockIssuanceItem mapStockIssuanceItem(
            ResultSet resultSet
    ) throws SQLException {

        return new StockIssuanceItem(
                resultSet.getInt(
                        "issuance_item_id"
                ),
                resultSet.getInt("issuance_id"),
                resultSet.getInt("material_id"),
                resultSet.getInt(
                        "quantity_issued"
                ),
                resultSet.getBigDecimal(
                        "unit_price_at_issue"
                )
        );
    }


    /*
     * Sets the parameters for the stock_issuances INSERT.
     */
    private void setIssuanceParameters(
            PreparedStatement statement,
            StockIssuance issuance
    ) throws SQLException {

        statement.setString(
                1,
                issuance.getIssuanceNumber()
        );

        statement.setInt(
                2,
                issuance.getCleanerId()
        );

        statement.setInt(
                3,
                issuance.getIssuedByUserId()
        );

        if (issuance.getIssuanceDate() == null) {
            statement.setNull(
                    4,
                    Types.TIMESTAMP
            );
        } else {
            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(
                            issuance.getIssuanceDate()
                    )
            );
        }

        if (issuance.getStatus() == null) {
            statement.setNull(
                    5,
                    Types.VARCHAR
            );
        } else {
            statement.setString(
                    5,
                    issuance.getStatus().name()
            );
        }

        if (issuance.getNotes() == null
                || issuance.getNotes().isBlank()) {

            statement.setNull(
                    6,
                    Types.VARCHAR
            );

        } else {
            statement.setString(
                    6,
                    issuance.getNotes().trim()
            );
        }
    }
}
