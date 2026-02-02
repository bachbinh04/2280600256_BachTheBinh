package com.bachthebinh2280600256.bachthebinh.controllers;

import com.bachthebinh2280600256.bachthebinh.entities.Book;
import com.bachthebinh2280600256.bachthebinh.models.BookDTO;
import com.bachthebinh2280600256.bachthebinh.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookApiController {

    private final BookService bookService;

    // GET: Lấy danh sách sách (Public - ai cũng xem được do config ở bước 8.1)
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        
        // Convert Entity sang DTO
        List<BookDTO> bookDTOs = books.stream()
                .map(book -> new BookDTO(
                        book.getId(), 
                        book.getTitle(), 
                        book.getAuthor(), 
                        book.getPrice(), 
                        book.getCategory() != null ? book.getCategory().getName() : "N/A"
                ))
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(bookDTOs);
    }

    // POST: Thêm sách (Chỉ ADMIN - Yêu cầu có Token Bearer)
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.addBook(book);
        return ResponseEntity.ok(savedBook);
    }
    
    // DELETE: Xóa sách (Chỉ ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok().build();
    }
}