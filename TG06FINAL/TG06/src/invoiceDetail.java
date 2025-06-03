import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.border.EmptyBorder;

public class invoiceDetail extends JFrame {
	private static final int FRAME_WIDTH = 700, FRAME_HEIGHT = 500, TEXT_LENGTH = 20;
	private int orderId;
	private String acc;
	private OrderDB ob;
	private ComplainDB cb;
	private RoughOrder or;
	JLabel title, vendorName, conditionLabel, invoiceID, timeLabel, wayLabel, moneyLabel, tableLabel, complainLabel,
			problemLabel;
	JTextField invoiceIDTextField, timeTextField, wayTextField, moneyTextField, complainTextArea;
	JButton logoutButton, backButton, complainButton;
	JRadioButton complete, waitTake, waitDeliver, prepare, waitGet, reject;
	JRadioButton service, deliver, dishes;
	ButtonGroup buttonGroup, problemGroup;
	JTable orderTable;
	JTableHeader tableHeader;
	JScrollPane scrollPane;
	
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);

	public invoiceDetail(int orderId,String acc) {
		this.orderId = orderId;
		this.acc = acc;
		cb = new ComplainDB();
		ob = new OrderDB();
		try {
			or = ob.getOrderHeaderDetail(orderId);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
		}
		setTitle("政大校園訂餐系統");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		buttonGroup = new ButtonGroup();
		problemGroup = new ButtonGroup();
		createLabel();
		createText();
		createRadioButton();
		createButton();
		createTable();
		createLayout();
		setBackground(WHITE_BACKGROUND);
	       setLocationRelativeTo(null);
		setVisible(true);
	}

	public void createLabel() {
		title = new JLabel("政大校園訂餐系統 - 使用者");
		vendorName = new JLabel("");
		vendorName.setText("");
		conditionLabel = new JLabel("訂單狀態");
		invoiceID = new JLabel("訂單編號");
		timeLabel = new JLabel("取餐時間");
		wayLabel = new JLabel("取餐方式");
		moneyLabel = new JLabel("訂單金額");
		tableLabel = new JLabel("訂單內容：");
		complainLabel = new JLabel("填寫回饋：");
		problemLabel = new JLabel("回饋類型：");

	}

	public void createText() {
		try {
			String orid = String.valueOf(orderId);
			invoiceIDTextField = new JTextField(orid, TEXT_LENGTH);
			invoiceIDTextField.setEditable(false);

			String time = or.getFinishTime();
			timeTextField = new JTextField(time, TEXT_LENGTH);
			timeTextField.setEditable(false);

			String type = or.getType();
			wayTextField = new JTextField(type, TEXT_LENGTH);
			wayTextField.setEditable(false);

			String money = String.valueOf(or.getMoney());
			moneyTextField = new JTextField(money, TEXT_LENGTH);
			moneyTextField.setEditable(false);

			complainTextArea = new JTextField(47);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
		}

	}

	public String getSelectedButtonText(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();
			if (button.isSelected()) {
				return button.getText();
			}
		}
		return null;
	}

	public void createButton() {
		logoutButton = new JButton("登出");
		logoutButton.setBackground(WHITE);
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				new LoginRegister();
				dispose();
			}
		});

		backButton = new JButton("返回");
		backButton.setBackground(WHITE);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* 跳轉到invoiceRough */
				new invoiceRough(acc);
				dispose();
			}
		});

		complainButton = new JButton("送出回饋");
		complainButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String type = getSelectedButtonText(problemGroup);
					String complainContent = complainTextArea.getText();
					cb.sendComplain(orderId, type, complainContent);
					JOptionPane.showMessageDialog(null, "已送出回饋，請等待管理者回覆！", "回饋成功", JOptionPane.INFORMATION_MESSAGE);
					complainTextArea.setText("");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	public void createRadioButton() {
		complete = new JRadioButton("已完成");
		complete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				complete.setSelected(true);
			}
		});

		waitTake = new JRadioButton("待取餐");
		waitTake.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				waitTake.setSelected(true);
			}
		});

		waitDeliver = new JRadioButton("待送餐");
		waitDeliver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				waitDeliver.setSelected(true);
			}
		});

		prepare = new JRadioButton("準備中");
		prepare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		waitGet = new JRadioButton("待接單");
		waitGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		reject = new JRadioButton("已拒絕");
		reject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		service = new JRadioButton("服務問題");
		service.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		deliver = new JRadioButton("送餐問題");
		deliver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		dishes = new JRadioButton("菜品問題");
		dishes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		buttonGroup.add(complete);
		buttonGroup.add(waitTake);
		buttonGroup.add(waitDeliver);
		buttonGroup.add(prepare);
		buttonGroup.add(waitGet);
		buttonGroup.add(reject);

		problemGroup.add(service);
		problemGroup.add(deliver);
		problemGroup.add(dishes);

		String status = or.getStatus();
		switch (status) {
		case "已完成":
			complete.setSelected(true);
			break;
		case "待取餐":
			waitTake.setSelected(true);
			break;
		case "待送餐":
			waitDeliver.setSelected(true);
			break;
		case "準備中":
			prepare.setSelected(true);
			break;
		case "待接單":
			waitGet.setSelected(true);
			break;
		case "已拒絕":
			reject.setSelected(true);
			break;
		default:
			System.out.println("未知狀態：" + status);
		}

		complete.setEnabled(false);
		waitTake.setEnabled(false);
		waitDeliver.setEnabled(false);
		prepare.setEnabled(false);
		waitGet.setEnabled(false);
		reject.setEnabled(false);

	}

	public void createTable() {
		String[] columnNames = { "商品名稱", "商品價格", "商品數量" };
		Object[][] data = new Object[1][3];
		;
		try {
			ArrayList<DetailOrder> ro = ob.getOrderItemDetail(orderId);
			data = new Object[ro.size()][3];
			for (int i = 0; i < ro.size(); i++) {
				DetailOrder r = ro.get(i);
				data[i][0] = r.getName();
				data[i][1] = r.getPrice();
				data[i][2] = r.getQuantity();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
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
		scrollPane = new JScrollPane(orderTable);
	}

	public void createLayout() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

		JPanel toppestPanel = new JPanel(new BorderLayout(20, 20));
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		toppestPanel.add(title, BorderLayout.WEST);
		toppestPanel.add(logoutButton, BorderLayout.EAST);
		mainPanel.add(toppestPanel);

		JPanel topperPanel = new JPanel(new BorderLayout(20, 20));
		topperPanel.add(vendorName, BorderLayout.WEST);
		topperPanel.add(backButton, BorderLayout.EAST);
		mainPanel.add(topperPanel);

		JPanel wayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		wayPanel.add(wayLabel);
		wayPanel.add(complete);
		wayPanel.add(waitTake);
		wayPanel.add(waitDeliver);
		wayPanel.add(prepare);
		wayPanel.add(waitGet);
		wayPanel.add(reject);
		mainPanel.add(wayPanel);

		JPanel middlePanel = new JPanel();
		JPanel leftOnePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel rightOnePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel leftTwoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel rightTwoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel info = new JPanel(new GridLayout(2, 2, 20, 20));

		leftOnePanel.add(invoiceID);
		leftOnePanel.add(invoiceIDTextField);
		rightOnePanel.add(timeLabel);
		rightOnePanel.add(timeTextField);
		leftTwoPanel.add(wayLabel);
		leftTwoPanel.add(wayTextField);
		rightTwoPanel.add(moneyLabel);
		rightTwoPanel.add(moneyTextField);
		info.add(leftOnePanel);
		info.add(rightOnePanel);
		info.add(leftTwoPanel);
		info.add(rightTwoPanel);
		mainPanel.add(info);

		JPanel complainPanelOne = new JPanel(new FlowLayout(FlowLayout.LEFT));
		complainPanelOne.add(problemLabel);
		complainPanelOne.add(service);
		complainPanelOne.add(deliver);
		complainPanelOne.add(dishes);
		complainPanelOne.add(new JLabel("                                                     "));
		complainPanelOne.add(complainButton);

		JPanel complainPanelTwo = new JPanel(new FlowLayout(FlowLayout.LEFT));
		complainPanelTwo.add(complainLabel);
		complainPanelTwo.add(complainTextArea);

		mainPanel.add(scrollPane);
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		mainPanel.add(complainPanelOne);
		mainPanel.add(complainPanelTwo);
		add(mainPanel);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new invoiceDetail(42,"user_0001"));
	}

}
