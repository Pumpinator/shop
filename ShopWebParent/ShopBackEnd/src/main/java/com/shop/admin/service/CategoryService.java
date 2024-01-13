package com.shop.admin.service;

import com.shop.admin.exception.CategoryNotFoundException;
import com.shop.admin.repository.CategoryRepository;
import com.shop.common.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    public List<Category> getAll() {
        Iterable<Category> categories = categoryRepository.findAll();
        List<Category> sortedCategories = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParent() == null) {
                listChildrenCategory(sortedCategories, category, 0);
            }
        }
        return sortedCategories;
    }

    private void listChildrenCategory(List<Category> sortedCategories, Category parent, int subLevel) {
        String name = "";
        for (int i = 0; i < subLevel; i++) {
            name += "\u200E \u200E \u200E \u200E ";
        }

        sortedCategories.add(new Category(name + parent.getName()));

        Set<Category> children = parent.getChildren();
        for (Category child : children) {
            listChildrenCategory(sortedCategories, child, subLevel + 1);
        }
    }

    public Page<Category> paginate(int pageNumber, String sortField, String sortOrder, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        if (keyword != null) return categoryRepository.findAllByKeyword(keyword, pageable);
        return categoryRepository.findAll(pageable);
    }

    public Category getById(Integer id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new CategoryNotFoundException("Could not find any category with id " + id);
        }
    }

    public Category save(Category category) {
        boolean isUpdatingCategory = category.getId() != null;
        if (isUpdatingCategory) {
            Category updatedCategory = categoryRepository.findById(category.getId()).get();
        }
        return categoryRepository.save(category);
    }
}