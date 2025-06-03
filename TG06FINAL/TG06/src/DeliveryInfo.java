import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;


//
public class DeliveryInfo extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cb;

    public DeliveryInfo() {
        setLayout(new BorderLayout());

        String[] columnNames = {"訂單編號", "店家名稱", "訂餐者名稱", "配送地點", "指定送達時間", "配送狀態"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
     // 雙擊顯示詳細資訊
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        try {
                            int orderId = Integer.parseInt(table.getValueAt(row, 0).toString());
                            new OrderDetail(orderId).setVisible(true);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "訂單編號格式錯誤！");
                        }
                    }
                }
            }
        });

        // 按鈕區
        JButton markDeliveredBtn = new JButton("標記為已送達");
        markDeliveredBtn.addActionListener(e -> markAsDelivered());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(markDeliveredBtn);
        
        // 創建選項
        String[] items = {"政大正門取餐處", "政大東側門取餐處", "政大西側門取餐處", "自強十舍取餐處"};
        // 創建 JComboBox
        cb = new JComboBox<>(items);
        buttonPanel.add(cb);

        // 為 JComboBox 添加 ActionListener
        cb.addActionListener(e -> loadDeliveryOrders());  // 當選擇改變時，重新載入訂單

        add(buttonPanel, BorderLayout.SOUTH);

        JButton dMan = new JButton("設置外送員");
        dMan.addActionListener(e -> assignDeliveryMan());
        buttonPanel.add(dMan);

        
        // 載入今日外送訂單
        loadDeliveryOrders();
    }

    private void loadDeliveryOrders() {
        model.setRowCount(0); // 清除原資料

        String locationFilter = (String) cb.getSelectedItem();  // 取得選擇的地點
        String sql = """
            SELECT o.order_id, v.vendor_name, u.name AS user_name, o.location, o.finish_time, o.status
            FROM `order` o 
            JOIN vendor v ON o.vendor_id = v.vendor_id
            JOIN user u ON o.user_id = u.user_id
            WHERE o.type = '外送'
              AND DATE(o.finish_time) = CURDATE()
              AND o.location = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, locationFilter);  // 設定地點條件

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("order_id"),
                        rs.getString("vendor_name"),
                        rs.getString("user_name"),
                        rs.getString("location"),
                        rs.getTimestamp("finish_time"),
                        rs.getString("status")
                    };
                    model.addRow(row);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "資料載入失敗：" + e.getMessage());
        }
    }


    private void markAsDelivered() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "請先選取要標記的訂單");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String updateSql = "UPDATE `order` SET status = '已送達' WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                for (int row : selectedRows) {
                    int orderId = (int) model.getValueAt(row, 0); // 第0欄是 order_id
                    stmt.setInt(1, orderId);
                    stmt.executeUpdate();
                    model.setValueAt("已送達", row, 5); // 同步更新前端表格的狀態
                }
            }
            JOptionPane.showMessageDialog(this, "已成功標記為送達");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "更新資料庫失敗：" + ex.getMessage());
        }
    }


    private void assignDeliveryMan() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "請先選取一筆訂單！");
            return;
        }

        String deliveryMan = JOptionPane.showInputDialog(this, "請輸入外送員編號："); //JOptionPane.showInputDialog 用來顯示一個簡單的對話框，允許用戶輸入資料並返回該資料。
        if (deliveryMan == null || deliveryMan.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "外送員編號不能為空！");
            return;
        }

        int orderId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());

        // 資料庫更新
        String updateSQL = "UPDATE `order` SET deliverman_id = ? WHERE order_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setString(1, deliveryMan);
            stmt.setInt(2, orderId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "外送員設置成功！");
            } else {
                JOptionPane.showMessageDialog(this, "更新失敗！");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "資料庫錯誤：" + ex.getMessage());
        }

        // 重新載入畫面
        loadDeliveryOrders();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("今日外送訂單");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new DeliveryInfo());
            frame.setVisible(true);
        });
    }
}




