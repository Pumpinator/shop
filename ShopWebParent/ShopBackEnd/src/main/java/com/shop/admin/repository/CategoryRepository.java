package com.shop.admin.repository;

import com.shop.common.entity.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepository  extends CrudRepository<Category, Integer>, PagingAndSortingRepository<Category, Integer> {
}
