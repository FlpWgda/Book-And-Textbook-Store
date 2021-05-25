package com.service.store.dao;

import com.service.store.entity.ImageUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageUrlRepository extends JpaRepository<ImageUrl, String> {
}
