import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame{
	
    private JTextField accountField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("商家登入 - 政大校園訂餐系統");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        JLabel titleLabel = new JLabel("商家登入", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titleLabel);

        JPanel accountPanel = new JPanel();
        accountPanel.add(new JLabel("帳號："));
        accountField = new JTextField(20); 
        accountPanel.add(accountField);
        add(accountPanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("密碼："));
        passwordField = new JPasswordField(20); 
        passwordPanel.add(passwordField);
        add(passwordPanel);

        loginButton = new JButton("登入"); 
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        add(buttonPanel);

        setVisible(true);
    }

    private void handleLogin() {
        String account = accountField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT vendor_id FROM vendor WHERE account = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, account);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int vendorId = rs.getInt("vendor_id");
                JOptionPane.showMessageDialog(this, "登入成功！");
                dispose(); // 關閉登入畫面
                new MerchantOrderListGUI(vendorId);
            } else {
                JOptionPane.showMessageDialog(this, "帳號或密碼錯誤！", "錯誤", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "資料庫連線失敗：" + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
