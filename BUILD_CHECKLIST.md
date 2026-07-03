# 📋 編譯打包 & Git 提交完成檢查清單

## ✅ Git 提交狀態

### 已提交檔案 (17 個)

```
📁 專案根目錄
├── .gitignore                    ✓ Git 忽略規則
├── README.md                     ✓ 專案說明
├── ENV_SETUP.md                  ✓ 環境變數指南
├── MAVEN_GUIDE.md                ✓ Maven 編譯指南
├── pom.xml                       ✓ Maven 依賴配置
│
├── 📁 src/main/java/com/example/library/
│   ├── LibraryApplication.java   ✓ Spring Boot 主程式
│   ├── config/
│   │   └── DatabaseConfig.java   ✓ 手動 JDBC 連線配置
│   ├── entity/
│   │   ├── Book.java             ✓ 書籍實體 + 驗證
│   │   ├── User.java             ✓ 讀者實體 + 驗證
│   │   └── BorrowRecord.java     ✓ 借閱紀錄實體 + 驗證
│   ├── dao/
│   │   ├── BookDAO.java          ✓ 書籍 DAO (8 個方法)
│   │   ├── UserDAO.java          ✓ 讀者 DAO (10 個方法)
│   │   └── BorrowRecordDAO.java  ✓ 借閱紀錄 DAO (12 個方法)
│   ├── service/
│   │   └── LibraryService.java   ✓ 業務邏輯 + 交易控制
│   └── controller/
│       └── LibraryController.java ✓ REST API (12 個端點)
│
├── 📁 src/main/resources/
│   ├── application.properties    ✓ Spring Boot 配置
│   └── static/
│       └── index.html            ✓ 前端雙控制台網頁
```

### Git 提交信息

```
Commit: 8e8ebd6
Author: Copilot Developer
Date: 2026-07-03

完成圖書館管理系統完整版本
- ACID 交易控制完全展現
- 防 SQL 注入 + XSS 防護
- 12 個 RESTful API 端點
- 雙控制台前端網頁
```

---

## 🔧 Maven 編譯準備

### 系統環境檢查

```bash
# 1. 檢查 Maven 版本
mvn --version
# 預期: Apache Maven 3.8+

# 2. 檢查 Java 版本
java -version
# 預期: Java 17+

# 3. 進入專案根目錄
cd "c:\Users\20260612\AI\Spring Boot"

# 4. 驗證 pom.xml 存在
test -f pom.xml && echo "✓ pom.xml 存在"
```

---

## 📦 編譯打包步驟

### Step 1️⃣: 清空並編譯

```bash
mvn clean package -DskipTests
```

**預期輸出（最後幾行）：**
```
[INFO] Building jar: target/library-management-1.0.0.jar
[INFO] BUILD SUCCESS
[INFO] Total time: 2 minutes 45 seconds
```

### Step 2️⃣: 驗證編譯結果

```bash
# 檢查 target 目錄
ls -la target/*.jar

# 預期結果:
# -rw-r--r--  library-management-1.0.0.jar (45 MB)
```

### Step 3️⃣: JAR 檔案驗證

```bash
# 檢查 JAR 內容
jar tf target/library-management-1.0.0.jar | head -20

# 預期: 能看到以下結構
# com/example/library/LibraryApplication.class
# com/example/library/entity/Book.class
# ... 等等
```

---

## 🚀 本地運行測試

### 設定環境變數

```bash
# PowerShell
$env:DB_URL="jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="admin"
$env:DB_PASSWORD="elaine1031"

# 或 Command Prompt
set DB_URL=jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
set DB_USERNAME=admin
set DB_PASSWORD=elaine1031
```

### 運行應用

```bash
java -jar target/library-management-1.0.0.jar
```

**預期輸出：**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

2026-07-03 23:31:56.123  INFO LibraryApplication : Starting LibraryApplication v1.0.0
2026-07-03 23:31:58.456  INFO LibraryApplication : Started LibraryApplication in 2.333 seconds
```

### 驗證應用運行

```bash
# 開啟新 Terminal/PowerShell，執行:
curl http://localhost:8080/api/health

# 預期回應:
{
  "status": "UP",
  "service": "Library Management System",
  "timestamp": 1688329616123
}
```

### 訪問前端頁面

開啟瀏覽器訪問：`http://localhost:8080`

**預期看到：**
- ✓ Bootstrap 5 界面
- ✓ 「讀者操作端主控台」分頁
- ✓ 書籍搜尋欄位
- ✓ 「系統管理員主控台」分頁

---

## 🌐 Git 遠端推送（如已連線 GitHub）

### 連結遠端倉庫

```bash
# 假設您的 GitHub 倉庫為: https://github.com/username/library-management

git remote add origin https://github.com/username/library-management.git
git branch -M main
git push -u origin main
```

### 驗證推送

```bash
# 檢查遠端
git remote -v

# 預期:
# origin  https://github.com/username/library-management.git (fetch)
# origin  https://github.com/username/library-management.git (push)
```

---

## ❌ 常見問題排除

### Q1: mvn command not found
**解決方案：**
- ✓ 確認 Maven 已安裝
- ✓ 檢查環境變數 `M2_HOME` 和 `Path`
- ✓ 重新啟動 Terminal

### Q2: BUILD FAILURE - Dependency not found
**解決方案：**
```bash
# 清空 Maven 快取
rm -rf ~/.m2/repository
# 重新執行編譯
mvn clean install
```

### Q3: 資料庫連線失敗
**解決方案：**
- ✓ 確認 AWS RDS 實例運行中
- ✓ 驗證安全群組允許您的 IP
- ✓ 確認環境變數已設定

### Q4: 埠口 8080 已被佔用
**解決方案：**
```bash
# 指定不同埠口
java -Dserver.port=9090 -jar target/library-management-1.0.0.jar
```

---

## 📊 專案統計

### 代碼行數

| 層級 | 檔案 | 行數 |
|------|------|------|
| Config | DatabaseConfig.java | ~80 |
| Entity | 3 個實體 | ~350 |
| DAO | 3 個 DAO | ~600 |
| Service | LibraryService.java | ~300 |
| Controller | LibraryController.java | ~350 |
| Frontend | index.html | ~600 |
| 配置 | pom.xml, properties | ~150 |
| **總計** | **17 個檔案** | **~2,500 行** |

### API 端點總數

| 類型 | 數量 |
|------|------|
| GET 查詢 | 7 個 |
| POST 操作 | 2 個 |
| 健康檢查 | 1 個 |
| **總計** | **12 個** |

### 核心技術特性

| 特性 | 實現方式 |
|------|--------|
| ACID 交易 | 手動 `setAutoCommit(false)` |
| 防 SQL 注入 | PreparedStatement 參數化查詢 |
| 防 XSS 攻擊 | HTML 轉義 + Content-Type |
| 欄位驗證 | Entity Setter 防呆驗證 |
| 異常處理 | 全域 try-catch + 標準化回應 |
| 前端通訊 | Fetch API + JSON |

---

## ✅ 最終檢查清單

- [x] 初始化 Git 倉庫
- [x] 提交所有代碼 (17 個檔案)
- [x] MAVEN_GUIDE.md 文檔已建立
- [ ] 系統安裝 Maven 3.8+
- [ ] 執行 `mvn clean package -DskipTests` 編譯
- [ ] 看到 **BUILD SUCCESS** 訊息
- [ ] target/ 目錄中有 .jar 檔案
- [ ] 環境變數已設定 (DB_URL, DB_USERNAME, DB_PASSWORD)
- [ ] 本地運行 `java -jar target/library-management-1.0.0.jar`
- [ ] 訪問 http://localhost:8080 驗證前端
- [ ] 調用 http://localhost:8080/api/health 驗證 API
- [ ] 進行 `git push origin main` 推送到 GitHub

---

## 🎯 下一步（部署到 AWS）

1. 在 AWS EC2 實例上安裝 Java 17
2. 上傳 JAR 檔案到 EC2
3. 設定環境變數
4. 在後台運行應用（使用 nohup 或 systemd）
5. 設定 Nginx 反向代理
6. 配置自動重啟服務

---

**祝您編譯順利！** 🚀
