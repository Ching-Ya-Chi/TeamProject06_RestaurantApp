import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class personalInfo extends JFrame {
	private static final int FRAME_WIDTH = 700, FRAME_HEIGHT = 500, TEXT_LENGTH = 20;
	private String account;
	private UserDB ub;
	JLabel title, intro, tip, name, tele, userName, password;
	JTextField nameTextField, teleTextField, userNameTextField, passwordTextField;
	JButton logoutButton, saveButton, orderQueryButton, basicInfoButton, responseButton;
	private JButton goToOrderSystemButton;
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

	public personalInfo(String account) {
		this.account = account;
		ub = new UserDB();
		setTitle("政大校園訂餐系統");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		createLabel();
		createTextField();
		createButton();
		createLayout();
		setBackground(WHITE_BACKGROUND);
	    setLocationRelativeTo(null);
		setVisible(true);
	}

	public void createLabel() {
		title = new JLabel("政大校園訂餐系統 - 使用者");
		intro = new JLabel("管理個人資料");
		tip = new JLabel("修改資料後按下儲存即可更新個人資料");
		name = new JLabel("姓名");
		tele = new JLabel("電話");
		userName = new JLabel("帳號");
		password = new JLabel("密碼");
	}

	public void createTextField() {
		try {
			nameTextField = new JTextField(ub.getUserName(account), TEXT_LENGTH);
			teleTextField = new JTextField(ub.getUserTele(account), TEXT_LENGTH);
			userNameTextField = new JTextField(account, TEXT_LENGTH);
			userNameTextField.setEditable(false);
			passwordTextField = new JTextField(ub.getUserPw(account), TEXT_LENGTH);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void createButton() {
		logoutButton = new JButton("登出");
		logoutButton.setBackground(WHITE);
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				personalInfo.this.dispose(); // 關掉頁面
			}
		});

		saveButton = new JButton("儲存變更");
		saveButton.setBackground(WHITE);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = passwordTextField.getText();
				String name = nameTextField.getText();
				String telephone = teleTextField.getText();
				try {
					ub.updateUserPro(account, password, name, telephone);
					JOptionPane.showMessageDialog(null, "更新成功！", "更新成功", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

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
			public void actionPerformed(ActionEvent e) {
				/* 什麼都不會發生 */
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

		JPanel leftOnePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel rightOnePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel leftTwoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel rightTwoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel info = new JPanel(new GridLayout(2, 1, 20, 20));

		leftOnePanel.add(name);
		leftOnePanel.add(nameTextField);
		rightOnePanel.add(tele);
		rightOnePanel.add(teleTextField);
		leftTwoPanel.add(userName);
		leftTwoPanel.add(userNameTextField);
		rightTwoPanel.add(password);
		rightTwoPanel.add(passwordTextField);
		info.add(leftOnePanel);
		info.add(rightOnePanel);
		info.add(leftTwoPanel);
		info.add(rightTwoPanel);

		JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		wrapperPanel.add(info);
		mainPanel.add(wrapperPanel, BorderLayout.CENTER);
		mainPanel.add(topButtonPanel, BorderLayout.NORTH);

		totalPanel.add(toppestPanel, BorderLayout.NORTH);
		totalPanel.add(mainPanel, BorderLayout.CENTER);
		totalPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
		totalPanel.add(saveButton, BorderLayout.SOUTH);

		add(totalPanel);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new personalInfo("111304012"));
	}

}