// MenuDisplayPanel.java
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuDisplayPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private final NavigationController navController;
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);
    private final int IMAGE_WIDTH = 100; // Desired image width
    private final int IMAGE_HEIGHT = 100; // Desired image height

    private JLabel titleLabel;
    private JPanel menuItemsListPanel;
    private String currentRestaurantName;
    private int currentRestaurantId; // 新增：存儲當前餐廳的 ID

    public MenuDisplayPanel(NavigationController navController) {
        this.navController = navController;
        setLayout(new BorderLayout());
        setBackground(WHITE_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        JPanel topControls = new JPanel(new BorderLayout());
        topControls.setOpaque(false);

        JButton backButton = new JButton("返回餐廳列表");
        backButton.setBackground(WHITE);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> navController.navigateBackToRestaurantList());
        topControls.add(backButton, BorderLayout.WEST);
        JButton viewCartButton = new JButton("查看點餐單");
        viewCartButton.setBackground(new Color(255, 165, 0)); // Orange color for cart
        viewCartButton.setOpaque(true);
        viewCartButton.setBorderPainted(false);
        viewCartButton.addActionListener(e -> {
            if (currentRestaurantName != null) {
                navController.showCartPage(currentRestaurantName,currentRestaurantId);
            }
        });
        // Add cart button to top controls, e.g., on the right
        // You might need a JPanel with FlowLayout.RIGHT for the east part of topControls
        JPanel eastButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        eastButtonsPanel.setOpaque(false);
        eastButtonsPanel.add(viewCartButton);
        topControls.add(eastButtonsPanel, BorderLayout.EAST);


        add(topControls, BorderLayout.NORTH);
        titleLabel = new JLabel("菜單", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        topControls.add(titleLabel, BorderLayout.CENTER);
        add(topControls, BorderLayout.NORTH);

        menuItemsListPanel = new JPanel();
        menuItemsListPanel.setLayout(new BoxLayout(menuItemsListPanel, BoxLayout.Y_AXIS));
        menuItemsListPanel.setBackground(WHITE_BACKGROUND);

        JScrollPane menuScrollPane = new JScrollPane(menuItemsListPanel);
        menuScrollPane.setBorder(BorderFactory.createEmptyBorder());
        menuScrollPane.getViewport().setBackground(WHITE_BACKGROUND);
        menuScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(menuScrollPane, BorderLayout.CENTER);
    }

    public void loadMenuForRestaurant(String restaurantName, int restaurantId) {
        this.currentRestaurantName = restaurantName;
        this.currentRestaurantId = restaurantId; // 存儲 ID
        titleLabel.setText("菜單 - " + restaurantName);
        refreshMenuItemsDisplay(restaurantId); // 使用 ID 獲取菜單
    }

    private void refreshMenuItemsDisplay(int vendorId) {
        menuItemsListPanel.removeAll();
        List<MenuItemData> items = getMenuItemsForRestaurantDB(vendorId); // 從DB獲取

        if (items.isEmpty()) {
            // ... (no menu label code) ...
        } else {
            for (MenuItemData item : items) {
                if ("供應中".equals(item.getStatus())) { // 只顯示供應中的商品
                    menuItemsListPanel.add(createMenuItemPanel(item));
                    menuItemsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }
        menuItemsListPanel.revalidate();
        menuItemsListPanel.repaint();
    }


    private JPanel createMenuItemPanel(MenuItemData item) {
        JPanel itemPanel = new JPanel(new BorderLayout(15, 5)); // Increased hgap for image
        itemPanel.setBackground(new Color(0xF0F0F0));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // --- Image Panel (WEST) ---
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Optional: border around image
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE); // Background for the image area

        if (item.getPictureUrl() != null && !item.getPictureUrl().trim().isEmpty()) {
            try {
                URL imageUrl = new URL(item.getPictureUrl());
                // ImageIcon handles loading in a somewhat non-blocking way for simple cases
                ImageIcon originalIcon = new ImageIcon(imageUrl);

                if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE && originalIcon.getIconWidth() > 0) {
                    Image image = originalIcon.getImage();
                    Image scaledImage = image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                    imageLabel.setText(""); // Clear any text if image loaded
                } else {
                    // Image failed to load properly from URL (e.g., 404, but ImageIcon might not throw)
                    System.err.println("Image not loaded or invalid for URL: " + item.getPictureUrl());
                    setPlaceholderImageOrText(imageLabel, "圖片加載失敗");
                }
            } catch (MalformedURLException e) {
                System.err.println("Malformed image URL: " + item.getPictureUrl() + " - " + e.getMessage());
                setPlaceholderImageOrText(imageLabel, "無效圖片路徑");
            } catch (Exception e) { // Catch other potential exceptions during image loading/scaling
                System.err.println("Error processing image: " + item.getPictureUrl() + " - " + e.getMessage());
                setPlaceholderImageOrText(imageLabel, "圖片錯誤");
            }
        } else {
            setPlaceholderImageOrText(imageLabel, "無圖片");
        }
        itemPanel.add(imageLabel, BorderLayout.WEST);

        // --- Text Info Panel (CENTER) ---
        JPanel textInfoPanel = new JPanel(new BorderLayout(5, 5));
        textInfoPanel.setOpaque(false); // Transparent to inherit itemPanel's background

        // Top Section: Name and Price
        JPanel topTextSection = new JPanel(new BorderLayout(10, 0));
        topTextSection.setOpaque(false);

        JLabel nameLabelText = new JLabel(item.getName());
        nameLabelText.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        topTextSection.add(nameLabelText, BorderLayout.CENTER);

        JLabel priceLabelText = new JLabel("價格: "+item.getPrice()); // Assuming MenuItemData has this
        priceLabelText.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        topTextSection.add(priceLabelText, BorderLayout.EAST);
        textInfoPanel.add(topTextSection, BorderLayout.NORTH);

        // Description (if available)
        if (item.getDescription() != null && !item.getDescription().trim().isEmpty()) {
            JTextArea descriptionArea = new JTextArea(item.getDescription());
            descriptionArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 13)); // Slightly smaller for description
            descriptionArea.setWrapStyleWord(true);
            descriptionArea.setLineWrap(true);
            descriptionArea.setOpaque(false);
            descriptionArea.setEditable(false);
            descriptionArea.setFocusable(false);
            // To limit the height of description area if it's too long
            descriptionArea.setRows(3); // Example: max 3 visible rows before scrolling (if in JScrollPane)
                                        // Or just let it expand and itemPanel height will adjust.
            JScrollPane descScrollPane = new JScrollPane(descriptionArea);
            descScrollPane.setOpaque(false);
            descScrollPane.getViewport().setOpaque(false);
            descScrollPane.setBorder(null); // No border for description scroll pane
            descScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            textInfoPanel.add(descScrollPane, BorderLayout.CENTER);
        } else {
            // Add a placeholder if no description to maintain some structure, or leave it blank
             textInfoPanel.add(Box.createRigidArea(new Dimension(0,20)), BorderLayout.CENTER);
        }

        itemPanel.add(textInfoPanel, BorderLayout.CENTER);

        // Mouse listener for the entire item panel
        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navController.showOrderPage(item, currentRestaurantName, currentRestaurantId);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { itemPanel.setBackground(new Color(0xDDEEFF)); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { itemPanel.setBackground(new Color(0xF0F0F0)); }
        });
        return itemPanel;
    }

    private void setPlaceholderImageOrText(JLabel label, String text) {
        // You could also load a default placeholder image here
        // For example: label.setIcon(new ImageIcon(getClass().getResource("/path/to/placeholder.png")));
        label.setText(text);
        label.setIcon(null); // Ensure no old icon is shown
        label.setFont(new Font("Microsoft JhengHei", Font.ITALIC, 12));
        label.setForeground(Color.DARK_GRAY);
    }


    private List<MenuItemData> getMenuItemsForRestaurantDB(int vendorId) {
        List<MenuItemData> menu = new ArrayList<>();
        // 根據 metadata.xlsx, product 表有 product_id, vendor_id, name, price, description, picture_url, status
        String sql = "SELECT product_id, vendor_id, name, price, description, picture_url, status " +
                     "FROM product WHERE vendor_id = ? AND status = '供應中'"; // 只查詢特定商家且供應中的商品

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vendorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                menu.add(new MenuItemData(
                        rs.getInt("product_id"), // product_id 是 INT，但 MenuItemData 之前是 String
                        rs.getInt("vendor_id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getString("description"),
                        rs.getString("picture_url"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching menu items for vendor " + vendorId + ": " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "無法加載菜單: " + e.getMessage(), "資料庫錯誤", JOptionPane.ERROR_MESSAGE);
        }
        return menu;
    }
 // 當此面板變為可見時，如果 currentRestaurantId 有效，則刷新
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag && isDisplayable() && currentRestaurantId > 0) {
            refreshMenuItemsDisplay(currentRestaurantId);
        }
    }
}
