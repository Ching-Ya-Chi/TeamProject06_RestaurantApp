import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.net.MalformedURLException;
import java.net.URL;
import java.awt.image.BufferedImage; 
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class LoginRegister extends JFrame {
    private static final int FRAME_WIDTH = 700, FRAME_HEIGHT = 580, d = 100;
    private JLabel type, nameLabel, passwordLabel, imgLabel, customTitleLabel;;
    private JTextField nameTextField;
    private JPasswordField passwordTextField;
    private JRadioButton userButton, vendorButton, workerButton;
    private JButton loginButton, registerButton;
    private ButtonGroup userBtnGroup;
    private JPanel totalPanel = (JPanel) this.getContentPane();
    private UserDB ub;
    
    private final int LOGO_IMAGE_WIDTH = 180; // Renamed from IMAGE_WIDTH for clarity
    private final int LOGO_IMAGE_HEIGHT = 180; // Renamed from IMAGE_HEIGHT for clarity
    
    private final Color WHITE = Color.WHITE;
    private final Color WHITE_BACKGROUND = new Color(238,238,238);
    private final Font DEFAULT_FONT = new Font("Microsoft JhengHei", Font.PLAIN, 14); // Define a default font
    private final Font TITLE_FONT_MAIN = new Font("Microsoft JhengHei", Font.BOLD, 24);
    private final Font CUSTOM_SUBTITLE_FONT = new Font("Microsoft JhengHei", Font.ITALIC | Font.BOLD, 18);

    public LoginRegister() {
        setTitle("政大校園訂餐系統");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ub = new UserDB();
        
        createLabel();
        createTextField();
        createButton();
        createRadioButton();
        setBackground(WHITE_BACKGROUND);
        createLayout();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void createTextField(){
        nameTextField = new JTextField(20);
        passwordTextField = new JPasswordField(20);
    }

    public void createRadioButton(){
        userButton = new JRadioButton("使用者");
        vendorButton = new JRadioButton("商家");
        workerButton = new JRadioButton("管理者");
        userBtnGroup = new ButtonGroup();
        userButton.setSelected(true);
        userBtnGroup.add(userButton);
        userBtnGroup.add(vendorButton);
        userBtnGroup.add(workerButton);
        userButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                userButton.setSelected(true);
            }
        });

        vendorButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                vendorButton.setSelected(true);
            }
        });

        workerButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                workerButton.setSelected(true);
            }
        });
    }

    public void createLabel(){
    	type = new JLabel("身份:");
        type.setFont(DEFAULT_FONT);
        nameLabel = new JLabel("帳號:");
        nameLabel.setFont(DEFAULT_FONT);
        passwordLabel = new JLabel("密碼:");
        passwordLabel.setFont(DEFAULT_FONT);

        imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(LOGO_IMAGE_WIDTH, LOGO_IMAGE_HEIGHT));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setVerticalAlignment(SwingConstants.CENTER);
        imgLabel.setOpaque(false); // Make background transparent
        // imgLabel.setBackground(Color.WHITE); // Set if you want a white box behind image

        String logoUrlString = "https://storage.googleapis.com/nccu_java_g6_final_project/img/logo.png";
        try {
            URL imageUrl = new URL(logoUrlString);
            ImageIcon originalIcon = new ImageIcon(imageUrl);

            if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE && originalIcon.getIconWidth() > 0) {
                Image image = originalIcon.getImage();
                Image scaledImage = image.getScaledInstance(LOGO_IMAGE_WIDTH, LOGO_IMAGE_HEIGHT, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                System.err.println("Image not loaded or invalid for URL: " + logoUrlString);
                setPlaceholderImageOrText(imgLabel, "Logo加載失敗");
            }
        } catch (MalformedURLException e) {
            System.err.println("Malformed image URL: " + logoUrlString + " - " + e.getMessage());
            setPlaceholderImageOrText(imgLabel, "無效圖片路徑");
        } catch (Exception e) {
            System.err.println("Error processing image: " + logoUrlString + " - " + e.getMessage());
            setPlaceholderImageOrText(imgLabel, "圖片錯誤");
            e.printStackTrace();
        }

    }

	private void setPlaceholderImageOrText(JLabel label, String text) {
		// You could also load a default placeholder image here
		// For example: label.setIcon(new ImageIcon(getClass().getResource("/path/to/placeholder.png")));
		label.setText(text);
		label.setIcon(null); // Ensure no old icon is shown
		label.setFont(new Font("Microsoft JhengHei", Font.ITALIC, 12));
		label.setForeground(Color.DARK_GRAY);
	}
    public void createButton(){
        loginButton = new JButton("登入");
        loginButton.setBackground(WHITE);
        registerButton = new JButton("註冊");
        registerButton.setBackground(WHITE);
        loginButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	String acc = nameTextField.getText();
            	String pw = passwordTextField.getText();
                // 根據不同身份，去資料庫撈出對應身份的帳號密碼檢查
                if (userButton.isSelected()) {
                	try {
            	        ub.login("user", acc, pw);
            	        invoiceRough UserGUI = new invoiceRough(acc);
            	        UserGUI.setVisible(true);
            	        dispose();
            	        /* 跳轉到RoughOrder*/
            	    } catch (UserError | PasswordError | TelephoneError | NameError ex) {
            	        JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            	    } catch (Exception e1) {
            	        e1.printStackTrace();
            	    }
                } else if (vendorButton.isSelected()) {
                	try {
            	        ub.login("vendor", acc, pw);
            	        Connection conn;
            	        String server = "jdbc:mysql://140.119.19.73:3315/";
            	    	String database = "TG06";
            	    	String url = server + database + "?useSSL=false";
            	    	String dbUser = "TG06";
            	    	String dbPassword = "bMIEqf";
            	        conn = DriverManager.getConnection(url, dbUser, dbPassword);
            	        PreparedStatement pstmt = null;
            			ResultSet result = null;
            			String query = "SELECT vendor_id FROM " + "vendor" + " WHERE account = ? AND status = '可使用'";
            			pstmt = conn.prepareStatement(query);
            			pstmt.setString(1, acc);
            			result = pstmt.executeQuery();
            			result.next();
            			MerchantOrderListGUI MerchantGUI = new MerchantOrderListGUI(result.getInt("vendor_id"));
            	        MerchantGUI.setVisible(true);
            	        /* 跳轉到商家頁面的某頁*/
            	        dispose();
            	    } catch (UserError | PasswordError | TelephoneError | NameError ex) {
            	        JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            	    } catch (Exception e1) {
            	        e1.printStackTrace();
            	    }
                } else if (workerButton.isSelected()) {
                	try {
            	        ub.login("manager", acc, pw);
            	        Connection conn;
            	        String server = "jdbc:mysql://140.119.19.73:3315/";
            	    	String database = "TG06";
            	    	String url = server + database + "?useSSL=false";
            	    	String dbUser = "TG06";
            	    	String dbPassword = "bMIEqf";
            	        conn = DriverManager.getConnection(url, dbUser, dbPassword);
            	        PreparedStatement pstmt = null;
            			ResultSet result = null;
            			String query = "SELECT manager_id FROM " + "manager" + " WHERE account = ?";
            			pstmt = conn.prepareStatement(query);
            			pstmt.setString(1, acc);
            			result = pstmt.executeQuery();
            			result.next();
            	        AdminHomePage AdminGUI = new AdminHomePage(result.getInt("manager_id"));
            	        AdminGUI.setVisible(true);
            	        dispose();
            	    } catch (UserError | PasswordError | TelephoneError | NameError ex) {
            	        JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            	    } catch (Exception e1) {
            	        e1.printStackTrace();
            	    }
                }

            }
        });

        registerButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	String acc = nameTextField.getText();
            	String pw = passwordTextField.getText();
                // 根據不同身份，檢查是否存在相同帳密的帳號，無的話創建新帳號
            	if (userButton.isSelected()) {
            		try {            	        
            	        /* 跳轉到UserRegister*/
            			ub.validRegister("user", acc,pw);
            			ub.registerAccPass("user", acc, pw);
            			new UserRegister(acc);
            	        dispose();
            		}catch(UserError | PasswordError ex){
            			JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            		}catch(Exception e1) {
            			e1.printStackTrace();
            		}
            	    
            	} else if (vendorButton.isSelected()) {
            		try {
            			ub.validRegister("vendor", acc,pw);
            			ub.registerAccPass("vendor", acc, pw);
            			new VendorRegister(acc);
            	        dispose();
            	        /* 跳轉到VendorRegister*/
            		}catch(UserError | PasswordError ex){
            			JOptionPane.showMessageDialog(null, ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            		}catch(Exception e1) {
            			e1.printStackTrace();
            		}
            	        
            	        
            	} else if (workerButton.isSelected()) {
            	    JOptionPane.showMessageDialog(null, "不可註冊管理者帳號", "提示", JOptionPane.INFORMATION_MESSAGE);
            	}
            }
        });

    }

    public void createLayout(){
    	JPanel totalGUIPanel = new JPanel(new BorderLayout(0, 15));
        totalGUIPanel.setBackground(WHITE_BACKGROUND);
        totalGUIPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel mainSystemTitle = new JLabel("政大校園訂餐系統");
        mainSystemTitle.setFont(TITLE_FONT_MAIN);
        mainSystemTitle.setHorizontalAlignment(SwingConstants.CENTER);
        totalGUIPanel.add(mainSystemTitle, BorderLayout.NORTH);

        JPanel centerContentPanel = new JPanel();
        centerContentPanel.setLayout(new BoxLayout(centerContentPanel, BoxLayout.Y_AXIS));
        centerContentPanel.setOpaque(false);

        // --- 修改：只添加圖片，移除 "時光正好" 相關的 Panel 和 Label ---
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContentPanel.add(imgLabel); // 直接添加 imgLabel
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 25))); // 圖片下方的間距

        // Your existing panel structure for form elements
        // We'll try to fit it into the BoxLayout of centerContentPanel
        // Or use a GridBagLayout for better control within this section

        // Panel for radio buttons (type selection)
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        radioPanel.setOpaque(false);
        radioPanel.add(type); // Add the "身份:" label
        radioPanel.add(userButton);
        radioPanel.add(vendorButton);
        radioPanel.add(workerButton);
        centerContentPanel.add(radioPanel);

        // Panel for account input
        JPanel accountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        accountPanel.setOpaque(false);
        accountPanel.add(nameLabel);
        accountPanel.add(nameTextField);
        centerContentPanel.add(accountPanel);

        // Panel for password input
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTextField);
        centerContentPanel.add(passwordPanel);

        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Space before buttons

        // Panel for Login/Register buttons
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        actionButtonPanel.setOpaque(false);
        loginButton.setPreferredSize(new Dimension(100, 35));
        registerButton.setPreferredSize(new Dimension(100, 35));
        actionButtonPanel.add(loginButton);
        actionButtonPanel.add(registerButton);
        centerContentPanel.add(actionButtonPanel);


        totalGUIPanel.add(centerContentPanel, BorderLayout.CENTER);
        // Add an empty panel at the bottom for spacing if needed, or rely on EmptyBorder
        totalGUIPanel.add(Box.createRigidArea(new Dimension(0,20)), BorderLayout.SOUTH);


        setContentPane(totalGUIPanel); // Set the new total panel as the content pane
    }
    


    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginRegister::new);
    }

}