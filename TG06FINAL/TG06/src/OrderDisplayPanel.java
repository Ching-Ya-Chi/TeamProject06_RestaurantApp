// OrderDisplayPanel.java
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal; // Add import

public class OrderDisplayPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private final NavigationController navController;
    private final Color LIGHT_BLUE = new Color(173, 216, 230);
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    private JLabel titleLabel;
    private JLabel orderItemIdLabel;
    private JLabel orderItemNameLabel;
    private JLabel orderItemPriceLabel;
    private JTextArea customizationField;
    private JSpinner quantitySpinner;
    
    private int currentRestaurantIdForOrder; // 新增
    private String currentMenuItemId;       // 新增，存儲 product_id
    private String currentRestaurantNameForOrder; // To know which menu to return to

    public OrderDisplayPanel(NavigationController navController) {
        this.navController = navController;
        setLayout(new BorderLayout());
        setBackground(WHITE_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        // --- Top Controls ---
        JPanel topControls = new JPanel(new BorderLayout());
        topControls.setOpaque(false);
        JButton backButton = new JButton("返回菜單");
        backButton.setBackground(LIGHT_BLUE);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> navController.navigateBackToMenu(currentRestaurantNameForOrder,currentRestaurantIdForOrder));
        topControls.add(backButton, BorderLayout.WEST);

        titleLabel = new JLabel("點餐", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        topControls.add(titleLabel, BorderLayout.CENTER);
        add(topControls, BorderLayout.NORTH);

        // --- Center Panel for Item Details ---
        JPanel centerOrderPanel = new JPanel();
        centerOrderPanel.setLayout(new BoxLayout(centerOrderPanel, BoxLayout.Y_AXIS));
        centerOrderPanel.setBackground(WHITE_BACKGROUND);
        centerOrderPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        orderItemIdLabel = new JLabel("編號：");
        orderItemIdLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        orderItemIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerOrderPanel.add(orderItemIdLabel);

        orderItemNameLabel = new JLabel("品名：");
        orderItemNameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        orderItemNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerOrderPanel.add(orderItemNameLabel);

        orderItemPriceLabel = new JLabel("價格：");
        orderItemPriceLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        orderItemPriceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerOrderPanel.add(orderItemPriceLabel);

        centerOrderPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel customizationLabel = new JLabel("客製化需求 (例如：少冰、不要香菜)：");
        customizationLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        customizationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customizationField = new JTextArea(8, 30);
        customizationField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        customizationField.setAlignmentX(Component.LEFT_ALIGNMENT);
        customizationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, customizationField.getPreferredSize().height));
        centerOrderPanel.add(customizationLabel);
        centerOrderPanel.add(customizationField);

        centerOrderPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        quantityPanel.setOpaque(false);
        quantityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel quantityLabel = new JLabel("選擇數量：");
        quantityLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        quantitySpinner.setFont(new Font("SansSerif", Font.PLAIN, 14));
        quantitySpinner.setPreferredSize(new Dimension(60, quantitySpinner.getPreferredSize().height));
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        centerOrderPanel.add(quantityPanel);

        centerOrderPanel.add(Box.createVerticalGlue());
        add(centerOrderPanel, BorderLayout.CENTER);

        // --- Bottom Panel for "Add to Cart" Button ---
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomButtonPanel.setOpaque(false);
        bottomButtonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        JButton addToCartButton = new JButton("加入點餐單");
        addToCartButton.setBackground(LIGHT_BLUE);
        addToCartButton.setOpaque(true);
        addToCartButton.setBorderPainted(false);
        addToCartButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        addToCartButton.setPreferredSize(new Dimension(150, 40));
        // 修改 addToCartButton 的 ActionListener
        addToCartButton.addActionListener(e -> {
            // String itemId = orderItemIdLabel.getText().replace("編號：","").trim(); // 改為從 currentMenuItemId 獲取
            String currentItemName = orderItemNameLabel.getText().replace("品名：","").trim();
            // 從 priceLabelText 解析價格（或者直接從傳入的 MenuItemData 獲取）
            // 為了更可靠，最好是在 loadOrderItem 時就存儲 BigDecimal 類型的單價
            String priceStringOnly = orderItemPriceLabel.getText().replaceAll("[^\\d.]", ""); // 提取數字和小數點
            BigDecimal unitPrice;
            try {
                if (priceStringOnly.isEmpty()){ // 如果解析不出數字 (例如 "價格：免費" 之類)
                    unitPrice = BigDecimal.ZERO; // 或其他默認值
                } else {
                    unitPrice = new BigDecimal(priceStringOnly);
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Error parsing price from label: " + orderItemPriceLabel.getText());
                JOptionPane.showMessageDialog(this, "價格解析錯誤，無法加入點餐單。", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = (Integer) quantitySpinner.getValue();
            String customization = customizationField.getText();

            OrderItem newItem = new OrderItem(
                    this.currentMenuItemId, // 使用存儲的 product_id
                    currentItemName,
                    unitPrice,
                    quantity,
                    customization,
                    currentRestaurantNameForOrder // restaurantName
                                                // OrderItem 內部可以考慮也存儲 restaurantId
            );

            navController.addItemToCart(newItem, this.currentRestaurantIdForOrder); // 傳遞 restaurantId

            JOptionPane.showMessageDialog(OrderDisplayPanel.this,
                    "已將 " + quantity + "份 " + currentItemName + " 加入點餐單。",
                    "點餐成功", JOptionPane.INFORMATION_MESSAGE);

            navController.navigateBackToMenu(currentRestaurantNameForOrder, this.currentRestaurantIdForOrder);
        });
        bottomButtonPanel.add(addToCartButton);
        add(bottomButtonPanel, BorderLayout.SOUTH);
    }

    public void loadOrderItem(MenuItemData item, String restaurantName, int restaurantId) {
        this.currentRestaurantNameForOrder = restaurantName;
        this.currentRestaurantIdForOrder = restaurantId; // 存儲 ID
        this.currentMenuItemId = ""+item.getID(); // 存儲 product_id

        titleLabel.setText("點餐 - " + item.getName());
        // orderItemIdLabel.setText("編號：" + item.id()); // product_id，如果還需要顯示
        orderItemIdLabel.setVisible(false); // 暫時隱藏編號，因為 product_id 可能不是用戶直接關心的

        orderItemNameLabel.setText("品名：" + item.getName());
        orderItemPriceLabel.setText("價格：" + item.getPrice()); // 使用 MenuItemData 的價格
        customizationField.setText("");
        quantitySpinner.setValue(1);
    }

}