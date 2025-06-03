// HistoryOrder.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat; // For formatting dates
import javax.swing.table.DefaultTableModel;






public class HistoryOrder extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public HistoryOrder() {
        setLayout(new BorderLayout());

        String[] columnNames = {"訂單編號", "店家名稱", "訂餐者名稱", "訂單類型", "訂單完成時間"};
        model = new DefaultTableModel(columnNames, 0) {  //初始沒有任何資料列
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止編輯表格
            }
        };
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
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


        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("選擇日期 (yyyy-mm-dd):"));
        JTextField timeField = new JTextField(10);
        filterPanel.add(timeField);

        JButton filterButton = new JButton("篩選");
        filterButton.addActionListener(e -> loadData(timeField.getText().trim()));
        filterPanel.add(filterButton);
        
        JButton allButton = new JButton("顯示全部");
        allButton.addActionListener(e -> loadData(""));
        filterPanel.add(allButton);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 初始化載入全部資料
        loadData(null);
    }

    private void loadData(String dateFilter) {
        model.setRowCount(0); // 清空原本資料

        String baseSql = """
            SELECT o.order_id, v.vendor_name, u.name AS user_name, o.type, o.finish_time
            FROM `order` o
            JOIN vendor v ON o.vendor_id = v.vendor_id
            JOIN user u ON o.user_id = u.user_id
        """;

        String sql = baseSql;
        boolean hasDateFilter = dateFilter != null && !dateFilter.isEmpty();

        if (hasDateFilter) {
            sql += " WHERE DATE_FORMAT(o.finish_time, '%Y-%m-%d') LIKE ?";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (hasDateFilter) {
                stmt.setString(1, "%" + dateFilter + "%"); // 模糊查詢
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("order_id"),
                    rs.getString("vendor_name"),
                    rs.getString("user_name"),
                    rs.getString("type"),
                    rs.getTimestamp("finish_time")
                };
                model.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "載入資料失敗: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("所有歷史訂單");
            frame.add(new HistoryOrder());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600); // Increased size for more columns
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}




