import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.border.EmptyBorder;

public class viewResponse extends JFrame {
    private static final int FRAME_WIDTH = 700, FRAME_HEIGHT = 500;
    private ComplainDB cb;
    private String account;
    private JLabel title;
    private JButton orderQueryButton, basicInfoButton, logoutButton, responseButton;
    private JTable orderTable;
    private JTableHeader tableHeader;
    private JScrollPane scrollPane;
    private JButton goToOrderSystemButton;
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

    public viewResponse(String account) {
    	cb = new ComplainDB();
    	this.account = account;
        setTitle("政大校園訂餐系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        createButton();
        createTable();
        createLayout();
        setBackground(WHITE_BACKGROUND);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void createButton () {
        orderQueryButton = new JButton("查詢歷史訂單");
        orderQueryButton.setBackground(WHITE);
        orderQueryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	invoiceRough i = new invoiceRough(account);
				i.setVisible(true);
				dispose();
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
        		/* 什麼都不會發生 */
        	}
        });

        logoutButton = new JButton("登出");
        logoutButton.setBackground(WHITE);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	viewResponse.this.dispose();
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
                dispose(); // 隱藏當前窗口
                UserGUI orderSystem = new UserGUI(account);
                orderSystem.setVisible(true);
                orderSystem.setLocationRelativeTo(null); // 確保 UserGUI 也居中
            }
        });
    }

    public void createTable() {
        String[] columnNames = {"回饋編號", "訂單編號", "傳送內容", "回覆內容"};
        Object[][] data = new Object[1][4];

        try {
            ArrayList<DetailComplain> ro = cb.searchComplain(account);
            data = new Object[ro.size()][4];
            for (int i = 0; i < ro.size(); i++) {
                DetailComplain r = ro.get(i);
                data[i][0] = r.getComplainId();
                data[i][1] = r.getOrderId();
                data[i][2] = r.getComplainContent();
                data[i][3] = r.getReplyContent();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel nonEditableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(nonEditableModel) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return new TextAreaRenderer();
            }
        };

        // 計算並調整每一列的高度（根據內容）
        for (int row = 0; row < orderTable.getRowCount(); row++) {
            int maxHeight = 0;
            for (int column = 0; column < orderTable.getColumnCount(); column++) {
                TableCellRenderer renderer = orderTable.getCellRenderer(row, column);
                Component comp = orderTable.prepareRenderer(renderer, row, column);
                maxHeight = Math.max(comp.getPreferredSize().height, maxHeight);
            }
            orderTable.setRowHeight(row, maxHeight);
        }

        orderTable.setFillsViewportHeight(true);
        tableHeader = orderTable.getTableHeader();
        tableHeader.setBackground(new Color(200, 230, 200));
        orderTable.setPreferredScrollableViewportSize(new Dimension(600, 300));
        scrollPane = new JScrollPane(orderTable);
    }
    
    private void adjustRowHeights(JTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int maxHeight = 0;
            for (int column = 0; column < table.getColumnCount(); column++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(cellRenderer, row, column);
                comp.setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);
                int height = comp.getPreferredSize().height;

                maxHeight = Math.max(height, maxHeight);
            }
            if (table.getRowHeight(row) != maxHeight) {
                table.setRowHeight(row, maxHeight);
            }
        }
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
		SwingUtilities.invokeLater(() -> new viewResponse("user_0001"));
	}

}

class TextAreaRenderer extends JTextArea implements TableCellRenderer {
    public TextAreaRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText(value == null ? "" : value.toString());
        setFont(table.getFont());
        setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE); // ★ 這很重要

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        return this;
    }
}

