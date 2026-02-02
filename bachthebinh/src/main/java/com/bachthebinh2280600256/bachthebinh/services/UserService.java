package com.bachthebinh2280600256.bachthebinh.services;

import java.util.List;

import com.bachthebinh2280600256.bachthebinh.entities.Role;
import com.bachthebinh2280600256.bachthebinh.entities.User;
import com.bachthebinh2280600256.bachthebinh.repositories.IRoleRepository;
import com.bachthebinh2280600256.bachthebinh.repositories.IUserRepository;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
public class UserService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;

    // 1. Lưu người dùng mới vào Database
    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    // 2. Gán quyền mặc định cho người dùng (Ví dụ: USER)
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    // Mặc định đăng ký sẽ là USER. Nếu chưa có role USER thì tạo mới.
                    Role role = roleRepository.findByName("USER"); 
                    if (role == null) {
                        role = new Role();
                        role.setName("USER");
                        roleRepository.save(role);
                    }
                    user.getRoles().add(role);
                    userRepository.save(user);
                },
                () -> { throw new UsernameNotFoundException("User not found"); }
        );
    }

    // 3. Tải thông tin chi tiết người dùng để đăng nhập
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(Role::getName).toArray(String[]::new))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
    // Lấy tất cả user (cho Admin xem)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy user theo ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Cập nhật thông tin User (Không đổi password/role ở hàm này để an toàn)
    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            // Giữ nguyên password và role cũ
            userRepository.save(existingUser);
        }
    }
    
    // Hàm riêng để Admin đổi Role
    public void updateUserRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElse(null);
        Role role = roleRepository.findById(roleId).orElse(null);
        if (user != null && role != null) {
            user.getRoles().clear(); // Xóa quyền cũ
            user.getRoles().add(role); // Thêm quyền mới
            userRepository.save(user);
        }
    }
    // --- THÊM HÀM NÀY ĐỂ SỬA LỖI ---
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    // Thêm hàm xử lý lưu User từ Google
    public void saveOauthUser(String email, String name) {
        // SỬA: Kiểm tra theo Email thay vì Username
        if (userRepository.findByEmail(email).isPresent()) {
            return; // Nếu email đã có thì thôi, không tạo nữa
        }
        
        var user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setName(name);
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user.setPhone("1234567890"); // Fix lỗi phone null
        
        userRepository.save(user);
        setDefaultRole(user.getUsername());
    }
    // Thêm hàm này vào trong class UserService
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}