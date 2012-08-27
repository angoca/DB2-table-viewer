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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

/**
 * Creates the main window of the applet to browse tables in DB2.
 * 
 * @author Andres Gomez Casanova
 * @version 2012-08-27
 */
public class DB2Viewer extends JApplet implements ActionListener {

    /**
     * Generated ID.
     */
    private static final long serialVersionUID = 7909104467572783948L;

    /**
     * DB2 logic.
     */
    private DB2Broker db2;
    /**
     * Label for the table result.
     */
    private JLabel labelResults;
    /**
     * Scroll for the table.
     */
    private JScrollPane scrollPane;
    /**
     * Layout.
     */
    private SpringLayout springLayout;
    /**
     * Table where the results are presented.
     */
    private JTable tableResults;
    /**
     * Area to write the query.
     */
    private JTextArea textSentence;

    /**
     * Default constructor.
     */
    public DB2Viewer() {
        // Nothing.
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.db2.executeQuery(textSentence.getText());
    }

    /**
     * Converts an arraylist into an array of Strings.
     * 
     * @param data
     *            Data to be converted.
     * @return array of Strings.
     */
    String[] convertArray(final ArrayList<String> data) {
        String[] ret = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            ret[i] = data.get(i);
        }
        return ret;
    }

    /**
     * Creates the table with the values of the query.
     * 
     * @param columnNames
     *            Name of the columns.
     * @param data
     *            Data of the table.
     */
    void createTable(final ArrayList<String> columnNames,
            final ArrayList<ArrayList<String>> data) {
        final String[][] dataConvered = new String[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            dataConvered[i] = convertArray(data.get(i));
        }
        final String[] columnsConverted = convertArray(columnNames);

        this.getContentPane().remove(this.tableResults);
        this.getContentPane().remove(this.scrollPane);
        this.tableResults = new JTable(dataConvered, columnsConverted);
        this.scrollPane = new JScrollPane(tableResults,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.tableResults.setFillsViewportHeight(true);

        this.getContentPane().validate();
        this.tableResults.validate();
        this.tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        springLayout.putConstraint(SpringLayout.NORTH, this.scrollPane, 6,
                SpringLayout.SOUTH, this.labelResults);
        springLayout.putConstraint(SpringLayout.WEST, this.scrollPane, 10,
                SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, this.scrollPane, -10,
                SpringLayout.SOUTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, this.scrollPane, -10,
                SpringLayout.EAST, this.getContentPane());
        this.getContentPane().add(this.scrollPane);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.applet.Applet#init()
     */
    @Override
    public void init() {
        initialize();
        this.db2 = new DB2Broker(this);
        showStatus("Establishing connection...");
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
            this.db2.getCredentials();
        } else {
            // Connect with the provided parameters from the HTML.
            final boolean ret = this.db2.connect(server, port, database, user,
                    password);
            if (!ret){
                this.db2.getCredentials();
            }
        }
    }

    /**
     * Initializes the UI.
     */
    private void initialize() {
        this.springLayout = new SpringLayout();
        this.getContentPane().setLayout(springLayout);

        final JLabel labelSentence = new JLabel("Sentence to execute:");
        springLayout.putConstraint(SpringLayout.NORTH, labelSentence, 10,
                SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, labelSentence, 10,
                SpringLayout.WEST, this.getContentPane());
        this.getContentPane().add(labelSentence);

        this.textSentence = new JTextArea();
        springLayout.putConstraint(SpringLayout.NORTH, this.textSentence, 6,
                SpringLayout.SOUTH, labelSentence);
        springLayout.putConstraint(SpringLayout.WEST, this.textSentence, 10,
                SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, this.textSentence, -10,
                SpringLayout.EAST, this.getContentPane());
        this.getContentPane().add(this.textSentence);

        final JButton buttonExecute = new JButton("Execute query");
        springLayout.putConstraint(SpringLayout.NORTH, buttonExecute, 6,
                SpringLayout.SOUTH, this.textSentence);
        springLayout.putConstraint(SpringLayout.WEST, buttonExecute, 10,
                SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, buttonExecute, -10,
                SpringLayout.EAST, this.getContentPane());
        buttonExecute.addActionListener(this);
        this.getContentPane().add(buttonExecute);

        final JSeparator separator = new JSeparator();
        springLayout.putConstraint(SpringLayout.SOUTH, this.textSentence, -35,
                SpringLayout.NORTH, separator);
        springLayout.putConstraint(SpringLayout.NORTH, separator, 150,
                SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, buttonExecute, -6,
                SpringLayout.NORTH, separator);
        springLayout.putConstraint(SpringLayout.EAST, separator, -10,
                SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, separator, 10,
                SpringLayout.WEST, this.getContentPane());
        this.getContentPane().add(separator);

        this.labelResults = new JLabel("Results:");
        springLayout.putConstraint(SpringLayout.NORTH, this.labelResults, 10,
                SpringLayout.SOUTH, separator);
        springLayout.putConstraint(SpringLayout.WEST, this.labelResults, 10,
                SpringLayout.WEST, this.getContentPane());
        this.getContentPane().add(this.labelResults);

        this.tableResults = new JTable();
        this.scrollPane = new JScrollPane(tableResults);
        this.tableResults.setFillsViewportHeight(true);
        springLayout.putConstraint(SpringLayout.NORTH, this.scrollPane, 6,
                SpringLayout.SOUTH, this.labelResults);
        springLayout.putConstraint(SpringLayout.WEST, this.scrollPane, 10,
                SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, this.scrollPane, -10,
                SpringLayout.SOUTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, this.scrollPane, -10,
                SpringLayout.EAST, this.getContentPane());
        this.getContentPane().add(this.scrollPane);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.applet.Applet#stop()
     */
    @Override
    public void stop() {
        this.showStatus("Closing connetion.");
        this.db2.closeConnection();
        super.stop();
    }

}
