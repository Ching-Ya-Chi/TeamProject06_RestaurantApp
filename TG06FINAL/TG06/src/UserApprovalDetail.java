

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserApprovalDetail extends JFrame {

    public UserApprovalDetail(String userName) {
        setTitle("審核使用者：「" + userName + "」");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 從資料庫抓取資料
        String user_id ="", name = "", telephone = "", email = "", registerDate = "", account="";
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT user_id, name, telephone, register_date, account FROM user WHERE account = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	user_id = rs.getString("user_id");
            	name = rs.getString("name");
            	telephone = rs.getString("telephone");
                registerDate = rs.getString("register_date");
                account = rs.getString("account");
            } else {
                JOptionPane.showMessageDialog(this, "找不到使用者資料！");
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "資料庫錯誤：" + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 店家資訊區塊
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBorder(BorderFactory.createTitledBorder("使用者資訊"));
        infoPanel.add(new JLabel("使用者id：" + user_id));
        infoPanel.add(new JLabel("使用者名稱：" + name));
        infoPanel.add(new JLabel("聯絡電話：" + telephone));
        infoPanel.add(new JLabel("註冊日期：" + registerDate));
        infoPanel.add(new JLabel("帳號：" + account));
            
        
        add(infoPanel, BorderLayout.NORTH);
    }

    
}