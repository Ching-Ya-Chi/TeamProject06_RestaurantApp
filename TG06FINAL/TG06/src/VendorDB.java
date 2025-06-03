import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VendorDB {
	private final String server = "jdbc:mysql://140.119.19.73:3315/";
	private final String database = "TG06";
	private final String url = server + database + "?useSSL=false";
	private final String dbUser = "TG06";
	private final String dbPassword = "bMIEqf";
	private Connection conn;
	private String query;

	public VendorDB() {
		try {
			conn = DriverManager.getConnection(url, dbUser, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 商家註冊營業時間更新(VendorRegister)
	public boolean openUpdate(String account, String day, String startTime, String endTime) throws Exception {

		if (startTime == null || startTime.isEmpty() || endTime == null || endTime.isEmpty() || !isValidTime(startTime)
				|| !isValidTime(endTime)) {
			throw new TimeFormatError("時間格式錯誤，請輸入 HH:mm 格式（例如 09:00）");
		}

		LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
		LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
		if (start.isAfter(end)) {
			LocalTime temp = start;
			start = end;
			end = temp;
		}
		startTime = start.format(DateTimeFormatter.ofPattern("HH:mm"));
		endTime = end.format(DateTimeFormatter.ofPattern("HH:mm"));

		switch (day) {
		case "星期一":
			query = "UPDATE vendor SET mon_open = TRUE, mon_start_time = ? , mon_end_time = ? WHERE account = ?";
			break;
		case "星期二":
			query = "UPDATE vendor SET tue_open = TRUE, tue_start_time = ? , tue_end_time = ? WHERE account = ?";
			break;
		case "星期三":
			query = "UPDATE vendor SET wed_open = TRUE, wed_start_time = ? , wed_end_time = ? WHERE account = ?";
			break;
		case "星期四":
			query = "UPDATE vendor SET thu_open = TRUE, thu_start_time = ? , thu_end_time = ? WHERE account = ?";
			break;
		case "星期五":
			query = "UPDATE vendor SET fri_open = TRUE, fri_start_time = ? , fri_end_time = ? WHERE account = ?";
			break;
		case "星期六":
			query = "UPDATE vendor SET sat_open = TRUE, sat_start_time = ? , sat_end_time = ? WHERE account = ?";
			break;
		case "星期日":
			query = "UPDATE vendor SET sun_open = TRUE, sun_start_time = ? , sun_end_time = ? WHERE account = ?";
			break;
		default:
			return false;
		}

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, startTime);
			stmt.setString(2, endTime);
			stmt.setString(3, account);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error executing query: " + query);
		}

		return true;
	}

	// 確認營業開始時間/結束時間是否符合輸入格式
	public boolean isValidTime(String timeStr) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			LocalTime.parse(timeStr, formatter);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	// 商家註冊詳情(VendorRegister)
	public void registerVendor(String account, String vendorName, String type, String ownerName, String address,
			String vendorPhone, String ownerPhone, String description) throws Exception {
		if (vendorName == null || vendorName.isEmpty()) {
			throw new RegisterError("尚未填入商家名稱");
		} else if (type == null || type.isEmpty()) {
			throw new RegisterError("尚未填入商家類型");
		} else if (ownerName == null || ownerName.isEmpty()) {
			throw new RegisterError("尚未填入負責人員名稱");
		} else if (address == null || address.isEmpty()) {
			throw new RegisterError("尚未填入商家地址");
		} else if (vendorPhone == null || vendorPhone.isEmpty()) {
			throw new RegisterError("尚未填入商家電話");
		} else if (vendorPhone.length() != 10) {
			throw new TelephoneError("商家電話須為十位數字");
		} else if (!vendorPhone.matches("\\d{10}")) {
			throw new TelephoneError("商家電話僅能輸入數字");
		} else if (ownerPhone == null || ownerPhone.isEmpty()) {
			throw new RegisterError("尚未填入負責人電話");
		} else if (ownerPhone.length() != 10) {
			throw new TelephoneError("商家電話須為十位數字");
		} else if (!ownerPhone.matches("\\d{10}")) {
			throw new TelephoneError("商家電話僅能輸入數字");
		} else if (description == null || description.isEmpty()) {
			throw new RegisterError("尚未填入商家介紹");
		}

		try {
			query = "UPDATE vendor SET vendor_name = ?, type = ?, owner_name = ?, address = ?, vendor_phone = ?, owner_phone = ?, description = ?, status = '待審核'  WHERE account = ?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, vendorName);
			stmt.setString(2, type);
			stmt.setString(3, ownerName);
			stmt.setString(4, address);
			stmt.setString(5, vendorPhone);
			stmt.setString(6, ownerPhone);
			stmt.setString(7, description);
			stmt.setString(8, account);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error executing query: " + query);
		}

	}
	public void deleteVendorAcc(String account) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			query = "SELECT account FROM vendor WHERE account = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			result = pstmt.executeQuery();
			if (!result.next()) {
				throw new UserError("查無此帳號");
			}
			query = "DELETE FROM vendor WHERE account = ?";
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

class TimeFormatError extends Exception {
	public TimeFormatError(String message) {
		super(message);
	}
}

class RegisterError extends Exception {
	public RegisterError(String message) {
		super(message);
	}
}
