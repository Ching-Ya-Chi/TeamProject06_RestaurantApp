import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ComplainDB {
	private final String server = "jdbc:mysql://140.119.19.73:3315/";
	private final String database = "TG06";
	private final String url = server + database + "?useSSL=false";
	private final String dbUser = "TG06";
	private final String dbPassword = "bMIEqf";
	private Connection conn;
	private String query;
	
	public ComplainDB() {
		try {
			conn = DriverManager.getConnection(url, dbUser, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 寄出回饋(invoiceDetail)
	public void sendComplain(int orderId, String type, String complainContent) throws Exception{
		int userId = 0;
		if (type == null || type.isEmpty()) {
	        throw new ComplainError("尚未選擇回饋類型");
	    } else if (complainContent == null || complainContent.isEmpty()) {
	        throw new ComplainError("尚未填入回饋內容");
	    }
		
		try {
			query = "SELECT user_id FROM `order` WHERE order_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, orderId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
			    userId = rs.getInt("user_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error executing query: " + query);
		}
		
		try {
			query = "INSERT INTO complain(order_id, user_id, type, complain_content, status) VALUES (?, ?, ?, ?, '未回覆')";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, orderId);
			pstmt.setInt(2, userId);
			pstmt.setString(3, type);
			pstmt.setString(4, complainContent);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error executing query: " + query);
		}
		
	}
	
	// 查看回饋
	public ArrayList<DetailComplain> searchComplain(String account) {
		int userId = 0;
		ArrayList<DetailComplain> dc = new ArrayList<DetailComplain>();
		
		try {
			query = "SELECT user_id FROM user WHERE account = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
			    userId = rs.getInt("user_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error executing query: " + query);
		}
		
		try {
			query = "SELECT complain_id, order_id, complain_content, reply_content, status FROM complain WHERE user_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int complainId = rs.getInt("complain_id");
				int orderId = rs.getInt("order_id");
				String complainContent = rs.getString("complain_content");
				String replyContent = rs.getString("reply_content");
				String status = rs.getString("status");
				if (status.equals("未回覆")) {
					replyContent = "尚未回覆";
				}
				DetailComplain com = new DetailComplain(complainId, orderId, complainContent, replyContent);
				dc.add(com);
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error executing query: " + query);
		}
		
		return dc;
		
	}
}

class ComplainError extends Exception {
	public ComplainError(String message) {
		super(message);
	}
}
