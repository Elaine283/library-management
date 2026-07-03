package com.example.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 資料庫連線配置類
 * 
 * 特點：
 * - 不使用 Spring Boot 預設的 HikariCP 連線池
 * - 手動透過 DriverManager 獲取原生 JDBC Connection 物件
 * - 支援在 Phase 3 中手動控制 autoCommit 進行交易管理
 * - 每次獲取新的連線物件，需要手動調用 close() 方法釋放資源
 * 
 * 環境變數：
 * - DB_URL: 資料庫連線字符串
 * - DB_USERNAME: 資料庫用戶名
 * - DB_PASSWORD: 資料庫密碼
 */
@Configuration
public class DatabaseConfig {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    /**
     * 獲取原生 JDBC 資料庫連線物件
     * 
     * 使用方法：
     * Connection conn = databaseConfig.getConnection();
     * try {
     *     // 執行 SQL 操作
     * } finally {
     *     if (conn != null) {
     *         conn.close();
     *     }
     * }
     * 
     * @return 新的 Connection 物件
     * @throws SQLException 如果連線失敗或找不到 JDBC 驅動程式
     */
    public Connection getConnection() throws SQLException {
        try {
            // 強制載入 MySQL 8.0+ 的 JDBC 驅動程式
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("找不到 MySQL JDBC 驅動程式類別！請確保 mysql-connector-j 已添加到依賴。", e);
        }

        // 通過 DriverManager 獲取原生連線
        Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        
        // 返回連線物件，由調用者負責關閉
        return connection;
    }

    /**
     * 測試資料庫連線
     * 
     * @return true 表示連線成功，false 表示連線失敗
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            // 如果能獲取連線且沒拋出例外，則表示連線成功
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("資料庫連線測試失敗: " + e.getMessage());
            return false;
        }
    }

    /**
     * 取得資料庫 URL（用於除錯用途）
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * 取得資料庫用戶名（用於除錯用途）
     */
    public String getDbUsername() {
        return dbUsername;
    }
}
