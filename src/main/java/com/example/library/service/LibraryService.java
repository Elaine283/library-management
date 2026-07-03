package com.example.library.service;

import com.example.library.config.DatabaseConfig;
import com.example.library.dao.BookDAO;
import com.example.library.dao.BorrowRecordDAO;
import com.example.library.dao.UserDAO;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.User;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 圖書館核心業務邏輯層
 * 
 * 核心特點：
 * ========================================
 * 【手動 JDBC 交易控制 - ACID 完全展現】
 * ========================================
 * 
 * ✓ 不使用高層級的 @Transactional 註解
 * ✓ 在同一個 Connection 中手動 setAutoCommit(false)
 * ✓ 確保多個 SQL 操作的原子性（All-or-Nothing）
 * ✓ 任何操作失敗 → 全面 Rollback（回滾）
 * ✓ 所有操作成功 → 整體 Commit（提交）
 * 
 * 面試加分點：
 * ✓ 展示對 ACID 特性的深入理解
 * ✓ 展示對髒資料問題的認知與防護
 * ✓ 展示原生 JDBC 事務管理的完全掌控
 * ✓ 展示異常處理與資源釋放的最佳實踐
 */
@Service
public class LibraryService {

    private final DatabaseConfig dbConfig;
    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final BorrowRecordDAO borrowRecordDAO;

    // 建構子注入
    public LibraryService(DatabaseConfig dbConfig, BookDAO bookDAO, UserDAO userDAO, BorrowRecordDAO borrowRecordDAO) {
        this.dbConfig = dbConfig;
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;
        this.borrowRecordDAO = borrowRecordDAO;
    }

    // ==================== 【借書業務】======================

    /**
     * 讀者借書 - 核心業務邏輯（手動 JDBC 交易控制機制）
     * 
     * 業務流程：
     * 1️⃣  檢查讀者是否存在
     * 2️⃣  檢查書籍是否存在
     * 3️⃣  檢查書籍是否可借（狀態為 AVAILABLE）
     * 4️⃣  檢查讀者是否有未還的書籍（防止借太多）
     * 5️⃣  更新書籍狀態為 BORROWED
     * 6️⃣  插入借閱紀錄
     * 
     * 交易特性：
     * ✓ 上述 6 個操作在同一個 Connection 中執行
     * ✓ 任何一個 SQL 失敗 → 所有操作全面回滾
     * ✓ 確保資料庫永遠處於一致性狀態（ACID 中的 C）
     * 
     * @param bookId 要借的書籍 ID
     * @param userId 借書的讀者 ID
     * @throws Exception 如果借書失敗（讀者/書籍不存在、書籍不可借等）
     */
    public void borrowBook(int bookId, int userId) throws Exception {
        // 1️⃣  取得底層原生資料庫連線
        Connection conn = dbConfig.getConnection();
        
        try {
            // 2️⃣  【關鍵核心】關閉自動提交，開啟手動交易控制模式
            conn.setAutoCommit(false);

            // 3️⃣  【業務驗證】檢查讀者是否存在
            User user = userDAO.getUserById(conn, userId);
            if (user == null) {
                throw new IllegalArgumentException("讀者 ID [" + userId + "] 不存在！");
            }

            // 4️⃣  【業務驗證】檢查書籍是否存在
            Book book = bookDAO.getBookById(conn, bookId);
            if (book == null) {
                throw new IllegalArgumentException("書籍 ID [" + bookId + "] 不存在！");
            }

            // 5️⃣  【庫存檢查】檢查書籍是否可借（重要防呆邏輯）
            if (!"AVAILABLE".equals(book.getStatus())) {
                throw new IllegalStateException(
                    "書籍 [" + book.getTitle() + "] 目前狀態為 [" + book.getStatus() + "]，無法借出！"
                );
            }

            // 6️⃣  【借書數量限制】檢查讀者是否有未還的書籍
            List<BorrowRecord> activeRecords = borrowRecordDAO.getActiveRecordsByUserId(conn, userId);
            if (activeRecords.size() >= 5) {
                throw new IllegalStateException(
                    "讀者 [" + user.getUsername() + "] 已有 " + activeRecords.size() + 
                    " 本未還的書籍，達到最高借書數量限制（5 本）！"
                );
            }

            // 7️⃣  【SQL 操作 A】更新書籍狀態為 BORROWED
            int bookUpdateCount = bookDAO.updateBookStatus(conn, bookId, "BORROWED");
            if (bookUpdateCount == 0) {
                throw new SQLException("更新書籍狀態失敗！");
            }

            // 8️⃣  【SQL 操作 B】寫入借閱紀錄表
            int recordId = borrowRecordDAO.insertRecord(conn, bookId, userId);
            if (recordId <= 0) {
                throw new SQLException("插入借閱紀錄失敗！");
            }

            // 9️⃣  【交易提交】通過考驗：全面提交交易
            conn.commit();
            System.out.println("✓ 借書成功！讀者 [" + user.getUsername() + "] 成功借得 [" + 
                             book.getTitle() + "]，紀錄 ID: " + recordId);

        } catch (Exception e) {
            // ❌ 【防呆核心】一旦中途有任何異常，立刻全面無條件回滾！
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("❌ 借書業務執行中途發生異常，交易已原子性回滾 (Rollback)！");
                    System.err.println("   異常訊息：" + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("❌ 回滾交易時發生異常：");
                    ex.printStackTrace();
                }
            }
            // 將異常拋出給上層 Controller 全域處理
            throw e;
        } finally {
            // ✓ 【資源釋放】確保關閉連線釋放資源
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("❌ 關閉連線時發生異常：");
                    e.printStackTrace();
                }
            }
        }
    }

    // ==================== 【還書業務】======================

    /**
     * 讀者還書 - 核心業務邏輯（手動 JDBC 交易控制機制）
     * 
     * 業務流程：
     * 1️⃣  檢查借閱紀錄是否存在
     * 2️⃣  檢查書籍是否存在
     * 3️⃣  更新借閱紀錄狀態為 RETURNED + 設置歸還日期
     * 4️⃣  更新書籍狀態為 AVAILABLE
     * 
     * 交易特性：
     * ✓ 上述 4 個操作在同一個 Connection 中執行
     * ✓ 確保借閱紀錄和書籍狀態時刻同步
     * ✓ 防止出現「書籍已可借但紀錄仍未還」的髒資料狀態
     * 
     * @param recordId 借閱紀錄 ID
     * @throws Exception 如果還書失敗（紀錄不存在、書籍不存在等）
     */
    public void returnBook(int recordId) throws Exception {
        Connection conn = dbConfig.getConnection();
        
        try {
            conn.setAutoCommit(false);

            // 檢查借閱紀錄是否存在
            BorrowRecord record = borrowRecordDAO.getRecordById(conn, recordId);
            if (record == null) {
                throw new IllegalArgumentException("借閱紀錄 ID [" + recordId + "] 不存在！");
            }

            // 檢查紀錄狀態是否為借中（防止重複還書）
            if (!"BORROWING".equals(record.getStatus())) {
                throw new IllegalStateException(
                    "該紀錄目前狀態為 [" + record.getStatus() + "]，無法還書！"
                );
            }

            // 檢查書籍是否存在
            Book book = bookDAO.getBookById(conn, record.getBookId());
            if (book == null) {
                throw new IllegalArgumentException("書籍 ID [" + record.getBookId() + "] 不存在！");
            }

            // 【SQL 操作 A】更新借閱紀錄狀態 + 設置歸還日期
            int recordUpdateCount = borrowRecordDAO.returnBook(conn, recordId);
            if (recordUpdateCount == 0) {
                throw new SQLException("更新借閱紀錄失敗！");
            }

            // 【SQL 操作 B】更新書籍狀態為 AVAILABLE
            int bookUpdateCount = bookDAO.updateBookStatus(conn, record.getBookId(), "AVAILABLE");
            if (bookUpdateCount == 0) {
                throw new SQLException("更新書籍狀態失敗！");
            }

            // 交易提交
            conn.commit();
            System.out.println("✓ 還書成功！書籍 [" + book.getTitle() + "] 已歸還，紀錄 ID: " + recordId);

        } catch (Exception e) {
            // 交易回滾
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("❌ 還書業務執行中途發生異常，交易已原子性回滾 (Rollback)！");
                    System.err.println("   異常訊息：" + e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            // 資源釋放
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ==================== 【查詢業務】======================

    /**
     * 查詢讀者的所有借閱歷史
     * 
     * @param userId 讀者 ID
     * @return 該讀者的所有借閱紀錄
     * @throws Exception 如果查詢失敗
     */
    public List<BorrowRecord> getUserBorrowHistory(int userId) throws Exception {
        Connection conn = dbConfig.getConnection();
        try {
            // 查詢無需交易控制，使用預設的 autoCommit = true
            return borrowRecordDAO.getRecordsByUserId(conn, userId);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 查詢讀者的未還書籍
     * 
     * @param userId 讀者 ID
     * @return 該讀者的未還紀錄列表
     * @throws Exception 如果查詢失敗
     */
    public List<BorrowRecord> getUserActiveRecords(int userId) throws Exception {
        Connection conn = dbConfig.getConnection();
        try {
            return borrowRecordDAO.getActiveRecordsByUserId(conn, userId);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 查詢書籍的借閱歷史
     * 
     * @param bookId 書籍 ID
     * @return 該書籍的所有借閱紀錄
     * @throws Exception 如果查詢失敗
     */
    public List<BorrowRecord> getBookBorrowHistory(int bookId) throws Exception {
        Connection conn = dbConfig.getConnection();
        try {
            return borrowRecordDAO.getRecordsByBookId(conn, bookId);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 查詢所有逾期未還的紀錄
     * 
     * @return 逾期紀錄列表
     * @throws Exception 如果查詢失敗
     */
    public List<BorrowRecord> getAllOverdueRecords() throws Exception {
        Connection conn = dbConfig.getConnection();
        try {
            return borrowRecordDAO.getOverdueRecords(conn);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 搜尋書籍
     * 
     * @param keyword 搜尋關鍵字
     * @return 符合條件的書籍列表
     * @throws Exception 如果查詢失敗
     */
    public List<Book> searchBooks(String keyword) throws Exception {
        Connection conn = dbConfig.getConnection();
        try {
            return bookDAO.searchBooks(conn, keyword);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 取得所有可借的書籍
     * 
     * @return 狀態為 AVAILABLE 的書籍列表
     * @throws Exception 如果查詢失敗
     */
    public List<Book> getAvailableBooks() throws Exception {
        Connection conn = dbConfig.getConnection();
        try {
            return bookDAO.getBooksByStatus(conn, "AVAILABLE");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 取得所有已借出的書籍
     * 
     * @return 狀態為 BORROWED 的書籍列表
     * @throws Exception 如果查詢失敗
     */
    public List<Book> getBorrowedBooks() throws Exception {
        Connection conn = dbConfig.getConnection();
        try {
            return bookDAO.getBooksByStatus(conn, "BORROWED");
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
