class CartItem {
    MenuItemData itemData;
    int quantity;
    String customization;

    public CartItem(MenuItemData itemData, int quantity, String customization) {
        this.itemData = itemData;
        this.quantity = quantity;
        this.customization = customization;
    }

    @Override
    public String toString() { // For simple display
        return String.format("%s (%s) x%d - %s [客製化: %s]",
                itemData.getName(), itemData.getPrice(), quantity, itemData.getID(), customization.isEmpty() ? "無" : customization);
    }
}
