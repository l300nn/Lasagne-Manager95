package javaSpiel;

import javax.swing.SwingUtilities;

public class game {
    public static void main(String[] args) {
        // Swing UI sollte im Event Dispatch Thread (EDT) gestartet werden
        SwingUtilities.invokeLater(() -> {
            GameUI ui = new GameUI();
            ui.setVisible(true);
            // Zentrieren des Fensters auf dem Bildschirm
            ui.setLocationRelativeTo(null);
        });
    }
}
