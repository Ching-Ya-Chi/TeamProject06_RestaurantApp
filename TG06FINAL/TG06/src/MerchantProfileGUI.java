import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MerchantProfileGUI extends JFrame{
    private JLabel titleLabel;
    private JButton logoutButton, backButton, btnUpdate, btnUpdateHours;
    private JComboBox<String> weekdayCombo;
    private JTextField txtOpenTime, txtCloseTime;
    private JCheckBox chkClosed;
    private JTextField txtShopName, txtManager, txtAddress, txtShopPhone, txtManagerPhone, txtEmail, txtMapUrl;
    private JComboBox<String> txtShopType;
    private JTextArea txtShopDescription;
    private int vendorId;
    
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    public MerchantProfileGUI(int vendorId) {
        this.vendorId = vendorId;
        setTitle("政大校園訂餐系統");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeComponents();
        layoutComponents();
        loadVendorInfo();
        loadCurrentBusinessHours();
        setBackground(WHITE_BACKGROUND);
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

        weekdayCombo = new JComboBox<>(new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"});
        weekdayCombo.addActionListener(e -> loadCurrentBusinessHours());
        txtOpenTime = new JTextField(8);
        txtCloseTime = new JTextField(8);
        chkClosed = new JCheckBox("公休");
        btnUpdateHours = createRoundedButton("確認更新營業時間");

        btnUpdateHours.addActionListener(e -> updateBusinessHours());

        txtShopName = new JTextField(20);
        txtShopType = new JComboBox<>(new String[]{"餐廳", "飲料店", "其他"});
        txtManager = new JTextField(20); // owner_name
        txtAddress = new JTextField(20);
        txtShopPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        txtMapUrl = new JTextField(20);
        txtManagerPhone = new JTextField(20); // owner_phone

        txtShopDescription = new JTextArea(4, 40);
        txtShopDescription.setLineWrap(true);
        txtShopDescription.setWrapStyleWord(true);

        btnUpdate = createRoundedButton("確認修改");
        btnUpdate.addActionListener(e -> updateVendorInfo());
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

        JPanel queryPanel = new JPanel(new GridLayout(1, 2, 520, 0));
        queryPanel.setBackground(new Color(240, 240, 240));
        queryPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel lblQuery = new JLabel("商家資訊");
        lblQuery.setFont(new Font("SansSerif", Font.BOLD, 16));
        queryPanel.add(lblQuery);
        queryPanel.add(backButton);
        mainPanel.add(queryPanel);

        JPanel hoursPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        hoursPanel.setBackground(new Color(240, 240, 240));
        hoursPanel.add(new JLabel("營業時間："));
        hoursPanel.add(weekdayCombo);
        hoursPanel.add(txtOpenTime);
        hoursPanel.add(new JLabel("～"));
        hoursPanel.add(txtCloseTime);
        hoursPanel.add(chkClosed);
        hoursPanel.add(btnUpdateHours);
        mainPanel.add(hoursPanel);

        mainPanel.add(buildInputRow("商家名稱：", txtShopName, "商家類型：", txtShopType));
        mainPanel.add(buildInputRow("負責人員：", txtManager, "商家地址：", txtAddress));
        mainPanel.add(buildInputRow("商家電話：", txtShopPhone, "負責人電話：", txtManagerPhone));
        mainPanel.add(buildInputRow("商家信箱：", txtEmail, "地圖網址：", txtMapUrl));

        JPanel descriptionBlock = new JPanel(new BorderLayout());
        descriptionBlock.setBackground(new Color(240, 240, 240));
        descriptionBlock.setBorder(new EmptyBorder(10, 15, 0, 15));
        descriptionBlock.add(new JLabel("商家介紹："), BorderLayout.NORTH);
        descriptionBlock.add(new JScrollPane(txtShopDescription), BorderLayout.CENTER);
        mainPanel.add(descriptionBlock);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        buttonPanel.add(btnUpdate);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel buildInputRow(String label1, JComponent field1, String label2, JComponent field2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setBackground(new Color(240, 240, 240));
        row.setBorder(new EmptyBorder(5, 15, 5, 15));
        row.add(wrapWithField(new JLabel(label1), field1));
        row.add(wrapWithField(new JLabel(label2), field2));
        return row;
    }

    private JPanel wrapWithField(JLabel label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.white);
        return btn;
    }

    private void loadVendorInfo() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM vendor WHERE vendor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                titleLabel.setText("政大校園訂餐系統 - " + rs.getString("vendor_name"));
                txtShopName.setText(rs.getString("vendor_name"));
                txtShopType.setSelectedItem(rs.getString("type"));
                txtManager.setText(rs.getString("owner_name"));
                txtAddress.setText(rs.getString("address"));
                txtShopPhone.setText(rs.getString("vendor_phone"));
                txtEmail.setText(rs.getString("email"));
                txtMapUrl.setText(rs.getString("map_url"));
                txtManagerPhone.setText(rs.getString("owner_phone"));
                txtShopDescription.setText(rs.getString("description"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "載入商家資料失敗：" + e.getMessage());
        }
    }

    private void loadCurrentBusinessHours() {
        int weekday = weekdayCombo.getSelectedIndex() + 1;
        String startCol = getStartTimeColumn(weekday);
        String endCol = getEndTimeColumn(weekday);
        String closedCol = getClosedColumn(weekday);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT " + startCol + ", " + endCol + ", " + closedCol + " FROM vendor WHERE vendor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtOpenTime.setText(rs.getString(1));
                txtCloseTime.setText(rs.getString(2));
                chkClosed.setSelected(!rs.getBoolean(3));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "讀取營業時間失敗：" + e.getMessage());
        }
    }

    private void updateVendorInfo() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE vendor SET vendor_name=?, type=?, owner_name=?, address=?, vendor_phone=?, owner_phone=?, email=?, map_url=?, description=? WHERE vendor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtShopName.getText());
            stmt.setString(2, (String) txtShopType.getSelectedItem());
            stmt.setString(3, txtManager.getText());
            stmt.setString(4, txtAddress.getText());
            stmt.setString(5, txtShopPhone.getText());
            stmt.setString(7, txtEmail.getText());
            stmt.setString(8, txtMapUrl.getText());
            stmt.setString(6, txtManagerPhone.getText());
            stmt.setString(9, txtShopDescription.getText());
            stmt.setInt(10, vendorId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "商家資料已更新");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "更新商家資料失敗：" + e.getMessage());
        }
    }

    private void updateBusinessHours() {
        int weekday = weekdayCombo.getSelectedIndex() + 1;
        String startCol = getStartTimeColumn(weekday);
        String endCol = getEndTimeColumn(weekday);
        String closedCol = getClosedColumn(weekday);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE vendor SET " + startCol + "=?, " + endCol + "=?, " + closedCol + "=? WHERE vendor_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtOpenTime.getText());
            stmt.setString(2, txtCloseTime.getText());
            stmt.setBoolean(3, !chkClosed.isSelected());
            stmt.setInt(4, vendorId);
            stmt.executeUpdate();
            String weekdayText = (String) weekdayCombo.getSelectedItem();
			String openText = chkClosed.isSelected() ? "公休" : "營業";
			JOptionPane.showMessageDialog(this, weekdayText + "營業時間已更新（" + openText + ")");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "更新營業時間失敗：" + e.getMessage());
        }
    }

    private String getStartTimeColumn(int weekday) {
        return new String[]{"mon_start_time", "tue_start_time", "wed_start_time", "thu_start_time", "fri_start_time", "sat_start_time", "sun_start_time"}[weekday - 1];
    }

    private String getEndTimeColumn(int weekday) {
        return new String[]{"mon_end_time", "tue_end_time", "wed_end_time", "thu_end_time", "fri_end_time", "sat_end_time", "sun_end_time"}[weekday - 1];
    }

    private String getClosedColumn(int weekday) {
        return new String[]{"mon_open", "tue_open", "wed_open", "thu_open", "fri_open", "sat_open", "sun_open"}[weekday - 1];
    }
}
