import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VendorRegister extends JFrame{

    private JLabel titleLabel, lblBusinessHours;
    private JButton logoutButton, btnUpdate, btnUpdateHours;
    private JComboBox<String> weekdayCombo, typeCombo;
    private JTextField txtOpenTime, txtCloseTime;
    private JTextField txtShopName, txtShopType, txtManager, txtAddress, txtShopPhone, txtManagerPhone, txtShopAddress;
    private JTextArea txtShopDescription;
    private String account;
    private VendorDB vb;

    
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);
    
    public VendorRegister(String account) {
    	this.account = account;
    	vb = new VendorDB();
        setTitle("政大校園訂餐系統");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initializeComponents();
        layoutComponents();
        setLocationRelativeTo(null);
        setBackground(WHITE_BACKGROUND);
        setVisible(true);
    }

    private void initializeComponents() {
        titleLabel = new JLabel("政大校園訂餐系統 - 商家註冊");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        lblBusinessHours = new JLabel(" 營業時間：");
        String[] weekdays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        weekdayCombo = new JComboBox<>(weekdays);
        
        String[] typess = {"飲料店", "餐廳", "其他"};
        typeCombo = new JComboBox<>(typess);
        txtOpenTime = new JTextField(5);
        txtCloseTime = new JTextField(5);

        txtShopName = new JTextField(20);
        txtManager = new JTextField(20);
        txtAddress = new JTextField(20);
        txtShopPhone = new JTextField(20);
        txtManagerPhone = new JTextField(20);
        txtShopAddress = new JTextField(50);

        txtShopDescription = new JTextArea(4, 40);
        txtShopDescription.setLineWrap(true);
        txtShopDescription.setWrapStyleWord(true);
        
        logoutButton = createRoundedButton("回上頁");
        logoutButton.setBackground(WHITE);
        logoutButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		/* 跳轉到LoginRegister */
        		try {
					vb.deleteVendorAcc(account);
					dispose();
					new LoginRegister();
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
				}
        	}
        		
        });
        
        btnUpdateHours = createRoundedButton("儲存營業時間");
        btnUpdateHours.setBackground(WHITE);
        btnUpdateHours.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String weekday = (String) weekdayCombo.getSelectedItem();
        		try {
        			vb.openUpdate(account, weekday, txtOpenTime.getText(), txtCloseTime.getText());
        			JOptionPane.showMessageDialog(null, "已儲存"+ weekday + "營業時間", "儲存成功", JOptionPane.INFORMATION_MESSAGE);
        		} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
				}
        	}
        		
        });
        
        btnUpdate = createRoundedButton("送出申請");
        btnUpdate.setBackground(WHITE);
        btnUpdate.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String name = txtShopName.getText();
        		String type = (String) typeCombo.getSelectedItem();
				String ownerName = txtManager.getText();
				String address = txtAddress.getText();
				String vendorPhone = txtShopPhone.getText();
				String managerPhone = txtManagerPhone.getText();
				String description = txtShopDescription.getText();
				
				try {
					vb.registerVendor(account, name, type, ownerName, address, vendorPhone, managerPhone, description);
					JOptionPane.showMessageDialog(null, "帳號已成功註冊！", "註冊成功", JOptionPane.INFORMATION_MESSAGE);
					/* 跳轉到LoginRegister */
					dispose();
					new LoginRegister();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
				}
        	}
        });
        
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 240, 240));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(new EmptyBorder(10, 15, 10, 15));
        header.add(titleLabel, BorderLayout.WEST);
        header.add(logoutButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel queryPanel = new JPanel(new GridLayout(1, 2, 520, 0));
        queryPanel.setBackground(new Color(240, 240, 240));
        queryPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel lblQuery = new JLabel("請填寫以下商家資訊");
        lblQuery.setFont(new Font("SansSerif", Font.BOLD, 16));
        queryPanel.add(lblQuery);
        mainPanel.add(queryPanel);

        JPanel hoursPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        hoursPanel.setBackground(new Color(240, 240, 240));
        //hoursPanel.add(new JLabel("星期："));
        hoursPanel.add(lblBusinessHours);
        hoursPanel.add(weekdayCombo);
        hoursPanel.add(txtOpenTime);
        hoursPanel.add(new JLabel("～"));
        hoursPanel.add(txtCloseTime);
        hoursPanel.add(btnUpdateHours);
        mainPanel.add(hoursPanel);

        mainPanel.add(buildInputRow("商家名稱：", txtShopName, "商家類型：", typeCombo));
        mainPanel.add(buildInputRow("負責人員：", txtManager, "商家地址：", txtAddress));
        mainPanel.add(buildInputRow("商家電話：", txtShopPhone, "負責人電話：", txtManagerPhone));

        JPanel descriptionBlock = new JPanel();
        descriptionBlock.setBackground(new Color(240, 240, 240));
        descriptionBlock.setBorder(new EmptyBorder(10, 15, 0, 15));
        descriptionBlock.setLayout(new BorderLayout());

        JPanel descriptionHeader = new JPanel(new GridLayout(1, 1));
        descriptionHeader.setBackground(new Color(240, 240, 240));
        descriptionHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        descriptionHeader.add(new JLabel("商家介紹："));
        descriptionBlock.add(descriptionHeader, BorderLayout.NORTH);

        descriptionBlock.add(new JScrollPane(txtShopDescription), BorderLayout.CENTER);
        mainPanel.add(descriptionBlock);
        

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        buttonPanel.add(btnUpdate);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel buildInputRow(String label1, JComponent field1, String label2, JComponent field2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setBackground(new Color(240, 240, 240));
        row.setBorder(new EmptyBorder(5, 15, 5, 15));
        row.add(wrapWithField(new JLabel(label1), field1));
        row.add(wrapWithField(new JLabel(label2), field2));
        return row;
    }

    private JPanel wrapWithField(JLabel label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.white);
        return btn;
    }

    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> new VendorRegister("111304012"));
    }
}