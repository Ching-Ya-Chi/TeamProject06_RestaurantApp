// MapDisplayPanel.java
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
// 不需要 URLEncoder 了，因為 URL 結構固定

public class MapDisplayPanel extends JPanel {
    private JFXPanel jfxMapPanel;
    private WebView webView;
    private WebEngine webEngine;
    private StackPane jfxRootPane;
    private VBox statusOverlay;
    private ProgressIndicator loadingIndicator;
    private Button refreshButton;

    private final Color WHITE_BACKGROUND = new Color(238,238,238);
    private final javafx.scene.paint.Color JFX_WHITE_BACKGROUND = javafx.scene.paint.Color.WHITE;
 // --- 地圖參數 ---
  
    private String targetLatitude = "24.987430"; 
    private String targetLongitude = "121.577068"; 
    private int targetZoomLevel = 19; // 初始縮放級別
    private String targetMapType = "m"; // 'm' for map, 'k' for satellite, 'h' for hybrid
    private String targetLanguage = "zh-TW";
    private String targetRegion = "TW";
    private String targetCID = "11424795872273888519"; // 地點的 Client ID，非常重要


    // 直接使用 "詳細地圖" 的 URL
    private String searchQuery;
    private String currentMapUrl;

    private Timeline timeoutTimeline;
    private static final double LOAD_TIMEOUT_SECONDS = 25.0; // 可以適當增加超時

    public MapDisplayPanel() {
    	buildMapUrl(); // 根據初始參數構建 URL
        setLayout(new BorderLayout());
        setBackground(WHITE_BACKGROUND);
        jfxMapPanel = new JFXPanel();
        add(jfxMapPanel, BorderLayout.CENTER);
        Platform.runLater(this::initFX);
    }
    private void buildMapUrl() {
        try {
            String encodedQuery = "政治大學附近餐廳";
            if (this.searchQuery != null && !this.searchQuery.isEmpty()) {
                encodedQuery = URLEncoder.encode(this.searchQuery, StandardCharsets.UTF_8.name());
            }

            // 注意：當 q 參數存在時，ll 和 z 參數的行為可能會有所不同。
            // 有時 q 參數會主導視圖，ll 和 z 可能作為初始參考或被部分忽略。
            // 你需要測試以找到最佳組合。
            // 為了最大化 q 的效果，有時可以考慮不傳遞 ll，讓 q 自己決定中心。
            // 但如果想在特定區域內搜索，ll 和 z 仍然有用。
            this.currentMapUrl = String.format(
                "https://maps.google.com/maps?ll=%s,%s&z=%d&t=%s&hl=%s&gl=%s&mapclient=embed&q=%s",
                targetLatitude,
                targetLongitude,
                targetZoomLevel,
                targetMapType,
                targetLanguage,
                targetRegion,
                encodedQuery // 將編碼後的查詢放在這裡
            );
             System.out.println("Constructed Map URL with Query: " + this.currentMapUrl);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            // Fallback URL
            this.currentMapUrl = String.format(
                "https://maps.google.com/maps?ll=%s,%s&z=%d&t=%s&hl=%s&gl=%s&mapclient=embed",
                targetLatitude, targetLongitude, targetZoomLevel, targetMapType, targetLanguage, targetRegion
            );
        }
    }


    private void initFX() {
        webView = new WebView();
        webEngine = webView.getEngine();

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50);
        loadingIndicator.setVisible(false);

        Label errorLabel = new Label();
        Button retryButton = new Button("重試 (Retry)");
        retryButton.setOnAction(event -> attemptLoadCurrentMap());

        statusOverlay = new VBox(10, errorLabel, retryButton);
        statusOverlay.setAlignment(Pos.CENTER);
        statusOverlay.setStyle("-fx-background-color: rgba(200, 200, 200, 0.8); -fx-padding: 20px;");
        statusOverlay.setVisible(false);

        refreshButton = new Button("刷新");
        try {
            InputStream iconStream = getClass().getResourceAsStream("/icons/refresh_icon.png");
            if (iconStream != null) {
                Image refreshIconImage = new Image(iconStream, 20, 20, true, true);
                ImageView refreshIconView = new ImageView(refreshIconImage);
                refreshButton.setGraphic(refreshIconView);
                refreshButton.setText("");
                refreshButton.setStyle("-fx-background-color: transparent; -fx-padding: 5px;");
                refreshButton.setCursor(Cursor.HAND);
            } else {
                 refreshButton.setText("刷新");
            }
        } catch (Exception e) {
            System.err.println("Failed to load refresh icon: " + e.getMessage());
            refreshButton.setText("刷新");
        }
        refreshButton.setOnAction(event -> {
            // webEngine.reload(); // 直接 reload 當前頁面
            attemptLoadCurrentMap(); // 或者使用我們的加載邏輯
        });

        jfxRootPane = new StackPane(webView, loadingIndicator, statusOverlay, refreshButton);
        StackPane.setAlignment(loadingIndicator, Pos.CENTER);
        StackPane.setAlignment(statusOverlay, Pos.CENTER);
        StackPane.setAlignment(refreshButton, Pos.TOP_RIGHT);
        StackPane.setMargin(refreshButton, new Insets(10));

        Scene scene = new Scene(jfxRootPane, JFX_WHITE_BACKGROUND);
        jfxMapPanel.setScene(scene);

        setupLoadListener();
        setupTimeoutMechanism();
        attemptLoadCurrentMap();
    }

    private void setupTimeoutMechanism() {
        timeoutTimeline = new Timeline(new KeyFrame(Duration.seconds(LOAD_TIMEOUT_SECONDS), event -> {
            Platform.runLater(() -> {
                if (webEngine.getLoadWorker().isRunning()) {
                    // webEngine.getLoadWorker().cancel(); // 不一定有效，不如直接停止
                    webEngine.load("about:blank"); // 停止當前加載
                    showStatusMessage("地圖加載超時，請檢查您的網絡連接或重試。", true);
                }
            });
        }));
        timeoutTimeline.setCycleCount(1);
    }

    private void setupLoadListener() {
        webEngine.getLoadWorker().stateProperty().addListener(
            (ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
                System.out.println("Map Page Load State: " + newState); // 修改日誌前綴
                switch (newState) {
                    case SCHEDULED:
                    case RUNNING:
                        loadingIndicator.setVisible(true);
                        refreshButton.setDisable(true);
                        hideStatusMessage();
                        timeoutTimeline.playFromStart();
                        break;
                    case SUCCEEDED:
                        loadingIndicator.setVisible(false);
                        refreshButton.setDisable(false);
                        hideStatusMessage();
                        timeoutTimeline.stop();
                        // 檢查是否真的加載了地圖，而不是Google的錯誤頁面 (較難自動判斷)
                        // 可以嘗試檢查頁面標題或特定元素，但會比較複雜
                        System.out.println("Page loaded: " + webEngine.getLocation());
                        break;
                    case FAILED:
                        loadingIndicator.setVisible(false);
                        refreshButton.setDisable(false);
                        timeoutTimeline.stop();
                        Throwable exception = webEngine.getLoadWorker().getException();
                        String errorMessage = "地圖頁面加載失敗。"; // 修改錯誤消息
                        if (exception != null) {
                            System.err.println("Map page loading error: " + exception.getMessage());
                            // 可以根據 exception.getMessage() 提供更具體的錯誤
                            // 例如，如果包含 "net::ERR_INTERNET_DISCONNECTED" -> "網絡未連接"
                            // 如果是 "net::ERR_NAME_NOT_RESOLVED" -> "無法解析服務器地址"
                        }
                        showStatusMessage(errorMessage, true);
                        break;
                    case CANCELLED:
                        loadingIndicator.setVisible(false);
                        refreshButton.setDisable(false);
                        timeoutTimeline.stop();
                        break;
                }
            });
    }


    private void attemptLoadCurrentMap() {
        Platform.runLater(() -> {
            hideStatusMessage();
            loadingIndicator.setVisible(true);
            refreshButton.setDisable(true);
            // 直接加載 URL，不再是 loadContent
            System.out.println("Attempting to load map URL: " + currentMapUrl);
            webEngine.load(currentMapUrl);
        });
    }

    // loadMapUrl 方法現在可以更改整個頁面的 URL
    public void loadMapUrl(String newMapPageUrl) {
        this.currentMapUrl = newMapPageUrl;
        if (jfxMapPanel.getScene() != null) {
            attemptLoadCurrentMap();
        } else {
            System.out.println("MapDisplayPanel: FX components not ready, URL will be loaded on init.");
        }
    }

    private void showStatusMessage(String message, boolean showRetryButton) {
        Platform.runLater(() -> {
            if (statusOverlay != null) {
                ((Label) statusOverlay.getChildren().get(0)).setText(message);
                statusOverlay.getChildren().get(1).setVisible(showRetryButton);
                statusOverlay.setVisible(true);
            }
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }
            webView.setVisible(false);
            refreshButton.setVisible(false);
        });
    }

    private void hideStatusMessage() {
        Platform.runLater(() -> {
            if (statusOverlay != null) {
                statusOverlay.setVisible(false);
            }
            webView.setVisible(true);
            refreshButton.setVisible(true);
        });
    }
}
