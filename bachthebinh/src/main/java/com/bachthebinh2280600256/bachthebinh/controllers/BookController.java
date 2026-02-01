package com.bachthebinh2280600256.bachthebinh.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bachthebinh2280600256.bachthebinh.daos.Item;
import com.bachthebinh2280600256.bachthebinh.entities.Book;
import com.bachthebinh2280600256.bachthebinh.services.BookService;
import com.bachthebinh2280600256.bachthebinh.services.CartService;
import com.bachthebinh2280600256.bachthebinh.services.CategoryService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final CartService cartService;

    // --- 1. HIỂN THỊ DANH SÁCH + TÌM KIẾM ---
    @GetMapping
    public String showAllBooks(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "") String keyword) {

        List<Book> books;
        if (keyword.isEmpty()) {
            // Nếu không tìm kiếm -> Lấy tất cả
            books = bookService.getAllBooks(pageNo, pageSize, sortBy);
            // Tính lại totalPages
            long totalBooks = bookService.getAllBooks(0, 10000, "id").size();
            model.addAttribute("totalPages", totalBooks / pageSize);
        } else {
            // Nếu có tìm kiếm -> Gọi hàm search
            books = bookService.searchBooks(keyword);
            model.addAttribute("totalPages", 1);
        }

        model.addAttribute("books", books);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        return "book/list";
    }

    // --- 2. THÊM SÁCH (ADD) ---
    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/add";
    }

    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/add";
        }
        bookService.addBook(book);
        return "redirect:/books";
    }

    // --- 3. SỬA SÁCH (EDIT) ---
    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/edit";
    }

    @PostMapping("/edit")
    public String editBook(@Valid @ModelAttribute("book") Book book,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/edit";
        }
        bookService.updateBook(book);
        return "redirect:/books";
    }

    // --- 4. XÓA SÁCH (DELETE) ---
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);
        return "redirect:/books";
    }

    // --- 5. GIỎ HÀNG ---
    @PostMapping("/add-to-cart")
    public String addToCart(HttpSession session,
            @RequestParam long id,
            @RequestParam String name,
            @RequestParam double price,
            @RequestParam(defaultValue = "1") int quantity) {
        var cart = cartService.getCart(session);
        
        // SỬA: Dùng addItems (có 's') để khớp với file Cart.java
        cart.addItem(new Item(id, name, price, quantity));
        
        cartService.updateCart(session, cart);
        return "redirect:/books";
    }
    
    
}