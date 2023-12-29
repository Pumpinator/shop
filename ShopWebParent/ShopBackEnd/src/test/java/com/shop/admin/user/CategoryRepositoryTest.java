package com.shop.admin.user;

import com.shop.admin.repository.CategoryRepository;
import com.shop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreateParentCategory() {
        Category category = new Category("Grocery");
        Category savedCategory = categoryRepository.save(category);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateChildrenCategory() {
        Category parentCategory = new Category(9);
        Category childrenCategory = new Category("Instant Coffee", parentCategory);
        Category savedCategory = categoryRepository.save(childrenCategory);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateTwoChildrenCategories() {
        Category parentCategory = new Category(6);
        Category childrenCategory1 = new Category("Breakfast & Cereal", parentCategory);
        Category childrenCategory2 = new Category("Coffee", parentCategory);
        Category childrenCategory3 = new Category("Meat & Seafood", parentCategory);
        Category childrenCategory4 = new Category("Beverages", parentCategory);
        categoryRepository.saveAll(List.of(childrenCategory1, childrenCategory2, childrenCategory3, childrenCategory4));
    }

    @Test
    public void testGetCategoryById() {
        Category category = categoryRepository.findById(1).get();
        System.out.println(category.getName());
        Set<Category> children = category.getChildren();
        for (Category childrenCategory : children) {
            System.out.println("\t" + childrenCategory.getName());
        }
        assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testGetAllCategories() {
        Iterable<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            if (category.getParent() == null) {
                printChildrenCategory(category, 0);
            }
        }
    }

    private void printChildrenCategory(Category parent, int subLevel) {
        for (int i = 0; i < subLevel; i++) {
            System.out.print("\t");
        }

        System.out.println(parent.getName());

        Set<Category> children = parent.getChildren();
        for (Category child : children) {
            printChildrenCategory(child, subLevel + 1);
        }
    }
}
