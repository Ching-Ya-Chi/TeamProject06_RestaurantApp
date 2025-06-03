package src;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SystemController {
    private static SystemController instance;
    private JFrame currentFrame;
    private User currentUser;
    private Vendor currentVendor;
    private Manager currentManager;

    private SystemController() {}

    public static SystemController getInstance() {
        if (instance == null) {
            instance = new SystemController();
        }
        return instance;
    }

    // 登入處理
    public void handleLogin(String account, String password, String userType) {
        switch (userType) {
            case "user":
                UserDB userDB = new UserDB();
                currentUser = userDB.login(account, password);
                if (currentUser != null) {
                    showUserInterface();
                }
                break;
            case "vendor":
                VendorDB vendorDB = new VendorDB();
                currentVendor = vendorDB.login(account, password);
                if (currentVendor != null) {
                    showVendorInterface();
                }
                break;
            case "manager":
                // 管理者登入邏輯
                break;
        }
    }

    // 顯示使用者介面
    private void showUserInterface() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new UserGUI(currentUser);
        currentFrame.setVisible(true);
    }

    // 顯示商家介面
    private void showVendorInterface() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new MerchantProfileGUI(currentVendor);
        currentFrame.setVisible(true);
    }

    // 顯示管理者介面
    private void showManagerInterface() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new AdminHomePage();
        currentFrame.setVisible(true);
    }

    // 登出處理
    public void handleLogout() {
        currentUser = null;
        currentVendor = null;
        currentManager = null;
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
    }

    // 切換面板
    public void switchPanel(JPanel newPanel) {
        if (currentFrame != null) {
            currentFrame.getContentPane().removeAll();
            currentFrame.getContentPane().add(newPanel);
            currentFrame.revalidate();
            currentFrame.repaint();
        }
    }

    // Getters
    public User getCurrentUser() {
        return currentUser;
    }

    public Vendor getCurrentVendor() {
        return currentVendor;
    }

    public Manager getCurrentManager() {
        return currentManager;
    }
} 