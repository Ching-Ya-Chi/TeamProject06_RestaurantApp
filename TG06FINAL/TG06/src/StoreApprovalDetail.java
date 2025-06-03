

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StoreApprovalDetail extends JFrame {

    public StoreApprovalDetail(String storeName) {
        setTitle("審核店家：「" + storeName + "」");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 從資料庫抓取店家資料
        String vendor_phone = "", email = "", registerDate = "", account="";
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT vendor_phone, email, register_date, account FROM vendor WHERE vendor_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, storeName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	vendor_phone = rs.getString("vendor_phone");
                email = rs.getString("email");
                registerDate = rs.getString("register_date");
                account = rs.getString("account");
            } else {
                JOptionPane.showMessageDialog(this, "找不到店家資料！");
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "資料庫錯誤：" + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 店家資訊區塊
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBorder(BorderFactory.createTitledBorder("店家資訊"));
        infoPanel.add(new JLabel("店家名稱：" + storeName));
        infoPanel.add(new JLabel("聯絡電話：" + vendor_phone));
        infoPanel.add(new JLabel("電子信箱：" + email));
        infoPanel.add(new JLabel("註冊日期：" + registerDate));
        infoPanel.add(new JLabel("帳號：" + account));
            
        
        add(infoPanel, BorderLayout.NORTH);
    }

    
}

