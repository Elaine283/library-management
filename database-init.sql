-- ==========================================
-- 圖書館管理系統資料庫初始化腳本
-- ==========================================

-- 1. 建立資料庫
CREATE DATABASE IF NOT EXISTS library_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

-- 2. 建立使用者表 (users)
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '使用者編號',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '帳號',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '電郵',
    role VARCHAR(20) NOT NULL DEFAULT 'READER' COMMENT '角色 (ADMIN, READER)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='讀者使用者表';

-- 3. 建立書籍表 (books)
CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '書籍編號',
    title VARCHAR(200) NOT NULL COMMENT '書名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    isbn VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN 碼',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '狀態 (AVAILABLE, BORROWED, ARCHIVED)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='書籍表';

-- 4. 建立借閱紀錄表 (borrow_records)
CREATE TABLE IF NOT EXISTS borrow_records (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '借閱紀錄編號',
    book_id INT NOT NULL COMMENT '書籍編號',
    user_id INT NOT NULL COMMENT '使用者編號',
    borrow_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '借閱日期',
    return_date TIMESTAMP NULL COMMENT '歸還日期',
    status VARCHAR(20) NOT NULL DEFAULT 'BORROWING' COMMENT '狀態 (BORROWING, RETURNED, OVERDUE)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_book_id (book_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date)
) ENGINE=InnoDB DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT='借閱紀錄表';

-- ==========================================
-- 插入測試資料
-- ==========================================

-- 插入測試使用者
INSERT INTO users (username, email, role) VALUES 
('alan', 'alan@example.com', 'ADMIN'),
('bob', 'bob@example.com', 'READER'),
('charlie', 'charlie@example.com', 'READER');

-- 插入測試書籍
INSERT INTO books (title, author, isbn, status) VALUES 
('Java 核心編程', '俞敏', '978-7-1111-1111-1', 'AVAILABLE'),
('Spring Boot 實戰', '王天', '978-7-2222-2222-2', 'AVAILABLE'),
('MySQL 數據庫設計', '李明', '978-7-3333-3333-3', 'AVAILABLE'),
('RESTful API 設計', '張勇', '978-7-4444-4444-4', 'AVAILABLE'),
('微服務架構', '劉易', '978-7-5555-5555-5', 'BORROWED');

-- ==========================================
-- 驗證表結構
-- ==========================================
SHOW TABLES;
SELECT * FROM users;
SELECT * FROM books;
