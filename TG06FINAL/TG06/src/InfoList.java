

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InfoList extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public InfoList() {
        setLayout(new BorderLayout());

        // 上方按鈕列
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        JButton storeButton = new JButton("店家清單");
        JButton userButton = new JButton("使用者清單");
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

        cardPanel.add(new StoreList(), "store");
        cardPanel.add(new UserList(), "user");

        add(cardPanel, BorderLayout.CENTER);

        // 按鈕事件：切換卡片
        storeButton.addActionListener(e -> cardLayout.show(cardPanel, "store"));
        userButton.addActionListener(e -> cardLayout.show(cardPanel, "user"));
    }
}


