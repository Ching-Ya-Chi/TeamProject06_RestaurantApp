// CartPanel.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent; //
import java.awt.event.ActionListener; //
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CartPanel extends JPanel {
    private static final long serialVersionUID = 1L; // Added serialVersionUID
    private final NavigationController navController;
    private final UserCartActions cartActions;

    // Use consistent color names if defined elsewhere, or define them here
    private final Color LIGHT_BLUE = new Color(173, 216, 230);
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    private JPanel itemsPanelContainer;
    private JScrollPane scrollPane;
    private JLabel totalAmountLabel;
    private JLabel restaurantContextLabel;
    private JTextArea orderNoteTextArea;

    private String currentRestaurantForCart;
    private int currentRestaurantIdForCart;
    private List<OrderItem> currentlyDisplayedItems;

    // UI for order type and location
    private JComboBox<String> orderTypeComboBox;
    private JComboBox<String> locationComboBox;
    private JComboBox<String> expectedTimeComboBox;
    private final String[] EXPECTED_TIME_OPTIONS = {
        "盡快 (約30分鐘後)",
        "1小時後",
        "1.5小時後",
        "2小時後",
        // "指定時間" // 如果選擇這個，可以彈出 JSpinner 或其他時間選擇器
    };
    private final String[] ORDER_TYPES = {"自取", "外送"}; // From metadata
    private final String[] SELF_PICKUP_LOCATIONS = {"到店自取"}; // From metadata 'order.location' ENUM
    private final String[] DELIVERY_LOCATIONS = {"政大正門取餐處", "政大東側門取餐處", "政大西側門取餐處", "自強十舍取餐處"}; // From metadata

    public interface UserCartActions {
        // Signature from previous step, accepting order type and location
    	void submitOrder(List<OrderItem> items, String restaurantName, int restaurantId,String orderType, String locationDetails,Date expectedFinishTime, String orderNote); // <--- 新增預期完成時間參數
    	void clearCartForRestaurant(String restaurantName, int restaurantId);
    }

    public CartPanel(NavigationController navController, UserCartActions cartActions) {
        this.navController = navController;
        this.cartActions = cartActions;
        this.currentlyDisplayedItems = new ArrayList<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(WHITE_BACKGROUND);
        initComponents();
    }

    private void initComponents() {
    	Font commonFont = new Font("Microsoft JhengHei", Font.PLAIN, 14);
        Font labelFont = new Font("Microsoft JhengHei", Font.BOLD, 14);
        // --- Top Panel: Title and Restaurant Context ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false); // Match background
        topPanel.setBackground(WHITE_BACKGROUND);


        JLabel titleLabel = new JLabel("我的點餐單", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24)); // Use consistent font
        topPanel.add(titleLabel, BorderLayout.CENTER);

        restaurantContextLabel = new JLabel("", SwingConstants.CENTER);
        restaurantContextLabel.setFont(new Font("Microsoft JhengHei", Font.ITALIC, 14));
        topPanel.add(restaurantContextLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

     // --- Center Panel: List of Items and Order Options ---
        JPanel centerContainerPanel = new JPanel(new BorderLayout(10,15));
        centerContainerPanel.setOpaque(false);
        centerContainerPanel.setBackground(WHITE_BACKGROUND);

        itemsPanelContainer = new JPanel();
        itemsPanelContainer.setLayout(new BoxLayout(itemsPanelContainer, BoxLayout.Y_AXIS));
        itemsPanelContainer.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(itemsPanelContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerContainerPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Order Options Panel ---
        JPanel orderOptionsPanel = new JPanel();
        orderOptionsPanel.setOpaque(false);
        orderOptionsPanel.setBackground(WHITE_BACKGROUND);
        orderOptionsPanel.setLayout(new BoxLayout(orderOptionsPanel, BoxLayout.Y_AXIS));
        orderOptionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), "訂單選項與備註", // Updated title
                    TitledBorder.LEFT, TitledBorder.TOP,
                    new Font("Microsoft JhengHei", Font.BOLD, 16), Color.DARK_GRAY // Slightly larger title
            ),
            new EmptyBorder(10,10,10,10) // Increased padding
        ));

        // Order Type
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Increased vertical gap
        typePanel.setOpaque(false);
        typePanel.setBackground(WHITE_BACKGROUND);
        JLabel typeDescLabel = new JLabel("取餐方式:");
        typeDescLabel.setFont(labelFont);
        typePanel.add(typeDescLabel);
        orderTypeComboBox = new JComboBox<>(ORDER_TYPES);
        orderTypeComboBox.setFont(commonFont);
        orderTypeComboBox.setSelectedIndex(0);
        typePanel.add(orderTypeComboBox);
        orderOptionsPanel.add(typePanel);

        // Location
        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        locationPanel.setOpaque(false);
        locationPanel.setBackground(WHITE_BACKGROUND);
        JLabel locDescLabel = new JLabel("選擇地點:");
        locDescLabel.setFont(labelFont);
        locationPanel.add(locDescLabel);
        locationComboBox = new JComboBox<>();
        locationComboBox.setFont(commonFont);
        locationComboBox.setPreferredSize(new Dimension(220, locationComboBox.getPreferredSize().height));
        locationPanel.add(locationComboBox);
        orderOptionsPanel.add(locationPanel);

        // Expected Time
        JPanel expectedTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        expectedTimePanel.setOpaque(false);
        expectedTimePanel.setBackground(WHITE_BACKGROUND);
        JLabel timeDescLabel = new JLabel("預期時間:"); // Shorter label
        timeDescLabel.setFont(labelFont);
        expectedTimePanel.add(timeDescLabel);
        expectedTimeComboBox = new JComboBox<>(EXPECTED_TIME_OPTIONS);
        expectedTimeComboBox.setFont(commonFont);
        expectedTimeComboBox.setSelectedIndex(0);
        expectedTimePanel.add(expectedTimeComboBox);
        orderOptionsPanel.add(expectedTimePanel);

        orderTypeComboBox.addActionListener(e -> updateLocationOptions());
        updateLocationOptions();

        // --- 新增：訂單備註 ---
        JPanel notePanel = new JPanel(new BorderLayout(5,2)); // BorderLayout for label on top
        notePanel.setOpaque(false);
        notePanel.setBackground(WHITE_BACKGROUND);
        JLabel noteDescLabel = new JLabel("訂單備註 (例如: 少餐具、發票統編等):");
        noteDescLabel.setFont(labelFont);
        notePanel.add(noteDescLabel, BorderLayout.NORTH);

        orderNoteTextArea = new JTextArea(3, 20); // 3行高，約20個字符寬 (會隨內容擴展)
        orderNoteTextArea.setFont(commonFont);
        orderNoteTextArea.setLineWrap(true); // 自動換行
        orderNoteTextArea.setWrapStyleWord(true); // 按單詞換行
        JScrollPane noteScrollPane = new JScrollPane(orderNoteTextArea); // 給備註區加滾動條
        noteScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        notePanel.add(noteScrollPane, BorderLayout.CENTER);
        orderOptionsPanel.add(Box.createRigidArea(new Dimension(0,5))); // Add some space
        orderOptionsPanel.add(notePanel);
        // ---------------------

        centerContainerPanel.add(orderOptionsPanel, BorderLayout.SOUTH);
        add(centerContainerPanel, BorderLayout.CENTER);

        // --- Bottom Panel: Total Amount and Action Buttons ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBackground(WHITE_BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        totalAmountLabel = new JLabel("總金額: $0.00");
        totalAmountLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        totalAmountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomPanel.add(totalAmountLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBackground(WHITE_BACKGROUND);


        JButton backToMenuButton = new JButton("返回菜單");
        styleButton(backToMenuButton, LIGHT_BLUE);
        backToMenuButton.addActionListener(e -> {
            if (currentRestaurantForCart != null && currentRestaurantIdForCart > 0) {
                navController.navigateBackToMenu(currentRestaurantForCart, currentRestaurantIdForCart);
            } else {
                navController.showRestaurantList();
            }
        });
        buttonsPanel.add(backToMenuButton);

        JButton submitOrderButton = new JButton("送出訂單");
        styleButton(submitOrderButton, new Color(144, 238, 144)); // Light green
        submitOrderButton.addActionListener(e -> {
            if (cartActions != null) {
                if (this.currentlyDisplayedItems == null || this.currentlyDisplayedItems.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "您的點餐單是空的！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String selectedOrderType = (String) orderTypeComboBox.getSelectedItem();
                String selectedLocation = (String) locationComboBox.getSelectedItem();
                String selectedExpectedTimeOption = (String) expectedTimeComboBox.getSelectedItem();
                String orderNote = orderNoteTextArea.getText().trim(); // <--- 獲取訂單備註

                if (selectedOrderType == null || selectedLocation == null || selectedLocation.trim().isEmpty() || selectedExpectedTimeOption == null) {
                    JOptionPane.showMessageDialog(this, "請完整選擇訂單選項！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 將選擇的預期時間轉換為 Date 對象
                Date expectedFinishTime = calculateExpectedFinishTime(selectedExpectedTimeOption);
                if (expectedFinishTime == null) { // 應該不會發生，除非選項解析錯誤
                    JOptionPane.showMessageDialog(this, "預期時間選擇錯誤！", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                cartActions.submitOrder(this.currentlyDisplayedItems, currentRestaurantForCart, currentRestaurantIdForCart,
                                        selectedOrderType, selectedLocation, expectedFinishTime,orderNote);
            }
        });
        buttonsPanel.add(submitOrderButton);

        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Color backgroundColor) { // Added color param
        button.setBackground(backgroundColor);
        button.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14)); // Consistent font
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
    }

    private void updateLocationOptions() {
        String selectedType = (String) orderTypeComboBox.getSelectedItem();
        locationComboBox.removeAllItems();

        if (ORDER_TYPES[0].equals(selectedType)) { // 自取
            for (String loc : SELF_PICKUP_LOCATIONS) {
                locationComboBox.addItem(loc);
            }
        } else if (ORDER_TYPES[1].equals(selectedType)) { // 外送
            for (String loc : DELIVERY_LOCATIONS) {
                locationComboBox.addItem(loc);
            }
        }
        if (locationComboBox.getItemCount() > 0) {
            locationComboBox.setSelectedIndex(0);
        }
    }

    private Date calculateExpectedFinishTime(String selectedOption) {
        Calendar calendar = Calendar.getInstance(); // 獲取當前時間
        switch (selectedOption) {
            case "盡快 (約30分鐘後)":
                calendar.add(Calendar.MINUTE, 30);
                break;
            case "1小時後":
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case "1.5小時後":
                calendar.add(Calendar.MINUTE, 90);
                break;
            case "2小時後":
                calendar.add(Calendar.HOUR_OF_DAY, 2);
                break;
            // case "指定時間":
                // TODO: 實現彈出 JSpinner<Date> 或其他時間選擇器
                // JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
                // JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "yyyy/MM/dd HH:mm");
                // timeSpinner.setEditor(timeEditor);
                // int result = JOptionPane.showConfirmDialog(this, timeSpinner, "請選擇指定時間", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                // if (result == JOptionPane.OK_OPTION) {
                //     return (Date) timeSpinner.getValue();
                // } else {
                //     return null; // 用戶取消
                // }
            default:
                // 默認情況，可以設為30分鐘後或返回 null 提示錯誤
                calendar.add(Calendar.MINUTE, 30); // 默認
                break;
        }
        return calendar.getTime();
    }

    public void displayOrderItems(List<OrderItem> items, String restaurantName, int restaurantId) {
        this.currentRestaurantForCart = restaurantName;
        this.currentRestaurantIdForCart = restaurantId;
        this.currentlyDisplayedItems = new ArrayList<>(items); // Create a new list copy

        restaurantContextLabel.setText("餐廳: " + (restaurantName != null ? restaurantName : "未知"));
        itemsPanelContainer.removeAll(); // Clear previous items
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (this.currentlyDisplayedItems.isEmpty()) { // Check the member variable
            itemsPanelContainer.setLayout(new BorderLayout());
            JLabel emptyCartLabel = new JLabel("您的點餐單目前是空的。", SwingConstants.CENTER);
            emptyCartLabel.setFont(new Font("Microsoft JhengHei", Font.ITALIC, 16));
            itemsPanelContainer.add(emptyCartLabel, BorderLayout.CENTER);
        } else {
            itemsPanelContainer.setLayout(new BoxLayout(itemsPanelContainer, BoxLayout.Y_AXIS));
            for (OrderItem item : this.currentlyDisplayedItems) { // Iterate over the member variable
                itemsPanelContainer.add(createItemPanel(item));
                itemsPanelContainer.add(Box.createRigidArea(new Dimension(0, 8)));
                // Assuming OrderItem.getTotalPrice() returns BigDecimal
                totalAmount = totalAmount.add(item.getTotalPrice()); // Use BigDecimal version for sum
            }
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.TAIWAN);
        totalAmountLabel.setText("總金額: " + currencyFormatter.format(totalAmount));

        itemsPanelContainer.revalidate();
        itemsPanelContainer.repaint();
    }

    public int getCurrentRestaurantIdForCart() {
        return currentRestaurantIdForCart;
    }

    private JPanel createItemPanel(OrderItem item) {
        JPanel panel = new JPanel(new BorderLayout(10, 2));
        panel.setBackground(new Color(0xF5F5F5)); // Slightly different light gray
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(8, 12, 8, 12)
        ));
        // Set a fixed or max height to prevent large variations if descriptions differ
        // panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Example, adjust as needed

        JLabel nameLabel = new JLabel(String.format("%dx %s", item.getQuantity(), item.getName()));
        nameLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        panel.add(nameLabel, BorderLayout.NORTH);

        String customText = item.getCustomization();
        if (customText != null && !customText.trim().isEmpty()) {
            JLabel customLabel = new JLabel("客製化: " + customText);
            customLabel.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 12)); // Smaller italic
            panel.add(customLabel, BorderLayout.CENTER);
        } else {
             // Add a placeholder or empty label to maintain similar height if no customization
            panel.add(Box.createRigidArea(new Dimension(0,15)), BorderLayout.CENTER); // Adjust height
        }


        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.TAIWAN);
        // Assuming OrderItem.getTotalPrice() returns int for display simplicity or use getTotalPriceBigDecimal()
        JLabel priceLabel = new JLabel(currencyFormatter.format(item.getTotalPrice()));
        priceLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(priceLabel, BorderLayout.EAST);

        return panel;
    }

    // getDisplayedItems() is no longer needed and can be removed.
}