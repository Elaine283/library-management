package com.example.library.dao;

import com.example.library.entity.Book;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 書籍資料存取物件 (Data Access Object)
 * 
 * 特點：
 * - 使用原生 JDBC PreparedStatement 防止 SQL 注入
 * - 所有 SQL 操作都使用參數化查詢（?佔位符）
 * - Connection 由調用者傳入並管理
 * - 支援 Phase 3 中的手動交易控制
 */
@Repository
public class BookDAO {

    /**
     * 依關鍵字進行模糊查詢書籍
     * 
     * 面試重點：
     * - 模糊查詢防注入防呆的最佳實踐
     * - 使用 LIKE ? 佔位符，參數透過 setString 傳遞
     * - 關鍵字由調用者提供，DAO 層負責添加 % 通配符
     * 
     * @param conn 資料庫連線
     * @param keyword 搜尋關鍵字（可為 null）
     * @return 符合條件的書籍列表
     * @throws SQLException 資料庫操作異常
     */
    public List<Book> searchBooks(Connection conn, String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 為關鍵字添加萬用字元，實現模糊查詢
            String searchKey = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            pstmt.setString(1, searchKey);
            pstmt.setString(2, searchKey);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }
        }
        return books;
    }

    /**
     * 依 ID 查詢單一書籍
     * 
     * @param conn 資料庫連線
     * @param id 書籍 ID
     * @return 如果找到則返回 Book 物件，否則返回 null
     * @throws SQLException 資料庫操作異常
     */
    public Book getBookById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        }
        return null;
    }

    /**
     * 查詢所有書籍
     * 
     * @param conn 資料庫連線
     * @return 所有書籍列表
     * @throws SQLException 資料庫操作異常
     */
    public List<Book> getAllBooks(Connection conn) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                books.add(mapRowToBook(rs));
            }
        }
        return books;
    }

    /**
     * 依狀態查詢書籍
     * 
     * @param conn 資料庫連線
     * @param status 書籍狀態（AVAILABLE, BORROWED, ARCHIVED）
     * @return 符合狀態的書籍列表
     * @throws SQLException 資料庫操作異常
     */
    public List<Book> getBooksByStatus(Connection conn, String status) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE status = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRowToBook(rs));
                }
            }
        }
        return books;
    }

    /**
     * 新增書籍
     * 
     * @param conn 資料庫連線
     * @param book 要新增的書籍物件
     * @return 新插入的書籍 ID（自增主鍵）
     * @throws SQLException 資料庫操作異常
     */
    public int insertBook(Connection conn, Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, NOW(), NOW())";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getStatus());
            
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
     * 更新書籍資訊
     * 
     * @param conn 資料庫連線
     * @param book 要更新的書籍物件
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int updateBook(Connection conn, Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, status = ?, updated_at = NOW() " +
                     "WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getStatus());
            pstmt.setInt(5, book.getId());
            
            return pstmt.executeUpdate();
        }
    }

    /**
     * 更新書籍狀態
     * 
     * 供借還書業務調用（Phase 3 手動交易控制中使用）
     * 
     * @param conn 資料庫連線
     * @param bookId 書籍 ID
     * @param status 新狀態
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int updateBookStatus(Connection conn, int bookId, String status) throws SQLException {
        String sql = "UPDATE books SET status = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, bookId);
            return pstmt.executeUpdate();
        }
    }

    /**
     * 刪除書籍
     * 
     * @param conn 資料庫連線
     * @param id 書籍 ID
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int deleteBook(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        }
    }

    /**
     * ORM 手動對應工具方法
     * 
     * 將 ResultSet 的一行資料對應到 Book 物件
     * 
     * @param rs 結果集
     * @return Book 物件
     * @throws SQLException 資料庫操作異常
     */
    private Book mapRowToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setStatus(rs.getString("status"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            book.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            book.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return book;
    }
}
