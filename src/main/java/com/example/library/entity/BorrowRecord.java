package com.example.library.entity;

import java.time.LocalDateTime;

/**
 * 借閱紀錄實體類
 * 
 * 對應資料表：borrow_records
 * 欄位特點：
 * - 所有欄位私有化（Encapsulation）
 * - 提供標準 Getter/Setter
 * - Setter 中實現輸入欄位防呆驗證
 * - bookId 和 userId 進行有效性檢驗
 */
public class BorrowRecord {
    private Integer id;
    private Integer bookId;
    private Integer userId;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private String status;              // BORROWING, RETURNED, OVERDUE

    // ==================== Getter / Setter ====================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("無效的書籍 ID！");
        }
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("無效的讀者 ID！");
        }
        this.userId = userId;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ==================== toString 方法 ====================

    @Override
    public String toString() {
        return "BorrowRecord{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                '}';
    }
}
