package com.example.library.entity;

import java.time.LocalDateTime;

/**
 * 讀者實體類
 * 
 * 對應資料表：users
 * 欄位特點：
 * - 所有欄位私有化（Encapsulation）
 * - 提供標準 Getter/Setter
 * - Setter 中實現輸入欄位防呆驗證
 */
public class User {
    private Integer id;
    private String username;
    private String email;
    private String role;              // ADMIN, READER
    private LocalDateTime createdAt;

    // ==================== Getter / Setter ====================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("使用者帳號不能為空！");
        }
        this.username = username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email 格式不正確！");
        }
        this.email = email.trim();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role != null && !role.trim().isEmpty()) {
            String upperRole = role.trim().toUpperCase();
            if (!upperRole.matches("ADMIN|READER")) {
                throw new IllegalArgumentException("無效的角色！允許的值：ADMIN, READER");
            }
            this.role = upperRole;
        } else {
            this.role = role;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ==================== toString 方法 ====================

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
