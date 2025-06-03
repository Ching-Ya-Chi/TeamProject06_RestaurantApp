import javax.swing.*;
import java.awt.*;

public class AdminHomePage extends JFrame {
    private JTabbedPane tabbedPane;
    private JButton loginOut;
    private JButton renew;
    private JPanel northPanel;
    private int adminId;

    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238, 238, 238);

    public AdminHomePage(int adminId) {
        this.adminId = adminId;
        setTitle("政大校園訂餐系統 - 管理者");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(WHITE_BACKGROUND);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245)); // 背景色

        // 標題區
        northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20)); // 上左下右的邊距
        JLabel titleLabel = new JLabel("     政大校園訂餐系統 - 管理者");
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 20));

        // 按鈕區域（重整與登出）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // 右對齊 + 間距
        buttonPanel.setOpaque(false); // 背景透明

        loginOut = new JButton("登出");
        renew = new JButton("重整");

        Dimension btnSize = new Dimension(80, 30);
        loginOut.setPreferredSize(btnSize);
        renew.setPreferredSize(btnSize);

        loginOut.setBackground(WHITE);
        renew.setBackground(WHITE);

        loginOut.addActionListener(e -> {
            dispose();
            new LoginRegister().setVisible(true);
        });

        renew.addActionListener(e -> {
            dispose();
            new AdminHomePage(adminId).setVisible(true);
        });

        buttonPanel.add(renew);
        buttonPanel.add(loginOut);

        northPanel.add(titleLabel, BorderLayout.WEST);
        northPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(northPanel, BorderLayout.NORTH);

        // TabbedPane 設定
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微軟正黑體", Font.PLAIN, 16));

        // 加入子頁籤
        tabbedPane.addTab("待審核清單", new ApprovalPanel());
        tabbedPane.addTab("用戶清單", new InfoList());
        tabbedPane.addTab("今日外送訂單", new DeliveryInfo());
        tabbedPane.addTab("歷史訂單資訊", new HistoryOrder());
        tabbedPane.addTab("意見回饋資訊", new Feedback());
        tabbedPane.addTab("                      ", new Feedback()); // 還沒想到放甚麼

        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 留白邊界
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminHomePage(1).setVisible(true);
        });
    }
}


