package com.service.store.dao;

import com.service.store.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByNameContaining(String keyword);
    List<Item> findByPublishingHouseContaining(String keyword);
    List<Item> findByAuthors_NameContaining(String keyword);
    List<Item> findByCategories_GenreName(String genreName);
    List<Item> findByNameContainingOrPublishingHouseContainingOrAuthors_NameContainingOrCategories_GenreNameContaining(String keyword1, String keyword2, String keyword3, String keyword4);
}
