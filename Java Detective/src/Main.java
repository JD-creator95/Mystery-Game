import gui.LoginWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow login = new LoginWindow();
            login.setVisible(true);
        });
    }
}
// This is the main entry point of the Java Detective game application.
// It initializes the GUI by showing the login window where players can enter their username and select a difficulty level.


