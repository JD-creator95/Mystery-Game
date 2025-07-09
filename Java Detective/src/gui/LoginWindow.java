package gui;

import game.GameEngine;
import model.Difficulty;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    public LoginWindow() {
        setTitle("Detective Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));
        

        ImageIcon icon = new ImageIcon("detective.png");
        setIconImage(icon.getImage());

        getContentPane().setBackground(new Color(82, 12, 222)); //background color

        JTextField usernameField = new JTextField();
        JComboBox<String> difficultyBox = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
        JButton loginBtn = new JButton("Start Investigation");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Select Difficulty:"));
        add(difficultyBox);
        add(loginBtn);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String diffStr = (String) difficultyBox.getSelectedItem();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username required!");
                return;
            }

            Difficulty difficulty = Difficulty.valueOf(diffStr);
            GameEngine engine = new GameEngine(username, difficulty);

            MainWindow window = new MainWindow(engine);
            window.setVisible(true);
            dispose(); // close login window
        });
    }
}
// This class represents the login window for the detective game.
// It allows the user to enter their username and select a difficulty level.
