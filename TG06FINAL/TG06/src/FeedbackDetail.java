
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FeedbackDetail extends JFrame {

    public FeedbackDetail(int feedbackID) {
        setTitle("回饋詳情：「" + feedbackID + "」");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 使用 GridBagLayout 
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("使用者回饋內容"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // 上、左、下、右邊距
        gbc.anchor = GridBagConstraints.WEST;

        // 從資料庫抓取資料
        String complain_id = "", order_id = "", type = "", complain_content = "", reply_content = "";
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT complain_id, order_id, type, complain_content, reply_content FROM complain WHERE complain_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, feedbackID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                complain_id = rs.getString("complain_id");
                order_id = rs.getString("order_id");
                type = rs.getString("type");
                complain_content = rs.getString("complain_content");
                reply_content = rs.getString("reply_content");
            } else {
                JOptionPane.showMessageDialog(this, "找不到回饋資料！");
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "資料庫錯誤：" + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 顯示資訊（label + 資料）
        int row = 0;
        addRow(infoPanel, gbc, row++, "回饋編號：", complain_id);
        addRow(infoPanel, gbc, row++, "訂單編號：", order_id);
        addRow(infoPanel, gbc, row++, "問題類型：", type);
        addRow(infoPanel, gbc, row++, "回饋內容：", "<html><body style='width: 350px'>" + complain_content + "</body></html>");
        addRow(infoPanel, gbc, row++, "回覆內容：", "<html><body style='width: 350px'>" + (reply_content != null ? reply_content : "（尚未回覆）") + "</body></html>");

        // 滾動區塊
        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    // 抽出方法：為每一列資訊加到 infoPanel
    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String valueText) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel(valueText), gbc);
    }

    
}


