
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StoreList extends JPanel{
    private JTable table;
    private JButton editButton, deleteButton, searchButton;
    
    private final String server = "jdbc:mysql://140.119.19.73:3315/";
	private final String database = "TG06"; // change to your own database
	private final String url = server + database + "?useSSL=false";
	private final String dbUser = "TG06";// change to your own username
	private final String dbPassword = "bMIEqf";// change to your own password
	
	private Connection conn;

    public StoreList() {
        setLayout(new BorderLayout());

        // 欄位名稱
        String[] columnNames = {"店家名稱", "電子信箱", "聯絡電話", "地址", "註冊日期"};  
        // 測試資料
        Object[][] data = {};

        // 設定表格不可編輯
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
     // 連線到資料庫並抓取資料
        try {
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
            String sql = "SELECT v.vendor_name, v.email, v.vendor_phone, v.address, v.register_date, v.status " +
                         "FROM vendor v " +
                         "WHERE v.status = '可使用'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("vendor_name"),
                    rs.getString("email"),
                    rs.getString("vendor_phone"),
                    rs.getString("address"),
                    rs.getString("register_date"),
                    rs.getString("status")
                };
                model.addRow(row);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "資料庫錯誤：" + e.getMessage());
            e.printStackTrace();
        }
        
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table); 
        add(scrollPane, BorderLayout.CENTER); 
        
        // 點擊後查看店家詳細資訊
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 雙擊
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String storeName = table.getValueAt(row, 0).toString(); 
                        new StoreApprovalDetail(storeName).setVisible(true);
                    }
                }
            }
        });

        // 設置按鈕
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        editButton = new JButton("修改");
        deleteButton = new JButton("刪除");
        searchButton = new JButton("搜尋");

        

        // 修改按鍵
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(StoreList.this, "請選擇要修改的店家");
                    return;
                }

                String storeName = "0";
                String email = "0";
                String phone ="0";
                String address ="0";
                if(table.getValueAt(selectedRow, 0) != null & table.getValueAt(selectedRow, 1) != null & table.getValueAt(selectedRow, 2) != null & table.getValueAt(selectedRow, 3) != null) {
                	storeName = table.getValueAt(selectedRow, 0).toString();
                	email = table.getValueAt(selectedRow, 1).toString();
                	phone = table.getValueAt(selectedRow, 2).toString();
                	address = table.getValueAt(selectedRow, 3).toString();
                }

                JTextField emailField = new JTextField(email);
                JTextField phoneField = new JTextField(phone);
                JTextField addressField = new JTextField(address);

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("電子信箱："));
                panel.add(emailField);
                panel.add(new JLabel("聯絡電話："));
                panel.add(phoneField);
                panel.add(new JLabel("地址："));
                panel.add(addressField);

                int result = JOptionPane.showConfirmDialog(StoreList.this, panel, "修改店家資訊", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
                        String updateSQL = "UPDATE vendor SET email = ?, vendor_phone = ?, address = ? WHERE vendor_name = ?";
                        PreparedStatement stmt = conn.prepareStatement(updateSQL);
                        stmt.setString(1, emailField.getText());
                        stmt.setString(2, phoneField.getText());
                        stmt.setString(3, addressField.getText());
                        stmt.setString(4, storeName);
                        stmt.executeUpdate();

                        // 更新表格顯示
                        table.setValueAt(emailField.getText(), selectedRow, 1);
                        table.setValueAt(phoneField.getText(), selectedRow, 2);
                        table.setValueAt(addressField.getText(), selectedRow, 3);

                        JOptionPane.showMessageDialog(StoreList.this, "更新成功！");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(StoreList.this, "更新失敗：" + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });

        
        // 刪除按鍵
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(StoreList.this, "請選擇要刪除的店家");
                    return;
                }

                String storeName = table.getValueAt(selectedRow, 0).toString();

                int confirm = JOptionPane.showConfirmDialog(StoreList.this, "確定要刪除店家「" + storeName + "」嗎？", "確認刪除", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
                    // 先找 vendor_id
                    String findIdSQL = "SELECT vendor_id FROM vendor WHERE vendor_name = ?";
                    PreparedStatement findIdStmt = conn.prepareStatement(findIdSQL);
                    findIdStmt.setString(1, storeName);
                    ResultSet rs = findIdStmt.executeQuery();

                    if (rs.next()) {
                        int vendorId = rs.getInt("vendor_id");
                        rs.close();
                        findIdStmt.close();

                        // 刪除 validate 資料
                        PreparedStatement deleteValidate = conn.prepareStatement("DELETE FROM validate WHERE vendor_id = ?");
                        deleteValidate.setInt(1, vendorId);
                        deleteValidate.executeUpdate();
                        deleteValidate.close();

                        // 刪除 vendor 資料
                        PreparedStatement deleteVendor = conn.prepareStatement("DELETE FROM vendor WHERE vendor_id = ?");
                        deleteVendor.setInt(1, vendorId);
                        deleteVendor.executeUpdate();
                        deleteVendor.close();

                        JOptionPane.showMessageDialog(StoreList.this, "刪除成功！");
                        model.removeRow(selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(StoreList.this, "找不到該店家");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StoreList.this, "刪除失敗：" + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        
        // 搜尋按鍵
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = ((JTextField) buttonPanel.getComponent(2)).getText().trim();

                // 清空現有表格資料
                model.setRowCount(0);

                try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
                    String sql = "SELECT v.vendor_name, v.email, v.vendor_phone, v.address, vl.validate_date, vl.status " +
                                 "FROM vendor v JOIN validate vl ON v.vendor_id = vl.vendor_id " +
                                 "WHERE vl.status = '可使用' AND v.vendor_name LIKE ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, "%" + keyword + "%");
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        Object[] row = {
                            rs.getString("vendor_name"),
                            rs.getString("email"),
                            rs.getString("vendor_phone"),
                            rs.getString("address"),
                            rs.getString("validate_date"),
                            rs.getString("status")
                        };
                        model.addRow(row);
                    }
                    rs.close();
                    stmt.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StoreList.this, "搜尋失敗：" + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });


        // 將按鈕加入面板
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(new JTextField(10));
        buttonPanel.add(searchButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    
}

