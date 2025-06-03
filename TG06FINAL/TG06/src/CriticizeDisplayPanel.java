// CriticizeDisplayPanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent; // For potential future use with actions
import java.awt.event.ActionListener; // For potential future use with actions
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp; // For comment_time
import java.util.Enumeration;

public class CriticizeDisplayPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final NavigationController navController;
    // private final Color WHITE = Color.WHITE; // Not used directly, LIGHT_BLUE is used for buttons
    private final Color LIGHT_BLUE = new Color(173, 216, 230); // Added for consistency
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    private JLabel restaurantNameLabel;
    private JTextArea criticizeTextArea;
    private ButtonGroup ratingGroup;
    private String currentRestaurantName;
    private int currentRestaurantId;
    // It's better if UserGUI provides the currentUserId through NavigationController or a dedicated method
    // For now, we might need to assume NavigationController can provide it or it's passed differently.
    // Let's assume NavigationController is UserGUI and UserGUI has a method getCurrentUserId().
    // This is not ideal coupling but works for this example.

    public CriticizeDisplayPanel(NavigationController navController) {
        this.navController = navController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(WHITE_BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBackground(WHITE_BACKGROUND); // Ensure consistency

        JButton backButton = new JButton("返回");
        styleButton(backButton, LIGHT_BLUE); // Apply styling
        backButton.addActionListener(e -> {
            if (currentRestaurantId > 0 && currentRestaurantName != null) {
                 // Navigate back to the specific menu of the current restaurant
                navController.navigateBackToMenu(currentRestaurantName, currentRestaurantId);
            } else {
                navController.navigateBackToRestaurantList(); // Fallback
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);

        restaurantNameLabel = new JLabel("評價餐廳: ", SwingConstants.CENTER);
        restaurantNameLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18)); // Consistent font
        topPanel.add(restaurantNameLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel contentFormPanel = new JPanel();
        contentFormPanel.setLayout(new BoxLayout(contentFormPanel, BoxLayout.Y_AXIS));
        contentFormPanel.setBackground(WHITE_BACKGROUND);
        contentFormPanel.setBorder(BorderFactory.createEmptyBorder(10,5,5,5));


        JLabel reviewLabel = new JLabel("請輸入您的評價:");
        reviewLabel.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        reviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentFormPanel.add(reviewLabel);
        contentFormPanel.add(Box.createRigidArea(new Dimension(0, 5)));


        criticizeTextArea = new JTextArea(8, 30); // Adjusted rows
        criticizeTextArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        criticizeTextArea.setLineWrap(true);
        criticizeTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(criticizeTextArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMinimumSize(new Dimension(100, 100)); // Ensure it has some min size
        contentFormPanel.add(scrollPane);

        contentFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        JLabel ratingLabel = new JLabel("評分 (請點選星級):");
        ratingLabel.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentFormPanel.add(ratingLabel);
        contentFormPanel.add(Box.createRigidArea(new Dimension(0, 5)));


        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Reduced gaps
        radioPanel.setBackground(WHITE_BACKGROUND);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ratingGroup = new ButtonGroup();
        for (int i = 1; i <= 5; i++) {
            JRadioButton ratingButton = new JRadioButton(i + " 星");
            ratingButton.setActionCommand(String.valueOf(i)); // Store numeric value
            ratingButton.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
            ratingButton.setBackground(WHITE_BACKGROUND);
            ratingGroup.add(ratingButton);
            radioPanel.add(ratingButton);
        }
        contentFormPanel.add(radioPanel);
        contentFormPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton submitButton = new JButton("提交評價");
        styleButton(submitButton, new Color(144, 238, 144)); // Green color for submit
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT); // Align button to left
        submitButton.addActionListener(e -> submitCriticism());
        contentFormPanel.add(submitButton);

        add(contentFormPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 35)); // Standardized button size
    }

    public void loadCriticizePage(String restaurantName, int restaurantId) {
        this.currentRestaurantName = restaurantName;
        this.currentRestaurantId = restaurantId;
        restaurantNameLabel.setText("評價餐廳: " + (restaurantName != null ? restaurantName : "未知"));
        if (criticizeTextArea != null) {
             criticizeTextArea.setText(""); // 清空上次的評價內容
        }
        if (ratingGroup != null) {
            ratingGroup.clearSelection(); // 清除上次的評分選擇
        }
    }

    private void submitCriticism() {
        String reviewText = criticizeTextArea.getText().trim();
        int selectedRatingValue = 0; // Store numeric rating
        String selectedRatingDisplay = "未評分"; // For display in JOptionPane

        ButtonModel selectedModel = ratingGroup.getSelection();
        if (selectedModel != null) {
            try {
                selectedRatingValue = Integer.parseInt(selectedModel.getActionCommand());
                // For display, find the button with this action command to get its text ("X 星")
                Enumeration<AbstractButton> buttons = ratingGroup.getElements();
                while (buttons.hasMoreElements()) {
                    AbstractButton button = buttons.nextElement();
                    if (button.getActionCommand().equals(String.valueOf(selectedRatingValue))) {
                        selectedRatingDisplay = button.getText();
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parsing rating value: " + selectedModel.getActionCommand());
                // This shouldn't happen if ActionCommand is set correctly
            }
        }

        if (selectedRatingValue == 0) { // 或者 selectedModel == null
            JOptionPane.showMessageDialog(this, "請選擇一個評分！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (reviewText.isEmpty() && JOptionPane.showConfirmDialog(this,
                "您沒有輸入評價內容，確定要提交嗎？", "確認",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
            return; // 用户选择不提交空评价
        }


        // --- 資料庫操作 ---
        int currentUserId = -1;
        // A more robust way to get userId is needed.
        // Assuming NavigationController is UserGUI and has a method to get current user ID.
        if (navController instanceof UserGUI) { // This is a cast, might not be ideal
            currentUserId = ((UserGUI) navController).getCurrentUserId(); // UserGUI needs this method
        }

        if (currentUserId == -1 && !"guest".equalsIgnoreCase(((UserGUI) navController).getCurrentUserAccount())) { // Allow guest reviews if account is "guest"
            JOptionPane.showMessageDialog(this, "無法獲取用戶信息，評價提交失敗。", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentRestaurantId <= 0) {
             JOptionPane.showMessageDialog(this, "無法確定評價的餐廳，評價提交失敗。", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }


        String sql = "INSERT INTO comment (vendor_id, user_id, comment_time, rating, review) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.currentRestaurantId);
            pstmt.setInt(2, currentUserId); // Make sure this is the actual logged-in user's ID
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Current time
            pstmt.setInt(4, selectedRatingValue); // Numeric rating
            if (reviewText.isEmpty()) {
                pstmt.setNull(5, java.sql.Types.NULL);
            } else {
                pstmt.setString(5, reviewText);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this,
                        "感謝您的評價!\n餐廳: " + currentRestaurantName + "\n評價: " + (reviewText.isEmpty() ? "(無內容)" : reviewText) + "\n評分: " + selectedRatingDisplay,
                        "評價已提交",
                        JOptionPane.INFORMATION_MESSAGE);
                // 提交成功後返回餐廳列表或菜單頁面
                if (currentRestaurantId > 0 && currentRestaurantName != null) {
                    navController.navigateBackToMenu(currentRestaurantName, currentRestaurantId);
                } else {
                    navController.navigateBackToRestaurantList();
                }
            } else {
                JOptionPane.showMessageDialog(this, "評價提交失敗，請稍後再試。", "錯誤", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "資料庫錯誤，評價提交失敗: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }
}