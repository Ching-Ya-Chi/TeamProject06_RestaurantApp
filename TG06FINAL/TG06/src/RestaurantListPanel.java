// RestaurantListPanel.java (修改)
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantListPanel extends JPanel {
    private final NavigationController navController;
    private final Color LIGHT_BLUE = new Color(173, 216, 230);
    private final Color WHITE_BACKGROUND = Color.WHITE;


    private JPanel listContentPanel; // 用於顯示餐廳行
    private JTextField searchField;  // 新增：搜索框
    private JButton searchButton;   // 新增：搜索按鈕

    public RestaurantListPanel(NavigationController navController) {
        this.navController = navController;
        setLayout(new BorderLayout());
        setBackground(WHITE_BACKGROUND);

        // --- 搜索區域 ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(WHITE_BACKGROUND);
        searchField = new JTextField(25); // 調整寬度
        searchField.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        searchButton = new JButton("搜索餐廳");
        searchButton.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        searchButton.setBackground(LIGHT_BLUE);
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);

        searchPanel.add(new JLabel("輸入餐廳名稱或類型: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // --- 列表區域 ---
        listContentPanel = new JPanel();
        listContentPanel.setLayout(new BoxLayout(listContentPanel, BoxLayout.Y_AXIS));
        listContentPanel.setBackground(WHITE_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(listContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE_BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);

        // 搜索按鈕事件
        searchButton.addActionListener(e -> loadRestaurants(searchField.getText().trim()));
        searchField.addActionListener(e -> loadRestaurants(searchField.getText().trim())); // 回車也觸發搜索

        // 初始加載所有餐廳 (或最近的，根據你的業務邏輯)
        loadRestaurants(null); // 傳遞 null 或空字符串表示加載所有
    }

    public void loadRestaurants(String searchTerm) {
        listContentPanel.removeAll(); // 清除舊列表
        List<RestaurantData> restaurants = fetchRestaurantsFromDB(searchTerm);

        if (restaurants.isEmpty()) {
            JLabel noResultsLabel = new JLabel(searchTerm != null && !searchTerm.isEmpty() ? "未找到符合條件的餐廳。" : "目前沒有餐廳資訊。", SwingConstants.CENTER);
            noResultsLabel.setFont(new Font("Microsoft JhengHei", Font.ITALIC, 16));
            // 為了讓標籤居中，當列表為空時可以臨時改變 listContentPanel 的佈局
            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setBackground(WHITE_BACKGROUND);
            centerPanel.add(noResultsLabel);
            listContentPanel.add(centerPanel);
        } else {
            for (RestaurantData restaurant : restaurants) {
                listContentPanel.add(createRestaurantRow(restaurant));
                JPanel separator = new JPanel();
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                separator.setBackground(Color.LIGHT_GRAY);
                listContentPanel.add(separator);
            }
        }
        listContentPanel.revalidate();
        listContentPanel.repaint();
    }


    private List<RestaurantData> fetchRestaurantsFromDB(String searchTerm) {
        List<RestaurantData> restaurants = new ArrayList<>();
        // 根據 metadata.xlsx, vendor 表有 vendor_id, vendor_name, type, address, map_url, status
        // 我們只獲取 '可使用' 的商家
        String sql = "SELECT vendor_id, vendor_name, type, address, map_url FROM vendor WHERE status = '可使用'";

        if (searchTerm != null && !searchTerm.isEmpty()) {
            // 允許搜索商家名稱或商家類型
            sql += " AND (vendor_name LIKE ? OR type LIKE ?)";
        }
        sql += " ORDER BY vendor_name"; // 按名稱排序

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (searchTerm != null && !searchTerm.isEmpty()) {
                pstmt.setString(1, "%" + searchTerm + "%");
                pstmt.setString(2, "%" + searchTerm + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                restaurants.add(new RestaurantData(
                        rs.getInt("vendor_id"),
                        rs.getString("vendor_name"),
                        rs.getString("type"),
                        rs.getString("address"),
                        rs.getString("map_url")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching restaurants: " + e.getMessage());
            e.printStackTrace();
            // 可以在 UI 上顯示錯誤信息
            JOptionPane.showMessageDialog(this, "無法加載餐廳列表: " + e.getMessage(), "資料庫錯誤", JOptionPane.ERROR_MESSAGE);
        }
        return restaurants;
    }

    private JPanel createRestaurantRow(RestaurantData restaurant) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        rowPanel.setBackground(WHITE_BACKGROUND);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // 調整高度
        rowPanel.setMinimumSize(new Dimension(0, 80));

        // 左側：餐廳名稱和類型
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(restaurant.getName());
        nameLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18)); // 增大字號
        infoPanel.add(nameLabel);

        JLabel typeLabel = new JLabel("類型: " + (restaurant.getType() != null ? restaurant.getType() : "未指定"));
        typeLabel.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 13));
        infoPanel.add(typeLabel);
        
        JLabel addressLabel = new JLabel("地址: " + (restaurant.getAddress() != null ? restaurant.getAddress() : "未提供"));
        addressLabel.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 13));
        infoPanel.add(addressLabel);


        rowPanel.add(infoPanel, BorderLayout.CENTER); // 將信息面板放在中間，以便按鈕在右側

        // 右側：按鈕
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonsPanel.setOpaque(false);
        Dimension buttonSize = new Dimension(90, 35); // 調整按鈕大小

        JButton menuButton = new JButton("菜單");
        menuButton.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        menuButton.setBackground(LIGHT_BLUE);
        menuButton.setOpaque(true);
        menuButton.setBorderPainted(false);
        menuButton.setPreferredSize(buttonSize);
        // 將 restaurant ID 傳遞給菜單頁面
        menuButton.addActionListener(e -> navController.showMenuPage(restaurant.getName(), restaurant.getId()));

        JButton criticizeButton = new JButton("評價");
        criticizeButton.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        criticizeButton.setBackground(LIGHT_BLUE);
        criticizeButton.setOpaque(true);
        criticizeButton.setBorderPainted(false);
        criticizeButton.setPreferredSize(buttonSize);
        // 將 restaurant ID 傳遞給評價頁面
        criticizeButton.addActionListener(e -> navController.showCriticizePage(restaurant.getName(), restaurant.getId()));

        buttonsPanel.add(menuButton);
        buttonsPanel.add(criticizeButton);

        rowPanel.add(buttonsPanel, BorderLayout.EAST);
        return rowPanel;
    }

    // 當此面板變為可見時，可以刷新列表（例如從其他頁面返回時）
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag && isDisplayable()) { // 確保組件已添加到容器層次結構中
             // 重新加載時可以保留當前的搜索詞，或者清空
            // loadRestaurants(searchField.getText().trim());
            // 或者總是加載所有
            loadRestaurants(null);
        }
    }
}
