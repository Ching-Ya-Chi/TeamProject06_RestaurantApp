// MainAppPanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainAppPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final NavigationController navController; // For UserGUI level navigation if needed
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    private CardLayout contentCardLayout;
    private JPanel contentDisplayPanel;
    private MapDisplayPanel mapDisplayPanel;
    private RestaurantListPanel restaurantListPanel;

    private JButton mapNavButton;
    private JButton restaurantListNavButton;
    private JButton userProfileNavButton;

    public MainAppPanel(NavigationController navController) {
        this.navController = navController; // navController for UserGUI
        setLayout(new BorderLayout(0, 5));
        setBackground(WHITE_BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        // --- Top Navigation Panel (Map/List) ---
        JPanel topNavigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topNavigationPanel.setBackground(WHITE_BACKGROUND);

        mapNavButton = new JButton("地圖");
        restaurantListNavButton = new JButton("餐廳列表");
        userProfileNavButton = new JButton("用戶中心"); 
        Dimension navButtonSize = new Dimension(120, 40);

        styleNavButton(mapNavButton, navButtonSize);
        styleNavButton(restaurantListNavButton, navButtonSize);
        styleNavButton(userProfileNavButton, navButtonSize); 
        
        mapNavButton.addActionListener(e -> {
            navController.showMap(); // This will be handled by UserGUI to call this panel's method
            updateNavButtonSelection(mapNavButton);
        });
        restaurantListNavButton.addActionListener(e -> {
            navController.showRestaurantList(); // ditto
            updateNavButtonSelection(restaurantListNavButton);
        });
        userProfileNavButton.addActionListener(new ActionListener(){ // <--- 為用戶中心按鈕添加事件
            @Override
            public void actionPerformed(ActionEvent e) {
                navController.showUserProfilePage(); // 調用 NavigationController 的新方法
                updateNavButtonSelection(userProfileNavButton); // 選中用戶中心按鈕
            }
        });
        topNavigationPanel.add(mapNavButton);
        topNavigationPanel.add(restaurantListNavButton);
        topNavigationPanel.add(userProfileNavButton); // <--- 添加按鈕到面板
        add(topNavigationPanel, BorderLayout.NORTH);

        // --- Content Display Panel (for map/list) ---
        contentCardLayout = new CardLayout();
        contentDisplayPanel = new JPanel(contentCardLayout);
        contentDisplayPanel.setBackground(WHITE_BACKGROUND);
        add(contentDisplayPanel, BorderLayout.CENTER);

        // Create and add map and restaurant list panels
        mapDisplayPanel = new MapDisplayPanel(); // Doesn't need navController for internal ops
        restaurantListPanel = new RestaurantListPanel(navController); // Needs navController for restaurant actions

        contentDisplayPanel.add(mapDisplayPanel, "MAP_VIEW");
        contentDisplayPanel.add(restaurantListPanel, "RESTAURANT_LIST_VIEW");

        // Default view
        showRestaurantList();
    }

    private void styleNavButton(JButton button, Dimension size) {
        button.setPreferredSize(size);
        button.setBackground(WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    public void updateNavButtonSelection(JButton selectedButton) {
        mapNavButton.setBackground(WHITE);
        restaurantListNavButton.setBackground(WHITE);
        if (selectedButton != null) {
            selectedButton.setBackground(WHITE.darker());
        }
    }

    public void showMap() {
        contentCardLayout.show(contentDisplayPanel, "MAP_VIEW");
        updateNavButtonSelection(mapNavButton);
    }

    public void showRestaurantList() {
        contentCardLayout.show(contentDisplayPanel, "RESTAURANT_LIST_VIEW");
        updateNavButtonSelection(restaurantListNavButton);
    }
}
