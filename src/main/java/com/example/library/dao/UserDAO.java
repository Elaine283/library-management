package com.example.library.dao;

import com.example.library.entity.User;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 讀者資料存取物件 (Data Access Object)
 * 
 * 特點：
 * - 使用原生 JDBC PreparedStatement 防止 SQL 注入
 * - 所有 SQL 操作都使用參數化查詢（?佔位符）
 * - Connection 由調用者傳入並管理
 * - 支援 Phase 3 中的手動交易控制
 */
@Repository
public class UserDAO {

    /**
     * 依帳號查詢讀者
     * 
     * 常見於登入驗證場景
     * 
     * @param conn 資料庫連線
     * @param username 讀者帳號
     * @return 如果找到則返回 User 物件，否則返回 null
     * @throws SQLException 資料庫操作異常
     */
    public User getUserByUsername(Connection conn, String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * 依 ID 查詢讀者
     * 
     * @param conn 資料庫連線
     * @param id 讀者 ID
     * @return 如果找到則返回 User 物件，否則返回 null
     * @throws SQLException 資料庫操作異常
     */
    public User getUserById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * 依郵件查詢讀者
     * 
     * @param conn 資料庫連線
     * @param email 讀者郵件
     * @return 如果找到則返回 User 物件，否則返回 null
     * @throws SQLException 資料庫操作異常
     */
    public User getUserByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * 查詢所有讀者
     * 
     * @param conn 資料庫連線
     * @return 所有讀者列表
     * @throws SQLException 資料庫操作異常
     */
    public List<User> getAllUsers(Connection conn) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        }
        return users;
    }

    /**
     * 依角色查詢讀者
     * 
     * @param conn 資料庫連線
     * @param role 讀者角色（ADMIN, READER）
     * @return 符合角色的讀者列表
     * @throws SQLException 資料庫操作異常
     */
    public List<User> getUsersByRole(Connection conn, String role) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        }
        return users;
    }

    /**
     * 模糊查詢讀者
     * 
     * 按帳號或郵件搜尋
     * 
     * @param conn 資料庫連線
     * @param keyword 搜尋關鍵字
     * @return 符合條件的讀者列表
     * @throws SQLException 資料庫操作異常
     */
    public List<User> searchUsers(Connection conn, String keyword) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? OR email LIKE ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchKey = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            pstmt.setString(1, searchKey);
            pstmt.setString(2, searchKey);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        }
        return users;
    }

    /**
     * 新增讀者
     * 
     * @param conn 資料庫連線
     * @param user 要新增的讀者物件
     * @return 新插入的讀者 ID（自增主鍵）
     * @throws SQLException 資料庫操作異常
     */
    public int insertUser(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, role, created_at) " +
                     "VALUES (?, ?, ?, NOW())";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole());
            
            pstmt.executeUpdate();
            
            // 取得自增 ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * 更新讀者資訊
     * 
     * @param conn 資料庫連線
     * @param user 要更新的讀者物件
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int updateUser(Connection conn, User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, role = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole());
            pstmt.setInt(4, user.getId());
            
            return pstmt.executeUpdate();
        }
    }

    /**
     * 更新讀者角色
     * 
     * @param conn 資料庫連線
     * @param userId 讀者 ID
     * @param role 新角色
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int updateUserRole(Connection conn, int userId, String role) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate();
        }
    }

    /**
     * 刪除讀者
     * 
     * @param conn 資料庫連線
     * @param id 讀者 ID
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int deleteUser(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        }
    }

    /**
     * ORM 手動對應工具方法
     * 
     * 將 ResultSet 的一行資料對應到 User 物件
     * 
     * @param rs 結果集
     * @return User 物件
     * @throws SQLException 資料庫操作異常
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return user;
    }
}
