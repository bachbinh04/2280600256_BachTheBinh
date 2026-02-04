package com.bachthebinh2280600256.bachthebinh.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.multipart.MultipartFile;

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
                          BindingResult result,
                          @RequestParam("imageFile") MultipartFile imageFile, // Nhận file từ form
                          Model model) {
        if (result.hasErrors()) {
            return "book/add";
        }

        // Xử lý lưu ảnh
        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                // Lưu vào thư mục static/images/
                Path path = Paths.get("src/main/resources/static/images/" + fileName);
                Files.write(path, imageFile.getBytes());
                book.setImage(fileName); // Lưu tên file vào DB
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    @PostMapping("/add-to-cart")
    public String addToCart(HttpSession session,
            @RequestParam long id,
            @RequestParam String name,
            @RequestParam double price,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(required = false) String image) { // 1. Nhận thêm tham số image (có thể null)
        
        var cart = cartService.getCart(session);
        
        // 2. Truyền image vào Constructor của Item
        // Lưu ý: Đảm bảo thứ tự tham số khớp với @AllArgsConstructor trong Item.java
        cart.addItem(new Item(id, name, price, quantity, image)); 
        
        cartService.updateCart(session, cart);
        return "redirect:/books";
    }
    
    
}