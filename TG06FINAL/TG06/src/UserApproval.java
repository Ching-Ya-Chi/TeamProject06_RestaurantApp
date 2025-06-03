
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;


public class UserApproval extends JPanel {
	private JTable table;
    private DefaultTableModel model;
    private JTextField searchName;

    private final String server = "jdbc:mysql://140.119.19.73:3315/";
    private final String database = "TG06";
    private final String url = server + database + "?useSSL=false";
    private final String dbUser = "TG06";
    private final String dbPassword = "bMIEqf";
    
    private String selectedAction = "";  // "通過" 或 "拒絕"


    public UserApproval() {
        setLayout(new BorderLayout());

        // 欄位名稱
        String[] columnNames = {"使用者名稱", "帳號", "聯絡電話", "申請日期", "審核狀態"};
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
                        String ac = table.getValueAt(row, 1).toString();
                        new UserApprovalDetail(ac).setVisible(true);
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
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(this, "請選擇至少一位使用者進行審核！");
                return;
            }

            if (selectedAction.isEmpty()) {
                JOptionPane.showMessageDialog(this, "請先點擊「通過」或「拒絕」再執行審核！");
                return;
            }

            //取得欲操作的使用者名稱
            List<String> selectedStoreNames = new ArrayList<>();
            for (int row : selectedRows) {
                String storeName = table.getValueAt(row, 0).toString();
                selectedStoreNames.add(storeName);
            }

            // 更新審核狀態
            updateApprovalStatus(selectedStoreNames, selectedAction.equals("通過") ? "可使用" : "已停權");
            fetchData(""); // 重新載入資料
            selectedAction = ""; // 重置狀態
            approveButton.setBackground(null);
            rejectButton.setBackground(null);
        });
    }
    
    private void fetchData(String nameFilter) {
        model.setRowCount(0); // 清除原資料
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
        	String sql = "SELECT u.name, u.account, u.telephone, u.register_date, u.status " +
                    "FROM user u WHERE u.status = '已停權'";

            //名稱搜尋
            if (!nameFilter.isEmpty()) {
                sql += " AND u.name LIKE ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            if (!nameFilter.isEmpty()) {
                stmt.setString(1, "%" + nameFilter + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("account"),
                    rs.getString("telephone"),
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

    //更新使用者的審核狀態
    private void updateApprovalStatus(List<String> storeNames, String newStatus) {
        if (storeNames.isEmpty()) return;

        String sql = "UPDATE user SET status = ? WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            for (String name : storeNames) {
                stmt.setString(1, newStatus);
                stmt.setString(2, name);
                stmt.addBatch();
            }

            stmt.executeBatch(); // 一次執行所有更新
            stmt.close();

            JOptionPane.showMessageDialog(this, "審核完成，已更新 " + storeNames.size() + " 筆資料為「" + newStatus + "」");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "更新資料時發生錯誤：" + e.getMessage());
        }
    }

    
    


   
}
