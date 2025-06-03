import java.time.*;
import java.util.*;


public class User {
	private int userId;
	private String name, telephone, account, password, status;
	private LocalDateTime registerDate;
	
	public User() {
		
	}
	
	public User(int userId, String name, String telephone, String account , String password, LocalDateTime registerDate, String status) {
		this.userId = userId;
		this.name = name;
		this.telephone = telephone;
		this.account = account;
		this.password = password;
		this.registerDate = registerDate;
		this.status = status;
	}
	
	public int getUserId() {
	    return userId;
	}

	public void setUserId(int userId) {
	    this.userId = userId;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getTelephone() {
	    return telephone;
	}

	public void setTelephone(String telephone) {
	    this.telephone = telephone;
	}

	public String getAccount() {
	    return account;
	}

	public void setAccount(String account) {
	    this.account = account;
	}

	public String getPassword() {
	    return password;
	}

	public void setPassword(String password) {
	    this.password = password;
	}

	public LocalDateTime getRegisterDate() {
	    return registerDate;
	}

	public void setRegisterDate(LocalDateTime registerDate) {
	    this.registerDate = registerDate;
	}

	public String getStatus() {
	    return status;
	}

	public void setStatus(String status) {
	    this.status = status;
	}

}
