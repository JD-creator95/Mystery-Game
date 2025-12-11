import game.GameController;

public class Main {
    public static void main(String[] args) {
        // Launch the game on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.startGame();
        });
    }
}
