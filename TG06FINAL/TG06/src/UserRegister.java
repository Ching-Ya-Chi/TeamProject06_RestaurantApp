import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class UserRegister extends JFrame {
	private static final int FRAME_WIDTH = 700, FRAME_HEIGHT = 500, TEXT_LENGTH = 20;
	private String account;
	private UserDB ub;
	JLabel title, intro, tip, name, tele, userName, password;
	JTextField nameTextField, teleTextField, userNameTextField, passwordTextField;
	JButton logoutButton, saveButton, orderQueryButton, basicInfoButton, responseButton;
	
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

	public UserRegister(String account) {
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
		intro = new JLabel("請填寫以下個人資料");
		name = new JLabel("姓名");
		tele = new JLabel("電話");
	}

	public void createTextField() {
		nameTextField = new JTextField(TEXT_LENGTH);
		teleTextField = new JTextField(TEXT_LENGTH);
	}

	public void createButton() {
		saveButton = new JButton("送出申請");
		saveButton.setBackground(WHITE);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = nameTextField.getText();
				String telephone = teleTextField.getText();
				try {
					ub.registerNameTele(account, name, telephone);
					JOptionPane.showMessageDialog(null, "帳號已成功註冊！", "註冊成功", JOptionPane.INFORMATION_MESSAGE);
					/* 跳轉到LoginRegister */
					dispose();
					new LoginRegister();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		logoutButton = new JButton("回上頁");
		logoutButton.setBackground(WHITE);
		logoutButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				/* 跳轉到LoginRegister */
				try {
					ub.deleteUserAcc(account);
					dispose();
					new LoginRegister();
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
				}
			}

		});
	}

	public void createLayout() {
		JPanel mainPanel = new JPanel(new BorderLayout());

		// 頂部按鈕區域
		JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel toppestPanel = new JPanel(new BorderLayout(20, 20));
		JPanel totalPanel = new JPanel(new BorderLayout(20, 20));

		title = new JLabel("政大校園訂餐系統 - 使用者");
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		toppestPanel.add(title, BorderLayout.WEST);
		toppestPanel.add(logoutButton, BorderLayout.EAST);

		JPanel leftOnePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel rightOnePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel info = new JPanel(new GridLayout(1, 1, 20, 20));

		leftOnePanel.add(name);
		leftOnePanel.add(nameTextField);
		rightOnePanel.add(tele);
		rightOnePanel.add(teleTextField);
		info.add(leftOnePanel);
		info.add(rightOnePanel);

		JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		wrapperPanel.add(info);
		mainPanel.add(wrapperPanel, BorderLayout.CENTER);
		mainPanel.add(intro, BorderLayout.NORTH);

		totalPanel.add(toppestPanel, BorderLayout.NORTH);
		totalPanel.add(mainPanel, BorderLayout.CENTER);
		totalPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
		totalPanel.add(saveButton, BorderLayout.SOUTH);

		add(totalPanel);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new UserRegister("hihi"));
	}

}