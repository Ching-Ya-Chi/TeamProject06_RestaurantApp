import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDB {
	private final String server = "jdbc:mysql://140.119.19.73:3315/";
	private final String database = "TG06";
	private final String url = server + database + "?useSSL=false";
	private final String dbUser = "TG06";
	private final String dbPassword = "bMIEqf";
	private Connection conn;
	private String query, table;

	public UserDB() {
		try {
			conn = DriverManager.getConnection(url, dbUser, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUserName(String account) throws Exception {
		String name = null;
		try {
			query = "SELECT name FROM user WHERE account = ? AND status = '可使用'";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			ResultSet result = pstmt.executeQuery();
			if (result.next()) {
				name = result.getString("name");
			}
			result.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	public String getUserTele(String account) throws Exception {
		String name = null;
		try {
			query = "SELECT telephone FROM user WHERE account = ? AND status = '可使用'";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			ResultSet result = pstmt.executeQuery();
			if (result.next()) {
				name = result.getString("telephone");
			}
			result.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	public String getUserPw(String account) throws Exception {
		String name = null;
		try {
			query = "SELECT password FROM user WHERE account = ? AND status = '可使用'";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			ResultSet result = pstmt.executeQuery();
			if (result.next()) {
				name = result.getString("password");
			}
			result.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	// 確認輸入的帳號密碼是否有效(personalInfo, LoginRegister)
	public void validRegister(String role, String account, String password) throws Exception {
		// account
		if (account == null || account.isEmpty()) {
			throw new UserError("請輸入帳號");
		}

		// password
		if (password == null || password.isEmpty()) {
			throw new PasswordError("請輸入密碼");
		} else if (password.length() > 8) {
			throw new PasswordError("密碼不可超過八位數");
		}

		switch (role) {
		case "user":
			table = "user";
			break;
		case "vendor":
			table = "vendor";
			break;
		case "manager":
			table = "manager";
			break;
		default:
			throw new Exception("角色類型錯誤");
		}

		ResultSet result = null;
		PreparedStatement pstmt = null;
		try {
			query = "SELECT account FROM " + table + " WHERE account = ? AND status = '可使用'";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			result = pstmt.executeQuery();
			if (result.next()) {
				throw new UserError("此帳號已被註冊");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (result != null)
				result.close();
			if (pstmt != null)
				pstmt.close();
		}
	}

	// 註冊完後放入帳號密碼(LoginRegister)
	public void registerAccPass(String role, String account, String password) throws Exception {
		int result = 0;
		switch (role) {
		case "user":
			table = "user";
			break;
		case "vendor":
			table = "vendor";
			break;
		default:
			table = "";
		}

		PreparedStatement pstmt = null;
		try {
			query = "INSERT INTO " + table + "(account, password) VALUES (?, ?)";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			pstmt.setString(2, password);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}

	}

	// 註冊完後，跳轉到填姓名電話(UserRegister)
	public void registerNameTele(String account, String name, String telephone) throws Exception {
		// telephone
		if (telephone == null || telephone.isEmpty()) {
			throw new TelephoneError("請輸入電話號碼");
		} else if (telephone.length() != 10) {
			throw new TelephoneError("電話號碼須為十位數字");
		} else if (!telephone.matches("\\d{10}")) {
			throw new TelephoneError("電話號碼僅能輸入數字");
		}

		// name
		if (name == null || name.isEmpty()) {
			throw new NameError("請輸入姓名");
		}

		PreparedStatement pstmt = null;
		try {
			query = "UPDATE user SET name = ?, telephone = ? , status = '可使用' WHERE account = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, name);
			pstmt.setString(2, telephone);
			pstmt.setString(3, account);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
	}

	// 登入
	public void login(String role, String account, String password) throws Exception {
		switch (role) {
		case "user":
			table = "user";
			break;
		case "vendor":
			table = "vendor";
			break;
		case "manager":
			table = "manager";
			break;
		default:
			table = "";
		}

		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			if(table.equals("user")||table.equals("vendor")) {
				query = "SELECT account, password FROM " + table + " WHERE account = ? AND status = '可使用'";
			}
			else {
				query = "SELECT account, password FROM " + table + " WHERE account = ?";
			}
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			result = pstmt.executeQuery();

			// account
			if (account == null || account.isEmpty()) {
				throw new UserError("請輸入帳號");
			} else if (!result.next()) {
				throw new UserError("此帳號不存在");
			}

			// password
			String pw = result.getString("password");
			if (!pw.equals(password)) {
				throw new PasswordError("密碼錯誤");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (result != null)
				result.close();
			if (pstmt != null)
				pstmt.close();
		}
	}

	// 更新個人資訊(personalInfo)
	public void updateUserPro(String account, String password, String name, String telephone) throws Exception {
		// telephone
		if (telephone == null || telephone.isEmpty()) {
			throw new TelephoneError("請輸入電話號碼");
		} else if (telephone.length() != 10) {
			throw new TelephoneError("電話號碼須為十位數字");
		} else if (!telephone.matches("\\d{10}")) {
			throw new TelephoneError("電話號碼僅能輸入數字");
		}

		// name
		if (name == null || name.isEmpty()) {
			throw new UserError("請輸入姓名");
		}

		// account
		if (account == null || account.isEmpty()) {
			throw new UserError("請輸入帳號");
		}

		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			query = "SELECT account FROM user WHERE account = ? AND status = '可使用'";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			result = pstmt.executeQuery();
			if (!result.next()) {
				throw new UserError("查無此帳號");
			}

			// password
			if (password == null || password.isEmpty()) {
				throw new PasswordError("請輸入密碼");
			} else if (password.length() > 8) {
				throw new PasswordError("密碼不可超過八位數");
			}

			query = "UPDATE user SET password = ?, name = ?, telephone = ? WHERE account = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, password);
			pstmt.setString(2, name);
			pstmt.setString(3, telephone);
			pstmt.setString(4, account);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (result != null)
				result.close();
			if (pstmt != null)
				pstmt.close();
		}
	}

	//delete
	public void deleteUserAcc(String account) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			query = "SELECT account FROM user WHERE account = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			result = pstmt.executeQuery();
			if (!result.next()) {
				throw new UserError("查無此帳號");
			}
			query = "DELETE FROM user WHERE account = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			pstmt.executeUpdate();
			
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (result != null)
				result.close();
			if (pstmt != null)
				pstmt.close();
		}
	}
}
	

class UserError extends Exception {
	public UserError(String message) {
		super(message);
	}
}

class PasswordError extends Exception {
	public PasswordError(String message) {
		super(message);
	}
}

class TelephoneError extends Exception {
	public TelephoneError(String message) {
		super(message);
	}
}

class NameError extends Exception {
	public NameError(String message) {
		super(message);
	}
}
