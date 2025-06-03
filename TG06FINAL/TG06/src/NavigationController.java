
public interface NavigationController {
	void showMainAppLayout(); // Navigates to the main screen with map/list
    void showRestaurantList(); // Specifically shows the restaurant list in the main app layout
    void showMap(); // Specifically shows the map in the main app layout

    void showMenuPage(String restaurantName, int restaurantId);
    void showCriticizePage(String restaurantName, int restaurantId);
    void showOrderPage(MenuItemData itemData, String restaurantName,int restautantId);

    void navigateBackToMenu(String restaurantName,int restaurantId); // From Order page back to Menu page
    void navigateBackToRestaurantList();
    void addItemToCart(OrderItem item, int restaurantId); // 假設 OrderItem 內部也會存 restaurantId
    void showCartPage(String restaurantName, int restaurantId);
    void showUserProfilePage(); // <--- 新增方法

}
