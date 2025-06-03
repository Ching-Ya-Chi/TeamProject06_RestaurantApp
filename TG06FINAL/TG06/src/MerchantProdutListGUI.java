import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MerchantProdutListGUI extends JFrame{
    private JLabel titleLabel;
    private JButton logoutButton, btnOrderQuery, btnProductInfo, btnBasicInfo, btnReviewInfo;
    private JButton btnAddProduct, btnEditProduct, btnDeleteProduct;
    private JTable productTable;
    private JScrollPane tableScrollPane;
    private int vendorId;
    
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    public MerchantProdutListGUI(int vendorId) {
        this.vendorId = vendorId;
        initializeFrame();
        initializeComponents();
        layoutComponents();
        fetchVendorName();
        loadProductData();
        setBackground(WHITE_BACKGROUND);
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("政大校園訂餐系統");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void initializeComponents() {
        titleLabel = new JLabel("政大校園訂餐系統 - 商家", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        logoutButton = new JButton("登出");
        logoutButton.setBackground(WHITE);
        logoutButton.addActionListener(e -> {
        	JOptionPane.showMessageDialog(this, "帳號已登出");
            dispose();
            new LoginRegister();
        });

        btnOrderQuery = new JButton("訂單查詢");
        btnOrderQuery.setBackground(WHITE);
        btnProductInfo = new JButton("商品資訊");
        btnProductInfo.setBackground(WHITE);
        btnBasicInfo = new JButton("基本資料");
        btnBasicInfo.setBackground(WHITE);
        btnReviewInfo = new JButton("顧客評價");
        btnReviewInfo.setBackground(WHITE);

        Dimension btnSize = new Dimension(100, 30);
        for (JButton btn : new JButton[]{btnOrderQuery, btnProductInfo, btnBasicInfo, btnReviewInfo}) {
            btn.setPreferredSize(btnSize);
        }

        btnProductInfo.setBackground(Color.LIGHT_GRAY);
        btnProductInfo.setOpaque(true);
        
        btnOrderQuery.addActionListener(e -> {
            dispose();
            new MerchantOrderListGUI(vendorId);
        });
        btnBasicInfo.addActionListener(e -> {
            dispose();
            new MerchantProfileGUI(vendorId);
        });
        btnReviewInfo.addActionListener(e -> {
            dispose();
            new MerchantReviewListGUI(vendorId);
        });

        String[] columns = {"商品編號", "商品名稱", "商品價格", "商品狀態", "商品介紹"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        productTable = new JTable(model);
        productTable.setRowHeight(25);
        productTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        productTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setPreferredSize(new Dimension(660, 300));

        btnAddProduct = new JButton("新增商品");
        btnEditProduct = new JButton("修改商品");
        btnDeleteProduct = new JButton("刪除商品");

        btnAddProduct.addActionListener(e -> {
            dispose();
            new MerchantProductGUI(null, vendorId); // 傳 null 表示新增
        });

        btnEditProduct.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                int productId = (int) productTable.getValueAt(row, 0);
                dispose();
                new MerchantProductGUI(productId, vendorId);
            } else {
                JOptionPane.showMessageDialog(this, "請先選擇要修改的商品");
            }
        });

        btnDeleteProduct.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                int productId = (int) productTable.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "確定要刪除商品編號 " + productId + " 嗎？", "確認刪除", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DBConnection.getConnection()) {
                        String sql = "DELETE FROM product WHERE product_id = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, productId);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "商品已刪除");
                        loadProductData(); // 重新載入
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "刪除失敗：" + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "請先選擇要刪除的商品");
            }
        });
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        titlePanel.add(titleLabel);
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        logoutPanel.add(logoutButton);
        topPanel.add(titlePanel, BorderLayout.CENTER);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        menuPanel.add(btnOrderQuery);
        menuPanel.add(btnProductInfo);
        menuPanel.add(btnBasicInfo);
        menuPanel.add(btnReviewInfo);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(menuPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = new JPanel();
        tablePanel.add(tableScrollPane);
        add(tablePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.add(btnAddProduct);
        bottomPanel.add(btnEditProduct);
        bottomPanel.add(btnDeleteProduct);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void fetchVendorName() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT vendor_name FROM vendor WHERE vendor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                titleLabel.setText("政大校園訂餐系統 - " + rs.getString("vendor_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "無法取得商家名稱：" + e.getMessage());
        }
    }

    private void loadProductData() {
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT product_id, name, price, description, status FROM product WHERE vendor_id = ? ORDER BY product_id ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    "$" + rs.getInt("price"),
                    rs.getString("status"),       // 調到前面
                    rs.getString("description")   // 改到後面
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "載入商品失敗：" + e.getMessage());
        }
    }

}
