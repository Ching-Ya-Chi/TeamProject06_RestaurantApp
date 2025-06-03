# TeamProject06_RestaurantApp
The final project of Java class, a restaurant app

## 專案簡介 (Project Introduction)

為了解決國立政治大學（政大）周邊餐廳選擇少、午餐尖峰時段排隊耗時的問題，特別是對於有助教課等時間壓力的師生，本專案開發了一個線上訂餐系統。系統旨在讓使用者能**預先點餐、快速取餐**，有效節省寶貴的等待時間，享受更從容的午餐時光。

### 核心功能設計

本系統主要劃分為三大使用者端，各司其職以確保流暢的訂餐體驗：

1.  **使用者端 (User End)**:
    *   透過地圖或列表方式瀏覽政大周邊合作商家及其菜單。
    *   線上預訂餐點，可選擇自取或外送服務。
    *   完成訂單後可對商家及餐點進行評價。

2.  **商家端 (Vendor End)**:
    *   即時查看顧客訂單，並更新訂單處理狀態（如：準備中、可取餐、已送出）。
    *   管理自家菜單內容（新增、修改、刪除品項）及營業相關資訊（如：營業時間、公告）。

3.  **管理者端 (Admin End)**:
    *   審核新進商家的註冊申請。
    *   追蹤平台整體訂單狀況與進度。
    *   管理與維護整個訂餐平台的正常運作及相關設定。

整體流程涵蓋了使用者從**下單**、商家處理訂單、使用者**取餐**，到後續的**評價**，以及管理者進行的**審核與管理**和商家的**訂單流程管理**。

## 編譯與執行需求

### Java 版本
本專案使用 JavaFX，為了確保最佳相容性與功能，**建議使用 Java SE 22 或更新版本的 JDK** 進行編譯與執行。

### Eclipse 環境設定
若您使用 Eclipse IDE 進行編譯與開發，請確保已安裝並在專案中配置以下函式庫：

1.  **JavaFX SDK**:
    *   **必要性**: JavaFX 是本專案圖形使用者介面 (GUI) 的核心。
    *   **取得方式**: 您可以從 [OpenJFX 官方網站](https://openjfx.io/) 下載適合您作業系統與 JDK 版本的 JavaFX SDK。
    *   **Eclipse 設定**:
        1.  將下載的 JavaFX SDK 中的 `lib` 資料夾內所有 JAR 檔案加入到您專案的 `Build Path` (通常是 "Modulepath" 或 "Classpath")。
        2.  設定 Run Configuration 的 VM arguments 以包含 JavaFX 模組，例如：
            ```
            --module-path /path/to/your/javafx-sdk-VERSION/lib --add-modules javafx.controls,javafx.fxml
            ```
            (請將 `/path/to/your/javafx-sdk-VERSION/lib` 替換成您實際的 JavaFX SDK 路徑，並根據專案需求調整 `--add-modules` 的內容。)

2.  **MySQL Connector/J**:
    *   **必要性**: 用於連接並操作 MySQL 資料庫。
    *   **取得方式**: 您可以從 [MySQL 官方網站](https://dev.mysql.com/downloads/connector/j/) 下載最新的 MySQL Connector/J JAR 檔案。
    *   **Eclipse 設定**: 將下載的 `mysql-connector-java-VERSION.jar` 檔案加入到您專案的 `Build Path` (通常是 "Classpath")。

## 如何編譯 (Eclipse)

1.  確保您的 Eclipse IDE 已安裝並設定好上述 JDK 版本。
2.  Clone 或下載本專案到您的本地端。
3.  在 Eclipse 中，選擇 `File > Import...`。
4.  選擇 `General > Existing Projects into Workspace` 並點擊 `Next`。
5.  選擇專案的根目錄，然後點擊 `Finish`。
6.  根據上述「Eclipse 環境設定」的指引，設定 JavaFX SDK 與 MySQL Connector/J。
7.  解決任何可能的相依性問題後，即可編譯並執行專案。

## 貢獻者 (Contributors)

感謝以下成員對本專案的貢獻：

*   **Jimxpm** ([@Jimxpm](https://github.com/Jimxpm))
    *   管理者端的前後端設計
    *   流程設計
    *   專案測試
*   **Charles-Hu-TW** ([@Charles-Hu-TW](https://github.com/Charles-Hu-TW))
    *   商家端的前後端設計
    *   資料庫的設計管理
*   **arica-ee** ([@arica-ee](https://github.com/arica-ee))
    *   使用者端的前後端設計
    *   資料庫的設計管理
*   **Ching-Ya-Chi** ([@Ching-Ya-Chi](https://github.com/Ching-Ya-Chi))
    *   使用者的前後端設計
    *   系統整合

---
