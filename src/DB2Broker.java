/*
 * Copyright (c) 2012 Andres Gomez Casanova
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

/**
 * Applet that allows to execute SQL queries against a local/remote DB2
 * database.
 * <p>
 * This applet is based on IBM Applt.java example, which is included in the DB2
 * samples directory.
 * 
 * @author Andres Gomez Casanova
 * @version 2012-08-23
 */
public class DB2Broker {
    /**
     * Graphic interface.
     */
    private DB2Viewer browserUI;
    /**
     * Database connection.
     */
    private Connection conn;
    /**
     * Results of the query execution.
     */
    private ResultSet result;
    /**
     * Statement to execute the query.
     */
    private Statement stmt;

    public DB2Broker(final DB2Viewer ui) {
        this.browserUI = ui;
    }

    /**
     * Closes the current connection.
     */
    void closeConnection() {
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (SQLException e) {
                this.showError("Error closing the connection.", e);
            }
        }
    }

    /**
     * Maps the column type of a table with the standard to java.sql.
     * 
     * @param resultMetaData
     *            Metadata of the result.
     * @param columnQty
     *            Quantity of columns.
     * @return A set with the different data types of each column in an array.
     *         Each type is defined according java.sql.Types.
     * @throws SQLException
     *             If there is a problem mapping the columns.
     */
    int[] columnMapping(final ResultSetMetaData resultMetaData, int columnQty)
            throws SQLException {
        final ArrayList<Integer> types = new ArrayList<Integer>(30);
        for (int i = 1; i <= columnQty; i++) {
            int datatype = resultMetaData.getColumnType(i);
            switch (datatype) {
            case Types.BIGINT:
                types.add(new Integer(Types.BIGINT));
                break;
            case Types.BOOLEAN:
                types.add(new Integer(Types.BOOLEAN));
                break;
            case Types.CHAR:
                types.add(new Integer(Types.CHAR));
                break;
            case Types.DATE:
                types.add(new Integer(Types.DATE));
                break;
            case Types.DECIMAL:
                types.add(new Integer(Types.DECIMAL));
                break;
            case Types.DOUBLE:
                types.add(new Integer(Types.DOUBLE));
                break;
            case Types.FLOAT:
                types.add(new Integer(Types.FLOAT));
                break;
            case Types.INTEGER:
                types.add(new Integer(Types.INTEGER));
                break;
            case Types.LONGVARCHAR:
                types.add(new Integer(Types.LONGVARCHAR));
                break;
            case Types.NUMERIC:
                types.add(new Integer(Types.NUMERIC));
                break;
            case Types.REAL:
                types.add(new Integer(Types.REAL));
                break;
            case Types.SMALLINT:
                types.add(new Integer(Types.SMALLINT));
                break;
            case Types.TIME:
                types.add(new Integer(Types.TIME));
                break;
            case Types.TIMESTAMP:
                types.add(new Integer(Types.TIMESTAMP));
                break;
            case Types.VARCHAR:
                types.add(new Integer(Types.VARCHAR));
                break;
            case Types.CLOB:
                types.add(new Integer(Types.CLOB));
                break;
            default:
                int type = resultMetaData.getColumnType(i);

                this.showError("Unknown data type: " + type, new Exception());
                types.add(new Integer(Types.VARCHAR));
            }
        }

        // Converts the arraylist into an array of integers.
        int size = types.size();
        int[] ret = new int[size];
        for (int i = 0; i < size; i++) {
            ret[i] = types.get(i).intValue();
        }
        types.clear();
        return ret;
    }

    /**
     * Connects to the database with the given credentials.
     * 
     * @param server
     *            Server name.
     * @param port
     *            Port number (service name associated number).
     * @param database
     *            Database name
     * @param user
     *            User name.
     * @param password
     *            Password.
     * @return The object connection if the connection could be established.
     */
    boolean connect(final String server, final String port,
            final String database, final String user, final String password) {
        browserUI.showStatus("Connecting");
        final String className = "com.ibm.db2.jcc.DB2Driver";

        // Constructs the URL.
        final String url = "jdbc:db2://" + server + ':' + port + '/' + database;

        boolean ret = false;
        try {
            Class.forName(className).newInstance();

            // Connect to the database with user and password.
            this.conn = DriverManager.getConnection(url, user, password);
            ret = true;
        } catch (InstantiationException e) {
            this.showError("Instantiation problem", e);
        } catch (IllegalAccessException e) {
            this.showError("Illegal Access problem", e);
        } catch (ClassNotFoundException e) {
            this.showError("Class not found" + className, e);
        } catch (SQLException e) {
            this.showError("SQL error", e);
        }
        browserUI.showStatus("Connected");
        return ret;
    }

    /**
     * Executes the query against the database.
     * 
     * @param sentence
     *            Query to execute.
     */
    void executeQuery(final String sentence) {
        this.browserUI.showStatus("Processing queries");
        System.out.println("Executing: " + sentence);
        try {
            this.stmt = this.conn.createStatement();
            this.result = this.stmt.executeQuery(sentence);

            final ResultSetMetaData resultMetaData = this.result.getMetaData();
            // Quantity of columns.
            int columnQty = resultMetaData.getColumnCount();
            final ArrayList<String> names = this.getColumnNames(resultMetaData);
            // Column mapping.
            int[] columnType = this.columnMapping(resultMetaData, columnQty);
            final ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
            while (this.result.next()) {
                final ArrayList<String> row = this.mappingAndFilling(
                        this.result, columnQty, columnType);
                data.add(row);
            }
            this.browserUI.showStatus("Closing statement.");
            try {
                this.result.close();
            } catch (SQLException e) {
                this.showError("Error closing result.", e);
            }
            try {
                this.stmt.close();
            } catch (SQLException e) {
                this.showError("Error closing statement.", e);
            }
            this.browserUI.createTable(names, data);
        } catch (SQLException e) {
            this.showError("Error executing the query.", e);
        }
    }

    /**
     * Retrieves the names of the columns.
     * 
     * @param metaData
     *            Metadata of the result.
     * @return Names of the columns.
     * @throws SQLException
     *             If there is a problem in the process.
     */
    private ArrayList<String> getColumnNames(final ResultSetMetaData metaData)
            throws SQLException {
        final ArrayList<String> names = new ArrayList<String>();
        final int size = metaData.getColumnCount();
        for (int i = 0; i < size; i++) {
            names.add(metaData.getColumnName(i + 1));
        }
        return names;
    }

    /**
     * Displays a windows asking for the credentials.
     */
    void getCredentials() {
        final Frame frame = new Frame(this);
        frame.setVisible(true);
    }

    /**
     * Takes the values of the result set and put them in an array. This process
     * is executed for each row.
     * 
     * @param result
     *            Result, positioned in the row to analyze.
     * @param columnQty
     *            Quantity of columns.
     * @param columnType
     *            Types of the columns.
     * @throws SQLException
     *             If there is a problem in the process.
     */
    ArrayList<String> mappingAndFilling(final ResultSet result,
            final int columnQty, final int[] columnType) throws SQLException {
        final ArrayList<String> row = new ArrayList<String>();
        int k;

        String value = null;
        for (k = 1; k <= columnQty; k++) {

            switch (columnType[k - 1]) {
            case Types.BIGINT:
                java.math.BigDecimal valueBigDecimal = result.getBigDecimal(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = valueBigDecimal.toEngineeringString();
                }
                break;
            case Types.BOOLEAN:
                boolean valueBoolean = result.getBoolean(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = Boolean.toString(valueBoolean);
                }
                break;
            case Types.DECIMAL:
                long valueLong = result.getLong(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = Long.toString(valueLong);
                }
                break;
            case Types.DOUBLE:
                double valueDouble = result.getDouble(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = Double.toString(valueDouble);
                }
                break;
            case Types.FLOAT:
                float valueFloat = result.getFloat(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = Float.toString(valueFloat);
                }
                break;
            case Types.INTEGER:
                int valueInt = result.getInt(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = Integer.toString(valueInt);
                }
                break;
            case Types.NUMERIC:
                valueLong = result.getLong(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = Long.toString(valueLong);
                }
                break;
            case Types.REAL:
                valueDouble = result.getDouble(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                }
                break;
            case Types.SMALLINT:
                int valueShort = result.getInt(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = Integer.toString(valueShort);
                }
                break;
            case Types.DATE:
                java.sql.Date valueDate = result.getDate(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = valueDate.toString();
                }
                break;
            case Types.TIME:
                java.sql.Time valueTime = result.getTime(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = valueTime.toString();
                }
                break;
            case Types.TIMESTAMP:
                java.sql.Timestamp valueTimestamp = result.getTimestamp(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = valueTimestamp.toString();
                }
                break;
            case Types.LONGVARCHAR:
                String valueString = result.getString(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = valueString;
                }
                break;
            case Types.VARCHAR:
                valueString = result.getString(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = valueString;
                }
                break;
            case Types.CHAR:
                valueString = result.getString(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = valueString;
                }
                break;
            case Types.CLOB:
                valueString = result.getString(k);
                if (result.wasNull()) {
                    value = "NULL";
                } else {
                    value = "CLOB";
                }
                break;
            default:
                this.showError("Unknown data type", new Exception());
                value = "UNKNOWN";
            }
            row.add(value);

        }
        return row;

    }

    /**
     * Displays a given error. The message in the GUI and the stack in the
     * output.
     * 
     * @param message
     *            Descriptive message.
     * @param exp
     *            Exception to show.
     */
    void showError(final String message, final Exception exp) {
        this.browserUI.showStatus("Error: " + message);
        System.err.println("Error " + message);
        exp.printStackTrace();
    }

}
