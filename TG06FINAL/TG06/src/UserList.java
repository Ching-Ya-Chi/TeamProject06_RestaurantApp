

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserList extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    private final String server = "jdbc:mysql://140.119.19.73:3315/";
    private final String database = "TG06";
    private final String url = server + database + "?useSSL=false";
    private final String dbUser = "TG06";
    private final String dbPassword = "bMIEqf";

    public UserList() {
        setLayout(new BorderLayout());

        String[] columnNames = {"使用者名稱", "帳號", "聯絡電話", "註冊日期", "狀態"};
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
                        String ac = table.getValueAt(row, 1).toString();
                        new UserApprovalDetail(ac).setVisible(true);
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton editButton = new JButton("修改");
        JButton deleteButton = new JButton("刪除");
        JButton searchButton = new JButton("搜尋");
        searchField = new JTextField(10);

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        add(buttonPanel, BorderLayout.SOUTH);

        fetchData("");

        // 搜尋功能
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            fetchData(keyword);
        });

        // 刪除功能
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "請先選擇要刪除的使用者！");
                return;
            }

            String account = table.getValueAt(selectedRow, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "確定要刪除帳號：" + account + "？", "確認刪除", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteUser(account);
                fetchData("");
            }
        });

        // 修改功能
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "請先選擇要修改的使用者！");
                return;
            }

            String account = table.getValueAt(selectedRow, 1).toString();
            String name = table.getValueAt(selectedRow, 0).toString();
            String phone = table.getValueAt(selectedRow, 2).toString();
            String status = table.getValueAt(selectedRow, 4).toString();

            JTextField nameField = new JTextField(name);
            JTextField phoneField = new JTextField(phone);
            JTextField statusField = new JTextField(status);

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("使用者名稱:"));
            panel.add(nameField);
            panel.add(new JLabel("聯絡電話:"));
            panel.add(phoneField);
            panel.add(new JLabel("狀態:"));
            panel.add(statusField);

            int result = JOptionPane.showConfirmDialog(null, panel, "修改使用者資料", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                updateUser(account, nameField.getText(), phoneField.getText(), statusField.getText());
                fetchData("");
            }
        });
    }

    private void fetchData(String nameFilter) {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String sql = "SELECT name, account, telephone, register_date, status FROM user WHERE 1=1";
            if (!nameFilter.isEmpty()) {
                sql += " AND name LIKE ?";
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
            JOptionPane.showMessageDialog(this, "資料庫讀取錯誤：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteUser(String account) {
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String sql = "DELETE FROM user WHERE account = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, account);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "刪除失敗：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateUser(String account, String name, String phone, String status) {
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String sql = "UPDATE user SET name = ?, telephone = ?, status = ? WHERE account = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, status);
            stmt.setString(4, account);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "更新失敗：" + e.getMessage());
            e.printStackTrace();
        }
    }

    
}


