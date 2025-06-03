// RestaurantData.java (新文件)
public class RestaurantData {
    private final int id;
    private final String name;
    private final String type; // 商家類型
    private final String address;
    private final String mapUrl;
    // 可以根據需要添加更多字段，如電話、營業時間等

    public RestaurantData(int id, String name, String type, String address, String mapUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.mapUrl = mapUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getAddress() { return address; }
    public String getMapUrl() { return mapUrl; }

    @Override
    public String toString() { // 主要用於 JComboBox 或 JList 的默認顯示
        return name;
    }
}