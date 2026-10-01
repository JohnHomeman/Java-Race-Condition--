# 執行計畫: [step1_inti_Java_spring_boots] 初始化 Java Spring Boot 基礎框架

## 1. 實作階段 (Implementation Stages)

### Stage 1: 基礎架構與環境組態 (Build & Config)
- [x] [NEW] `pom.xml`: 依據 Spec §6.1 與 Spec §6.2 建立 Maven 與 Java 21 / Spring Boot 3 依賴設定。
- [x] [NEW] `src/main/resources/application.yml`: 依據 Spec §2 建立應用程式基礎配置與名稱。
- [x] [NEW] `src/main/java/com/racecondition/Application.java`: 建立 Spring Boot 啟動進入點。
- [x] [NEW] `src/main/java/com/racecondition/config/AppConfig.java`: 依據 Spec §2 建立系統全域組態類別。
- **✅ Checkpoint**: 專案可透過 Maven 成功編譯且主應用程式可正常啟動。

### Stage 2: 傳輸物件與例外處理 (DTO & Advice)
- [x] [NEW] `src/main/java/com/racecondition/dto/ApiResponse.java`: 依據 Spec §3 與 Spec §5 實作標準化回應封裝。
- [x] [NEW] `src/main/java/com/racecondition/dto/HealthData.java`: 依據 Spec §3 與 Spec §5.1 實作健康檢查資料物件。
- [x] [NEW] `src/main/java/com/racecondition/exception/GlobalExceptionHandler.java`: 依據 Spec §3 與 Spec §6.3 實作全域例外攔截器。
- **✅ Checkpoint**: DTO 與例外處理器可成功編譯。

### Stage 3: 控制器與路由 (Controller & Route)
- [x] [NEW] `src/main/java/com/racecondition/controller/HealthController.java`: 依據 Spec §2 與 Spec §5.1 實作健康檢查端點。
- **✅ Checkpoint**: Controller 編譯通過且健康檢查端點路由註冊完成。

### Stage 4: 單元與整合測試 (Unit & Integration Test)
- [x] [NEW] `src/test/java/com/racecondition/controller/HealthControllerTest.java`: 依據 Spec §7.1 撰寫測試程式以驗證健康探針與例外攔截。
- **✅ Checkpoint**: 執行 Maven 測試並通過所有 Spec §7.1 的 Table-Driven 測項。

## 2. 風險與注意事項 (Risks & Notes)
- [Stage 1 相關] Java 21 LTS 執行環境：編譯與執行環境須確保 JDK 21 正確配置 (引用 Spec §6.1)。
- [Stage 2 相關] 例外處理器宣告優先級：需確保特定例外（如 `HttpRequestMethodNotSupportedException`）優先於頂層 `Exception` 攔截，避免錯誤代碼失真 (引用 Spec §6.3)。

## 3. 驗證動作 (Verification Actions)

### 3.1 自動化測試指令
> 測試數據來源：Spec §7.1
```bash
mvn test
```

### 3.2 手動驗證步驟 (cURL/腳本)
> 驗收情境來源：Spec §7.2
> 腳本存放：`ai_context/step1_inti_Java_spring_boots/workspace/`，命名遵循 `test_[序號]_[環境/測項].md` 規範。
1. 啟動後端服務：`mvn spring-boot:run`。
2. 執行健康檢查驗證：發送 `GET /api/v1/health` 請求，確認狀態碼與資料欄位符合 Spec §7.2 情境 A。
3. 執行不支援方法攔截驗證：發送 `POST /api/v1/health` 請求，確認狀態碼與錯誤訊息符合 Spec §7.2 情境 B。
