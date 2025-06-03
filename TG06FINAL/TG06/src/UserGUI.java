// UserGUI.java
import javax.swing.*;
import java.sql.Statement; // For RETURN_GENERATED_KEYS
import java.sql.Timestamp; // For order_datetime
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.plaf.FontUIResource;
import java.util.Enumeration;  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserGUI extends JFrame implements NavigationController, CartPanel.UserCartActions {
	private static final long serialVersionUID = 1L;
    private CardLayout rootCardLayout;
    private JPanel rootCardPanel;

    // Panels for different views
    private MainAppPanel mainAppPanel; // Holds map/list and their navigation
    private MenuDisplayPanel menuDisplayPanel;
    private OrderDisplayPanel orderDisplayPanel;
    private CriticizeDisplayPanel criticizeDisplayPanel;
    private CartPanel cartPanel;
    private String currentRestaurantName; // Still useful to track context
    private String currentUserAccount; // <--- 新增：存儲當前用戶賬號
    private int currentUserId = -1; // Store user_id after login/retrieval
    private Map<Integer, List<OrderItem>> shoppingCartById;

    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    // Panel Names for CardLayout
    private static final String MAIN_APP_VIEW = "MAIN_APP_VIEW";
    private static final String MENU_VIEW = "MENU_VIEW";
    private static final String ORDER_VIEW = "ORDER_VIEW";
    private static final String CRITICIZE_VIEW = "CRITICIZE_VIEW";
    private static final String CART_VIEW = "CART_VIEW"; // New view name


    public UserGUI(String account) {
        setTitle("餐廳應用程式");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        rootCardLayout = new CardLayout();
        rootCardPanel = new JPanel(rootCardLayout);
        rootCardPanel.setBackground(WHITE_BACKGROUND);
        add(rootCardPanel);

        // Initialize page panels, passing 'this' as the NavigationController
        this.shoppingCartById = new HashMap<>(); 
        this.currentUserAccount = account;
        this.currentUserId = getUserIdByAccount(this.currentUserAccount);

        if (this.currentUserId == -1) {
            JOptionPane.showMessageDialog(this, "無法獲取用戶ID，訂單功能可能受限。", "用戶錯誤", JOptionPane.ERROR_MESSAGE);
        }
        mainAppPanel = new MainAppPanel(this);
        menuDisplayPanel = new MenuDisplayPanel(this);
        orderDisplayPanel = new OrderDisplayPanel(this);
        criticizeDisplayPanel = new CriticizeDisplayPanel(this);
        cartPanel = new CartPanel(this, this);
        
        // Add panels to the root CardLayout
        rootCardPanel.add(mainAppPanel, MAIN_APP_VIEW);
        rootCardPanel.add(menuDisplayPanel, MENU_VIEW);
        rootCardPanel.add(orderDisplayPanel, ORDER_VIEW);
        rootCardPanel.add(criticizeDisplayPanel, CRITICIZE_VIEW);
        rootCardPanel.add(cartPanel, CART_VIEW); // Add cart panel

        // Show the main app layout by default
        showMainAppLayout();
    }
    public int getCurrentUserId() {
        return this.currentUserId;
    }
    public String getCurrentUserAccount() { // 也許有用，例如允許 guest 評論
        return this.currentUserAccount;
    }
    
        private int getUserIdByAccount(String account) {
            if (account == null || account.trim().isEmpty()) return -1;
            String sql = "SELECT user_id FROM user WHERE account = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, account);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("user_id");
                } else {
                    System.err.println("No user found with account: " + account);
                    return -1; // User not found
                }
            } catch (SQLException e) {
                System.err.println("Error fetching user_id for account " + account + ": " + e.getMessage());
                e.printStackTrace();
                return -1;
            }
        }
        
    public static void setDefaultFont() {
        Font defaultFont = new Font("Microsoft JhengHei", Font.BOLD, 13); // 選擇合適的字號
        FontUIResource fontResource = new FontUIResource(defaultFont);

        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontResource);
            }
        }
    }
 // --- NavigationController Implementation ---
    @Override
    public void showUserProfilePage() {
        this.setVisible(false); // 或 this.dispose() 如果確定要關閉 UserGUI
        // 假設 invoiceRough 的構造函數會 setVisible(true)
        new invoiceRough(this.currentUserAccount);
        // 如果 invoiceRough 也需要 NavigationController (例如從那裡返回 UserGUI)
        // 則 invoiceRough 可能需要一個方法來設置 UserGUI 的引用，或者 UserGUI 變為單例
    }
    @Override
    public void showMap() {
        // 這個方法應該告訴 MainAppPanel 去顯示地圖視圖
        if (mainAppPanel != null) {
            mainAppPanel.showMap(); // 調用 MainAppPanel 自己的方法來切換其內部 CardLayout
        }
        // 同時，確保 UserGUI 的 rootCardLayout 顯示的是 mainAppPanel
        if (rootCardPanel != null && mainAppPanel != null && !mainAppPanel.isVisible()) {
            rootCardLayout.show(rootCardPanel, MAIN_APP_VIEW);
        }
    }
    @Override
    public void showMenuPage(String restaurantName, int restaurantId) { // 更新簽名
        this.currentRestaurantName = restaurantName; // 仍然可以用於標題等
        // this.currentRestaurantId = restaurantId; // UserGUI 可能也需要存儲當前 ID
        menuDisplayPanel.loadMenuForRestaurant(restaurantName, restaurantId);
        rootCardLayout.show(rootCardPanel, MENU_VIEW);
    }

    @Override
    public void showOrderPage(MenuItemData itemData, String restaurantName, int restaurantId) { // 更新簽名
        this.currentRestaurantName = restaurantName;
        orderDisplayPanel.loadOrderItem(itemData, restaurantName, restaurantId);
        rootCardLayout.show(rootCardPanel, ORDER_VIEW);
    }

    @Override
    public void showCriticizePage(String restaurantName, int restaurantId) { // 更新簽名
        this.currentRestaurantName = restaurantName;
        // criticizeDisplayPanel.loadCriticizePage(restaurantName); // 舊的
        criticizeDisplayPanel.loadCriticizePage(restaurantName, restaurantId); // 假設 CriticizePanel 也需要ID
        rootCardLayout.show(rootCardPanel, CRITICIZE_VIEW);
    }
    
    @Override
    public void navigateBackToMenu(String restaurantName, int restaurantId) { // 更新簽名
        this.currentRestaurantName = restaurantName;
        menuDisplayPanel.loadMenuForRestaurant(restaurantName, restaurantId);
        rootCardLayout.show(rootCardPanel, MENU_VIEW);
    }

    @Override
    public void addItemToCart(OrderItem item, int restaurantId) { // 更新簽名
        // OrderItem 應該包含 restaurantName，但我們用 restaurantId 作為購物車的 key
        shoppingCartById.computeIfAbsent(restaurantId, k -> new ArrayList<>()).add(item);
        System.out.println("Item added to cart: " + item.getName() + " for restaurant ID: " + restaurantId);
    }

    @Override
    public void showCartPage(String restaurantName, int restaurantId) { // 更新簽名
        this.currentRestaurantName = restaurantName; // 用於 CartPanel 標題
        List<OrderItem> itemsForRestaurant = shoppingCartById.getOrDefault(restaurantId, new ArrayList<>());
        cartPanel.displayOrderItems(itemsForRestaurant, restaurantName,restaurantId); // CartPanel 仍然用 restaurantName 顯示
        rootCardLayout.show(rootCardPanel, CART_VIEW);
    }
    @Override
    public void showMainAppLayout() {
        // 1. 將 UserGUI 的 rootCardLayout 切換到顯示 mainAppPanel
        if (rootCardPanel != null && mainAppPanel != null) {
            rootCardLayout.show(rootCardPanel, MAIN_APP_VIEW); // MAIN_APP_VIEW 是 mainAppPanel 在 rootCardLayout 中的標識符
        }
        // 2. (可選) 設置 MainAppPanel 內部的默認視圖，例如餐廳列表
        //    這一步通常在 UserGUI 構造函數的末尾，或者在 MainAppPanel 自己的初始化中完成。
        //    但如果需要確保每次回到 MainAppPanel 時都顯示特定視圖，可以在這裡也調用。
        //    為了保持一致性，可以讓它默認顯示餐廳列表。
        if (mainAppPanel != null) {
            mainAppPanel.showRestaurantList(); // 讓 MainAppPanel 內部默認顯示餐廳列表
        }
    }

    // --- CartPanel.UserCartActions Implementation ---
    @Override
    public void submitOrder(List<OrderItem> itemsToSubmit, String restaurantName, int restaurantId,
                            String orderType, String locationDetails, java.util.Date expectedFinishTime, String orderNote) { // Renamed location to locationDetails
        if (this.currentUserId == -1) {
            JOptionPane.showMessageDialog(this, "用戶信息無效，無法提交訂單。", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (itemsToSubmit == null || itemsToSubmit.isEmpty()) {
             JOptionPane.showMessageDialog(this, "點餐單是空的，無法送出。", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal totalAmountDecimal = itemsToSubmit.stream()
                                     .map(OrderItem::getTotalPrice)
                                     .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalAmountInt = totalAmountDecimal.intValue(); // Assuming 'money' in DB is INT

        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtOrderDetail = null;
        ResultSet generatedKeys = null;
        long orderId = -1;

        String sqlOrder = "INSERT INTO `order` (type, vendor_id, user_id, location, finish_time, status, money, note, deliverman_id) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlOrderDetail = "INSERT INTO orderdetail (order_id, product_id, name, price, quantity, customization) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert into 'order' table
            pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            pstmtOrder.setString(1, orderType);
            pstmtOrder.setInt(2, restaurantId);
            pstmtOrder.setInt(3, this.currentUserId);
            pstmtOrder.setString(4, locationDetails);
            pstmtOrder.setString(6, "待接單"); // Initial status
            pstmtOrder.setInt(7, totalAmountInt);
            pstmtOrder.setTimestamp(5, new Timestamp(expectedFinishTime.getTime())); // order_datetime
            pstmtOrder.setString(8,orderNote); // 'note' - assuming no note from UI yet
            // 'manager_id' - NULL for self-pickup, or needs logic for delivery
            if ("外送".equals(orderType)) {
                // pstmtOrder.setInt(9, someManagerId); // Needs logic to assign manager for delivery
                pstmtOrder.setNull(9, java.sql.Types.INTEGER); // For now, set to NULL
            } else {
                pstmtOrder.setNull(9, java.sql.Types.INTEGER);
            }

            int affectedRows = pstmtOrder.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            generatedKeys = pstmtOrder.getGeneratedKeys();
            if (generatedKeys.next()) {
                orderId = generatedKeys.getLong(1);
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }

            // Insert into 'orderdetail' table for each item
            pstmtOrderDetail = conn.prepareStatement(sqlOrderDetail);
            for (OrderItem item : itemsToSubmit) {
                pstmtOrderDetail.setLong(1, orderId);
                // Assuming OrderItem.getId() returns product_id as String, parse to int
                try {
                    pstmtOrderDetail.setInt(2, Integer.parseInt(item.getId()));
                } catch (NumberFormatException ex) {
                     throw new SQLException("Invalid product_id format: " + item.getId());
                }
                pstmtOrderDetail.setString(3, item.getName());
                pstmtOrderDetail.setInt(4, item.getUnitPrice().intValue()); // Assuming price in OrderItem is per unit and stored as int in DB
                pstmtOrderDetail.setInt(5, item.getQuantity());
                pstmtOrderDetail.setString(6, item.getCustomization());
                pstmtOrderDetail.addBatch();
            }
            pstmtOrderDetail.executeBatch();

            conn.commit(); // Commit transaction

            // --- Display success message (same as before) ---
            StringBuilder orderDetailsMsg = new StringBuilder("訂單已送出！ (訂單編號: " + orderId + ")\n");
            orderDetailsMsg.append("餐廳: ").append(restaurantName).append(" (ID: ").append(restaurantId).append(")\n");
            orderDetailsMsg.append("訂單類型: ").append(orderType).append("\n");
            orderDetailsMsg.append("取餐/配送地點: ").append(locationDetails).append("\n");
            orderDetailsMsg.append("訂單內容:\n");
            for (OrderItem item : itemsToSubmit) {
                orderDetailsMsg.append(String.format("- %dx %s (%s) %s\n",
                        item.getQuantity(), item.getName(), item.getUnitPrice().toString(),
                        item.getCustomization().isEmpty() ? "" : "(客製化: " + item.getCustomization() + ")"));
            }
            orderDetailsMsg.append(String.format("總金額: $%.2f\n", totalAmountDecimal)); // Use BigDecimal for display
            orderDetailsMsg.append("感謝您的訂購！");
            JOptionPane.showMessageDialog(this, orderDetailsMsg.toString(), "訂單確認", JOptionPane.INFORMATION_MESSAGE);

            clearCartForRestaurantById(restaurantId);
            showRestaurantList();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.err.println("Transaction is being rolled back for order of restaurant ID: " + restaurantId);
                    conn.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "訂單提交失敗: " + e.getMessage(), "資料庫錯誤", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmtOrder != null) pstmtOrder.close();
                if (pstmtOrderDetail != null) pstmtOrderDetail.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    @Override
    public void navigateBackToRestaurantList() {
        // 1. 確保 UserGUI 的 rootCardLayout 顯示的是 mainAppPanel
        if (rootCardPanel != null && mainAppPanel != null) {
            rootCardLayout.show(rootCardPanel, MAIN_APP_VIEW);
        }
        // 2. 告訴 MainAppPanel 去顯示餐廳列表視圖
        if (mainAppPanel != null) {
            mainAppPanel.showRestaurantList(); // 調用 MainAppPanel 自己的方法來切換其內部 CardLayout
        }
    }
    @Override
    public void showRestaurantList() {
        // 1. 確保 UserGUI 的 rootCardLayout 顯示的是 mainAppPanel
        if (rootCardPanel != null && mainAppPanel != null) {
            rootCardLayout.show(rootCardPanel, MAIN_APP_VIEW);
        }
        // 2. 告訴 MainAppPanel 去顯示餐廳列表視圖
        if (mainAppPanel != null) {
            mainAppPanel.showRestaurantList(); // 調用 MainAppPanel 自己的方法來切換其內部 CardLayout
        }
    }
    @Override
    public void clearCartForRestaurant(String restaurantName,int restaurantId) {
        // 這個方法可能不再直接被 CartPanel 調用，如果 CartPanel 改為傳遞 ID
        // 或者保留它，並在內部找到對應的 ID
        System.out.println("clearCartForRestaurant by name called for: " + restaurantName + " - needs to map to ID.");
        // 實現通過 name 找到 ID 並調用 clearCartForRestaurantById
    }

    public void clearCartForRestaurantById(int restaurantId) {
        shoppingCartById.remove(restaurantId);
        System.out.println("Cart cleared for restaurant ID: " + restaurantId);
        if (cartPanel.isVisible() && cartPanel.getCurrentRestaurantIdForCart() == restaurantId) { // CartPanel 需要 getter for ID
            cartPanel.displayOrderItems(new ArrayList<>(), this.currentRestaurantName,restaurantId); // 仍然用 name 顯示
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	 setDefaultFont();
            UserGUI userGUI = new UserGUI("user_0001");
            userGUI.setVisible(true);
        });
    }
}
