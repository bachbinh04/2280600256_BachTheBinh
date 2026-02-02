package com.bachthebinh2280600256.bachthebinh.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bachthebinh2280600256.bachthebinh.entities.Book;
import com.bachthebinh2280600256.bachthebinh.repositories.IBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final IBookRepository bookRepository;

    // --- 1. Hàm cũ: Dùng cho trang Web (Thymeleaf) có phân trang ---
    public List<Book> getAllBooks(Integer pageNo, Integer pageSize, String sortBy) {
        return bookRepository.findAllBooks(pageNo, pageSize, sortBy);
    }

    // --- 2. THÊM MỚI: Hàm dùng cho API (Lấy tất cả, không phân trang) ---
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // --- 3. CẬP NHẬT: Sửa để trả về Book (thay vì void) ---
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    // --- 4. CẬP NHẬT: Sửa để trả về Book (thay vì void) ---
    public Book updateBook(Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        existingBook.setCategory(book.getCategory());

        return bookRepository.save(existingBook);
    }

    // Hàm cũ dùng cho Web
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
    
    // --- 5. THÊM MỚI: Hàm dùng cho API (Gọi hàm xóa cũ) ---
    public void deleteBook(Long id) {
        deleteBookById(id);
    }

    // --- Tìm kiếm sách theo Tiêu đề hoặc Tác giả ---
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBook(keyword);
    }
}