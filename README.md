# 圖書館管理系統 (Library Management System)

Spring Boot 圖書館管理系統，提供書籍管理、使用者管理、借閱記錄等功能。

## 📁 專案結構

```
src/main/java/com/example/library/
├── config/          # 資料庫連線配置
├── entity/          # JPA 實體類別
├── dao/             # 資料存取層 (Phase 3)
├── service/         # 業務邏輯層 (Phase 3)
└── controller/      # REST API 層 (Phase 3)
```

## 🛠️ 技術棧

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven**

## 📋 前置需求

- JDK 17 或以上版本
- MySQL 8.0 或以上版本
- Maven 3.8.0 或以上版本

## 🚀 快速開始

### 1. 建立資料庫

```sql
CREATE DATABASE library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 修改連線設定

編輯 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. 執行應用

```bash
mvn clean install
mvn spring-boot:run
```

應用將運行在 `http://localhost:8080/api`

## 📦 依賴項

- spring-boot-starter-web: Web 應用支援
- spring-boot-starter-data-jpa: ORM 框架
- mysql-connector-j: MySQL 驅動
- lombok: 減少樣板代碼

## 📝 開發階段

- **Phase 1**: 建立專案結構與基礎設定 ✓
- **Phase 2**: 建立實體類別 (待進行)
- **Phase 3**: 實作 DAO、Service、Controller 層 (待進行)

## 📄 授權

MIT License
