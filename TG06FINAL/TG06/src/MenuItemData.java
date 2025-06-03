class MenuItemData {
    private int id;
    private int vendorID;
    private String name;
    private int price;
    private String description;
    private String status;
    private String pictureUrl;

    public MenuItemData(int id, int vendorId, String name, int price, String description, String pictureUrl, String status) {
        this.id = id;
        this.vendorID = vendorId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.status = status;
        this.pictureUrl = pictureUrl;
    }
    int getID() {
    	return id;
    }
    int getVendorID() {
    	return vendorID;
    }
    String getName() {
    	return name;
    }
    int getPrice() {
    	return price;
    }
    String getDescription() {
    	return description;
    }
    String getStatus() {
    	return status;
    }
    String getPictureUrl() {
    	return  pictureUrl;
    }
}
