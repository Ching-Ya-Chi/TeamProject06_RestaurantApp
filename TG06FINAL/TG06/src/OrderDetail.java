

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OrderDetail extends JFrame {
	
    

    public OrderDetail(int orderId) {
        setTitle("訂單詳細資訊 - 編號：" + orderId);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== 1. 抓訂單基本資料 =====
        JPanel orderInfoPanel = new JPanel(new GridLayout(0, 1));
        orderInfoPanel.setBorder(BorderFactory.createTitledBorder("訂單資訊"));
        try (Connection conn = DBUtil.getConnection()) {
            String sqlOrder = "SELECT * FROM `order` WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sqlOrder);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                orderInfoPanel.add(new JLabel("訂單類型：" + rs.getString("type")));
                orderInfoPanel.add(new JLabel("使用者編號：" + rs.getInt("user_id")));
                orderInfoPanel.add(new JLabel("商家編號：" + rs.getInt("vendor_id")));
                orderInfoPanel.add(new JLabel("領取/配送地點：" + rs.getString("location")));
                orderInfoPanel.add(new JLabel("外送員編號：" + rs.getString("deliverman_id")));
                orderInfoPanel.add(new JLabel("實際取餐時間：" + rs.getString("finish_time")));
                orderInfoPanel.add(new JLabel("訂單狀態：" + rs.getString("status")));
                orderInfoPanel.add(new JLabel("總金額：" + rs.getInt("money")));
                orderInfoPanel.add(new JLabel("訂單備註：" + rs.getString("note")));
            } else {
                JOptionPane.showMessageDialog(this, "找不到此訂單！");
                dispose();
                return;
            }

            rs.close();
            stmt.close();

            // ===== 2. 抓訂單明細資料 =====
            String sqlDetail = "SELECT name, price, quantity, customization FROM orderdetail WHERE order_id = ?";
            PreparedStatement detailStmt = conn.prepareStatement(sqlDetail);
            detailStmt.setInt(1, orderId);
            ResultSet detailRs = detailStmt.executeQuery();

            // 表格欄位
            String[] columnNames = {"商品名稱", "單價", "數量", "客製化需求", "小計"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            while (detailRs.next()) {
                String name = detailRs.getString("name");
                int price = detailRs.getInt("price");
                int quantity = detailRs.getInt("quantity");
                String customization = detailRs.getString("customization");
                int subtotal = price * quantity;

                Object[] row = {name, price, quantity, customization, subtotal};
                tableModel.addRow(row);
            }

            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createTitledBorder("商品明細"));

            // 加入畫面
            add(orderInfoPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);

            detailRs.close();
            detailStmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "資料庫錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }

    
}


