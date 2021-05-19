package com.service.store.dao;

import com.service.store.entity.ListOfItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListOfItemsRepository extends JpaRepository<ListOfItems, Integer> {
}
