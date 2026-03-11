package com.bachthebinh2280600256.bachthebinh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bachthebinh2280600256.bachthebinh.entities.Flower;

@Repository
public interface IFlowerRepository extends JpaRepository<Flower, Long> {
    // Để trống thế này là đủ, Spring Boot sẽ tự động sinh ra các hàm thêm/sửa/xóa/tìm kiếm
}