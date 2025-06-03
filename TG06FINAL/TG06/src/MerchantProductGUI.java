import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MerchantProductGUI extends JFrame{
    private JLabel titleLabel, lblProductStatus;
    private JButton logoutButton, backButton, btnUpdate;
    private JRadioButton[] statusButtons;
    private ButtonGroup statusGroup;
    private JTextField txtProductId, txtProductName, txtProductPrice, txtProductDescription;
    private JLabel imageDisplay;
    private JButton btnUploadImage;
    private Integer productId;
    private int vendorId;
    private String imageUrl = null;
    
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    public MerchantProductGUI(Integer productId, int vendorId) {
        this.productId = productId;
        this.vendorId = vendorId;

        setTitle("政大校園訂餐系統");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeComponents();
        layoutComponents();
        loadProductData();
        fetchVendorName();
        setBackground(WHITE_BACKGROUND);
        setVisible(true);
    }

    private void initializeComponents() {
        titleLabel = new JLabel("政大校園訂餐系統 - 商家");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        logoutButton = createRoundedButton("登出");
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "帳號已登出");
            dispose();
            new LoginRegister();
        });

        backButton = createRoundedButton("回上頁");
        backButton.addActionListener(e -> {
            dispose();
            new MerchantProdutListGUI(vendorId);
        });

        lblProductStatus = new JLabel("商品狀態：");
        String[] statusTexts = {"供應中", "暫停供應"};
        statusButtons = new JRadioButton[statusTexts.length];
        statusGroup = new ButtonGroup();
        for (int i = 0; i < statusTexts.length; i++) {
            statusButtons[i] = new JRadioButton(statusTexts[i]);
            statusButtons[i].setOpaque(false);
            statusGroup.add(statusButtons[i]);
        }

        txtProductId = new JTextField(20);
        txtProductId.setEditable(false);
        txtProductName = new JTextField(20);
        txtProductPrice = new JTextField(20);
        txtProductDescription = new JTextField(40);

        imageDisplay = new JLabel();
        imageDisplay.setPreferredSize(new Dimension(200, 100));
        imageDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        btnUploadImage = createRoundedButton("上傳圖片");
        btnUploadImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                imageDisplay.setIcon(new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH)));
                imageUrl = path; // 模擬儲存圖片 URL
            }
        });

        btnUpdate = createRoundedButton("確認修改或新增");
        btnUpdate.addActionListener(e -> saveOrUpdateProduct());
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
        JLabel lblQuery = new JLabel("商品資訊");
        lblQuery.setFont(new Font("SansSerif", Font.BOLD, 16));
        queryPanel.add(lblQuery);
        queryPanel.add(backButton);
        mainPanel.add(queryPanel);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.add(lblProductStatus);
        for (JRadioButton btn : statusButtons) statusPanel.add(btn);
        mainPanel.add(statusPanel);

        mainPanel.add(buildInputRow("商品編號：", txtProductId, "商品名稱：", txtProductName));
        mainPanel.add(buildInputRow("商品售價：", txtProductPrice, "商品介紹：", txtProductDescription));

        JPanel tableBlock = new JPanel();
        tableBlock.setBackground(new Color(240, 240, 240));
        tableBlock.setBorder(new EmptyBorder(10, 15, 0, 15));
        tableBlock.setLayout(new BorderLayout());

        JPanel tableHeader = new JPanel(new GridLayout(1, 1));
        tableHeader.setBackground(new Color(240, 240, 240));
        tableHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableHeader.add(new JLabel("商品圖片："));
        tableBlock.add(tableHeader, BorderLayout.NORTH);

        JPanel imagePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.add(imageDisplay);
        imagePanel.add(btnUploadImage);
        tableBlock.add(imagePanel, BorderLayout.CENTER);
        mainPanel.add(tableBlock);

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

    private void loadProductData() {
        if (productId == null) return;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM product JOIN vendor USING(vendor_id) WHERE product_id = ?");
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                titleLabel.setText("政大校園訂餐系統 - " + rs.getString("vendor_name"));
                txtProductId.setText(String.valueOf(productId));
                txtProductName.setText(rs.getString("name"));
                txtProductPrice.setText(String.valueOf(rs.getInt("price")));
                txtProductDescription.setText(rs.getString("description"));
                String status = rs.getString("status");
                for (JRadioButton btn : statusButtons) {
                    btn.setSelected(btn.getText().equals(status));
                }
                imageUrl = rs.getString("picture_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    imageDisplay.setIcon(new ImageIcon(new ImageIcon(new java.net.URL(imageUrl)).getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH)));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "載入商品失敗：" + e.getMessage());
        }
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

    private void saveOrUpdateProduct() {
        String name = txtProductName.getText();
        try (Connection conn = DBConnection.getConnection()) {
            if (productId == null) {
                PreparedStatement check = conn.prepareStatement("SELECT * FROM product WHERE vendor_id=? AND name=?");
                check.setInt(1, vendorId);
                check.setString(2, name);
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "此商品已存在");
                    return;
                }
                PreparedStatement insert = conn.prepareStatement("INSERT INTO product (vendor_id, name, price, description, status, picture_url) VALUES (?, ?, ?, ?, ?, ?)");
                insert.setInt(1, vendorId);
                insert.setString(2, name);
                insert.setInt(3, Integer.parseInt(txtProductPrice.getText().replace("$", "")));
                insert.setString(4, txtProductDescription.getText());
                insert.setString(5, statusButtons[0].isSelected() ? "供應中" : "暫停供應");
                insert.setString(6, imageUrl);
                insert.executeUpdate();
                JOptionPane.showMessageDialog(this, "新增成功");
                dispose();
                new MerchantProdutListGUI(vendorId);
            } else {
                PreparedStatement update = conn.prepareStatement("UPDATE product SET name=?, price=?, description=?, status=?, picture_url=? WHERE product_id = ?");
                update.setString(1, name);
                update.setInt(2, Integer.parseInt(txtProductPrice.getText().replace("$", "")));
                update.setString(3, txtProductDescription.getText());
                update.setString(4, statusButtons[0].isSelected() ? "供應中" : "暫停供應");
                update.setString(5, imageUrl);
                update.setInt(6, productId);
                update.executeUpdate();
                JOptionPane.showMessageDialog(this, "修改成功");
                dispose();
                new MerchantProdutListGUI(vendorId);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "儲存失敗：" + e.getMessage());
        }
    }
}
