
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;


public class StoreApproval extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchName;

    private final String server = "jdbc:mysql://140.119.19.73:3315/";
    private final String database = "TG06";
    private final String url = server + database + "?useSSL=false";
    private final String dbUser = "TG06";
    private final String dbPassword = "bMIEqf";
    
    private String selectedAction = "";  // "通過" 或 "拒絕"

    public StoreApproval() {
        setLayout(new BorderLayout());

        // 欄位名稱
        String[] columnNames = {"店家名稱", "電子信箱", "聯絡電話", "地址", "申請日期", "審核狀態"};
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
                        String storeName = table.getValueAt(row, 0).toString();
                        new StoreApprovalDetail(storeName).setVisible(true);
                    }
                }
            }
        });

        // 按鈕區
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton approveButton = new JButton("通過");
        JButton rejectButton = new JButton("拒絕");
        JButton executeButton = new JButton("執行審核");
        JButton searchButton = new JButton("搜尋");
        searchName = new JTextField(10);

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(executeButton);
        buttonPanel.add(searchName);
        buttonPanel.add(searchButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 抓資料
        fetchData("");

        // 搜尋功能
        searchButton.addActionListener(e -> {
            String keyword = searchName.getText().trim();
            fetchData(keyword);
        });

        //通過鍵
        approveButton.addActionListener(e -> {
            selectedAction = "通過";
            approveButton.setBackground(new Color(125,209,71));
            rejectButton.setBackground(null);
        });

        //拒絕鍵
        rejectButton.addActionListener(e -> {
            selectedAction = "拒絕";
            rejectButton.setBackground(new Color(255,118,84));
            approveButton.setBackground(null);
        });

        // 執行審核
        executeButton.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows(); //抓row的索引
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(this, "請選擇至少一位店家進行審核！");
                return;
            }

            if (selectedAction.isEmpty()) {
                JOptionPane.showMessageDialog(this, "請先點擊「通過」或「拒絕」再執行審核！");
                return;
            }

            //取得欲操作的店家名稱
            List<String> selectedStoreNames = new ArrayList<>();
            for (int row : selectedRows) {
                String storeName = table.getValueAt(row, 0).toString();
                selectedStoreNames.add(storeName);
            }

            // 更新審核狀態
            updateApprovalStatus(selectedStoreNames, selectedAction.equals("通過") ? "可使用" : "待審核");
            fetchData(""); // 重新載入資料
            selectedAction = ""; // 重置狀態
            approveButton.setBackground(null);
            rejectButton.setBackground(null);
        });
    }

    private void fetchData(String nameFilter) {
        model.setRowCount(0); // 清除原資料
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String sql = "SELECT v.vendor_name, v.account, v.vendor_phone, v.address, v.register_date, v.status " +
                         "FROM vendor v LEFT JOIN validate vl ON v.vendor_id = vl.vendor_id " +
                         "WHERE v.status = '待審核'";

            //名稱搜尋
            if (!nameFilter.isEmpty()) {
                sql += " AND v.vendor_name LIKE ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            if (!nameFilter.isEmpty()) {
                stmt.setString(1, "%" + nameFilter + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("vendor_name"),
                    rs.getString("account"),
                    rs.getString("vendor_phone"),
                    rs.getString("address"),
                    rs.getString("register_date"),
                    rs.getString("status")
                });
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "資料庫讀取錯誤：" + e.getMessage());
        }
    }

    //更新多家店家的審核狀態
    private void updateApprovalStatus(List<String> storeNames, String newStatus) {
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            for (String storeName : storeNames) {
                // 找出 vendor_id
                String getIdSQL = "SELECT vendor_id FROM vendor WHERE vendor_name = ?";
                try (PreparedStatement getIdStmt = conn.prepareStatement(getIdSQL)) {
                    getIdStmt.setString(1, storeName);
                    ResultSet rs = getIdStmt.executeQuery();
                    if (rs.next()) {
                        int vendorId = rs.getInt("vendor_id");
                        rs.close();
                     // 更新 validate 的狀態
                        String updateValidateSQL = "UPDATE validate SET status = ? WHERE vendor_id = ?";
                        try (PreparedStatement updateValidateStmt = conn.prepareStatement(updateValidateSQL)) {
                            updateValidateStmt.setString(1, newStatus);
                            updateValidateStmt.setInt(2, vendorId);
                            updateValidateStmt.executeUpdate();
                        }

                        // 更新 vendor 的狀態
                        String updateVendorSQL = "UPDATE vendor SET status = ? WHERE vendor_id = ?";
                        try (PreparedStatement updateVendorStmt = conn.prepareStatement(updateVendorSQL)) {
                            updateVendorStmt.setString(1, newStatus);
                            updateVendorStmt.setInt(2, vendorId);
                            updateVendorStmt.executeUpdate();
                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "查無店家：「" + storeName + "」");
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "已將選取的店家審核為：" + newStatus);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "審核失敗：" + e.getMessage());
        }
    }

    //測試用
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("待審核店家");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new StoreApproval());
            frame.setVisible(true);
        });
    }
}




