import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.border.EmptyBorder;

public class invoiceRough extends JFrame {
    private static final int FRAME_WIDTH = 700, FRAME_HEIGHT = 500;
    private String account;
    private OrderDB ob;
    private JLabel title;
    private JButton orderQueryButton, basicInfoButton, logoutButton, responseButton;
    private JButton goToOrderSystemButton; // <--- 新增按鈕
    private JTable orderTable;
    private JTableHeader tableHeader;
    private JScrollPane scrollPane;
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);
    
    public invoiceRough(String account) {
    	this.account = account;
    	ob = new OrderDB();
        setTitle("政大校園訂餐系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setBackground(WHITE_BACKGROUND);
        createButton();
        createTable();
        createLayout();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void createButton () {
        orderQueryButton = new JButton("查詢歷史訂單");
        orderQueryButton.setBackground(WHITE);
        orderQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	/* 什麼都不會發生 */
            	
            }
        });

        basicInfoButton = new JButton("更改基本資料");
        basicInfoButton.setBackground(WHITE);
        basicInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	/* 跳轉到personalInfo */
            	personalInfo perInfo = new personalInfo(account);
            	perInfo.setVisible(true);
            	dispose();
            }
        });
        
        responseButton = new JButton("查看管理員回覆");
        responseButton.setBackground(WHITE);
        responseButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		/* 跳轉到viewResponse */
        		viewResponse viewR = new viewResponse(account);
        		viewR.setVisible(true);
        		dispose();
        	}
        });

        logoutButton = new JButton("登出");
        logoutButton.setBackground(WHITE);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	invoiceRough.this.dispose(); // 關掉
            	LoginRegister l = new LoginRegister();
            	l.setVisible(true);
            }
        });
        goToOrderSystemButton = new JButton("前往訂餐");
        goToOrderSystemButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14)); // 突出一些
        goToOrderSystemButton.setBackground(new Color(100, 180, 100)); // 給個不同顏色
        goToOrderSystemButton.setForeground(Color.WHITE);
        goToOrderSystemButton.setOpaque(true);
        goToOrderSystemButton.setBorderPainted(false);
        goToOrderSystemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                invoiceRough.this.dispose(); // 隱藏當前窗口
                UserGUI orderSystem = new UserGUI(account);
                orderSystem.setVisible(true);
                orderSystem.setLocationRelativeTo(null); // 確保 UserGUI 也居中
            }
        });
    }

    public void createTable() {
    	String[] columnNames = {"訂單編號", "取餐時間", "取餐方式", "訂單狀態"};
    	ArrayList<RoughOrder> ro = ob.searchOrder(account);
    	Object[][] data = new Object[ro.size()][4];
    	for (int i = 0; i < ro.size(); i++) {
    	    RoughOrder r = ro.get(i);
    	    data[i][0] = r.getOrderId();      
    	    data[i][1] = r.getFinishTime();   
    	    data[i][2] = r.getType(); 
    	    data[i][3] = r.getStatus();
    	}

        DefaultTableModel nonEditableModel = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(nonEditableModel);
        orderTable.setRowHeight(20);
        orderTable.setFillsViewportHeight(true);
        tableHeader = orderTable.getTableHeader();
        tableHeader.setBackground(new Color(200, 230, 200));
        orderTable.setPreferredScrollableViewportSize(new Dimension(600, 300));
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    Object orderId = orderTable.getValueAt(selectedRow, 0);
                    /* 跳轉到invoiceDetail , orderId要一起傳過去 */
                    
                    
                    // JOptionPane.showMessageDialog(null, "你點了！", "點了", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
      //----------------------
        // 雙擊顯示詳細資訊
           orderTable.addMouseListener(new MouseAdapter() {
               public void mouseClicked(MouseEvent e) {
                   if (e.getClickCount() == 2) {
                       int row = orderTable.getSelectedRow();
                       if (row != -1) {
                           try {
                               int orderId = Integer.parseInt(orderTable.getValueAt(row, 0).toString());
                               dispose();
                               new invoiceDetail(orderId,account).setVisible(true);
                           } catch (NumberFormatException ex) {
                               JOptionPane.showMessageDialog(null, "訂單編號格式錯誤！");
                           }
                       }
                   }
               }
           });
        //----------------------
        scrollPane = new JScrollPane(orderTable);
    }

    public void createLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 頂部按鈕區域
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel toppestPanel = new JPanel(new BorderLayout(20, 20));
        JPanel totalPanel = new JPanel(new BorderLayout(20, 20));

        topButtonPanel.add(orderQueryButton);
        topButtonPanel.add(basicInfoButton);
        topButtonPanel.add(responseButton);
        topButtonPanel.add(goToOrderSystemButton);

        title = new JLabel("政大校園訂餐系統 - 使用者");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        toppestPanel.add(title, BorderLayout.WEST);
        toppestPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(topButtonPanel, BorderLayout.NORTH);
        JPanel tablePanel = new JPanel(new BorderLayout());
        JPanel scrollWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        scrollWrapper.add(scrollPane);
        tablePanel.add(scrollWrapper, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        totalPanel.add(toppestPanel, BorderLayout.NORTH);
        totalPanel.add(mainPanel, BorderLayout.CENTER);
        totalPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        add(totalPanel);
    }
    
    public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new invoiceRough("user_0001"));
	}
}