package com.example.library.entity;

import java.time.LocalDateTime;

/**
 * 書籍實體類
 * 
 * 對應資料表：books
 * 欄位特點：
 * - 所有欄位私有化（Encapsulation）
 * - 提供標準 Getter/Setter
 * - Setter 中實現輸入欄位防呆驗證
 */
public class Book {
    private Integer id;
    private String title;
    private String author;
    private String isbn;
    private String status;      // AVAILABLE, BORROWED, ARCHIVED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== Getter / Setter ====================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("書籍名稱不能為空！");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("作者名稱不能為空！");
        }
        this.author = author.trim();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN 不能為空！");
        }
        String cleanIsbn = isbn.trim().replaceAll("-", "");
        if (cleanIsbn.length() != 10 && cleanIsbn.length() != 13) {
            throw new IllegalArgumentException("ISBN 格式無效！必須為 10 或 13 位數字。");
        }
        if (!cleanIsbn.matches("\\d+")) {
            throw new IllegalArgumentException("ISBN 必須只包含數字！");
        }
        this.isbn = isbn.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("書籍狀態不能為空！");
        }
        String upperStatus = status.trim().toUpperCase();
        if (!upperStatus.matches("AVAILABLE|BORROWED|ARCHIVED")) {
            throw new IllegalArgumentException("無效的書籍狀態！允許的值：AVAILABLE, BORROWED, ARCHIVED");
        }
        this.status = upperStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==================== toString 方法 ====================

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
