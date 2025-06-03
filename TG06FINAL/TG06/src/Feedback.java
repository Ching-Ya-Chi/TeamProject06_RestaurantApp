
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Feedback extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextArea replyArea;
    private JComboBox<String> statusFilter;

    public Feedback() {
        setLayout(new BorderLayout());

        // 上方篩選條件區
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("篩選狀態："));
        statusFilter = new JComboBox<>(new String[]{"全部", "未回覆", "已回覆"});
        statusFilter.addActionListener(e -> loadFeedbackData((String) statusFilter.getSelectedItem()));
        topPanel.add(statusFilter);
        add(topPanel, BorderLayout.NORTH);

        // 表格欄位
        String[] columnNames = {"回饋編號", "訂單編號", "使用者名稱", "問題類型", "回覆狀態", "訂單日期"};
        model = new DefaultTableModel(columnNames, 0) {  //初始沒有任何資料列
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止編輯表格
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
     // 雙擊顯示詳細資訊
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        try {
                            int FeedbackId = Integer.parseInt(table.getValueAt(row, 0).toString());
                            new FeedbackDetail(FeedbackId).setVisible(true);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "訂單編號格式錯誤！");
                        }
                    }
                }
            }
        });

        // 回覆面板
        JPanel replyPanel = new JPanel();
        replyPanel.add(new JLabel("回覆:"));
        replyArea = new JTextArea(3, 30);
        replyPanel.add(replyArea);

        JButton replyButton = new JButton("回覆");
        replyButton.addActionListener(e -> replyToFeedback());
        replyPanel.add(replyButton);
        add(replyPanel, BorderLayout.SOUTH);

        // 預設載入所有資料
        loadFeedbackData("全部");
    }

    // 依據選擇狀態載入資料
    private void loadFeedbackData(String status) {
        model.setRowCount(0); // 清除舊資料

        String sql =
            "SELECT c.complain_id, c.order_id, u.name AS user_name, c.type, c.status, o.finish_time " +
            "FROM complain c " +
            "JOIN user u ON c.user_id = u.user_id " +
            "JOIN `order` o ON c.order_id = o.order_id ";

        if (!"全部".equals(status)) {
            sql += "WHERE c.status = ? ";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (!"全部".equals(status)) {
                stmt.setString(1, status);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("complain_id"),
                    rs.getInt("order_id"),
                    rs.getString("user_name"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getTimestamp("finish_time") // 顯示時間
                };
                model.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "載入回饋資料失敗: " + ex.getMessage());
        }
    }

    // 回覆回饋
    private void replyToFeedback() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "請選擇一筆回饋進行回覆");
            return;
        }

        int complainId = (int) model.getValueAt(selectedRow, 0);
        String replyContent = replyArea.getText().trim();

        if (replyContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "回覆內容不能為空");
            return;
        }

        String sql = "UPDATE complain SET status = '已回覆', reply_content = ? WHERE complain_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, replyContent);
            stmt.setInt(2, complainId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "回覆成功");
                loadFeedbackData((String) statusFilter.getSelectedItem()); // 重新載入
                replyArea.setText("");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "回覆失敗: " + ex.getMessage());
        }
    }

   
}


