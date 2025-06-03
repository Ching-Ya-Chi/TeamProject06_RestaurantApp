// OrderItem.java
import java.math.BigDecimal; // For precise currency calculations

public class OrderItem {
    private final String id;
    private final String name;
    private final BigDecimal unitPrice; // 單價
    private int quantity;
    private final String customization;
    private final String restaurantName; // 屬於哪個餐廳的訂單

    public OrderItem(String id, String name, BigDecimal unitPrice, int quantity, String customization, String restaurantName) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.customization = customization;
        this.restaurantName = restaurantName;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public String getCustomization() { return customization; }
    public String getRestaurantName() { return restaurantName; }


    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    @Override
    public String toString() {
        return String.format("%dx %s (%s) - 客製化: %s [小計: $%.2f]",
                quantity, name, unitPrice.toString(), customization.isEmpty() ? "無" : customization, getTotalPrice());
    }
}