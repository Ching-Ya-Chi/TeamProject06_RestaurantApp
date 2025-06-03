import java.time.LocalDateTime;

public class RoughOrder {
    private int orderId;
    private String finishTime;
    private String type;
    private String status;
    private String vendorName;
    private int money;

    public RoughOrder(int orderId, String finishTime, String type, String status, String vendorName, int money) {
        this.orderId = orderId;
        this.finishTime = finishTime;
        this.type = type;
        this.status = status;
        this.vendorName = vendorName;
        this.money = money;
    }
    
    public RoughOrder() {
    	
    }

    public int getOrderId() {
        return orderId;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
    
    public String getVendorName() {
    	return vendorName;
    }
    
    public int getMoney() {
    	return money;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setVendorName(String vendorName) {
    	this.vendorName = vendorName;
    }
    
    public void setMoney(int money) {
    	this.money = money;
    }
}

