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
        Category category = new Category();
        Category savedCategory = categoryRepository.save(category);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateChildrenCategory() {
        Category parentCategory = new Category(16);
        Category childrenCategory = new Category("Ground Coffee", parentCategory);
        Category savedCategory = categoryRepository.save(childrenCategory);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateParentCategories() {
        categoryRepository.saveAll(List.of(
                        new Category("Food"),
                        new Category("Electronics")
                )
        );
    }

    @Test
    public void testCreateChildrenCategories() {
        Category foodCategory = new Category(1);
        Category electronicsCategory = new Category(2);
        categoryRepository.saveAll(List.of(
                        new Category("Fresh Produce", foodCategory),
                        new Category("Meat & Seafood", foodCategory),
                        new Category("Deli", foodCategory),
                        new Category("Snacks", foodCategory),
                        new Category("Pantry", foodCategory),
                        new Category("Beverages", foodCategory),
                        new Category("Breakfast & Cereal", foodCategory),
                        new Category("Bread & Bakery", foodCategory),
                        new Category("Dairy & Eggs", foodCategory),
                        new Category("Candy", foodCategory),
                        new Category("Baking", foodCategory),
                        new Category("Alcohol", foodCategory),
                        new Category("Frozen", foodCategory),
                        new Category("Coffee", foodCategory),
                        new Category("Tea", foodCategory),
                        new Category("TV & Video", electronicsCategory),
                        new Category("Computers & Tablets", electronicsCategory),
                        new Category("Video Games", electronicsCategory),
                        new Category("PC Gaming", electronicsCategory),
                        new Category("Audio", electronicsCategory),
                        new Category("Cell Phones", electronicsCategory),
                        new Category("Wearable Technology", electronicsCategory),
                        new Category("Smart Home & Wi-Fi", electronicsCategory),
                        new Category("Cameras", electronicsCategory)
                )
        );
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
