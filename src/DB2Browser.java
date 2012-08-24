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

import java.applet.Applet;
import java.awt.Graphics;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

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
public class DB2Browser extends Applet {
    /**
     * Generated ID.
     */
    private static final long serialVersionUID = 3666831621495499025L;
    /**
     * Database connection.
     */
    private Connection conn;

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
    private Connection connect(final String server, final String port,
            final String database, final String user, final String password) {
        final String className = "com.ibm.db2.jcc.DB2Driver";

        // Constructs the URL.
        final String url = "jdbc:db2://" + server + ':' + port + '/' + database;

        Connection conn = null;
        try {
            Class.forName(className).newInstance();

            // Connect to the database with user and password.
            conn = DriverManager.getConnection(url, user, password);
        } catch (InstantiationException e) {
            showError("Instantiation problem", e);
        } catch (IllegalAccessException e) {
            showError("Illegal Access problem", e);
        } catch (ClassNotFoundException e) {
            showError("Class not found" + className, e);
        } catch (SQLException e) {
            showError("SQL error", e);
        }
        return conn;
    }

    /**
     * Displays a windows asking for the credentials.
     * 
     * @return The connection if it could have been established.
     */
    private Connection getCredentials() {
        // TODO Asks for the credentials.
        final String server = null;
        final String port = null;
        final String database = null;
        final String user = null;
        final String password = null;
        final Connection conn = connect(server, port, database, user, password);
        return conn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.applet.Applet#init()
     */
    @Override
    public void init() {
        // Get parameter values from the HTML page.
        final String server = getParameter("server");
        final String port = getParameter("port");
        final String database = getParameter("database");
        final String user = getParameter("user");
        final String password = getParameter("password");

        if (server == null || server.equals("") || port == null
                || port.equals("") || database == null || database.equals("")
                || user == null || user.equals("") || password == null
                || password.equals("")) {
            // Asks for the credentials.
            System.err.println("Asks for credentials: " + server + port
                    + database + user + password);
            this.conn = getCredentials();
        } else {
            // Connect with the provided parameters from the HTML.
            this.conn = connect(server, port, database, user, password);
        }
    }

    @Override
    public void paint(final Graphics g) {
        if (this.conn != null) {
            try {
                // retrieve data from database
                g.drawString(
                        "First, let's retrieve some data from the database...",
                        10, 10);

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT tabname FROM syscat.tables WHERE tabschema not like 'SYS%'");
                g.drawString("Received results:", 10, 25);

                // display the result set
                // rs.next() returns false when there are no more rows
                int y = 50;
                int i = 0;
                while (rs.next() && (i < 2)) {
                    i++;
                    String a = rs.getString(1);
                    String oneLine = " table name = " + a;
                    g.drawString(oneLine, 20, y);
                    y = y + 15;
                }
                stmt.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            g.drawString("There is not a valid database connection.", 10, 10);
        }
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
    private void showError(final String message, final Exception exp) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    JLabel label = new JLabel(message);
                    exp.printStackTrace();
                    add(label);
                }
            });
        } catch (Exception e) {
            System.err.println("Problem showing the error: " + exp);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.applet.Applet#stop()
     */
    @Override
    public void stop() {
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (SQLException e) {
                showError("Error closing the connection.", e);
            }
        }
        super.stop();
    }
}
