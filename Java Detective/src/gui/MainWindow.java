package gui;

import game.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {
    private GameEngine engine;

    public MainWindow(GameEngine engine) {
        this.engine = engine;
        setTitle("Java Detective");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        ImageIcon icon = new ImageIcon("detective.png");
        setIconImage(icon.getImage());

        getContentPane().setBackground(new Color(82, 12, 222)); //background color

        JButton startBtn = new JButton("Start Case");
        JButton viewCluesBtn = new JButton("View Clues");
        JButton questionBtn = new JButton("Question Suspects");
        JButton accuseBtn = new JButton("Make Accusation");
        JButton exitBtn = new JButton("Exit");

        JTextArea outputArea = new JTextArea(15, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(startBtn);
        add(viewCluesBtn);
        add(questionBtn);
        add(accuseBtn);
        add(exitBtn);
        add(scrollPane);

        startBtn.addActionListener(e -> outputArea.setText(engine.getStory()));
        viewCluesBtn.addActionListener(e -> outputArea.setText(engine.getAllClues()));
        questionBtn.addActionListener(e -> outputArea.setText(engine.questionSuspects()));
        accuseBtn.addActionListener(e -> {
            String suspect = JOptionPane.showInputDialog("Who do you accuse?");
            outputArea.setText(engine.makeAccusation(suspect));
        });
        exitBtn.addActionListener(e -> System.exit(0));
    }
}
// This class represents the main window of the detective game.
// It allows the user to start the case, view clues, question suspects, make accusations,

