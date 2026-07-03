# 環境變數配置指南

本專案使用**環境變數**方式管理敏感資訊（資料庫密碼、用戶名等），以保護您的 AWS 認證資訊安全。

## 🔐 環境變數配置

需要設定以下三個環境變數：

| 環境變數 | 說明 | 範例 |
|---------|------|------|
| `DB_URL` | AWS RDS MySQL 連線字符串 | `jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USERNAME` | 資料庫用戶名 | `admin` |
| `DB_PASSWORD` | 資料庫密碼 | `elaine1031` |

---

## 🪟 Windows 系統設定

### 方法 1: 使用命令提示字元（CMD）- 臨時設定

```batch
# 在命令提示字元中執行以下命令
set DB_URL=jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
set DB_USERNAME=admin
set DB_PASSWORD=elaine1031

# 然後執行應用
mvn spring-boot:run
```

### 方法 2: 使用 PowerShell - 臨時設定

```powershell
# 在 PowerShell 中執行以下命令
$env:DB_URL="jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="admin"
$env:DB_PASSWORD="elaine1031"

# 然後執行應用
mvn spring-boot:run
```

### 方法 3: 系統環境變數 - 永久設定 ⭐ 推薦

1. 按 `Win + X`，選擇「系統」
2. 點擊「進階系統設定」
3. 點擊「環境變數」按鈕
4. 在「系統變數」區域，點擊「新增」
5. 添加以下三個變數：
   - 變數名稱：`DB_URL`
   - 變數值：`jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`

   - 變數名稱：`DB_USERNAME`
   - 變數值：`admin`

   - 變數名稱：`DB_PASSWORD`
   - 變數值：`elaine1031`

6. 點擊「確定」，重新啟動 IDE 或命令提示字元

---

## 🐧 Linux / macOS 系統設定

### 方法 1: 暫時設定

```bash
export DB_URL="jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="admin"
export DB_PASSWORD="elaine1031"

# 然後執行應用
mvn spring-boot:run
```

### 方法 2: 永久設定 ⭐ 推薦

編輯 `~/.bashrc` 或 `~/.zshrc` 文件：

```bash
# 添加以下行到檔案末尾
export DB_URL="jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="admin"
export DB_PASSWORD="elaine1031"
```

然後執行：
```bash
source ~/.bashrc  # 或 source ~/.zshrc
```

---

## 🚀 IDE 中設定環境變數

### IntelliJ IDEA

1. 點擊 `Run` → `Edit Configurations...`
2. 選擇 Maven 配置或應用啟動配置
3. 在「Environment variables」欄位中添加：
   ```
   DB_URL=jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC;DB_USERNAME=admin;DB_PASSWORD=elaine1031
   ```
4. 點擊「Apply」和「OK」

### VS Code

在 `.vscode/launch.json` 中添加：

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Spring Boot App",
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "cwd": "${workspaceFolder}",
      "console": "integratedTerminal",
      "env": {
        "DB_URL": "jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
        "DB_USERNAME": "admin",
        "DB_PASSWORD": "elaine1031"
      }
    }
  ]
}
```

---

## ✅ 驗證環境變數設定

運行應用時，您應該看到類似的日誌輸出：

```
INFO  : Tomcat started on port(s): 8080 (http)
INFO  : Started LibraryApplication in XX.XXX seconds (JVM running for XX.XXX)
```

如果看到資料庫連線錯誤，請檢查：
1. 環境變數是否正確設定
2. AWS RDS 實例是否正常運行
3. 安全群組是否允許您的 IP 訪問（通常需要允許 MySQL 3306 連接埠）

---

## 🔒 安全提示

✅ **該做的事情：**
- ✓ 使用環境變數存儲敏感資訊
- ✓ 將 `application-*.properties` 添加到 `.gitignore`
- ✓ 在 CI/CD 管道中使用密鑰管理系統

❌ **不要做的事情：**
- ✗ 不要將密碼提交到 Git 版本控制
- ✗ 不要在代碼中硬編碼敏感資訊
- ✗ 不要分享包含密碼的配置檔案
