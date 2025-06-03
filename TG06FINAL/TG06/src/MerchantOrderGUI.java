import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;


public class MerchantOrderGUI extends JFrame{
    private JLabel titleLabel, lblOrderStatus, lblOrderNoteLabel, lblOrderNoteText;
    private JButton logoutButton, backButton;
    private JRadioButton[] statusButtons;
    private ButtonGroup statusGroup;
    private JTable itemTable;
    private JScrollPane tableScrollPane;

    private JLabel lblOrderId, lblCustomerName, lblPickupTime, lblCustomerPhone, lblPickupMethod, lblTotalAmount;
    private JLabel valOrderId, valCustomerName, valPickupTime, valCustomerPhone, valPickupMethod, valTotalAmount;

    private int orderId;
    private int vendorId;
    private boolean isUpdatingStatus = false;
    
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    public MerchantOrderGUI(int orderId) {
        this.orderId = orderId;
        setTitle("政大校園訂餐系統");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initializeComponents();
        layoutComponents();
        setBackground(WHITE_BACKGROUND);
        loadOrderData();  // 載入資料
        setVisible(true);
    }

    private void initializeComponents() {
        titleLabel = new JLabel("政大校園訂餐系統 - 商家");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        logoutButton = createRoundedButton("登出");
        logoutButton.setBackground(WHITE);
        logoutButton.addActionListener(e -> {
        	JOptionPane.showMessageDialog(this, "帳號已登出");
            dispose();
            new LoginRegister();
        });
        
        backButton = createRoundedButton("回上頁");
        backButton.setBackground(WHITE);
        backButton.addActionListener(e -> {
            dispose();
            new MerchantOrderListGUI(vendorId);
        });

        lblOrderStatus = new JLabel("訂單狀態：");
        String[] statusTexts = {"已完成", "待取餐", "待送餐", "準備中", "待接單", "已拒絕"};
        statusButtons = new JRadioButton[statusTexts.length];
        statusGroup = new ButtonGroup();
        for (int i = 0; i < statusTexts.length; i++) {
            final String status = statusTexts[i];
            statusButtons[i] = new JRadioButton(status);
            statusButtons[i].setOpaque(false);
            statusGroup.add(statusButtons[i]);
            statusButtons[i].addActionListener(e -> {
                if (!isUpdatingStatus) {
                    updateOrderStatus(status);
                }
            });
        }

        lblOrderId = new JLabel("訂單編號："); valOrderId = createValueLabel("");
        lblCustomerName = new JLabel("顧客姓名："); valCustomerName = createValueLabel("");
        lblPickupTime = new JLabel("取餐時間："); valPickupTime = createValueLabel("");
        lblCustomerPhone = new JLabel("顧客電話："); valCustomerPhone = createValueLabel("");
        lblPickupMethod = new JLabel("取餐方式："); valPickupMethod = createValueLabel("");
        lblTotalAmount = new JLabel("訂單金額："); valTotalAmount = createValueLabel("");

        String[] headers = {"商品編號", "商品名稱", "商品價格", "商品數量", "商品備註"};
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        itemTable = new JTable(model);
        itemTable.setRowHeight(24);
        itemTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        itemTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tableScrollPane = new JScrollPane(itemTable);
        tableScrollPane.setPreferredSize(new Dimension(640, 80));

        lblOrderNoteLabel = new JLabel("訂單備註：");
        lblOrderNoteText = createValueLabel("");
    }
    

    private void layoutComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 240, 240));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(new EmptyBorder(10, 15, 10, 15));
        header.add(titleLabel, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel queryPanel = new JPanel(new GridLayout(1,2,520,0));
        queryPanel.setBackground(new Color(240, 240, 240));
        queryPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel lblQuery = new JLabel("訂單資訊");
        lblQuery.setFont(new Font("SansSerif", Font.BOLD, 16));
        queryPanel.add(lblQuery);
        queryPanel.add(backButton);
        mainPanel.add(queryPanel);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.add(lblOrderStatus);
        for (JRadioButton btn : statusButtons) statusPanel.add(btn);
        mainPanel.add(statusPanel);

        mainPanel.add(buildInfoRow(lblOrderId, valOrderId, lblCustomerName, valCustomerName));
        mainPanel.add(buildInfoRow(lblPickupTime, valPickupTime, lblCustomerPhone, valCustomerPhone));
        mainPanel.add(buildInfoRow(lblPickupMethod, valPickupMethod, lblTotalAmount, valTotalAmount));

        JPanel tableBlock = new JPanel();
        tableBlock.setBackground(new Color(240, 240, 240));
        tableBlock.setBorder(new EmptyBorder(10, 15, 0, 15));
        tableBlock.setLayout(new BorderLayout());
        JPanel tableHeader = new JPanel(new GridLayout(1,1));
        tableHeader.setBackground(new Color(240, 240, 240));
        tableHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableHeader.add(new JLabel("訂單內容："));
        tableBlock.add(tableHeader, BorderLayout.NORTH);
        tableBlock.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(tableBlock);

        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notePanel.setBackground(new Color(240, 240, 240));
        notePanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        notePanel.add(lblOrderNoteLabel);
        notePanel.add(lblOrderNoteText);
        mainPanel.add(notePanel);

        add(mainPanel, BorderLayout.CENTER);
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
        try (Connection conn = DBConnection.getConnection()) {
        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            // 查訂單資訊 + 顧客
            String sql = "SELECT o.*, u.name AS customer_name, u.telephone AS customer_phone " +
                         "FROM `order` o JOIN `user` u ON o.user_id = u.user_id WHERE o.order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                valOrderId.setText(String.valueOf(orderId));
                valCustomerName.setText(rs.getString("customer_name"));
                valCustomerPhone.setText(rs.getString("customer_phone"));
                valPickupTime.setText(formatter.format(rs.getTimestamp("finish_time")));

                String type = rs.getString("type");
                String location = rs.getString("location");
                valPickupMethod.setText(type + " - " + location);
                valTotalAmount.setText("$" + rs.getInt("money"));
                lblOrderNoteText.setText(rs.getString("note") != null ? rs.getString("note") : "");
                vendorId = rs.getInt("vendor_id");
                fetchVendorName();

                // 設定狀態按鈕預設值
                String currentStatus = rs.getString("status");
                isUpdatingStatus = true;
                for (JRadioButton btn : statusButtons) {
                    btn.setSelected(btn.getText().equals(currentStatus));
                }
                isUpdatingStatus = false;
            }

            // 查訂單明細
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            model.setRowCount(0);
            String sqlDetail = "SELECT product_id, name, price, quantity, customization FROM orderdetail WHERE order_id = ?";
            PreparedStatement detailStmt = conn.prepareStatement(sqlDetail);
            detailStmt.setInt(1, orderId);
            ResultSet rsDetail = detailStmt.executeQuery();
            while (rsDetail.next()) {
                Object[] row = {
                    rsDetail.getInt("product_id"),
                    rsDetail.getString("name"),
                    "$" + rsDetail.getInt("price"),
                    rsDetail.getInt("quantity"),
                    rsDetail.getString("customization")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "讀取訂單資料失敗：" + e.getMessage());
        }
    }

    private void updateOrderStatus(String newStatus) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE `order` SET status = ? WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "訂單狀態已更新");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "更新訂單狀態失敗：" + e.getMessage());
        }
    }

    private JPanel buildInfoRow(JLabel l1, JLabel v1, JLabel l2, JLabel v2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setBackground(new Color(240, 240, 240));
        row.setBorder(new EmptyBorder(5, 15, 5, 15));
        row.add(wrapWithField(l1, v1));
        row.add(wrapWithField(l2, v2));
        return row;
    }

    private JPanel wrapWithField(JLabel label, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.add(label, BorderLayout.WEST);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }
    

    private JLabel createValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setOpaque(true);
        lbl.setBackground(Color.white);
        lbl.setBorder(new EmptyBorder(4, 8, 4, 8));
        return lbl;
    }

    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(WHITE);
        btn.setBackground(Color.white);
        return btn;
    }
}
