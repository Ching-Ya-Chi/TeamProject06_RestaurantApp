import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OrderDB {
	private final String server = "jdbc:mysql://140.119.19.73:3315/";
	private final String database = "TG06";
	private final String url = server + database + "?useSSL=false";
	private final String dbUser = "TG06";
	private final String dbPassword = "bMIEqf";
	private Connection conn;
	private String query;
	
	public OrderDB() {
		try {
			conn = DriverManager.getConnection(url, dbUser, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 找該使用者的訂單列表（大略）(invoiceRough)
	public ArrayList<RoughOrder> searchOrder(String account){
		ArrayList<RoughOrder> ro = new ArrayList<RoughOrder>();
		
		try {
			query = "SELECT user_id FROM user WHERE account = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) {
				rs.close();
				pstmt.close();
				return ro; 
			}

			int userId = rs.getInt("user_id");

			query = "SELECT order_id, finish_time, type, status, location FROM `order` WHERE user_id = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				int orderId = rs.getInt("order_id");
				Timestamp finishTimestamp = rs.getTimestamp("finish_time");
				LocalDateTime finishTime = finishTimestamp.toLocalDateTime();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			    String finishTimeStr = finishTime.format(formatter);
				String type = rs.getString("type");
				if (type.equals("外送")) {
					type = type + "-" + rs.getString("location");
				}
				String status = rs.getString("status");

				RoughOrder order = new RoughOrder(orderId, finishTimeStr, type, status, "", 0);
				ro.add(order);
			}

			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error executing query: " + query);
		}

		return ro;
	}
	
	// 使用者點訂單後印出訂單明細（上半部）(invoiceDetail)
	public RoughOrder getOrderHeaderDetail(int orderId) throws Exception{
		RoughOrder ro = null;
		try {
			query = "SELECT o.order_id, o.finish_time, o.type, o.status, o.location, v.vendor_name, o.money FROM `order` o JOIN vendor v ON v.vendor_id = o.vendor_id WHERE o.order_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, orderId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Timestamp finishTimestamp = rs.getTimestamp("finish_time");
				LocalDateTime finishTime = finishTimestamp.toLocalDateTime();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			    String finishTimeStr = finishTime.format(formatter);
			    
				String type = rs.getString("type");
				if (type.equals("外送")) {
					type = type + "-" + rs.getString("location");
				}
				String status = rs.getString("status");
				String vendorName = rs.getString("vendor_name");
				int money = rs.getInt("money");

				ro = new RoughOrder(orderId, finishTimeStr, type, status, vendorName, money);
			}

			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error executing query: " + query);
		}
		
		return ro;
	}
	
	// 使用者點訂單之後印出訂單明細（下半部）(invoiceDetail)
	public ArrayList<DetailOrder> getOrderItemDetail(int orderId) throws Exception {
		ArrayList<DetailOrder> dor = new ArrayList<DetailOrder>();
		try {
			query = "SELECT name, quantity, price FROM orderdetail WHERE order_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, orderId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString("name");
				int quantity = rs.getInt("quantity");
				int price = rs.getInt("price");
				DetailOrder order = new DetailOrder(name, quantity, price);
				dor.add(order);
			}

			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error executing query: " + query);
		}
		
		return dor;
	}
	
	
	
	
}
