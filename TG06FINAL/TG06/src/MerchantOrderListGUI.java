import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;


public class MerchantOrderListGUI extends JFrame {
    private JLabel titleLabel;
    private JButton logoutButton;
    private JButton btnOrderQuery, btnProductInfo, btnBasicInfo, btnReviewInfo;
    private JTable orderTable;
    private JScrollPane tableScrollPane;
    private int vendorId;

    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);
    
    public MerchantOrderListGUI(int vendorId) {
        this.vendorId = vendorId;
        initializeFrame();
        initializeComponents();
        layoutComponents();
        fetchVendorName();
        loadOrderData();
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
        logoutButton.setPreferredSize(new Dimension(70, 30));
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

        Dimension buttonSize = new Dimension(100, 30);
        btnOrderQuery.setPreferredSize(buttonSize);
        btnProductInfo.setPreferredSize(buttonSize);
        btnBasicInfo.setPreferredSize(buttonSize);
        btnReviewInfo.setPreferredSize(buttonSize);

        btnOrderQuery.setBackground(Color.LIGHT_GRAY);
        btnOrderQuery.setOpaque(true);
        
        btnProductInfo.addActionListener(e -> {
            dispose();
            new MerchantProdutListGUI(vendorId);
        });

        btnBasicInfo.addActionListener(e -> {
            dispose();
            new MerchantProfileGUI(vendorId);
        });

        btnReviewInfo.addActionListener(e -> {
            dispose();
            new MerchantReviewListGUI(vendorId);
        });

        String[] columnNames = {"訂單編號", "取餐時間", "取餐方式", "訂單狀態"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(model);
        orderTable.setRowHeight(25);
        orderTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        orderTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        orderTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = orderTable.getSelectedRow();
                if (row >= 0) {
                    int orderId = (int) orderTable.getValueAt(row, 0);
                    dispose();
                    new MerchantOrderGUI(orderId);
                }
            }
        });
        
        // 設定顏色區分訂單狀態
        orderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                            boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && column == 3) { // 訂單狀態欄
                    String status = value.toString();
                    switch (status) {
                        case "待接單": c.setBackground(new Color(255, 250, 205)); break;
                        case "準備中": c.setBackground(new Color(173, 216, 230)); break;
                        case "待取餐":
                        case "待送餐": c.setBackground(new Color(144, 238, 144)); break;
                        case "已完成": c.setBackground(new Color(211, 211, 211)); break;
                        case "已拒絕": c.setBackground(new Color(255, 182, 193)); break;
                        default: c.setBackground(Color.white); break;
                    }
                } else if (!isSelected) {
                    c.setBackground(Color.white);
                }
                return c;
            }
        });

        tableScrollPane = new JScrollPane(orderTable);
        tableScrollPane.setPreferredSize(new Dimension(660, 300));

        // 自動刷新：每 1 分鐘重新載入資料
        Timer refreshTimer = new Timer(60000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadOrderData();
            }
        });
        refreshTimer.start();
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(700, 50));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        titlePanel.add(titleLabel);
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        logoutPanel.add(logoutButton);
        topPanel.add(titlePanel, BorderLayout.CENTER);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        menuPanel.setPreferredSize(new Dimension(700, 45));
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
    }
    
    private void fetchVendorName() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT vendor_name FROM vendor WHERE vendor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String vendorName = rs.getString("vendor_name");
                titleLabel.setText("政大校園訂餐系統 - " + vendorName);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "無法取得商家名稱：" + e.getMessage());
        }
    }

    private void loadOrderData() {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT order_id, finish_time, type, status, location FROM `order` WHERE vendor_id = ? ORDER BY finish_time DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Timestamp finishTime = rs.getTimestamp("finish_time");
                String type = rs.getString("type") + " - " + rs.getString("location");
                String status = rs.getString("status");

                Object[] row = {orderId, formatter.format(finishTime), type, status};
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "讀取訂單失敗：" + e.getMessage());
        }
    }
}
