package com.shop.admin.service;

import com.shop.admin.exception.CategoryNotFoundException;
import com.shop.admin.repository.CategoryRepository;
import com.shop.common.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    private static final int PAGESIZE = 5;

    public List<Category> getAll() {
        Iterable<Category> categories = categoryRepository.findAll();
        List<Category> sortedCategories = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParent() == null) {
                listHierarchically(sortedCategories, category, 0);
            }
        }
        return sortedCategories;
    }

    private void listHierarchically(List<Category> sortedCategories, Category parent, int subLevel) {
        String name = "";
        for (int i = 0; i < subLevel; i++) {
            name += " > ";
        }

        sortedCategories.add(Category.copy(parent, name + parent.getName()));

        Set<Category> children = parent.getChildren();
        for (Category child : children) {
            listHierarchically(sortedCategories, child, subLevel + 1);
        }
    }

    public Page<Category> paginate(int pageNumber, String sortField, String sortOrder, String keyword) {
        Iterable<Category> categories = categoryRepository.findAll();
        List<Category> sortedCategories = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParent() == null) {
                listHierarchically(sortedCategories, category, 0);
            }
        }

        if (keyword != null) {
            sortedCategories = sortedCategories.stream()
                    .filter(c -> c.getName().contains(keyword))
                    .collect(Collectors.toList());
        }

        int start = (pageNumber - 1) * PAGESIZE;
        int end = Math.min((start + PAGESIZE), sortedCategories.size());
        List<Category> list = sortedCategories.subList(start, end);

        Page<Category> categoryPage = new PageImpl<>(list, PageRequest.of(pageNumber - 1, PAGESIZE), sortedCategories.size());

        return categoryPage;
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