package com.example.library.dao;

import com.example.library.entity.BorrowRecord;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 借閱紀錄資料存取物件 (Data Access Object)
 * 
 * 特點：
 * - 使用原生 JDBC PreparedStatement 防止 SQL 注入
 * - 所有 SQL 操作都使用參數化查詢（?佔位符）
 * - Connection 由調用者傳入並管理
 * - 支援 Phase 3 中的手動交易控制（借書、還書需要跨表更新）
 */
@Repository
public class BorrowRecordDAO {

    /**
     * 新增一筆借閱紀錄
     * 
     * 在 Service 層的借書業務中調用
     * 配合 BookDAO.updateBookStatus() 使用，達成交易一致性
     * 
     * @param conn 資料庫連線
     * @param bookId 書籍 ID
     * @param userId 讀者 ID
     * @return 新插入的紀錄 ID（自增主鍵）
     * @throws SQLException 資料庫操作異常
     */
    public int insertRecord(Connection conn, int bookId, int userId) throws SQLException {
        String sql = "INSERT INTO borrow_records (book_id, user_id, borrow_date, status) " +
                     "VALUES (?, ?, NOW(), 'BORROWING')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
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
     * 依 ID 查詢借閱紀錄
     * 
     * @param conn 資料庫連線
     * @param id 紀錄 ID
     * @return 如果找到則返回 BorrowRecord 物件，否則返回 null
     * @throws SQLException 資料庫操作異常
     */
    public BorrowRecord getRecordById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM borrow_records WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBorrowRecord(rs);
                }
            }
        }
        return null;
    }

    /**
     * 查詢讀者的所有借閱紀錄
     * 
     * @param conn 資料庫連線
     * @param userId 讀者 ID
     * @return 該讀者的借閱紀錄列表
     * @throws SQLException 資料庫操作異常
     */
    public List<BorrowRecord> getRecordsByUserId(Connection conn, int userId) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE user_id = ? ORDER BY borrow_date DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRowToBorrowRecord(rs));
                }
            }
        }
        return records;
    }

    /**
     * 查詢書籍的借閱紀錄
     * 
     * @param conn 資料庫連線
     * @param bookId 書籍 ID
     * @return 該書籍的借閱紀錄列表
     * @throws SQLException 資料庫操作異常
     */
    public List<BorrowRecord> getRecordsByBookId(Connection conn, int bookId) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE book_id = ? ORDER BY borrow_date DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRowToBorrowRecord(rs));
                }
            }
        }
        return records;
    }

    /**
     * 查詢所有借閱紀錄
     * 
     * @param conn 資料庫連線
     * @return 所有借閱紀錄列表
     * @throws SQLException 資料庫操作異常
     */
    public List<BorrowRecord> getAllRecords(Connection conn) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records ORDER BY borrow_date DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                records.add(mapRowToBorrowRecord(rs));
            }
        }
        return records;
    }

    /**
     * 依狀態查詢借閱紀錄
     * 
     * @param conn 資料庫連線
     * @param status 紀錄狀態（BORROWING, RETURNED, OVERDUE）
     * @return 符合狀態的借閱紀錄列表
     * @throws SQLException 資料庫操作異常
     */
    public List<BorrowRecord> getRecordsByStatus(Connection conn, String status) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE status = ? ORDER BY borrow_date DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRowToBorrowRecord(rs));
                }
            }
        }
        return records;
    }

    /**
     * 查詢讀者的借中紀錄（未歸還）
     * 
     * 常用於檢查讀者是否有未歸還的書籍
     * 
     * @param conn 資料庫連線
     * @param userId 讀者 ID
     * @return 該讀者的借中紀錄列表
     * @throws SQLException 資料庫操作異常
     */
    public List<BorrowRecord> getActiveRecordsByUserId(Connection conn, int userId) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE user_id = ? AND status IN ('BORROWING', 'OVERDUE') " +
                     "ORDER BY borrow_date DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRowToBorrowRecord(rs));
                }
            }
        }
        return records;
    }

    /**
     * 查詢逾期紀錄
     * 
     * 查詢狀態為 OVERDUE 的紀錄，供後續結算或催繳
     * 
     * @param conn 資料庫連線
     * @return 所有逾期紀錄列表
     * @throws SQLException 資料庫操作異常
     */
    public List<BorrowRecord> getOverdueRecords(Connection conn) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE status = 'OVERDUE' ORDER BY borrow_date ASC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                records.add(mapRowToBorrowRecord(rs));
            }
        }
        return records;
    }

    /**
     * 更新借閱紀錄狀態
     * 
     * @param conn 資料庫連線
     * @param recordId 紀錄 ID
     * @param status 新狀態
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int updateRecordStatus(Connection conn, int recordId, String status) throws SQLException {
        String sql = "UPDATE borrow_records SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, recordId);
            return pstmt.executeUpdate();
        }
    }

    /**
     * 歸還書籍
     * 
     * 更新 returnDate 和 status，在 Service 層的還書業務中調用
     * 
     * @param conn 資料庫連線
     * @param recordId 紀錄 ID
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int returnBook(Connection conn, int recordId) throws SQLException {
        String sql = "UPDATE borrow_records SET return_date = NOW(), status = 'RETURNED' WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            return pstmt.executeUpdate();
        }
    }

    /**
     * 標記記錄為逾期
     * 
     * 供定時任務或業務邏輯調用
     * 
     * @param conn 資料庫連線
     * @param recordId 紀錄 ID
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int markAsOverdue(Connection conn, int recordId) throws SQLException {
        String sql = "UPDATE borrow_records SET status = 'OVERDUE' WHERE id = ? AND status = 'BORROWING'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            return pstmt.executeUpdate();
        }
    }

    /**
     * 刪除借閱紀錄
     * 
     * @param conn 資料庫連線
     * @param id 紀錄 ID
     * @return 受影響的行數
     * @throws SQLException 資料庫操作異常
     */
    public int deleteRecord(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM borrow_records WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();
        }
    }

    /**
     * ORM 手動對應工具方法
     * 
     * 將 ResultSet 的一行資料對應到 BorrowRecord 物件
     * 
     * @param rs 結果集
     * @return BorrowRecord 物件
     * @throws SQLException 資料庫操作異常
     */
    private BorrowRecord mapRowToBorrowRecord(ResultSet rs) throws SQLException {
        BorrowRecord record = new BorrowRecord();
        record.setId(rs.getInt("id"));
        record.setBookId(rs.getInt("book_id"));
        record.setUserId(rs.getInt("user_id"));
        
        Timestamp borrowDate = rs.getTimestamp("borrow_date");
        if (borrowDate != null) {
            record.setBorrowDate(borrowDate.toLocalDateTime());
        }
        
        Timestamp returnDate = rs.getTimestamp("return_date");
        if (returnDate != null) {
            record.setReturnDate(returnDate.toLocalDateTime());
        }
        
        record.setStatus(rs.getString("status"));
        
        return record;
    }
}
