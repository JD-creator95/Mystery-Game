package gui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private JTextArea storyArea;
    private JPanel buttonPanel;
    
    public GameWindow() {
        setTitle("Mystery Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Story/narrative area
        storyArea = new JTextArea();
        storyArea.setEditable(false);
        storyArea.setLineWrap(true);
        storyArea.setWrapStyleWord(true);
        storyArea.setFont(new Font("Arial", Font.PLAIN, 14));
        storyArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(storyArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel for choices
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void displayText(String text) {
        storyArea.setText(text);
    }
    
    public void appendText(String text) {
        storyArea.append("\n\n" + text);
    }
    
    public void clearButtons() {
        buttonPanel.removeAll();
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
    
    public void addChoiceButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        buttonPanel.add(button);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
}
