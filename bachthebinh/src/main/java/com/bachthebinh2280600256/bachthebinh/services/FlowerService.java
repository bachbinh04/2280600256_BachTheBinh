package com.bachthebinh2280600256.bachthebinh.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bachthebinh2280600256.bachthebinh.entities.Flower;
import com.bachthebinh2280600256.bachthebinh.repositories.IFlowerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlowerService {
    private final IFlowerRepository flowerRepository;

    public List<Flower> getAllFlowers() {
        return flowerRepository.findAll();
    }
}