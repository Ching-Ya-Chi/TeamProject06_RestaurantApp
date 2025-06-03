import javax.swing.*;
import java.awt.*;

public class ApprovalPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public ApprovalPanel() {
        setLayout(new BorderLayout());

        // 上方切換按鈕列
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        JButton storeButton = new JButton("店家審核");
        JButton userButton = new JButton("使用者審核");
        storeButton.setPreferredSize(new Dimension(120, 20));
        userButton.setPreferredSize(new Dimension(120, 20));
        storeButton.setBackground(new Color(245, 245, 245));
        userButton.setBackground(new Color(245, 245, 245));

        buttonPanel.add(storeButton);
        buttonPanel.add(userButton);
        add(buttonPanel, BorderLayout.NORTH);

        // 中央卡片容器
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(new StoreApproval(), "store");
        cardPanel.add(new UserApproval(), "user");

        add(cardPanel, BorderLayout.CENTER);

        // 切換邏輯
        storeButton.addActionListener(e -> cardLayout.show(cardPanel, "store"));
        userButton.addActionListener(e -> cardLayout.show(cardPanel, "user"));
    }
}


