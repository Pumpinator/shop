package com.shop.admin.repository;

import com.shop.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer>, PagingAndSortingRepository<Category, Integer> {
    @Query("SELECT c FROM Category c WHERE CONCAT(c.id, ' ',c.name, ' ',c.alias) LIKE %?1% ")
    public Page<Category> findAllByKeyword(String keyword, Pageable pageable);
}
