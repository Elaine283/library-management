package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 圖書館 RESTful API 控制層
 * 
 * 功能：
 * ✓ 開闢 RESTful API 路由供前端調用
 * ✓ 基本的請求參數驗證（防呆）
 * ✓ 將 Service 層邏輯包裝成 HTTP 端點
 * ✓ 統一的 JSON 回應格式
 * ✓ 異常捕捉與轉換成 HTTP 狀態碼
 * 
 * 路由前綴：/api
 * 支持 CORS：允許前端網頁跨域調用
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // 允許前端網頁跨域呼叫 API
public class LibraryController {

    private final LibraryService libraryService;

    // 建構子注入
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // ==================== 【搜尋與查詢 API】======================

    /**
     * 搜尋書籍 API
     * 
     * 路由：GET /api/books
     * 參數：keyword (可選)
     * 範例：GET /api/books?keyword=Java
     * 
     * @param keyword 搜尋關鍵字
     * @return 符合條件的書籍列表
     */
    @GetMapping("/books")
    public ResponseEntity<?> searchBooks(
            @RequestParam(required = false, defaultValue = "") String keyword) {
        try {
            List<Book> books = libraryService.searchBooks(keyword);
            return ResponseEntity.ok(buildSuccessResponse("搜尋成功", books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("搜尋書籍時發生異常: " + e.getMessage()));
        }
    }

    /**
     * 取得所有可借書籍 API
     * 
     * 路由：GET /api/books/available
     * 
     * @return 狀態為 AVAILABLE 的所有書籍
     */
    @GetMapping("/books/available")
    public ResponseEntity<?> getAvailableBooks() {
        try {
            List<Book> books = libraryService.getAvailableBooks();
            return ResponseEntity.ok(buildSuccessResponse("取得可借書籍成功", books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("取得可借書籍時發生異常: " + e.getMessage()));
        }
    }

    /**
     * 取得所有已借書籍 API
     * 
     * 路由：GET /api/books/borrowed
     * 
     * @return 狀態為 BORROWED 的所有書籍
     */
    @GetMapping("/books/borrowed")
    public ResponseEntity<?> getBorrowedBooks() {
        try {
            List<Book> books = libraryService.getBorrowedBooks();
            return ResponseEntity.ok(buildSuccessResponse("取得已借書籍成功", books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("取得已借書籍時發生異常: " + e.getMessage()));
        }
    }

    /**
     * 查詢讀者借閱歷史 API
     * 
     * 路由：GET /api/users/{userId}/borrow-history
     * 
     * @param userId 讀者 ID
     * @return 該讀者的所有借閱紀錄
     */
    @GetMapping("/users/{userId}/borrow-history")
    public ResponseEntity<?> getUserBorrowHistory(@PathVariable int userId) {
        try {
            List<BorrowRecord> records = libraryService.getUserBorrowHistory(userId);
            return ResponseEntity.ok(buildSuccessResponse("查詢借閱歷史成功", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("查詢借閱歷史時發生異常: " + e.getMessage()));
        }
    }

    /**
     * 查詢讀者未還書籍 API
     * 
     * 路由：GET /api/users/{userId}/active-records
     * 
     * @param userId 讀者 ID
     * @return 該讀者的未還紀錄列表
     */
    @GetMapping("/users/{userId}/active-records")
    public ResponseEntity<?> getUserActiveRecords(@PathVariable int userId) {
        try {
            List<BorrowRecord> records = libraryService.getUserActiveRecords(userId);
            return ResponseEntity.ok(buildSuccessResponse("查詢未還書籍成功", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("查詢未還書籍時發生異常: " + e.getMessage()));
        }
    }

    /**
     * 查詢書籍借閱歷史 API
     * 
     * 路由：GET /api/books/{bookId}/borrow-history
     * 
     * @param bookId 書籍 ID
     * @return 該書籍的所有借閱紀錄
     */
    @GetMapping("/books/{bookId}/borrow-history")
    public ResponseEntity<?> getBookBorrowHistory(@PathVariable int bookId) {
        try {
            List<BorrowRecord> records = libraryService.getBookBorrowHistory(bookId);
            return ResponseEntity.ok(buildSuccessResponse("查詢書籍借閱歷史成功", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("查詢書籍借閱歷史時發生異常: " + e.getMessage()));
        }
    }

    /**
     * 查詢所有逾期紀錄 API
     * 
     * 路由：GET /api/overdue-records
     * 
     * @return 所有逾期的借閱紀錄
     */
    @GetMapping("/overdue-records")
    public ResponseEntity<?> getAllOverdueRecords() {
        try {
            List<BorrowRecord> records = libraryService.getAllOverdueRecords();
            return ResponseEntity.ok(buildSuccessResponse("查詢逾期紀錄成功", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("查詢逾期紀錄時發生異常: " + e.getMessage()));
        }
    }

    // ==================== 【借書業務 API】======================

    /**
     * 讀者借書 API
     * 
     * 路由：POST /api/borrow
     * 請求格式：
     * {
     *   "bookId": 1,
     *   "userId": 2
     * }
     * 
     * 核心邏輯：
     * ✓ 檢查讀者和書籍是否存在
     * ✓ 檢查書籍是否可借
     * ✓ 檢查讀者未還書籍數量
     * ✓ 手動交易控制：更新書籍狀態 + 插入借閱紀錄
     * ✓ 任何步驟失敗 → 全面回滾
     * 
     * @param request 包含 bookId 和 userId 的 JSON 請求
     * @return 成功/失敗訊息
     */
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody Map<String, Integer> request) {
        try {
            // 1. 參數驗證防呆
            Integer bookId = request.get("bookId");
            Integer userId = request.get("userId");

            if (bookId == null || userId == null) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("請求參數不完整，缺少 bookId 或 userId！"));
            }

            if (bookId <= 0 || userId <= 0) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("bookId 和 userId 必須為正整數！"));
            }

            // 2. 執行 Service 手動交易控制借書
            libraryService.borrowBook(bookId, userId);

            // 3. 成功回應
            return ResponseEntity.ok(
                    buildSuccessResponse("借閱手續完成，祝您閱讀愉快！", null)
            );

        } catch (IllegalArgumentException e) {
            // 業務邏輯異常（讀者/書籍不存在）
            return ResponseEntity.badRequest()
                    .body(buildErrorResponse(e.getMessage()));

        } catch (IllegalStateException e) {
            // 業務狀態異常（書籍不可借、超過借書數量等）
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(buildErrorResponse(e.getMessage()));

        } catch (Exception e) {
            // 系統異常（資料庫錯誤等）
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("系統異常，無法處理借閱請求: " + e.getMessage()));
        }
    }

    // ==================== 【還書業務 API】======================

    /**
     * 讀者還書 API
     * 
     * 路由：POST /api/return
     * 請求格式：
     * {
     *   "recordId": 5
     * }
     * 
     * 核心邏輯：
     * ✓ 檢查借閱紀錄是否存在
     * ✓ 檢查紀錄狀態是否為借中（防止重複還書）
     * ✓ 手動交易控制：更新紀錄 + 更新書籍狀態
     * ✓ 任何步驟失敗 → 全面回滾
     * 
     * @param request 包含 recordId 的 JSON 請求
     * @return 成功/失敗訊息
     */
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody Map<String, Integer> request) {
        try {
            // 1. 參數驗證防呆
            Integer recordId = request.get("recordId");

            if (recordId == null) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("請求參數不完整，缺少 recordId！"));
            }

            if (recordId <= 0) {
                return ResponseEntity.badRequest()
                        .body(buildErrorResponse("recordId 必須為正整數！"));
            }

            // 2. 執行 Service 手動交易控制還書
            libraryService.returnBook(recordId);

            // 3. 成功回應
            return ResponseEntity.ok(
                    buildSuccessResponse("還書手續完成，感謝您的借閱！", null)
            );

        } catch (IllegalArgumentException e) {
            // 業務邏輯異常（紀錄不存在）
            return ResponseEntity.badRequest()
                    .body(buildErrorResponse(e.getMessage()));

        } catch (IllegalStateException e) {
            // 業務狀態異常（紀錄狀態不是借中）
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(buildErrorResponse(e.getMessage()));

        } catch (Exception e) {
            // 系統異常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("系統異常，無法處理還書請求: " + e.getMessage()));
        }
    }

    // ==================== 【健康檢查 API】======================

    /**
     * API 健康檢查端點
     * 
     * 路由：GET /api/health
     * 用途：檢查 API 服務是否正常運行
     * 
     * @return 健康狀態信息
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Library Management System");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    // ==================== 【回應格式工具方法】======================

    /**
     * 構建成功回應 JSON
     * 
     * 格式：
     * {
     *   "success": true,
     *   "message": "...",
     *   "data": {...}
     * }
     * 
     * @param message 成功訊息
     * @param data 返回的數據
     * @return 標準化的成功回應 Map
     */
    private Map<String, Object> buildSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    /**
     * 構建錯誤回應 JSON
     * 
     * 格式：
     * {
     *   "success": false,
     *   "error": "..."
     * }
     * 
     * @param error 錯誤訊息
     * @return 標準化的錯誤回應 Map
     */
    private Map<String, Object> buildErrorResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        return response;
    }
}
package com.example.library.controller; // 確保這裡的 package 名稱與你原本檔案頂部的一致

// 導入 Spring Boot 處理 HTTP 請求與跨域所需的套件
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 導入 Java 內建的資料結構
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // 【重要】這個標籤告訴 Spring，這是一個專門用來提供 RESTful API 的控制器
@RequestMapping("/api") // 【重要】設定這個控制器底下所有的網址，都會以 /api 作為開頭
@CrossOrigin(origins = "*") // 【解決 CORS 問題】允許所有外部網域（包含你的 GitHub Pages）來索取資料
public class LibraryController {

    // 【設定 GET 請求】對應到完整的網址就是 /api/books
    @GetMapping("/books")
    public List<Map<String, Object>> getAllBooks() {
        
        // 這裡我們先建立一份「假資料 (Mock Data)」來測試連線是否成功
        // 等連線測試沒問題後，你就可以將這裡改成呼叫你的 BookDAO 去資料庫抓真正的資料
        List<Map<String, Object>> bookList = new ArrayList<>();
        
        // 建立第一本書的資料
        Map<String, Object> book1 = new HashMap<>();
        book1.put("id", 1);
        book1.put("title", "崛起的薪勢力 - 在 Linux 下用 AMD GPU 加速實作");
        book1.put("author", "劉京洋、趙新達");
        
        bookList.add(book1); // 把書加進清單中

        // Spring Boot 會非常聰明地自動將這個 List 轉換成 JSON 格式回傳給前端
        return bookList; 
    }
}
