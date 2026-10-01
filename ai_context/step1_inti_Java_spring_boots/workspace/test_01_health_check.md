# 驗收測試報告: 健康檢查探針與全域例外攔截驗證

- **工單號**: `step1_inti_Java_spring_boots`
- **測試項目**: 服務健康探測與全域例外攔截
- **測試環境**: Local (Java 21, Spring Boot 3.3.4, Port 8080)
- **測試時間**: 2026-10-01

---

## 1. 自動化測試 (Maven Test)

執行指令：
```bash
mvn test
```

測試結果：
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.racecondition.controller.HealthControllerTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.183 s -- in com.racecondition.controller.HealthControllerTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 2. 業務情境驗收 (cURL 手動驗證)

### 情境 A：服務啟動與健康檢查探針檢測
- **驗證指令**:
  ```bash
  curl -i -s http://localhost:8080/api/v1/health
  ```
- **實際回應**:
  ```http
  HTTP/1.1 200 
  Content-Type: application/json

  {"code":0,"message":"success","data":{"status":"UP","service":"java-race-condition","timestamp":1790838578045}}
  ```
- **判定結果**: 符合 Spec §7.2 情境 A 預期（HTTP 200，code 0，status UP，service java-race-condition）。

### 情境 B：不支援之請求方法攔截
- **驗證指令**:
  ```bash
  curl -i -s -X POST http://localhost:8080/api/v1/health
  ```
- **實際回應**:
  ```http
  HTTP/1.1 405 
  Content-Type: application/json

  {"code":1002,"message":"method not allowed","data":null}
  ```
- **判定結果**: 符合 Spec §7.2 情境 B 預期（HTTP 405，code 1002，message "method not allowed"）。

### 情境 C：不存在之路由攔截
- **驗證指令**:
  ```bash
  curl -i -s http://localhost:8080/api/v1/unknown
  ```
- **實際回應**:
  ```http
  HTTP/1.1 404 
  Content-Type: application/json

  {"code":1003,"message":"resource not found","data":null}
  ```
- **判定結果**: 符合 Spec §7.1 測項預期（HTTP 404，code 1003，message "resource not found"）。
