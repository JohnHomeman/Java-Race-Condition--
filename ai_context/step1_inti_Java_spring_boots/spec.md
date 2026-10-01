# step1_inti_Java_spring_boots: 初始化 Java Spring Boot 基礎框架

## 1. 需求背景 (Background)
本專案為全新存放庫，目前尚未建立後端應用程式骨幹與基礎合約。為提供後續高併發與競態條件實驗環境，本次需求建立核心後端服務，定義服務健康探測介面，並規範標準化 API 回應與全域例外處理規則。

## 2. 系統設計與影響範圍 (System Design)
- **核心目標**：建立 Java 21 與 Spring Boot 3 後端基礎框架，提供健康檢查探針、統一 API 回應封裝與全域例外攔截機制。
- **影響分層**：

| 分層 | 受影響模組（職責描述） | 變更類型 |
| :--- | :--- | :--- |
| Controller | HealthController — 提供服務運行狀態健康檢查 | 新增 |
| Advice | GlobalExceptionHandler — 全域攔截未預期或已知異常並轉換為標準錯誤回應 | 新增 |
| Config | AppConfig — 管理系統全域基礎設定與環境組態 | 新增 |
| DTO | ApiResponse — 定義全域標準化回應封裝結構 | 新增 |

## 3. 核心業務邏輯 (Core Logic)
- **健康探測邏輯**：
  - 探針驗證服務運作狀態，回傳服務名稱、當前系統時間戳記與運行狀態（`UP`）。
- **統一回應封裝邏輯**：
  - 成功請求：HTTP 狀態碼 `200`，業務代碼 `code: 0`，回應訊息 `message: "success"`，攜帶業務資料物件 `data`。
  - 失敗請求：依錯誤類型對應 HTTP 狀態碼（400, 404, 405, 500），非零業務代碼 `code: <ErrorCode>`，錯誤描述 `message: <ErrorMessage>`，`data: null`。
- **全域例外攔截規則**：
  - 參數校驗失敗（`MethodArgumentNotValidException`）：回傳 HTTP `400`，錯誤代碼 `1001`。
  - 不支援的 HTTP 方法（`HttpRequestMethodNotSupportedException`）：回傳 HTTP `405`，錯誤代碼 `1002`。
  - 資源路由不存在（`NoResourceFoundException` / `NoHandlerFoundException`）：回傳 HTTP `404`，錯誤代碼 `1003`。
  - 未攔截之系統例外（`Exception`）：回傳 HTTP `500`，錯誤代碼 `9999`，不洩漏伺服器內部堆疊，回傳通用錯誤訊息。

## 4. 資料架構 (Data Schema)
本階段專注於基礎框架初始化與核心生命週期建立，暫無業務實體資料表之 DDL 變動。

## 5. 介面合約 (API Contract)

### 5.1 REST API — 服務健康檢查
- **Endpoint**: `GET /api/v1/health`
- **Request Body**: 無
- **Success Response (200)**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "status": "UP",
    "service": "java-race-condition",
    "timestamp": 1727154000000
  }
}
```
- **Error Responses**:

| HTTP Code | Error Code | 條件 | 回傳 Message |
| :--- | :--- | :--- | :--- |
| 405 | 1002 | 請求採用不支援之 HTTP 方法（如 POST） | method not allowed |
| 500 | 9999 | 內部伺服器未知錯誤 | internal server error |

## 6. 技術決策與權衡 (Design Decisions)

### 6.1 語言版本與框架選型
- **選項 A**：Java 17 LTS + Spring Boot 3.x
- **選項 B**：Java 21 LTS + Spring Boot 3.x
- **決策**：選擇 B。本專案核心為高併發與競態條件實驗，Java 21 原生支援虛擬執行緒（Virtual Threads），具備高吞吐與輕量執行緒調度能力。
- **風險**：建置與執行環境須全面配置 JDK 21。

### 6.2 建置管理工具選型
- **選項 A**：Gradle
- **選項 B**：Maven
- **決策**：選擇 B (Maven)。宣告式相依管理清晰穩定，配置規範統一，降低跨環境建置差異。
- **風險**：增量編譯速度略慢於 Gradle，但在中小型專案影響極小。

### 6.3 統一例外封裝策略
- **選項 A**：各 Controller 獨立捕捉並回傳 `ResponseEntity`。
- **選項 B**：使用 `@RestControllerAdvice` 集中攔截並轉換為 `ApiResponse`。
- **決策**：選擇 B。確保系統在任何未預期例外或合法請求下，皆具備一致的 JSON 回應規格。
- **風險**：需確保例外處理器的宣告優先級，避免特殊例外被最上層 `Exception` 攔截器提前捕獲。

## 7. 驗證目標與測試數據 (Verification Goals & Test Data)

### 7.1 單元測試測項 (Table-Driven)

| Case | 輸入 (Input) | 條件 (Condition) | 期望輸出 (Expected) |
| :--- | :--- | :--- | :--- |
| 健康檢查探針成功 | `GET /api/v1/health` | 服務正常運作 | `status: 200`, `code: 0`, `data.status: "UP"` |
| 不支援方法攔截 | `POST /api/v1/health` | 呼叫不支援之 HTTP 方法 | `status: 405`, `code: 1002`, `message: "method not allowed"` |
| 不存在端點攔截 | `GET /api/v1/unknown` | 請求未定義之路由 | `status: 404`, `code: 1003` |

### 7.2 業務驗收情境 (Acceptance Criteria)
- **情境 A：服務啟動與探針檢測**：服務正常啟動後，呼叫 `GET /api/v1/health`，期望 HTTP 回傳碼為 `200`，且 JSON `data.status` 為 `"UP"`、`data.service` 為 `"java-race-condition"`。
- **情境 B：不支援之請求方法**：呼叫 `POST /api/v1/health`，期望 HTTP 回傳碼為 `405`，且 JSON 包含 `code: 1002` 與 `message: "method not allowed"`。

## 8. 預期效益 (Expected Benefits)
- 完成 Java 21 + Spring Boot 3 骨幹架構建置。
- 確立全域一致的 API 回應與錯誤合約標準。
