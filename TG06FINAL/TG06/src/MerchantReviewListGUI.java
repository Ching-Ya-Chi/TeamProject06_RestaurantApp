import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class MerchantReviewListGUI extends JFrame{
    private JLabel titleLabel;
    private JButton logoutButton;
    private JButton btnOrderQuery, btnProductInfo, btnBasicInfo, btnReviewInfo;
    private JTable reviewTable;
    private JScrollPane tableScrollPane;
    private JLabel avgLabelTitle, avgLabelValue;
    private int vendorId;
    
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    public MerchantReviewListGUI(int vendorId) {
        this.vendorId = vendorId;
        initializeFrame();
        initializeComponents();
        layoutComponents();
        fetchVendorName();
        loadReviewData();
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

        btnReviewInfo.setBackground(Color.LIGHT_GRAY);
        btnReviewInfo.setOpaque(true);
        btnReviewInfo.setContentAreaFilled(true);

        btnOrderQuery.addActionListener(e -> {
            dispose();
            new MerchantOrderListGUI(vendorId);
        });

        btnProductInfo.addActionListener(e -> {
            dispose();
            new MerchantProdutListGUI(vendorId);
        });

        btnBasicInfo.addActionListener(e -> {
            dispose();
            new MerchantProfileGUI(vendorId);
        });

        String[] columnNames = {"評分時間", "評分", "評論內容"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reviewTable = new JTable(model);
        reviewTable.setRowHeight(25);
        reviewTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        reviewTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tableScrollPane = new JScrollPane(reviewTable);
        tableScrollPane.setPreferredSize(new Dimension(660, 300));

        avgLabelTitle = new JLabel("平均評分：");
        avgLabelTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        avgLabelValue = new JLabel("--");
        avgLabelValue.setFont(new Font("SansSerif", Font.PLAIN, 14));
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

        JPanel avgPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        avgPanel.add(avgLabelTitle);
        avgPanel.add(avgLabelValue);
        add(avgPanel, BorderLayout.SOUTH);
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

    private void loadReviewData() {
        DefaultTableModel model = (DefaultTableModel) reviewTable.getModel();
        model.setRowCount(0);
        double totalRating = 0;
        int count = 0;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT rating, review, comment_time FROM comment WHERE vendor_id = ? ORDER BY comment_time DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            while (rs.next()) {
                String time = formatter.format(rs.getTimestamp("comment_time"));
                int rating = rs.getInt("rating");
                String review = rs.getString("review");
                model.addRow(new Object[]{time, rating, review});
                totalRating += rating;
                count++;
            }
            if (count > 0) {
                DecimalFormat df = new DecimalFormat("0.00");
                avgLabelValue.setText(df.format(totalRating / count));
            } else {
                avgLabelValue.setText("--");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "讀取評論失敗：" + e.getMessage());
        }
    }
}
