# Maven 編譯打包與部署指南

## ⚠️ 系統環境檢查

您的系統目前**未檢測到 Maven**。請按照以下步驟進行：

## 方案 1: 安裝 Maven（推薦）

### Windows 系統

#### 步驟 1: 下載 Maven
1. 訪問 https://maven.apache.org/download.cgi
2. 下載 **Apache Maven 3.9.x** 的二進位版本 (ZIP)
3. 解壓到本地，例如：`C:\maven`

#### 步驟 2: 設定環境變數
1. 按 `Win + X`，選擇「系統」
2. 點擊「進階系統設定」
3. 點擊「環境變數」
4. 在「系統變數」中新增：
   - **變數名稱**: `M2_HOME`
   - **變數值**: `C:\maven`（根據實際路徑調整）

5. 編輯 `Path` 變數，添加：`C:\maven\bin`

6. 重新啟動 PowerShell 或 Command Prompt

#### 步驟 3: 驗證安裝
```bash
mvn --version
```
應該看到類似的輸出：
```
Apache Maven 3.9.x
Java version: 17.x.x
```

---

## 方案 2: 使用 IntelliJ IDEA 內建的 Maven

### 步驟 1: 在 IntelliJ 中打開 Terminal
- 點擊 IntelliJ IDEA 底部的 **Terminal** 標籤
- 確保路徑位於專案根目錄（能看到 `pom.xml`）

### 步驟 2: 執行 Maven 命令

如果 Maven 在系統 PATH 中：
```bash
mvn clean package -DskipTests
```

---

## 編譯打包步驟

### 1️⃣ 進入專案根目錄
```bash
cd c:\Users\20260612\AI\Spring Boot
```

### 2️⃣ 執行編譯命令
```bash
mvn clean package -DskipTests
```

**命令說明：**
- `clean` - 清空之前的 target 目錄
- `package` - 編譯並打包成 JAR
- `-DskipTests` - 跳過測試（加快編譯速度）

### 3️⃣ 等待編譯完成

編譯時間通常為 **2-5 分鐘**，第一次會比較長（需要下載依賴）

**成功標誌：** 看到 ✅ **BUILD SUCCESS** 的綠色訊息

---

## 📦 驗證打包結果

### 檢查 target 目錄
```bash
# Windows PowerShell
Get-ChildItem -Path .\target -Filter "*.jar"

# 或使用 cmd
dir target\*.jar
```

### 預期結果
```
Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
-a----        2026/7/3  下午11:30         45MB   library-management-1.0.0.jar
```

---

## 🚀 本地運行測試

編譯成功後，可以在本地測試運行：

### 設定環境變數
```powershell
# PowerShell
$env:DB_URL="jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="admin"
$env:DB_PASSWORD="elaine1031"
```

### 運行 JAR 檔案
```bash
java -jar target/library-management-1.0.0.jar
```

### 訪問應用
- 主頁: http://localhost:8080
- API: http://localhost:8080/api/health

---

## 🌳 Git 提交與推送

編譯成功後，將所有代碼提交到 GitHub：

### 步驟 1: 檢查 Git 狀態
```bash
git status
```

### 步驟 2: 新增所有檔案（除了 target）
```bash
git add .
# 注意：.gitignore 會自動排除 target/ 目錄
```

### 步驟 3: 提交代碼
```bash
git commit -m "完整的圖書館管理系統：Entity + DAO + Service + Controller + 前端"
```

### 步驟 4: 推送到 GitHub
```bash
git push origin main
# 或
git push origin master
```

---

## ❌ 常見問題排除

### 問題 1: "mvn: 找不到命令"
**解決方案：**
- ✓ 確認 Maven 已安裝並在 PATH 中
- ✓ 重新啟動 PowerShell/Terminal
- ✓ 使用絕對路徑執行 Maven

### 問題 2: "Java 版本不相容"
**解決方案：**
- 確認系統安裝了 JDK 17+
- 執行 `java -version` 驗證

### 問題 3: "依賴下載失敗"
**解決方案：**
- 檢查網路連線
- 清空 Maven 快取：`mvn clean install -U`

### 問題 4: "資料庫連線失敗"
**解決方案：**
- 確認環境變數已設定（DB_URL、DB_USERNAME、DB_PASSWORD）
- 檢查 AWS RDS 實例是否運行
- 驗證安全群組允許您的 IP 訪問

---

## 📋 編譯檢查清單

- [ ] 系統已安裝 Maven 3.8+
- [ ] 系統已安裝 JDK 17+
- [ ] 進入專案根目錄（能看到 pom.xml）
- [ ] 執行 `mvn clean package -DskipTests`
- [ ] 看到 **BUILD SUCCESS** 訊息
- [ ] target/ 目錄中有 .jar 檔案
- [ ] 環境變數已設定（DB_URL、DB_USERNAME、DB_PASSWORD）
- [ ] 代碼已 git commit 和 git push

---

## 🎯 下一步

1. ✅ 安裝 Maven（如果還未安裝）
2. ✅ 執行 `mvn clean package -DskipTests`
3. ✅ 驗證編譯成功
4. ✅ 執行 `git commit` 和 `git push`
5. ✅ 在 AWS EC2 上部署應用

---

## 📞 技術支援

如遇到問題，請檢查：
- pom.xml 中的依賴配置
- 本地 Maven 倉庫 (~/.m2/repository)
- IntelliJ IDEA 的 Maven 設定 (File → Settings → Build, Execution, Deployment → Build Tools → Maven)
