/*
 *  File Name:    NewAccountController.java
 *  Project Name: GUIClient
 *
 *  Copyright (c) 2021 Bradley Willcott
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * ****************************************************************
 * Name: Bradley Willcott
 * ID:   M198449
 * Date: 7 Oct 2021
 * ****************************************************************
 */

package com.bewsoftware.tafe.java3.at2.four.gui.view;

import com.bewsoftware.tafe.java3.at2.four.gui.App;
import com.bewsoftware.tafe.java3.at2.four.gui.ViewController;
import com.bewsoftware.tafe.java3.at2.four.gui.Views;
import common.UserAccount;
import java.beans.PropertyChangeEvent;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static com.bewsoftware.tafe.java3.at2.four.gui.Views.BLANK;
import static com.bewsoftware.tafe.java3.at2.four.gui.Views.NEW_ACCOUNT;
import static common.UserAccount.RMI_NAME;
import static java.util.regex.Pattern.compile;

/**
 * FXML Controller class for the 'NewAccount.fxml' file.
 *
 * @author <a href="mailto:bw.opensource@yahoo.com">Bradley Willcott</a>
 *
 * @since 1.0
 * @version 1.0
 */
public class NewAccountController implements ViewController
{
    private static final String PASSWORD_PATTERN_1_STRING = "^.*[A-Z]+.*.*$";

    private static final String PASSWORD_PATTERN_2_STRING = "^.*[0-9]+.*[0-9]+.*$";

    private static final String USERNAME_PATTERN_1_STRING = "^[a-zA-Z]{1}.+$";

    private static final String USERNAME_PATTERN_2_STRING = "^[a-zA-Z]{1}[a-zA-Z0-9]+$";

    /**
     * Used to validate a new password - pattern 1.
     */
    private static final Pattern PASSWORD_PATTERN_1;

    /**
     * Used to validate a new password - pattern 2.
     */
    private static final Pattern PASSWORD_PATTERN_2;

    /**
     * Used to validate a new username - pattern 1.
     */
    private static final Pattern USERNAME_PATTERN_1;

    /**
     * Used to validate a new username - pattern 2.
     */
    private static final Pattern USERNAME_PATTERN_2;

    static
    {
        USERNAME_PATTERN_1 = Pattern.compile(USERNAME_PATTERN_1_STRING);
        USERNAME_PATTERN_2 = Pattern.compile(USERNAME_PATTERN_2_STRING);
        PASSWORD_PATTERN_1 = compile(PASSWORD_PATTERN_1_STRING);
        PASSWORD_PATTERN_2 = compile(PASSWORD_PATTERN_2_STRING);
    }

    /**
     * Verify that the password fulfills the basic requirements.
     * <p>
     * 1. Minimum of 8 characters in length
     * 2. Contains at least one CAPITAL letter
     * 3. Contains at least two numbers: 0 - 9
     *
     * @param password1 text to verify
     * @param password2 text to verify
     *
     * @return {@code true} if valid
     */
    private boolean validPassword(String password1, String password2)
    {
        boolean rtn = false;

        if (password1 != null && password1.length() >= 8)
        {
            Matcher matcher1 = PASSWORD_PATTERN_1.matcher(password1);
            Matcher matcher2 = PASSWORD_PATTERN_2.matcher(password1);

            if (matcher1.matches())
            {
                if (matcher2.matches())
                {
                    if (password1.equals(password2))
                    {
                        rtn = true;
                    } else
                    {
                        app.setStatusText("Passwords must be the same.");
                        secondPasswordField.requestFocus();
                    }
                } else
                {
                    app.setStatusText("Must contain at least two numbers.");
                    firstPasswordField.requestFocus();
                }
            } else
            {
                app.setStatusText("Must contain at least one UPPERCASE letter.");
                firstPasswordField.requestFocus();
            }

        } else
        {
            app.setStatusText("Must be at least 8 characters long.");
            firstPasswordField.requestFocus();
        }

        return rtn;
    }

    /**
     * Verify that the username fulfills the basic requirements.
     * <p>
     * 1. Minimum of 8 characters in length
     * 2. Begins with an alphabetic character
     * 3. Contains only alphanumeric characters
     *
     * @param username text to verify
     *
     * @return {@code true} if valid
     */
    private boolean validUsername(String username)
    {
        boolean rtn = false;

        if (username != null && username.length() >= 8)
        {
            Matcher matcher1 = USERNAME_PATTERN_1.matcher(username);
            Matcher matcher2 = USERNAME_PATTERN_2.matcher(username);

            if (matcher1.matches())
            {
                if (matcher2.matches())
                {
                    rtn = true;
                } else
                {
                    app.setStatusText("Must contain only letters and numbers.");
                    usernameTextField.requestFocus();
                }
            } else
            {
                app.setStatusText("Must begin with a letter.");
                usernameTextField.requestFocus();
            }
        } else
        {
            app.setStatusText("Must be at least 8 characters long.");
            usernameTextField.requestFocus();
        }

        return rtn;
    }

    @FXML
    private Label confirmPasswordLabel;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private PasswordField firstPasswordField;

    @FXML
    private Button resetButton;

    @FXML
    private PasswordField secondPasswordField;

    @FXML
    private Button submitButton;

    @FXML
    private TextField usernameTextField;

    /**
     * Instantiate a new copy of NewAccountController class.
     */
    public NewAccountController()
    {
        // NoOP
    }

    private App app;

    @Override
    public void setApp(App app)
    {
        this.app = app;
        app.setStatusText("");
        app.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        switch (evt.getPropertyName())
        {
            case App.PROP_ACTIVEVIEW:
            {
                if ((Views) evt.getOldValue() == NEW_ACCOUNT)
                {
                    app.removePropertyChangeListener(this);
                }

                break;
            }

            default:
            {
                break;
            }
        }
    }

    @Override
    public void setFocus()
    {
        usernameTextField.requestFocus();
    }

    /**
     * Handle the Reset Button event.
     *
     * @param event
     */
    @FXML
    private void handleResetButton(ActionEvent event)
    {
        usernameTextField.clear();
        firstPasswordField.clear();
        secondPasswordField.clear();
        event.consume();
    }

    /**
     * Handle the Submit Button event.
     *
     * @param event
     */
    @FXML
    private void handleSubmitButton(ActionEvent event)
    {
        if (validUsername(usernameTextField.getText())
                && validPassword(firstPasswordField.getText(), secondPasswordField.getText()))
        {
            try
            {
                Registry registry = LocateRegistry.getRegistry();
                UserAccount userAccount = (UserAccount) registry.lookup(RMI_NAME);

                if (userAccount.create(usernameTextField.getText(), firstPasswordField.getText()))
                {
                    // Login successful!
                    app.setStatusText("New account created! You are logged in.");
                    app.setLoggedIn(true);
                    app.setUserName(usernameTextField.getText());
                    app.showView(BLANK);
                } else
                {
                    // Login FAILED!
                    app.setStatusText("New account creation FAILED! Try again.");
                    usernameTextField.requestFocus();
                }
            } catch (RemoteException | NotBoundException ex)
            {
                if (ex instanceof ConnectException cause
                        && cause.getMessage().startsWith("Connection refused"))
                {
                    app.setStatusText("User Account Server: Connection refused!");
                    usernameTextField.requestFocus();
                } else
                {
                    Logger.getLogger(NewAccountController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        event.consume();
    }
}
