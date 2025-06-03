package src;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // 設置系統外觀
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // 在EDT線程中啟動GUI
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 