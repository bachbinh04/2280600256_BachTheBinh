package com.bachthebinh2280600256.bachthebinh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bachthebinh2280600256.bachthebinh.entities.Category;

@Repository
public interface ICategoryRepository extends
        JpaRepository<Category, Long> {
}
