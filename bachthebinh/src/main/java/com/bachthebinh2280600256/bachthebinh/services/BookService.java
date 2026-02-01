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

    public List<Book> getAllBooks(Integer pageNo,
                                  Integer pageSize,
                                  String sortBy) {
        return bookRepository.findAllBooks(pageNo, pageSize, sortBy);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        existingBook.setCategory(book.getCategory());

        bookRepository.save(existingBook);
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
    // --- THÊM MỚI: Tìm kiếm sách theo Tiêu đề hoặc Tác giả ---
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBook(keyword);
    }
}
