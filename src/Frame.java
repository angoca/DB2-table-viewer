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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

/**
 * Creates a window to ask for the credentials.
 * 
 * @author Andres Gomez Casanova
 * @version 2012-08-27
 */
public class Frame extends JFrame implements ActionListener {

    /**
     * Generated ID.
     */
    private static final long serialVersionUID = 4467361042563754806L;
    /**
     * Root container.
     */
    private JPanel panelConnection;
    /**
     * Text of the hostname.
     */
    private JTextField textHostname;
    /**
     * Text of the port.
     */
    private JTextField textPort;
    /**
     * Text of the database.
     */
    private JTextField textDatabase;
    /**
     * Text of the username.
     */
    private JTextField textUsername;
    /**
     * Text of the password.
     */
    private JPasswordField textPassword;
    /**
     * DB2 logic.
     */
    final DB2Broker broker;

    /**
     * Creates the frame.
     */
    public Frame(final DB2Broker browser) {
        this.broker = browser;
        this.setTitle("Connection properties");
        this.setBounds(100, 100, 250, 230);
        this.panelConnection = new JPanel();
        this.panelConnection.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.panelConnection);
        this.panelConnection.setLayout(new MigLayout("", "[46px][86px,grow]",
                "[][23px][][][][][]"));

        final JLabel labelProperties = new JLabel("Connection properties");
        this.panelConnection.add(labelProperties, "cell 0 0 2 1,alignx center");

        JLabel labelHostname = new JLabel("Hostname / IP:");
        this.panelConnection.add(labelHostname, "cell 0 1,alignx trailing");

        this.textHostname = new JTextField();
        this.textHostname.setToolTipText("Hostname or IP address of the "
                + "database server");
        this.textHostname.setText("localhost");
        this.panelConnection.add(this.textHostname, "cell 1 1,growx");

        JLabel labelPort = new JLabel("Port:");
        this.panelConnection.add(labelPort, "cell 0 2,alignx trailing");

        this.textPort = new JTextField();
        this.textPort.setToolTipText("Port of the instance (Service "
                + "name port)");
        this.textPort.setText("50000");
        this.panelConnection.add(this.textPort, "cell 1 2,growx");

        JLabel labelDatabase = new JLabel("Database:");
        this.panelConnection.add(labelDatabase, "cell 0 3,alignx trailing");

        this.textDatabase = new JTextField();
        this.textDatabase.setToolTipText("Database name");
        this.textDatabase.setText("sample");
        this.panelConnection.add(this.textDatabase, "cell 1 3,growx");

        JLabel labelUsername = new JLabel("Username:");
        this.panelConnection.add(labelUsername, "cell 0 4,alignx trailing");

        this.textUsername = new JTextField();
        this.textUsername.setToolTipText("Username used for the connection");
        this.textUsername.setText("db2admin");
        this.panelConnection.add(this.textUsername, "cell 1 4,growx");

        JLabel labelPassword = new JLabel("Password:");
        this.panelConnection.add(labelPassword, "cell 0 5,alignx trailing");

        this.textPassword = new JPasswordField();
        this.textPassword.setToolTipText("Password for the provided username");
        this.textPassword.setText("admin");
        this.panelConnection.add(this.textPassword, "cell 1 5,growx");

        JButton buttonConnect = new JButton("Connect");
        buttonConnect.addActionListener(this);
        buttonConnect.setToolTipText("Establishes a connection to the "
                + "database with the provided credentials");
        this.panelConnection.add(buttonConnect, "cell 0 6 2 1,growx");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String server = textHostname.getText();
        final String port = textPort.getText();
        final String database = textDatabase.getText();
        final String username = textUsername.getText();
        final char[] password = textPassword.getPassword();
        final boolean ret = broker.connect(server, port, database, username,
                new String(password));
        if (ret) {
            this.setVisible(false);
        }
    }
}
