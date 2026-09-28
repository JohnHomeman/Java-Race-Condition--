# Step1_init_java_spring_boots: 初始化 Java Spring Boot 框架

## 1. 需求背景 (Background)
本專案為全新存放庫，尚無任何後端服務框架與基底程式碼。為了後續能安全且精準地進行高併發情境與競態條件（Race Condition）實驗，需要建立標準化的後端服務骨幹。本次任務將定義基於 Java 21 與 Spring Boot 3 的核心服務架構，規範服務健康檢查、統一回應合約與全域例外處理機制，作為後續併發業務模組的開發基石。

## 2. 系統設計與影響範圍 (System Design)
- **核心目標**：建立 Java 21 與 Spring Boot 3 後端基礎框架，提供健康檢查介面、統一 API 回應結構與全域異常攔截能力。
- **影響分層**：

| 分層 | 受影響模組（職責描述） | 變更類型 |
| :--- | :--- | :--- |
| Handler / Controller | HealthController — 提供服務運行狀態與連線探測介面 | 新增 |
| Advice / Filter | GlobalExceptionHandler — 統一攔截系統例外並轉換為標準錯誤回應 | 新增 |
| Configuration | AppConfig — 管理系統全域基礎設定與時區編碼 | 新增 |
| Model / DTO | ApiResponse — 定義全域標準化回應封裝結構 | 新增 |

## 3. 核心業務邏輯 (Core Logic)
- **健康檢查判定邏輯**：
  - 探針確認服務啟動狀態，回傳服務名稱、環境標籤、目前系統時間戳記與運行狀態（`UP`）。
- **統一回應封裝邏輯**：
  - 成功請求：`HTTP 200 OK`，狀態碼 `code: 0`，訊息 `message: "success"`，攜帶業務資料物件 `data`。
  - 異常請求：對應 HTTP Status Code（如 400, 404, 500），非零業務錯誤碼 `code: <ErrorCode>`，明確錯誤訊息 `message: <ErrorMessage>`，`data: null`。
- **全域例外處理規則**：
  - 攔截 `MethodArgumentNotValidException` / 參數校驗失敗：回傳 `HTTP 400`，錯誤碼 `1001`。
  - 攔截 `HttpRequestMethodNotSupportedException` / 不支援的 HTTP 方法：回傳 `HTTP 405`，錯誤碼 `1002`。
  - 攔截未受檢之全域例外 `Exception`：回傳 `HTTP 500`，錯誤碼 `9999`，隱藏內部敏感堆疊資訊，回傳通用錯誤提示。

## 4. 資料架構 (Data Schema)
本階段為基礎框架初始化，專注於服務骨幹、生命週期與 API 合約之建立，本階段暫無業務實體資料表之 DDL 變更。

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
| 500 | 9999 | 內部未知系統異常 | internal server error |

### 5.2 REST API — 服務 Ping 測試
- **Endpoint**: `GET /api/v1/ping`
- **Request Body**: 無
- **Success Response (200)**:
```json
{
  "code": 0,
  "message": "success",
  "data": "pong"
}
```
- **Error Responses**:

| HTTP Code | Error Code | 條件 | 回傳 Message |
| :--- | :--- | :--- | :--- |
| 405 | 1002 | 使用不支援的 HTTP 方法（例如 POST） | method not allowed |

## 6. 技術決策與權衡 (Design Decisions)

### 6.1 執行環境與框架選型
- **選項 A**：Java 17 LTS + Spring Boot 3.x
- **選項 B**：Java 21 LTS + Spring Boot 3.x
- **決策**：選擇 B。本專案核心目標為併發與競態條件實驗，Java 21 原生支援虛擬執行緒（Virtual Threads - Project Loom），能提供更高效的高併發乘載能力與現代語言特性。
- **風險**：需確保執行環境與 CI/CD 容器均具備 JDK 21 執行時期。

### 6.2 建置工具選型
- **選項 A**：Gradle (Kotlin DSL / Groovy DSL)
- **選項 B**：Maven (`pom.xml`)
- **決策**：選擇 B (Maven)。Maven 結構嚴謹且標準化，相依套件宣告單純透明，降低團隊協作與跨平台建置的配置複雜度。
- **風險**：大型專案多模組建置速度相較 Gradle 略慢，但本專案規模適中，影響甚微。

### 6.3 統一例外處理機制
- **選項 A**：各 Controller 自行處理例外並回傳 `ResponseEntity`。
- **選項 B**：使用 `@RestControllerAdvice` 搭配全域標準化回應物件。
- **決策**：選擇 B。統一收斂錯誤碼與回應格式，確保所有 API 在任何異常情境下輸出一致的合約規格。
- **風險**：需嚴格維護全域例外捕捉的優先順序，避免特殊業務例外被萬用例外捕捉吞沒。

## 7. 驗證目標與測試數據 (Verification Goals & Test Data)

### 7.1 單元測試測項 (Table-Driven)

| Case | 輸入 (Input) | 條件 (Condition) | 期望輸出 (Expected) |
| :--- | :--- | :--- | :--- |
| 健康檢查正常響應 | `GET /api/v1/health` | 服務正常啟動 | `status: 200`, `code: 0`, `data.status: "UP"` |
| Ping 探測正常響應 | `GET /api/v1/ping` | 服務正常啟動 | `status: 200`, `code: 0`, `data: "pong"` |
| 不支援方法錯誤攔截 | `POST /api/v1/ping` | 呼叫不支援的 HTTP 方法 | `status: 405`, `code: 1002`, `message: "method not allowed"` |
| 路由不存在錯誤攔截 | `GET /api/v1/not-found-route` | 請求不存在的端點 | `status: 404`, `code: 1003` |

### 7.2 業務驗收情境 (Acceptance Criteria)
- **情境 A：基礎服務啟動與探針檢測**：啟動 Spring Boot 應用程式，發送 `GET /api/v1/health`，期望 HTTP 回傳碼為 `200`，且 JSON `data.status` 為 `"UP"`、`data.service` 為 `"java-race-condition"`。
- **情境 B：全域例外攔截與合約格式**：發送非 GET 方法至 `/api/v1/ping`，期望 HTTP 回傳碼為 `405`，且 JSON 包含 `code: 1002` 與 `message: "method not allowed"`。

## 8. 預期效益 (Expected Benefits)
- 建立具備現代高併發潛力的 Java 21 + Spring Boot 3 骨幹。
- 統一所有 API 請求與錯誤回應結構，降低後續模組開發與除錯成本。

## 9. Backlog / Future Scope（非本次實作範圍）
- **商品庫存搶購模組（下期）**：設計商品扣庫存邏輯，模擬多執行緒高併發下的超賣問題。
- **併發控制方案（下期）**：實作資料庫悲觀鎖（Pessimistic Lock）、樂觀鎖（Optimistic Lock - Version 機制）與 Redis 分散式鎖。
